import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FilialService } from '../../../../core/services';
import { FilialResponse } from '../../../../core/models';
import { LoadingComponent } from '../../../../shared/components/loading/loading.component';
import { ButtonComponent } from '../../../../shared/components/button/button.component';

@Component({
  selector: 'app-filiais-list',
  standalone: true,
  imports: [CommonModule, LoadingComponent, ButtonComponent],
  templateUrl: './filiais-list.component.html',
  styleUrls: ['./filiais-list.component.scss']
})
export class FiliaisListComponent implements OnInit {
  filiais: FilialResponse[] = [];
  loading = false;

  constructor(
    private filialService: FilialService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadFiliais();
  }

  loadFiliais(): void {
    this.loading = true;

    this.filialService.getAll().subscribe({
      next: (filiais) => {
        this.filiais = filiais;
        this.loading = false;
      },
      error: (err) => {
        this.loading = false;
        alert('Erro ao carregar filiais');
        console.error('Erro ao carregar filiais:', err);
      }
    });
  }

  viewFilial(id: number): void {
    this.router.navigate(['/admin/filiais', id]);
  }

  editFilial(id: number): void {
    this.router.navigate(['/admin/filiais', id, 'edit']);
  }

  deleteFilial(id: number): void {
    if (confirm('Tem certeza que deseja excluir esta filial?')) {
      this.loading = true;
      this.filialService.delete(id).subscribe({
        next: () => {
          this.loadFiliais();
        },
        error: (err) => {
          this.loading = false;
          // Extrair mensagem de erro
          let errorMessage = 'Erro ao excluir filial. Tente novamente.';
          if (err.error?.extractedMessage) {
            errorMessage = err.error.extractedMessage;
          } else if (err.error?.message) {
            errorMessage = err.error.message;
          } else if (err.status === 400) {
            errorMessage = 'Não foi possível excluir a filial. Verifique se há funcionários associados.';
          } else if (err.status === 404) {
            errorMessage = 'Filial não encontrada.';
          }
          alert(errorMessage);
          console.error('Erro ao excluir filial:', err);
        }
      });
    }
  }

  createFilial(): void {
    this.router.navigate(['/admin/filiais/new']);
  }
}
