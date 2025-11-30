import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { FuncionarioService } from '../../../core/services/funcionario.service';
import { FilialService } from '../../../core/services/filial.service';
import { FuncionarioResponse } from '../../../core/models/funcionario.model';
import { FilialResponse } from '../../../core/models/filial.model';
import { LoadingComponent } from '../../../shared/components/loading/loading.component';

@Component({
  selector: 'app-funcionarios-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, LoadingComponent],
  templateUrl: './funcionarios-list.component.html',
  styleUrls: ['./funcionarios-list.component.scss']
})
export class FuncionariosListComponent implements OnInit {
  funcionarios: FuncionarioResponse[] = [];
  filiais: FilialResponse[] = [];
  selectedFilialId: number | null = null;
  loading = false;
  error: string | null = null;

  constructor(
    private funcionarioService: FuncionarioService,
    private filialService: FilialService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadFiliais();
    this.loadFuncionarios();
  }

  loadFiliais(): void {
    this.filialService.getAll().subscribe({
      next: (filiais: FilialResponse[]) => {
        this.filiais = filiais;
      },
      error: (err: any) => {
        console.error('Erro ao carregar filiais:', err);
      }
    });
  }

  loadFuncionarios(): void {
    this.loading = true;
    this.error = null;

    this.funcionarioService.findAll(this.selectedFilialId || undefined).subscribe({
      next: (funcionarios) => {
        this.funcionarios = funcionarios;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erro ao carregar funcionários';
        this.loading = false;
        console.error('Erro:', err);
      }
    });
  }

  onFilialFilterChange(): void {
    this.loadFuncionarios();
  }

  clearFilter(): void {
    this.selectedFilialId = null;
    this.loadFuncionarios();
  }

  editFuncionario(id: number): void {
    this.router.navigate(['/admin/funcionarios', id, 'edit']);
  }

  toggleAtivo(funcionario: FuncionarioResponse): void {
    if (confirm(`Deseja ${funcionario.ativo ? 'desativar' : 'ativar'} o funcionário ${funcionario.nome}?`)) {
      this.funcionarioService.toggleAtivo(funcionario.id, !funcionario.ativo).subscribe({
        next: () => {
          this.loadFuncionarios();
        },
        error: (err) => {
          alert('Erro ao alterar status do funcionário');
          console.error('Erro:', err);
        }
      });
    }
  }

  deleteFuncionario(funcionario: FuncionarioResponse): void {
    if (confirm(`Tem certeza que deseja excluir o funcionário ${funcionario.nome}?`)) {
      this.funcionarioService.delete(funcionario.id).subscribe({
        next: () => {
          this.loadFuncionarios();
        },
        error: (err) => {
          alert('Erro ao excluir funcionário');
          console.error('Erro:', err);
        }
      });
    }
  }

  getStatusClass(ativo: boolean): string {
    return ativo ? 'status-ativo' : 'status-inativo';
  }

  getStatusText(ativo: boolean): string {
    return ativo ? 'Ativo' : 'Inativo';
  }
}
