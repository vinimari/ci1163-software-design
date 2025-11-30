import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { EspacoService } from '../../../core/services/espaco.service';
import { EspacoResponse } from '../../../core/models';
import { LoadingComponent } from '../../../shared/components/loading/loading.component';

@Component({
  selector: 'app-espaco-detail',
  standalone: true,
  imports: [CommonModule, RouterModule, LoadingComponent],
  templateUrl: './espaco-detail.component.html',
  styleUrl: './espaco-detail.component.scss'
})
export class EspacoDetailComponent implements OnInit {
  espaco: EspacoResponse | null = null;
  loading = false;
  error = '';

  constructor(
    private espacoService: EspacoService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadEspaco(Number(id));
    }
  }

  loadEspaco(id: number): void {
    this.loading = true;
    this.error = '';

    this.espacoService.getById(id).subscribe({
      next: (espaco) => {
        this.espaco = espaco;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erro ao carregar detalhes do espaço.';
        this.loading = false;
        console.error('Error loading espaco:', err);
      }
    });
  }
}
