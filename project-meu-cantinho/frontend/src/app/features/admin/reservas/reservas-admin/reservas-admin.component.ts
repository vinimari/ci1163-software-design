import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ReservaService } from '../../../../core/services';
import { ReservaResponse, StatusReserva } from '../../../../core/models';
import { LoadingComponent } from '../../../../shared/components/loading/loading.component';
import { ButtonComponent } from '../../../../shared/components/button/button.component';

@Component({
  selector: 'app-reservas-admin',
  standalone: true,
  imports: [CommonModule, FormsModule, LoadingComponent, ButtonComponent],
  templateUrl: './reservas-admin.component.html',
  styleUrl: './reservas-admin.component.scss'
})
export class ReservasAdminComponent implements OnInit {
  reservas: ReservaResponse[] = [];
  reservasFiltradas: ReservaResponse[] = [];
  loading = false;
  error: string | null = null;

  // Filtros
  searchTerm = '';
  statusFilter: StatusReserva | 'TODOS' = 'TODOS';
  dataFilter: 'TODAS' | 'FUTURAS' | 'PASSADAS' = 'FUTURAS';
  pagamentoFilter: 'TODOS' | 'QUITADA' | 'PENDENTE' = 'TODOS';

  StatusReserva = StatusReserva;

  constructor(
    private reservaService: ReservaService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadReservas();
  }

  loadReservas(): void {
    this.loading = true;
    this.error = null;

    this.reservaService.getAll().subscribe({
      next: (reservas) => {
        this.reservas = reservas;
        this.applyFilters();
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erro ao carregar reservas';
        this.loading = false;
        console.error('Erro ao carregar reservas:', err);
      }
    });
  }

  applyFilters(): void {
    let filtered = [...this.reservas];

    // Filtro por termo de busca (cliente ou espaço)
    if (this.searchTerm) {
      const term = this.searchTerm.toLowerCase();
      filtered = filtered.filter(reserva =>
        (reserva.usuario.nome?.toLowerCase().includes(term)) ||
        (reserva.espaco.nome?.toLowerCase().includes(term))
      );
    }

    // Filtro por status
    if (this.statusFilter !== 'TODOS') {
      filtered = filtered.filter(reserva => reserva.status === this.statusFilter);
    }

    // Filtro por data
    const hoje = new Date();
    hoje.setHours(0, 0, 0, 0);

    if (this.dataFilter === 'FUTURAS') {
      filtered = filtered.filter(reserva => {
        const dataEvento = new Date(reserva.dataEvento);
        return dataEvento >= hoje;
      });
    } else if (this.dataFilter === 'PASSADAS') {
      filtered = filtered.filter(reserva => {
        const dataEvento = new Date(reserva.dataEvento);
        return dataEvento < hoje;
      });
    }

    // Filtro por status de pagamento
    if (this.pagamentoFilter === 'QUITADA') {
      filtered = filtered.filter(reserva =>
        reserva.saldo === 0 || (reserva.totalPago !== undefined && reserva.totalPago >= reserva.valorTotal)
      );
    } else if (this.pagamentoFilter === 'PENDENTE') {
      filtered = filtered.filter(reserva =>
        reserva.saldo !== undefined && reserva.saldo > 0
      );
    }

    // Ordenar por data do evento (mais próximas primeiro para futuras, mais recentes primeiro para passadas)
    filtered.sort((a, b) => {
      const dataA = new Date(a.dataEvento).getTime();
      const dataB = new Date(b.dataEvento).getTime();
      return this.dataFilter === 'PASSADAS' ? dataB - dataA : dataA - dataB;
    });

    this.reservasFiltradas = filtered;
  }

  onSearchChange(term: string): void {
    this.searchTerm = term;
    this.applyFilters();
  }

  onStatusFilterChange(status: string): void {
    this.statusFilter = status as StatusReserva | 'TODOS';
    this.applyFilters();
  }

  onDataFilterChange(filter: string): void {
    this.dataFilter = filter as 'TODAS' | 'FUTURAS' | 'PASSADAS';
    this.applyFilters();
  }

  onPagamentoFilterChange(filter: string): void {
    this.pagamentoFilter = filter as 'TODOS' | 'QUITADA' | 'PENDENTE';
    this.applyFilters();
  }

  viewReserva(id: number): void {
    this.router.navigate(['/admin/reservas', id]);
  }

  updateStatus(reserva: ReservaResponse, novoStatus: StatusReserva): void {
    if (confirm(`Tem certeza que deseja alterar o status para ${novoStatus}?`)) {
      this.loading = true;
      this.reservaService.updateStatus(reserva.id, novoStatus).subscribe({
        next: () => {
          this.loadReservas();
        },
        error: (err) => {
          this.error = 'Erro ao atualizar status da reserva';
          this.loading = false;
          console.error('Erro ao atualizar status:', err);
        }
      });
    }
  }

  getStatusClass(status: StatusReserva): string {
    const statusMap: { [key in StatusReserva]: string } = {
      [StatusReserva.AGUARDANDO_SINAL]: 'status-aguardando',
      [StatusReserva.CONFIRMADA]: 'status-confirmada',
      [StatusReserva.QUITADA]: 'status-quitada',
      [StatusReserva.CANCELADA]: 'status-cancelada',
      [StatusReserva.FINALIZADA]: 'status-finalizada'
    };
    return statusMap[status] || '';
  }

  getStatusIcon(status: StatusReserva): string {
    const iconMap: { [key in StatusReserva]: string } = {
      [StatusReserva.AGUARDANDO_SINAL]: '⏳',
      [StatusReserva.CONFIRMADA]: '✅',
      [StatusReserva.QUITADA]: '💰',
      [StatusReserva.CANCELADA]: '❌',
      [StatusReserva.FINALIZADA]: '🎉'
    };
    return iconMap[status] || '📋';
  }

  getPagamentoStatus(reserva: ReservaResponse): string {
    if (reserva.saldo === 0 || (reserva.totalPago !== undefined && reserva.totalPago >= reserva.valorTotal)) {
      return '✅ Quitada';
    } else if (reserva.totalPago && reserva.totalPago > 0) {
      return '⚠️ Sinal Pago';
    } else {
      return '❌ Pendente';
    }
  }

  getPagamentoClass(reserva: ReservaResponse): string {
    if (reserva.saldo === 0 || (reserva.totalPago !== undefined && reserva.totalPago >= reserva.valorTotal)) {
      return 'pagamento-quitada';
    } else if (reserva.totalPago && reserva.totalPago > 0) {
      return 'pagamento-parcial';
    } else {
      return 'pagamento-pendente';
    }
  }

  getDataClass(dataEvento: string): string {
    const hoje = new Date();
    hoje.setHours(0, 0, 0, 0);
    const data = new Date(dataEvento);

    if (data < hoje) {
      return 'data-passada';
    } else if (data.toDateString() === hoje.toDateString()) {
      return 'data-hoje';
    } else {
      return 'data-futura';
    }
  }

  getTotalReservas(): number {
    return this.reservasFiltradas.length;
  }

  getValorTotalReservas(): number {
    return this.reservasFiltradas.reduce((total, reserva) => total + reserva.valorTotal, 0);
  }

  getValorTotalPago(): number {
    return this.reservasFiltradas.reduce((total, reserva) => total + (reserva.totalPago || 0), 0);
  }

  getSaldoPendente(): number {
    return this.reservasFiltradas.reduce((total, reserva) => total + (reserva.saldo || 0), 0);
  }
}
