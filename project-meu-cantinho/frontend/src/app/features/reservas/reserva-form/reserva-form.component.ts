import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule, ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService, ReservaService, ClienteService, EspacoService, FilialService } from '../../../core/services';
import { ReservaRequest, ClienteResponse, EspacoResponse, FilialResponse, PerfilUsuario } from '../../../core/models';
import { LoadingComponent } from '../../../shared/components/loading/loading.component';
import { ButtonComponent } from '../../../shared/components/button/button.component';

@Component({
  selector: 'app-reserva-form',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, LoadingComponent, ButtonComponent],
  templateUrl: './reserva-form.component.html',
  styleUrl: './reserva-form.component.scss'
})
export class ReservaFormComponent implements OnInit {
  clientes: ClienteResponse[] = [];
  espacos: EspacoResponse[] = [];
  filiais: FilialResponse[] = [];
  
  loading = false;
  error: string | null = null;
  
  reserva: ReservaRequest = {
    dataEvento: '',
    valorTotal: 0,
    observacoes: '',
    usuarioId: 0,
    espacoId: 0
  };
  
  selectedFilialId: number | null = null;
  espacosFiltrados: EspacoResponse[] = [];
  
  isAdmin = false;
  isFuncionario = false;
  isCliente = false;
  userFilialId: number | null = null;

  constructor(
    private authService: AuthService,
    private reservaService: ReservaService,
    private clienteService: ClienteService,
    private espacoService: EspacoService,
    private filialService: FilialService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.checkUserPermissions();
    this.loadInitialData();
    
    // Pré-selecionar espaço se vier da URL
    const espacoId = this.route.snapshot.queryParamMap.get('espacoId');
    if (espacoId) {
      this.reserva.espacoId = +espacoId;
    }
  }

  checkUserPermissions(): void {
    const user = this.authService.getCurrentUser();
    if (!user) {
      this.router.navigate(['/login']);
      return;
    }

    this.isAdmin = this.authService.isAdmin();
    this.isFuncionario = this.authService.isFuncionario();
    this.isCliente = this.authService.isCliente();

    // Cliente faz reserva para si mesmo
    if (this.isCliente) {
      this.reserva.usuarioId = user.id;
    }

    // TODO: Funcionário precisa ter filialId no perfil
    // Por enquanto, vamos assumir que está disponível
    // this.userFilialId = user.filialId;
  }

  loadInitialData(): void {
    this.loading = true;

    // Carregar filiais primeiro
    this.filialService.getAll().subscribe({
      next: (filiais) => {
        this.filiais = filiais.filter(f => f.ativo);
        
        // Se funcionário, pré-selecionar sua filial
        if (this.isFuncionario && this.userFilialId) {
          this.selectedFilialId = this.userFilialId;
        }
        
        this.loadEspacos();
      },
      error: (err) => {
        this.error = 'Erro ao carregar filiais';
        console.error(err);
        this.loading = false;
      }
    });

    // Carregar clientes se for admin ou funcionário
    if (this.isAdmin || this.isFuncionario) {
      this.clienteService.getAll().subscribe({
        next: (clientes) => {
          this.clientes = clientes.filter(c => c.ativo);
        },
        error: (err) => {
          console.error('Erro ao carregar clientes:', err);
        }
      });
    }
  }

  loadEspacos(): void {
    this.espacoService.getAll().subscribe({
      next: (espacos) => {
        this.espacos = espacos.filter(e => e.ativo);
        this.filterEspacos();
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erro ao carregar espaços';
        console.error(err);
        this.loading = false;
      }
    });
  }

  onFilialChange(): void {
    this.reserva.espacoId = 0;
    this.filterEspacos();
  }

  filterEspacos(): void {
    if (this.isFuncionario && this.userFilialId) {
      // Funcionário só vê espaços da sua filial
      this.espacosFiltrados = this.espacos.filter(e => e.filial.id === this.userFilialId);
    } else if (this.selectedFilialId) {
      // Admin/Cliente filtra por filial selecionada
      this.espacosFiltrados = this.espacos.filter(e => e.filial.id === this.selectedFilialId);
    } else {
      // Mostrar todos
      this.espacosFiltrados = this.espacos;
    }
  }

  onEspacoChange(): void {
    const espaco = this.espacos.find(e => e.id === this.reserva.espacoId);
    if (espaco) {
      this.reserva.valorTotal = espaco.precoDiaria;
      
      // Auto-selecionar filial se ainda não foi selecionada
      if (!this.selectedFilialId) {
        this.selectedFilialId = espaco.filial.id;
        this.filterEspacos();
      }
    }
  }

  onSubmit(): void {
    if (!this.validateForm()) {
      return;
    }

    this.loading = true;
    this.error = null;

    this.reservaService.create(this.reserva).subscribe({
      next: (reserva) => {
        // Redirecionar baseado no perfil
        if (this.isCliente) {
          this.router.navigate(['/reservas']);
        } else {
          this.router.navigate(['/admin/reservas']);
        }
      },
      error: (err) => {
        this.error = err.error?.message || 'Erro ao criar reserva';
        this.loading = false;
        console.error('Erro ao criar reserva:', err);
      }
    });
  }

  validateForm(): boolean {
    if (!this.reserva.usuarioId) {
      this.error = 'Selecione um cliente';
      return false;
    }

    if (!this.reserva.espacoId) {
      this.error = 'Selecione um espaço';
      return false;
    }

    if (!this.reserva.dataEvento) {
      this.error = 'Selecione a data do evento';
      return false;
    }

    const dataEvento = new Date(this.reserva.dataEvento);
    const hoje = new Date();
    hoje.setHours(0, 0, 0, 0);

    if (dataEvento < hoje) {
      this.error = 'A data do evento não pode ser no passado';
      return false;
    }

    if (this.reserva.valorTotal <= 0) {
      this.error = 'Valor total deve ser maior que zero';
      return false;
    }

    this.error = null;
    return true;
  }

  cancel(): void {
    if (this.isCliente) {
      this.router.navigate(['/espacos']);
    } else {
      this.router.navigate(['/admin/reservas']);
    }
  }

  getClienteNome(clienteId: number): string {
    const cliente = this.clientes.find(c => c.id === clienteId);
    return cliente ? cliente.nome : '';
  }

  getEspacoInfo(espacoId: number): { nome: string; filial: string; capacidade: number; preco: number } | null {
    const espaco = this.espacos.find(e => e.id === espacoId);
    if (!espaco) return null;
    
    return {
      nome: espaco.nome,
      filial: espaco.filial.nome,
      capacidade: espaco.capacidade || 0,
      preco: espaco.precoDiaria
    };
  }

  getMinDate(): string {
    const hoje = new Date();
    return hoje.toISOString().split('T')[0];
  }
}
