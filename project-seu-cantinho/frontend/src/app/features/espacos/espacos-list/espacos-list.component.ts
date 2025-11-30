import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { EspacoService } from '../../../core/services/espaco.service';
import { EspacoResponse } from '../../../core/models';
import { CardComponent } from '../../../shared/components/card/card.component';
import { LoadingComponent } from '../../../shared/components/loading/loading.component';

@Component({
  selector: 'app-espacos-list',
  standalone: true,
  imports: [CommonModule, RouterModule, CardComponent, LoadingComponent],
  templateUrl: './espacos-list.component.html',
  styleUrl: './espacos-list.component.scss'
})
export class EspacosListComponent implements OnInit {
  espacos: EspacoResponse[] = [];
  loading = false;
  error = '';

  constructor(private espacoService: EspacoService) {}

  ngOnInit(): void {
    this.loadEspacos();
  }

  loadEspacos(): void {
    this.loading = true;
    this.error = '';

    this.espacoService.getAtivos().subscribe({
      next: (espacos) => {
        this.espacos = espacos;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erro ao carregar espaços.';
        this.loading = false;
        console.error('Error loading espacos:', err);
      }
    });
  }
}
