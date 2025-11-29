import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ReservaService } from '../../../core/services/reserva.service';
import { AuthService } from '../../../core/services/auth.service';
import { ReservaResponse, StatusReserva } from '../../../core/models';
import { CardComponent } from '../../../shared/components/card/card.component';
import { LoadingComponent } from '../../../shared/components/loading/loading.component';

@Component({
  selector: 'app-reservas-list',
  standalone: true,
  imports: [CommonModule, RouterModule, CardComponent, LoadingComponent],
  templateUrl: './reservas-list.component.html',
  styleUrl: './reservas-list.component.scss'
})
export class ReservasListComponent implements OnInit {
  reservas: ReservaResponse[] = [];
  loading = false;

  constructor(
    private reservaService: ReservaService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadReservas();
  }

  loadReservas(): void {
    const user = this.authService.getCurrentUser();
    if (!user) return;

    this.loading = true;

    this.reservaService.getByUsuarioId(user.id).subscribe({
      next: (reservas) => {
        this.reservas = reservas;
        this.loading = false;
      },
      error: (err) => {
        this.loading = false;
        alert('Erro ao carregar reservas.');
        console.error('Error loading reservas:', err);
      }
    });
  }

  getStatusLabel(status: StatusReserva): string {
    const labels: Record<StatusReserva, string> = {
      [StatusReserva.AGUARDANDO_SINAL]: 'Aguardando Sinal',
      [StatusReserva.CONFIRMADA]: 'Confirmada',
      [StatusReserva.QUITADA]: 'Quitada',
      [StatusReserva.CANCELADA]: 'Cancelada',
      [StatusReserva.FINALIZADA]: 'Finalizada'
    };
    return labels[status] || status;
  }
}
