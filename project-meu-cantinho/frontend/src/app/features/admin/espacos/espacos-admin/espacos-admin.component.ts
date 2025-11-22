import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { EspacoService, AuthService, FilialService } from '../../../../core/services';
import { EspacoResponse, EspacoRequest } from '../../../../core/models';
import { EspacoFormComponent } from '../espaco-form/espaco-form.component';

@Component({
  selector: 'app-espacos-admin',
  standalone: true,
  imports: [CommonModule, RouterModule, EspacoFormComponent],
  templateUrl: './espacos-admin.component.html',
  styleUrl: './espacos-admin.component.scss'
})
export class EspacosAdminComponent implements OnInit {
  espacos: EspacoResponse[] = [];
  espacosFiltrados: EspacoResponse[] = [];
  loading = false;
  showForm = false;
  espacoSelecionado?: EspacoResponse;
  filialIdFuncionario?: number;
  filtroFilial = '';
  filtroStatus = '';
  searchTerm = '';

  constructor(
    private espacoService: EspacoService,
    private authService: AuthService,
    private filialService: FilialService
  ) {}

  ngOnInit(): void {
    this.verificarPermissoes();
    this.loadEspacos();
  }

  private verificarPermissoes(): void {
    const user = this.authService.getCurrentUser();
    if (this.authService.isFuncionario() && user) {
      // TODO: Buscar filialId do funcionário do backend
      // Por enquanto, vamos usar um mock
      this.filialIdFuncionario = 1; // Mock
    }
  }

  loadEspacos(): void {
    this.loading = true;

    if (this.filialIdFuncionario) {
      // Funcionário: busca apenas da sua filial
      this.espacoService.getByFilialId(this.filialIdFuncionario).subscribe({
        next: (espacos: EspacoResponse[]) => {
          this.espacos = espacos;
          this.aplicarFiltros();
          this.loading = false;
        },
        error: (error: any) => {
          console.error('Erro ao carregar espaços:', error);
          this.loading = false;
        }
      });
    } else {
      // Admin: busca todos
      this.espacoService.getAll().subscribe({
        next: (espacos: EspacoResponse[]) => {
          this.espacos = espacos;
          this.aplicarFiltros();
          this.loading = false;
        },
        error: (error: any) => {
          console.error('Erro ao carregar espaços:', error);
          this.loading = false;
        }
      });
    }
  }

  aplicarFiltros(): void {
    this.espacosFiltrados = this.espacos.filter(espaco => {
      const matchSearch = !this.searchTerm ||
        espaco.nome.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        espaco.filial.nome?.toLowerCase().includes(this.searchTerm.toLowerCase());

      const matchStatus = !this.filtroStatus ||
        (this.filtroStatus === 'ativo' && espaco.ativo) ||
        (this.filtroStatus === 'inativo' && !espaco.ativo);

      return matchSearch && matchStatus;
    });
  }

  onSearchChange(term: string): void {
    this.searchTerm = term;
    this.aplicarFiltros();
  }

  onStatusFilterChange(status: string): void {
    this.filtroStatus = status;
    this.aplicarFiltros();
  }

  novoEspaco(): void {
    this.espacoSelecionado = undefined;
    this.showForm = true;
  }

  editarEspaco(espaco: EspacoResponse): void {
    this.espacoSelecionado = espaco;
    this.showForm = true;
  }

  onSubmitForm(espacoRequest: EspacoRequest): void {
    if (this.espacoSelecionado) {
      // Editar
      this.espacoService.update(this.espacoSelecionado.id, espacoRequest).subscribe({
        next: () => {
          this.showForm = false;
          this.loadEspacos();
          alert('Espaço atualizado com sucesso!');
        },
        error: (error: any) => {
          console.error('Erro ao atualizar espaço:', error);
          alert('Erro ao atualizar espaço. Tente novamente.');
        }
      });
    } else {
      // Criar
      this.espacoService.create(espacoRequest).subscribe({
        next: () => {
          this.showForm = false;
          this.loadEspacos();
          alert('Espaço criado com sucesso!');
        },
        error: (error: any) => {
          console.error('Erro ao criar espaço:', error);
          alert('Erro ao criar espaço. Tente novamente.');
        }
      });
    }
  }

  onCancelForm(): void {
    this.showForm = false;
    this.espacoSelecionado = undefined;
  }

  toggleAtivo(espaco: EspacoResponse): void {
    const novoStatus = !espaco.ativo;
    const espacoAtualizado: EspacoRequest = {
      nome: espaco.nome,
      descricao: espaco.descricao,
      capacidade: espaco.capacidade,
      precoDiaria: espaco.precoDiaria,
      ativo: novoStatus,
      urlFotoPrincipal: espaco.urlFotoPrincipal,
      filialId: espaco.filial.id
    };

    this.espacoService.update(espaco.id, espacoAtualizado).subscribe({
      next: () => {
        this.loadEspacos();
        alert(`Espaço ${novoStatus ? 'ativado' : 'desativado'} com sucesso!`);
      },
      error: (error: any) => {
        console.error('Erro ao alterar status:', error);
        alert('Erro ao alterar status do espaço.');
      }
    });
  }

  excluirEspaco(espaco: EspacoResponse): void {
    if (confirm(`Tem certeza que deseja excluir o espaço "${espaco.nome}"?`)) {
      this.espacoService.delete(espaco.id).subscribe({
        next: () => {
          this.loadEspacos();
          alert('Espaço excluído com sucesso!');
        },
        error: (error: any) => {
          console.error('Erro ao excluir espaço:', error);
          alert('Erro ao excluir espaço. Verifique se não há reservas associadas.');
        }
      });
    }
  }
}
