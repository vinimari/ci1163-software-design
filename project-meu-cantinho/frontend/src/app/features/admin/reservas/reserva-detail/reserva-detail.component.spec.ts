import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { ReservaDetailComponent } from './reserva-detail.component';
import { ReservaService } from '../../../../core/services/reserva.service';
import { PagamentoService } from '../../../../core/services/pagamento.service';
import { StatusReserva, TipoPagamento } from '../../../../core/models';

describe('ReservaDetailComponent', () => {
  let component: ReservaDetailComponent;
  let fixture: ComponentFixture<ReservaDetailComponent>;
  let reservaService: jest.Mocked<ReservaService>;
  let pagamentoService: jest.Mocked<PagamentoService>;
  let router: jest.Mocked<Router>;

  const mockReserva = {
    id: 1,
    dataEvento: '2025-12-25',
    status: StatusReserva.CONFIRMADA,
    valorTotal: 1000,
    observacoes: '',
    usuarioId: 1,
    espacoId: 1,
    dataCriacao: '2024-01-01',
    usuarioNome: 'Test User',
    espacoNome: 'Sala',
    totalPago: 500,
    saldo: 500
  };

  const mockPagamentos = [
    {
      id: 1,
      valor: 500,
      tipo: TipoPagamento.SINAL,
      formaPagamento: 'PIX',
      dataPagamento: '2024-01-15',
      reservaId: 1
    }
  ];

  beforeEach(async () => {
    const reservaServiceMock = {
      getById: jest.fn(),
      updateStatus: jest.fn()
    };

    const pagamentoServiceMock = {
      getByReservaId: jest.fn(),
      create: jest.fn(),
      delete: jest.fn()
    };

    const routerMock = {
      navigate: jest.fn()
    };

    const activatedRouteMock = {
      snapshot: {
        paramMap: {
          get: jest.fn().mockReturnValue('1')
        }
      }
    };

    await TestBed.configureTestingModule({
      imports: [ReservaDetailComponent],
      providers: [
        { provide: ReservaService, useValue: reservaServiceMock },
        { provide: PagamentoService, useValue: pagamentoServiceMock },
        { provide: Router, useValue: routerMock },
        { provide: ActivatedRoute, useValue: activatedRouteMock }
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
      reservaService.getById.mockReturnValue(of(mockReserva as any));
      pagamentoService.getByReservaId.mockReturnValue(of(mockPagamentos as any));

      component.ngOnInit();

      expect(reservaService.getById).toHaveBeenCalledWith(1);
      expect(pagamentoService.getByReservaId).toHaveBeenCalledWith(1);
    });
  });

  describe('loadReserva', () => {
    it('deve carregar reserva successfully', () => {
      reservaService.getById.mockReturnValue(of(mockReserva as any));

      component.loadReserva(1);

      expect(component.reserva).toEqual(mockReserva);
      expect(component.loading).toBe(false);
    });

    it('deve tratar erro when loading reserva', () => {
      const consoleError = jest.spyOn(console, 'error').mockImplementation();
      reservaService.getById.mockReturnValue(throwError(() => new Error('Error')));

      component.loadReserva(1);

      expect(component.loading).toBe(false);
      consoleError.mockRestore();
    });
  });

  describe('loadPagamentos', () => {
    it('deve carregar and sort pagamentos', () => {
      const unsortedPagamentos = [
        { ...mockPagamentos[0], dataPagamento: '2024-01-10' },
        { ...mockPagamentos[0], id: 2, dataPagamento: '2024-01-20' }
      ];
      pagamentoService.getByReservaId.mockReturnValue(of(unsortedPagamentos as any));

      component.loadPagamentos(1);

      expect(component.pagamentos[0].dataPagamento).toBe('2024-01-20');
      expect(component.loadingPagamentos).toBe(false);
    });

    it('deve tratar erro when loading pagamentos', () => {
      const consoleError = jest.spyOn(console, 'error').mockImplementation();
      pagamentoService.getByReservaId.mockReturnValue(throwError(() => new Error('Error')));

      component.loadPagamentos(1);

      expect(component.loadingPagamentos).toBe(false);
      consoleError.mockRestore();
    });
  });

  describe('goBack', () => {
    it('deve navegar to admin reservas', () => {
      component.goBack();

      expect(router.navigate).toHaveBeenCalledWith(['/admin/reservas']);
    });
  });

  describe('openPagamentoModal', () => {
    it('deve abrir modal with calculated payment', () => {
      component.reserva = mockReserva as any;
      component.pagamentos = [];

      component.openPagamentoModal();

      expect(component.showPagamentoModal).toBe(true);
      expect(component.novoPagamento.valor).toBe(500);
      expect(component.novoPagamento.tipo).toBe(TipoPagamento.SINAL);
    });
  });

  describe('calcularProximoPagamento', () => {
    it('deve retornar SINAL when no pagamentos exist', () => {
      component.reserva = mockReserva as any;
      component.pagamentos = [];

      const result = component.calcularProximoPagamento();

      expect(result.tipo).toBe(TipoPagamento.SINAL);
      expect(result.valor).toBe(500);
    });

    it('deve retornar QUITACAO when SINAL exists', () => {
      component.reserva = mockReserva as any;
      component.pagamentos = mockPagamentos as any;

      const result = component.calcularProximoPagamento();

      expect(result.tipo).toBe(TipoPagamento.QUITACAO);
      expect(result.valor).toBe(500);
    });

    it('deve retornar zero valor when QUITACAO already exists', () => {
      component.reserva = mockReserva as any;
      component.pagamentos = [{ ...mockPagamentos[0], tipo: TipoPagamento.QUITACAO }] as any;

      const result = component.calcularProximoPagamento();

      expect(result.valor).toBe(0);
    });
  });

  describe('permitirTrocarTipoPagamento', () => {
    it('deve retornar true when no pagamentos', () => {
      component.pagamentos = [];

      expect(component.permitirTrocarTipoPagamento()).toBe(true);
    });

    it('deve retornar false when pagamentos exist', () => {
      component.pagamentos = mockPagamentos as any;

      expect(component.permitirTrocarTipoPagamento()).toBe(false);
    });
  });

  describe('alternarTipoPagamento', () => {
    beforeEach(() => {
      component.reserva = mockReserva as any;
      component.pagamentos = [];
    });

    it('deve alternar from SINAL to TOTAL', () => {
      component.novoPagamento.tipo = TipoPagamento.SINAL;
      component.novoPagamento.valor = 500;

      component.alternarTipoPagamento();

      expect(component.novoPagamento.tipo).toBe(TipoPagamento.TOTAL);
      expect(component.novoPagamento.valor).toBe(1000);
    });

    it('deve alternar from TOTAL to SINAL', () => {
      component.novoPagamento.tipo = TipoPagamento.TOTAL;
      component.novoPagamento.valor = 1000;

      component.alternarTipoPagamento();

      expect(component.novoPagamento.tipo).toBe(TipoPagamento.SINAL);
      expect(component.novoPagamento.valor).toBe(500);
    });

    it('não deve switch when pagamentos exist', () => {
      component.pagamentos = mockPagamentos as any;
      component.novoPagamento.tipo = TipoPagamento.SINAL;

      component.alternarTipoPagamento();

      expect(component.novoPagamento.tipo).toBe(TipoPagamento.SINAL);
    });
  });

  describe('closePagamentoModal', () => {
    it('deve fechar modal', () => {
      component.showPagamentoModal = true;

      component.closePagamentoModal();

      expect(component.showPagamentoModal).toBe(false);
    });
  });

  describe('salvarPagamento', () => {
    beforeEach(() => {
      component.reserva = mockReserva as any;
      component.novoPagamento = {
        valor: 500,
        tipo: TipoPagamento.SINAL,
        formaPagamento: 'PIX',
        codigoTransacaoGateway: '',
        reservaId: 1
      };
    });

    it('deve save pagamento successfully', () => {
      pagamentoService.create.mockReturnValue(of({} as any));
      reservaService.getById.mockReturnValue(of(mockReserva as any));
      pagamentoService.getByReservaId.mockReturnValue(of(mockPagamentos as any));

      component.salvarPagamento();

      expect(pagamentoService.create).toHaveBeenCalled();
      expect(component.showPagamentoModal).toBe(false);
    });

    it('deve tratar erro when saving pagamento', () => {
      const consoleError = jest.spyOn(console, 'error').mockImplementation();
      pagamentoService.create.mockReturnValue(throwError(() => ({ error: { message: 'Erro' } })));

      component.salvarPagamento();

      expect(component.loading).toBe(false);
      consoleError.mockRestore();
    });

    it('não deve save if validation fails', () => {
      component.novoPagamento.valor = 0;

      component.salvarPagamento();

      expect(pagamentoService.create).not.toHaveBeenCalled();
    });
  });

  describe('validarPagamento', () => {
    it('deve validar successfully', () => {
      component.novoPagamento = {
        valor: 500,
        tipo: TipoPagamento.SINAL,
        formaPagamento: 'PIX',
        codigoTransacaoGateway: '',
        reservaId: 1
      };

      expect(component.validarPagamento()).toBe(true);
    });

    it('deve fail if valor is zero', () => {
      component.novoPagamento = {
        valor: 0,
        tipo: TipoPagamento.SINAL,
        formaPagamento: 'PIX',
        codigoTransacaoGateway: '',
        reservaId: 1
      };

      expect(component.validarPagamento()).toBe(false);
    });

    it('deve fail if formaPagamento is empty', () => {
      component.novoPagamento = {
        valor: 500,
        tipo: TipoPagamento.SINAL,
        formaPagamento: '',
        codigoTransacaoGateway: '',
        reservaId: 1
      };

      expect(component.validarPagamento()).toBe(false);
    });
  });

  describe('excluirPagamento', () => {
    beforeEach(() => {
      component.reserva = mockReserva as any;
      global.confirm = jest.fn(() => true);
    });

    it('deve delete pagamento when confirmed', () => {
      pagamentoService.delete.mockReturnValue(of({} as any));
      reservaService.getById.mockReturnValue(of(mockReserva as any));
      pagamentoService.getByReservaId.mockReturnValue(of(mockPagamentos as any));

      component.excluirPagamento(1);

      expect(pagamentoService.delete).toHaveBeenCalledWith(1);
    });

    it('deve tratar erro when deleting', () => {
      const consoleError = jest.spyOn(console, 'error').mockImplementation();
      pagamentoService.delete.mockReturnValue(throwError(() => new Error('Error')));

      component.excluirPagamento(1);

      consoleError.mockRestore();
    });

    it('não deve delete if not confirmed', () => {
      global.confirm = jest.fn(() => false);

      component.excluirPagamento(1);

      expect(pagamentoService.delete).not.toHaveBeenCalled();
    });
  });

  describe('updateStatus', () => {
    beforeEach(() => {
      component.reserva = mockReserva as any;
      global.confirm = jest.fn(() => true);
    });

    it('deve atualizar status when confirmed', () => {
      reservaService.updateStatus.mockReturnValue(of({} as any));
      reservaService.getById.mockReturnValue(of(mockReserva as any));

      component.updateStatus(StatusReserva.QUITADA);

      expect(reservaService.updateStatus).toHaveBeenCalledWith(1, StatusReserva.QUITADA);
    });

    it('deve tratar erro when updating status', () => {
      const consoleError = jest.spyOn(console, 'error').mockImplementation();
      reservaService.updateStatus.mockReturnValue(throwError(() => new Error('Error')));

      component.updateStatus(StatusReserva.QUITADA);

      consoleError.mockRestore();
    });

    it('não deve update if not confirmed', () => {
      global.confirm = jest.fn(() => false);

      component.updateStatus(StatusReserva.QUITADA);

      expect(reservaService.updateStatus).not.toHaveBeenCalled();
    });
  });

  describe('helper methods', () => {
    it('deve retornar corretamente status class', () => {
      expect(component.getStatusClass(StatusReserva.CONFIRMADA)).toBe('status-confirmada');
    });

    it('deve retornar corretamente status icon', () => {
      expect(component.getStatusIcon(StatusReserva.CONFIRMADA)).toBe('✅');
    });

    it('deve retornar corretamente tipo pagamento icon', () => {
      expect(component.getTipoPagamentoIcon(TipoPagamento.SINAL)).toBe('💰');
    });

    it('deve calcular total pago', () => {
      component.pagamentos = mockPagamentos as any;

      expect(component.getTotalPago()).toBe(500);
    });

    it('deve calcular saldo pendente', () => {
      component.reserva = mockReserva as any;
      component.pagamentos = mockPagamentos as any;

      expect(component.getSaldoPendente()).toBe(500);
    });

    it('deve check if pagamento is quitado', () => {
      component.reserva = mockReserva as any;
      component.pagamentos = mockPagamentos as any;

      expect(component.isPagamentoQuitado()).toBe(false);
    });

    it('deve retornar corretamente data class for past date', () => {
      expect(component.getDataClass('2020-01-01')).toBe('data-passada');
    });
  });
});
