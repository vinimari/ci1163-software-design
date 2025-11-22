import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, ActivatedRoute } from '@angular/router';
import { FilialService } from '../../../../core/services';
import { FilialResponse } from '../../../../core/models';
import { CardComponent } from '../../../../shared/components/card/card.component';
import { LoadingComponent } from '../../../../shared/components/loading/loading.component';

@Component({
  selector: 'app-filial-detail',
  standalone: true,
  imports: [CommonModule, CardComponent, LoadingComponent],
  templateUrl: './filial-detail.component.html',
  styleUrl: './filial-detail.component.scss'
})
export class FilialDetailComponent implements OnInit {
  filial: FilialResponse | null = null;
  loading = false;
  error: string | null = null;

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
        this.error = 'Erro ao carregar filial';
        this.loading = false;
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
          this.error = 'Erro ao excluir filial';
          this.loading = false;
          console.error('Erro ao excluir filial:', err);
        }
      });
    }
  }

  goBack(): void {
    this.router.navigate(['/admin/filiais']);
  }
}
