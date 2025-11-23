import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ReservaService } from '../../../core/services/reserva.service';
import { PagamentoService } from '../../../core/services/pagamento.service';
import { ReservaResponse, PagamentoRequest, PagamentoResponse, StatusReserva, TipoPagamento } from '../../../core/models';
import { CardComponent } from '../../../shared/components/card/card.component';
import { LoadingComponent } from '../../../shared/components/loading/loading.component';
import { ButtonComponent } from '../../../shared/components/button/button.component';

@Component({
  selector: 'app-reserva-detail',
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule, CardComponent, LoadingComponent, ButtonComponent],
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
      const { tipo, valor } = this.calcularProximoPagamento();
      this.pagamentoForm.patchValue({
        valor: valor,
        tipo: tipo
      });
      // Desabilita os campos tipo e valor para que não possam ser alterados
      this.pagamentoForm.get('tipo')?.disable();
      this.pagamentoForm.get('valor')?.disable();
    } else {
      // Reabilita os campos quando fechar
      this.pagamentoForm.get('tipo')?.enable();
      this.pagamentoForm.get('valor')?.enable();
    }
  }

  calcularProximoPagamento(): { tipo: TipoPagamento, valor: number } {
    if (!this.reserva) {
      return { tipo: TipoPagamento.SINAL, valor: 0 };
    }

    const metadeValor = this.reserva.valorTotal / 2;
    const possuiPagamentos = this.pagamentos.length > 0;

    if (!possuiPagamentos) {
      // Primeiro pagamento: pode ser SINAL (50%) ou TOTAL (100%)
      // Por padrão, sugerimos SINAL
      return { tipo: TipoPagamento.SINAL, valor: metadeValor };
    } else {
      // Segundo pagamento: deve ser QUITACAO (50% restantes)
      const primeiroPagamento = this.pagamentos[0];
      if (primeiroPagamento.tipo === TipoPagamento.SINAL) {
        return { tipo: TipoPagamento.QUITACAO, valor: metadeValor };
      }
      // Se o primeiro foi TOTAL, não deve permitir mais pagamentos
      return { tipo: TipoPagamento.QUITACAO, valor: 0 };
    }
  }

  getTipoPagamentoInfo(): string {
    if (!this.reserva) return '';

    const possuiPagamentos = this.pagamentos.length > 0;

    if (!possuiPagamentos) {
      return 'Você pode escolher entre pagar 50% agora (SINAL) ou o valor total (100%).';
    } else {
      const primeiroPagamento = this.pagamentos[0];
      if (primeiroPagamento.tipo === TipoPagamento.SINAL) {
        return 'Complete o pagamento quitando os 50% restantes.';
      }
      return 'Esta reserva já foi totalmente paga.';
    }
  }

  permitirTrocarTipoPagamento(): boolean {
    // Só permite trocar entre SINAL e TOTAL no primeiro pagamento
    return this.pagamentos.length === 0;
  }

  onSubmitPagamento(): void {
    if (!this.reserva) return;

    // Valida apenas os campos habilitados
    const formaPagamento = this.pagamentoForm.get('formaPagamento')?.value;
    if (!formaPagamento) {
      this.error = 'Forma de pagamento é obrigatória';
      return;
    }

    this.loading = true;
    this.error = '';
    this.successMessage = '';

    // Pega os valores diretamente dos campos, mesmo que estejam desabilitados
    const { tipo, valor } = this.calcularProximoPagamento();

    const pagamento: PagamentoRequest = {
      valor: valor,
      tipo: tipo,
      formaPagamento: formaPagamento,
      codigoTransacaoGateway: this.pagamentoForm.get('codigoTransacaoGateway')?.value || '',
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
        this.error = err.error?.message || 'Erro ao registrar pagamento';
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

  goBack(): void {
    this.router.navigate(['/reservas']);
  }
}
