import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule, ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService, ReservaService, ClienteService, EspacoService, FilialService } from '../../../core/services';
import { PagamentoService } from '../../../core/services/pagamento.service';
import { ReservaRequest, ClienteResponse, EspacoResponse, FilialResponse, PerfilUsuario, PagamentoRequest, TipoPagamento } from '../../../core/models';

@Component({
  selector: 'app-reserva-form',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './reserva-form.component.html',
  styleUrl: './reserva-form.component.scss'
})
export class ReservaFormComponent implements OnInit {
  clientes: ClienteResponse[] = [];
  espacos: EspacoResponse[] = [];
  loading = false;
  error: string | null = null;

  reserva: ReservaRequest = {
    dataEvento: '',
    valorTotal: 0,
    observacoes: '',
    usuarioId: 0,
    espacoId: 0
  };

  isAdmin = false;
  isFuncionario = false;
  isCliente = false;

  // Pagamento
  tipoPagamento: TipoPagamento = TipoPagamento.SINAL;
  formaPagamento: string = '';
  codigoTransacao: string = '';
  readonly TipoPagamento = TipoPagamento;

  constructor(
    private authService: AuthService,
    private reservaService: ReservaService,
    private clienteService: ClienteService,
    private espacoService: EspacoService,
    private pagamentoService: PagamentoService,
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

    // Carregar espaços diretamente (sem filiais)
    this.espacoService.getAll().subscribe({
      next: (espacos) => {
        this.espacos = espacos.filter(e => e.ativo);
        // Se já existe um espacoId pré-selecionado, definir valorTotal
        if (this.reserva.espacoId) {
          const espaco = this.espacos.find(e => e.id === this.reserva.espacoId);
          if (espaco) {
            this.reserva.valorTotal = espaco.precoDiaria;
          }
        }
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erro ao carregar espaços';
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
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erro ao carregar espaços';
        console.error(err);
        this.loading = false;
      }
    });
  }

  // Filtro de filial removido: todos os espaços são exibidos diretamente

  onEspacoChange(): void {
    // Converter para número caso venha como string do select
    const espacoId = typeof this.reserva.espacoId === 'string'
      ? parseInt(this.reserva.espacoId)
      : this.reserva.espacoId;

    const espaco = this.espacos.find(e => e.id === espacoId);
    if (espaco) {
      this.reserva.espacoId = espacoId; // Garantir que é número
      this.reserva.valorTotal = espaco.precoDiaria;
    }
  }

  onSubmit(): void {
    if (!this.validateForm()) {
      return;
    }

    this.loading = true;
    this.error = null;

    // Criar reserva primeiro
    this.reservaService.create(this.reserva).subscribe({
      next: (reserva) => {
        // Criar pagamento
        const valorPagamento = this.tipoPagamento === TipoPagamento.TOTAL
          ? this.reserva.valorTotal
          : this.reserva.valorTotal * 0.5; // 50% para sinal

        const pagamento: PagamentoRequest = {
          valor: valorPagamento,
          tipo: this.tipoPagamento,
          formaPagamento: this.formaPagamento,
          codigoTransacaoGateway: this.codigoTransacao || undefined,
          reservaId: reserva.id
        };

        this.pagamentoService.create(pagamento).subscribe({
          next: () => {
            // Redirecionar baseado no perfil
            if (this.isCliente) {
              this.router.navigate(['/reservas']);
            } else {
              this.router.navigate(['/admin/reservas']);
            }
          },
          error: (err) => {
            this.error = 'Reserva criada mas erro ao registrar pagamento: ' + (err.error?.message || 'Erro desconhecido');
            this.loading = false;
            console.error('Erro ao criar pagamento:', err);
          }
        });
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

    if (!this.formaPagamento) {
      this.error = 'Selecione a forma de pagamento';
      return false;
    }

    this.error = null;
    return true;
  }

  getValorPagamento(): number {
    return this.tipoPagamento === TipoPagamento.TOTAL
      ? this.reserva.valorTotal
      : this.reserva.valorTotal * 0.5;
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
