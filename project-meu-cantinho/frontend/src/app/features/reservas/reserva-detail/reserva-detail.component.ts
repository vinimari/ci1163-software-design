import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ReservaService } from '../../../core/services/reserva.service';
import { PagamentoService } from '../../../core/services/pagamento.service';
import { ReservaResponse, PagamentoRequest, PagamentoResponse, StatusReserva, TipoPagamento } from '../../../core/models';
import { CardComponent } from '../../../shared/components/card/card.component';
import { LoadingComponent } from '../../../shared/components/loading/loading.component';

@Component({
  selector: 'app-reserva-detail',
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule, CardComponent, LoadingComponent],
  templateUrl: './reserva-detail.component.html',
  styleUrl: './reserva-detail.component.scss'
})
export class ReservaDetailComponent implements OnInit {
  reserva?: ReservaResponse;
  pagamentos: PagamentoResponse[] = [];
  loading = false;
  error = '';
  successMessage = '';
  showPagamentoForm = false;
  pagamentoForm: FormGroup;

  readonly StatusReserva = StatusReserva;
  readonly TipoPagamento = TipoPagamento;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private reservaService: ReservaService,
    private pagamentoService: PagamentoService,
    private fb: FormBuilder
  ) {
    this.pagamentoForm = this.fb.group({
      valor: [0, [Validators.required, Validators.min(0.01)]],
      tipo: [TipoPagamento.SINAL, Validators.required],
      formaPagamento: ['', Validators.required],
      codigoTransacaoGateway: ['']
    });
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadReserva(+id);
      this.loadPagamentos(+id);
    }
  }

  loadReserva(id: number): void {
    this.loading = true;
    this.error = '';

    this.reservaService.getById(id).subscribe({
      next: (reserva) => {
        this.reserva = reserva;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erro ao carregar reserva';
        this.loading = false;
        console.error(err);
      }
    });
  }

  loadPagamentos(reservaId: number): void {
    this.pagamentoService.getByReservaId(reservaId).subscribe({
      next: (pagamentos) => {
        this.pagamentos = pagamentos;
      },
      error: (err) => {
        console.error('Erro ao carregar pagamentos', err);
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

  getTipoPagamentoLabel(tipo: TipoPagamento): string {
    const labels: Record<TipoPagamento, string> = {
      [TipoPagamento.SINAL]: 'Sinal',
      [TipoPagamento.QUITACAO]: 'Quitação',
      [TipoPagamento.TOTAL]: 'Total'
    };
    return labels[tipo] || tipo;
  }

  togglePagamentoForm(): void {
    this.showPagamentoForm = !this.showPagamentoForm;
    if (this.showPagamentoForm && this.reserva) {
      // Preenche o valor com o saldo restante
      this.pagamentoForm.patchValue({
        valor: this.reserva.saldo
      });
    }
  }

  onSubmitPagamento(): void {
    if (this.pagamentoForm.invalid || !this.reserva) return;

    this.loading = true;
    this.error = '';
    this.successMessage = '';

    const pagamento: PagamentoRequest = {
      ...this.pagamentoForm.value,
      reservaId: this.reserva.id
    };

    this.pagamentoService.create(pagamento).subscribe({
      next: () => {
        this.successMessage = 'Pagamento registrado com sucesso!';
        this.loading = false;
        this.showPagamentoForm = false;
        this.pagamentoForm.reset({
          tipo: TipoPagamento.SINAL
        });
        // Recarrega os dados
        this.loadReserva(this.reserva!.id);
        this.loadPagamentos(this.reserva!.id);
      },
      error: (err) => {
        this.error = 'Erro ao registrar pagamento';
        this.loading = false;
        console.error(err);
      }
    });
  }

  cancelarReserva(): void {
    if (!this.reserva) return;

    if (!confirm('Tem certeza que deseja cancelar esta reserva?')) return;

    this.loading = true;
    this.error = '';

    this.reservaService.updateStatus(this.reserva.id, StatusReserva.CANCELADA).subscribe({
      next: () => {
        this.successMessage = 'Reserva cancelada com sucesso!';
        this.loading = false;
        setTimeout(() => {
          this.router.navigate(['/reservas']);
        }, 2000);
      },
      error: (err) => {
        this.error = 'Erro ao cancelar reserva';
        this.loading = false;
        console.error(err);
      }
    });
  }

  canCancelar(): boolean {
    if (!this.reserva) return false;
    return this.reserva.status !== StatusReserva.CANCELADA &&
           this.reserva.status !== StatusReserva.FINALIZADA;
  }

  canPagar(): boolean {
    if (!this.reserva || this.reserva.saldo === undefined) return false;
    return this.reserva.saldo > 0 &&
           this.reserva.status !== StatusReserva.CANCELADA &&
           this.reserva.status !== StatusReserva.FINALIZADA;
  }
}
