import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ReservaService, PagamentoService, ClienteService, EspacoService } from '../../../../core/services';
import { ReservaResponse, PagamentoResponse, PagamentoRequest, StatusReserva, TipoPagamento } from '../../../../core/models';
import { LoadingComponent } from '../../../../shared/components/loading/loading.component';
import { ButtonComponent } from '../../../../shared/components/button/button.component';

@Component({
  selector: 'app-reserva-detail',
  standalone: true,
  imports: [CommonModule, FormsModule, LoadingComponent, ButtonComponent],
  templateUrl: './reserva-detail.component.html',
  styleUrl: './reserva-detail.component.scss'
})
export class ReservaDetailComponent implements OnInit {
  reserva: ReservaResponse | null = null;
  pagamentos: PagamentoResponse[] = [];
  loading = false;
  loadingPagamentos = false;
  error: string | null = null;

  // Modal de novo pagamento
  showPagamentoModal = false;
  novoPagamento: PagamentoRequest = {
    valor: 0,
    tipo: TipoPagamento.SINAL,
    formaPagamento: '',
    codigoTransacaoGateway: '',
    reservaId: 0
  };

  StatusReserva = StatusReserva;
  TipoPagamento = TipoPagamento;

  constructor(
    private reservaService: ReservaService,
    private pagamentoService: PagamentoService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadReserva(+id);
      this.loadPagamentos(+id);
    }
  }

  loadReserva(id: number): void {
    this.loading = true;
    this.error = null;

    this.reservaService.getById(id).subscribe({
      next: (reserva) => {
        this.reserva = reserva;
        this.novoPagamento.reservaId = reserva.id;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erro ao carregar reserva';
        this.loading = false;
        console.error('Erro ao carregar reserva:', err);
      }
    });
  }

  loadPagamentos(reservaId: number): void {
    this.loadingPagamentos = true;

    this.pagamentoService.getByReservaId(reservaId).subscribe({
      next: (pagamentos) => {
        this.pagamentos = pagamentos.sort((a, b) =>
          new Date(b.dataPagamento).getTime() - new Date(a.dataPagamento).getTime()
        );
        this.loadingPagamentos = false;
      },
      error: (err) => {
        console.error('Erro ao carregar pagamentos:', err);
        this.loadingPagamentos = false;
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/admin/reservas']);
  }

  openPagamentoModal(): void {
    this.novoPagamento = {
      valor: this.getSaldoPendente(),
      tipo: TipoPagamento.QUITACAO,
      formaPagamento: '',
      codigoTransacaoGateway: '',
      reservaId: this.reserva!.id
    };
    this.showPagamentoModal = true;
  }

  closePagamentoModal(): void {
    this.showPagamentoModal = false;
  }

  salvarPagamento(): void {
    if (!this.validarPagamento()) {
      return;
    }

    this.loading = true;
    this.pagamentoService.create(this.novoPagamento).subscribe({
      next: () => {
        this.loadReserva(this.reserva!.id);
        this.loadPagamentos(this.reserva!.id);
        this.closePagamentoModal();
        this.loading = false;
      },
      error: (err) => {
        this.error = err.error?.message || 'Erro ao registrar pagamento';
        this.loading = false;
        console.error('Erro ao registrar pagamento:', err);
      }
    });
  }

  validarPagamento(): boolean {
    if (this.novoPagamento.valor <= 0) {
      this.error = 'Valor deve ser maior que zero';
      return false;
    }

    const saldo = this.getSaldoPendente();
    if (this.novoPagamento.valor > saldo) {
      this.error = `Valor não pode exceder o saldo pendente (R$ ${saldo.toFixed(2)})`;
      return false;
    }

    if (!this.novoPagamento.formaPagamento) {
      this.error = 'Forma de pagamento é obrigatória';
      return false;
    }

    this.error = null;
    return true;
  }

  excluirPagamento(pagamentoId: number): void {
    if (confirm('Tem certeza que deseja excluir este pagamento?')) {
      this.loading = true;
      this.pagamentoService.delete(pagamentoId).subscribe({
        next: () => {
          this.loadReserva(this.reserva!.id);
          this.loadPagamentos(this.reserva!.id);
          this.loading = false;
        },
        error: (err) => {
          this.error = 'Erro ao excluir pagamento';
          this.loading = false;
          console.error('Erro ao excluir pagamento:', err);
        }
      });
    }
  }

  updateStatus(novoStatus: StatusReserva): void {
    if (confirm(`Tem certeza que deseja alterar o status para ${novoStatus}?`)) {
      this.loading = true;
      this.reservaService.updateStatus(this.reserva!.id, novoStatus).subscribe({
        next: () => {
          this.loadReserva(this.reserva!.id);
          this.loading = false;
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

  getTipoPagamentoIcon(tipo: TipoPagamento): string {
    const iconMap: { [key in TipoPagamento]: string } = {
      [TipoPagamento.SINAL]: '💰',
      [TipoPagamento.QUITACAO]: '💳',
      [TipoPagamento.TOTAL]: '✅'
    };
    return iconMap[tipo] || '💵';
  }

  getTotalPago(): number {
    return this.pagamentos.reduce((total, p) => total + p.valor, 0);
  }

  getSaldoPendente(): number {
    if (!this.reserva) return 0;
    return Math.max(0, this.reserva.valorTotal - this.getTotalPago());
  }

  isPagamentoQuitado(): boolean {
    return this.getSaldoPendente() === 0;
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
}
