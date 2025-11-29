import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, ActivatedRoute } from '@angular/router';
import { FilialService } from '../../../../core/services';
import { FilialResponse } from '../../../../core/models';
import { LoadingComponent } from '../../../../shared/components/loading/loading.component';
import { ButtonComponent } from '../../../../shared/components/button/button.component';

@Component({
  selector: 'app-filial-detail',
  standalone: true,
  imports: [CommonModule, LoadingComponent, ButtonComponent],
  templateUrl: './filial-detail.component.html',
  styleUrls: ['./filial-detail.component.scss']
})
export class FilialDetailComponent implements OnInit {
  filial: FilialResponse | null = null;
  loading = false;

  constructor(
    private filialService: FilialService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadFilial(+id);
    }
  }

  loadFilial(id: number): void {
    this.loading = true;
    this.filialService.getById(id).subscribe({
      next: (filial) => {
        this.filial = filial;
        this.loading = false;
      },
      error: (err) => {
        this.loading = false;
        alert('Erro ao carregar filial');
        console.error('Erro ao carregar filial:', err);
      }
    });
  }

  editFilial(): void {
    if (this.filial) {
      this.router.navigate(['/admin/filiais', this.filial.id, 'edit']);
    }
  }

  deleteFilial(): void {
    if (this.filial && confirm('Tem certeza que deseja excluir esta filial?')) {
      this.loading = true;
      this.filialService.delete(this.filial.id).subscribe({
        next: () => {
          this.router.navigate(['/admin/filiais']);
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

  goBack(): void {
    this.router.navigate(['/admin/filiais']);
  }
}
