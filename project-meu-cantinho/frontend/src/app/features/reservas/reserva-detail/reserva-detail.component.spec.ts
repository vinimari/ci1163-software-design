import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of, throwError } from 'rxjs';
import { ReservaDetailComponent } from './reserva-detail.component';
import { ReservaService } from '../../../core/services/reserva.service';
import { PagamentoService } from '../../../core/services/pagamento.service';
import { StatusReserva, TipoPagamento } from '../../../core/models';

describe('ReservaDetailComponent', () => {
  let component: ReservaDetailComponent;
  let fixture: ComponentFixture<ReservaDetailComponent>;
  let reservaService: jest.Mocked<ReservaService>;
  let pagamentoService: jest.Mocked<PagamentoService>;
  let router: jest.Mocked<Router>;

  const mockReserva = {
    id: 1,
    dataEvento: '2024-12-25',
    valorTotal: 1000,
    status: StatusReserva.CONFIRMADA,
    usuarioId: 1,
    espacoId: 1,
    espacoNome: 'Salão de Festas',
    totalPago: 500,
    saldo: 500,
    dataCriacao: '2024-01-01'
  };

  const mockPagamentos = [
    {
      id: 1,
      valor: 500,
      tipo: TipoPagamento.SINAL,
      formaPagamento: 'PIX',
      dataHora: '2024-01-01T10:00:00',
      reservaId: 1
    }
  ];

  beforeEach(async () => {
    const activatedRouteMock = {
      snapshot: {
        paramMap: {
          get: jest.fn().mockReturnValue('1')
        }
      }
    };

    const reservaServiceMock = {
      getById: jest.fn(),
      updateStatus: jest.fn()
    };

    const pagamentoServiceMock = {
      getByReservaId: jest.fn(),
      create: jest.fn()
    };

    const routerMock = {
      navigate: jest.fn()
    };

    await TestBed.configureTestingModule({
      imports: [ReservaDetailComponent, HttpClientTestingModule],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRouteMock },
        { provide: ReservaService, useValue: reservaServiceMock },
        { provide: PagamentoService, useValue: pagamentoServiceMock },
        { provide: Router, useValue: routerMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ReservaDetailComponent);
    component = fixture.componentInstance;
    reservaService = TestBed.inject(ReservaService) as jest.Mocked<ReservaService>;
    pagamentoService = TestBed.inject(PagamentoService) as jest.Mocked<PagamentoService>;
    router = TestBed.inject(Router) as jest.Mocked<Router>;
  });

  it('deve criar', () => {
    expect(component).toBeTruthy();
  });

  describe('ngOnInit', () => {
    it('deve carregar reserva and pagamentos on init', () => {
      reservaService.getById.mockReturnValue(of(mockReserva));
      pagamentoService.getByReservaId.mockReturnValue(of(mockPagamentos));

      component.ngOnInit();

      expect(reservaService.getById).toHaveBeenCalledWith(1);
      expect(pagamentoService.getByReservaId).toHaveBeenCalledWith(1);
    });
  });

  describe('loadReserva', () => {
    it('deve carregar reserva successfully', () => {
      reservaService.getById.mockReturnValue(of(mockReserva));

      component.loadReserva(1);

      expect(component.loading).toBe(false);
      expect(component.reserva).toEqual(mockReserva);
      expect(component.error).toBe('');
    });

    it('deve tratar erro when loading reserva', () => {
      reservaService.getById.mockReturnValue(throwError(() => new Error('Error')));

      component.loadReserva(1);

      expect(component.loading).toBe(false);
      expect(component.error).toBe('Erro ao carregar reserva');
    });
  });

  describe('loadPagamentos', () => {
    it('deve carregar pagamentos successfully', () => {
      pagamentoService.getByReservaId.mockReturnValue(of(mockPagamentos));

      component.loadPagamentos(1);

      expect(component.pagamentos).toEqual(mockPagamentos);
    });
  });

  describe('getStatusLabel', () => {
    it('deve retornar corretamente label for each status', () => {
      expect(component.getStatusLabel(StatusReserva.AGUARDANDO_SINAL)).toBe('Aguardando Sinal');
      expect(component.getStatusLabel(StatusReserva.CONFIRMADA)).toBe('Confirmada');
      expect(component.getStatusLabel(StatusReserva.QUITADA)).toBe('Quitada');
      expect(component.getStatusLabel(StatusReserva.CANCELADA)).toBe('Cancelada');
      expect(component.getStatusLabel(StatusReserva.FINALIZADA)).toBe('Finalizada');
    });
  });

  describe('getTipoPagamentoLabel', () => {
    it('deve retornar corretamente label for each tipo', () => {
      expect(component.getTipoPagamentoLabel(TipoPagamento.SINAL)).toBe('Sinal');
      expect(component.getTipoPagamentoLabel(TipoPagamento.QUITACAO)).toBe('Quitação');
      expect(component.getTipoPagamentoLabel(TipoPagamento.TOTAL)).toBe('Total');
    });
  });

  describe('togglePagamentoForm', () => {
    it('deve toggle form visibility and set valores', () => {
      component.reserva = mockReserva;
      component.pagamentos = mockPagamentos;

      component.togglePagamentoForm();

      expect(component.showPagamentoForm).toBe(true);
    });
  });

  describe('calcularProximoPagamento', () => {
    it('deve retornar SINAL when no pagamentos exist', () => {
      component.reserva = mockReserva;
      component.pagamentos = [];

      const result = component.calcularProximoPagamento();

      expect(result.tipo).toBe(TipoPagamento.SINAL);
      expect(result.valor).toBe(500);
    });

    it('deve retornar QUITACAO after SINAL payment', () => {
      component.reserva = mockReserva;
      component.pagamentos = mockPagamentos;

      const result = component.calcularProximoPagamento();

      expect(result.tipo).toBe(TipoPagamento.QUITACAO);
      expect(result.valor).toBe(500);
    });
  });

  describe('onSubmitPagamento', () => {
    beforeEach(() => {
      component.reserva = mockReserva;
      component.pagamentos = [];
      component.pagamentoForm.patchValue({
        formaPagamento: 'PIX'
      });
    });

    it('deve criar pagamento successfully', fakeAsync(() => {
      reservaService.getById.mockReturnValue(of(mockReserva));
      pagamentoService.getByReservaId.mockReturnValue(of([]));
      pagamentoService.create.mockReturnValue(of({
        id: 1,
        valor: 500,
        tipo: TipoPagamento.SINAL,
        formaPagamento: 'PIX',
        dataHora: '2024-01-01T10:00:00',
        reservaId: 1
      }));

      component.onSubmitPagamento();
      tick();

      expect(pagamentoService.create).toHaveBeenCalled();
      expect(component.successMessage).toBe('Pagamento registrado com sucesso!');
      expect(component.showPagamentoForm).toBe(false);
    }));

    it('deve tratar erro when creating pagamento', () => {
      pagamentoService.create.mockReturnValue(throwError(() => ({ error: { message: 'Erro ao processar' } })));

      component.onSubmitPagamento();

      expect(component.error).toBe('Erro ao processar');
    });

    it('deve show error if forma pagamento is empty', () => {
      component.pagamentoForm.patchValue({ formaPagamento: '' });

      component.onSubmitPagamento();

      expect(component.error).toBe('Forma de pagamento é obrigatória');
    });
  });

  describe('cancelarReserva', () => {
    beforeEach(() => {
      component.reserva = mockReserva;
      global.confirm = jest.fn(() => true);
    });

    it('deve cancel reserva successfully', fakeAsync(() => {
      reservaService.updateStatus.mockReturnValue(of({ ...mockReserva, status: StatusReserva.CANCELADA }));

      component.cancelarReserva();
      tick();

      expect(reservaService.updateStatus).toHaveBeenCalledWith(1, StatusReserva.CANCELADA);
      expect(component.successMessage).toBe('Reserva cancelada com sucesso!');
    }));

    it('não deve cancel if user cancels confirmation', () => {
      global.confirm = jest.fn(() => false);

      component.cancelarReserva();

      expect(reservaService.updateStatus).not.toHaveBeenCalled();
    });

    it('deve tratar erro when canceling reserva', () => {
      reservaService.updateStatus.mockReturnValue(throwError(() => new Error('Error')));

      component.cancelarReserva();

      expect(component.error).toBe('Erro ao cancelar reserva');
    });
  });

  describe('canCancelar', () => {
    it('deve retornar true for active reservas', () => {
      component.reserva = mockReserva;

      expect(component.canCancelar()).toBe(true);
    });

    it('deve retornar false for cancelada reservas', () => {
      component.reserva = { ...mockReserva, status: StatusReserva.CANCELADA };

      expect(component.canCancelar()).toBe(false);
    });

    it('deve retornar false for finalizada reservas', () => {
      component.reserva = { ...mockReserva, status: StatusReserva.FINALIZADA };

      expect(component.canCancelar()).toBe(false);
    });
  });

  describe('canPagar', () => {
    it('deve retornar true when has saldo', () => {
      component.reserva = mockReserva;

      expect(component.canPagar()).toBe(true);
    });

    it('deve retornar false when saldo is zero', () => {
      component.reserva = { ...mockReserva, saldo: 0 };

      expect(component.canPagar()).toBe(false);
    });

    it('deve retornar false for cancelada reservas', () => {
      component.reserva = { ...mockReserva, status: StatusReserva.CANCELADA };

      expect(component.canPagar()).toBe(false);
    });
  });

  describe('goBack', () => {
    it('deve navegar to reservas list', () => {
      component.goBack();

      expect(router.navigate).toHaveBeenCalledWith(['/reservas']);
    });
  });
});
