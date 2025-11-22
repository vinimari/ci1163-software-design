import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, ActivatedRoute } from '@angular/router';
import { ClienteService, ReservaService } from '../../../../core/services';
import { ClienteResponse, ReservaResponse } from '../../../../core/models';
import { LoadingComponent } from '../../../../shared/components/loading/loading.component';
import { ButtonComponent } from '../../../../shared/components/button/button.component';

@Component({
  selector: 'app-cliente-detail',
  standalone: true,
  imports: [CommonModule, LoadingComponent, ButtonComponent],
  templateUrl: './cliente-detail.component.html',
  styleUrls: ['./cliente-detail.component.scss']
})
export class ClienteDetailComponent implements OnInit {
  cliente: ClienteResponse | null = null;
  reservas: ReservaResponse[] = [];
  loading = false;
  loadingReservas = false;
  error: string | null = null;

  constructor(
    private clienteService: ClienteService,
    private reservaService: ReservaService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadCliente(+id);
      this.loadReservas(+id);
    }
  }

  loadCliente(id: number): void {
    this.loading = true;
    this.clienteService.getById(id).subscribe({
      next: (cliente) => {
        this.cliente = cliente;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erro ao carregar cliente';
        this.loading = false;
        console.error('Erro ao carregar cliente:', err);
      }
    });
  }

  loadReservas(usuarioId: number): void {
    this.loadingReservas = true;
    this.reservaService.getByUsuarioId(usuarioId).subscribe({
      next: (reservas: ReservaResponse[]) => {
        this.reservas = reservas;
        this.loadingReservas = false;
      },
      error: (err: any) => {
        console.error('Erro ao carregar reservas:', err);
        this.loadingReservas = false;
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/admin/clientes']);
  }

  viewReserva(reservaId: number): void {
    this.router.navigate(['/admin/reservas', reservaId]);
  }

  getStatusClass(status: string): string {
    const statusMap: { [key: string]: string } = {
      'PENDENTE': 'status-pendente',
      'CONFIRMADA': 'status-confirmada',
      'CANCELADA': 'status-cancelada',
      'CONCLUIDA': 'status-concluida'
    };
    return statusMap[status] || '';
  }

  getStatusIcon(status: string): string {
    const iconMap: { [key: string]: string } = {
      'PENDENTE': '⏳',
      'CONFIRMADA': '✅',
      'CANCELADA': '❌',
      'CONCLUIDA': '🎉'
    };
    return iconMap[status] || '📋';
  }

  getTotalReservas(): number {
    return this.reservas.length;
  }

  getValorTotalReservas(): number {
    return this.reservas.reduce((total, reserva) => total + reserva.valorTotal, 0);
  }
}
