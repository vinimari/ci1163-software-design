import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { ReservasAdminComponent } from './reservas-admin.component';
import { ReservaService } from '../../../../core/services/reserva.service';
import { AuthService } from '../../../../core/services/auth.service';
import { StatusReserva, PerfilUsuario } from '../../../../core/models';

describe('ReservasAdminComponent', () => {
  let component: ReservasAdminComponent;
  let fixture: ComponentFixture<ReservasAdminComponent>;
  let reservaService: jest.Mocked<ReservaService>;
  let authService: jest.Mocked<AuthService>;
  let router: jest.Mocked<Router>;

  const mockUser = {
    id: 1,
    nome: 'Admin User',
    email: 'admin@example.com',
    perfil: PerfilUsuario.ADMIN,
    ativo: true,
    dataCadastro: '2024-01-01',
    token: 'test-token'
  };

  const tomorrow = new Date();
  tomorrow.setDate(tomorrow.getDate() + 1);

  const yesterday = new Date();
  yesterday.setDate(yesterday.getDate() - 1);

  const mockReservas = [
    {
      id: 1,
      dataEvento: tomorrow.toISOString().split('T')[0],
      status: StatusReserva.CONFIRMADA,
      valorTotal: 1000,
      observacoes: '',
      usuarioId: 1,
      espacoId: 1,
      dataCriacao: '2024-01-01',
      usuarioNome: 'Cliente A',
      espacoNome: 'Sala Principal',
      totalPago: 500,
      saldo: 500,
      usuario: { id: 1, nome: 'Cliente A', email: 'clientea@example.com', perfil: PerfilUsuario.CLIENTE, ativo: true, dataCadastro: '2024-01-01' },
      espaco: { id: 1, nome: 'Sala Principal', descricao: '', capacidade: 50, precoDiaria: 1000, ativo: true, filialId: 1 }
    },
    {
      id: 2,
      dataEvento: yesterday.toISOString().split('T')[0],
      status: StatusReserva.QUITADA,
      valorTotal: 500,
      observacoes: '',
      usuarioId: 2,
      espacoId: 2,
      dataCriacao: '2024-01-01',
      usuarioNome: 'Cliente B',
      espacoNome: 'Sala Secundária',
      totalPago: 500,
      saldo: 0,
      usuario: { id: 2, nome: 'Cliente B', email: 'clienteb@example.com', perfil: PerfilUsuario.CLIENTE, ativo: true, dataCadastro: '2024-01-01' },
      espaco: { id: 2, nome: 'Sala Secundária', descricao: '', capacidade: 30, precoDiaria: 500, ativo: true, filialId: 1 }
    }
  ];

  beforeEach(async () => {
    const reservaServiceMock = {
      getByAcesso: jest.fn(),
      updateStatus: jest.fn()
    };

    const authServiceMock = {
      getCurrentUser: jest.fn()
    };

    const routerMock = {
      navigate: jest.fn()
    };

    await TestBed.configureTestingModule({
      imports: [ReservasAdminComponent],
      providers: [
        { provide: ReservaService, useValue: reservaServiceMock },
        { provide: AuthService, useValue: authServiceMock },
        { provide: Router, useValue: routerMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ReservasAdminComponent);
    component = fixture.componentInstance;
    reservaService = TestBed.inject(ReservaService) as jest.Mocked<ReservaService>;
    authService = TestBed.inject(AuthService) as jest.Mocked<AuthService>;
    router = TestBed.inject(Router) as jest.Mocked<Router>;

    authService.getCurrentUser.mockReturnValue(mockUser);
    reservaService.getByAcesso.mockReturnValue(of(mockReservas as any));
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('ngOnInit', () => {
    it('should load reservas on init', () => {
      component.ngOnInit();

      expect(reservaService.getByAcesso).toHaveBeenCalledWith('admin@example.com');
    });
  });

  describe('loadReservas', () => {
    it('should load and filter reservas successfully', () => {
      component.loadReservas();

      expect(component.reservas.length).toBe(2);
      expect(component.loading).toBe(false);
    });

    it('should handle error when no user', () => {
      authService.getCurrentUser.mockReturnValue(null);

      component.loadReservas();

      expect(component.error).toBe('Usuário não autenticado');
    });

    it('should handle error when loading reservas', () => {
      const consoleError = jest.spyOn(console, 'error').mockImplementation();
      reservaService.getByAcesso.mockReturnValue(throwError(() => new Error('Error')));

      component.loadReservas();

      expect(component.error).toBe('Erro ao carregar reservas');
      consoleError.mockRestore();
    });
  });

  describe('applyFilters', () => {
    beforeEach(() => {
      component.reservas = mockReservas as any;
    });

    it('should filter by search term', () => {
      component.searchTerm = 'Cliente A';
      component.applyFilters();

      expect(component.reservasFiltradas.length).toBe(1);
      expect(component.reservasFiltradas[0].usuarioNome).toBe('Cliente A');
    });

    it('should filter by espaco name', () => {
      component.dataFilter = 'TODAS';
      component.searchTerm = 'Secundária';
      component.applyFilters();

      expect(component.reservasFiltradas.length).toBe(1);
      expect(component.reservasFiltradas[0].espacoNome).toBe('Sala Secundária');
    });

    it('should filter by status', () => {
      component.dataFilter = 'TODAS';
      component.statusFilter = StatusReserva.QUITADA;
      component.applyFilters();

      expect(component.reservasFiltradas.length).toBe(1);
      expect(component.reservasFiltradas[0].status).toBe(StatusReserva.QUITADA);
    });

    it('should filter future dates', () => {
      component.dataFilter = 'FUTURAS';
      component.applyFilters();

      expect(component.reservasFiltradas.length).toBe(1);
      expect(component.reservasFiltradas[0].id).toBe(1);
    });

    it('should filter past dates', () => {
      component.dataFilter = 'PASSADAS';
      component.applyFilters();

      expect(component.reservasFiltradas.length).toBe(1);
      expect(component.reservasFiltradas[0].id).toBe(2);
    });

    it('should filter quitadas', () => {
      component.dataFilter = 'TODAS';
      component.pagamentoFilter = 'QUITADA';
      component.applyFilters();

      expect(component.reservasFiltradas.length).toBe(1);
      expect(component.reservasFiltradas[0].saldo).toBe(0);
    });

    it('should filter pendentes', () => {
      component.pagamentoFilter = 'PENDENTE';
      component.applyFilters();

      expect(component.reservasFiltradas.length).toBe(1);
      expect(component.reservasFiltradas[0].saldo).toBeGreaterThan(0);
    });
  });

  describe('filter change methods', () => {
    it('should update search term', () => {
      component.onSearchChange('test');

      expect(component.searchTerm).toBe('test');
    });

    it('should update status filter', () => {
      component.onStatusFilterChange(StatusReserva.CONFIRMADA);

      expect(component.statusFilter).toBe(StatusReserva.CONFIRMADA);
    });

    it('should update data filter', () => {
      component.onDataFilterChange('PASSADAS');

      expect(component.dataFilter).toBe('PASSADAS');
    });

    it('should update pagamento filter', () => {
      component.onPagamentoFilterChange('QUITADA');

      expect(component.pagamentoFilter).toBe('QUITADA');
    });
  });

  describe('viewReserva', () => {
    it('should navigate to reserva detail', () => {
      component.viewReserva(1);

      expect(router.navigate).toHaveBeenCalledWith(['/admin/reservas', 1]);
    });
  });

  describe('updateStatus', () => {
    beforeEach(() => {
      global.confirm = jest.fn(() => true);
    });

    it('should update status when confirmed', () => {
      reservaService.updateStatus.mockReturnValue(of({} as any));
      const reserva = mockReservas[0] as any;

      component.updateStatus(reserva, StatusReserva.FINALIZADA);

      expect(reservaService.updateStatus).toHaveBeenCalledWith(1, StatusReserva.FINALIZADA);
    });

    it('should handle error when updating status', () => {
      const consoleError = jest.spyOn(console, 'error').mockImplementation();
      reservaService.updateStatus.mockReturnValue(throwError(() => new Error('Error')));
      const reserva = mockReservas[0] as any;

      component.updateStatus(reserva, StatusReserva.FINALIZADA);

      expect(component.error).toBe('Erro ao atualizar status da reserva');
      consoleError.mockRestore();
    });

    it('should not update if not confirmed', () => {
      global.confirm = jest.fn(() => false);
      const reserva = mockReservas[0] as any;

      component.updateStatus(reserva, StatusReserva.FINALIZADA);

      expect(reservaService.updateStatus).not.toHaveBeenCalled();
    });
  });

  describe('helper methods', () => {
    it('should return correct status class', () => {
      expect(component.getStatusClass(StatusReserva.CONFIRMADA)).toBe('status-confirmada');
    });

    it('should return correct status icon', () => {
      expect(component.getStatusIcon(StatusReserva.QUITADA)).toBe('💰');
    });

    it('should return quitada pagamento status', () => {
      const reserva = { ...mockReservas[1] } as any;
      expect(component.getPagamentoStatus(reserva)).toBe('✅ Quitada');
    });

    it('should return sinal pago status', () => {
      const reserva = { ...mockReservas[0] } as any;
      expect(component.getPagamentoStatus(reserva)).toBe('⚠️ Sinal Pago');
    });

    it('should return pendente status', () => {
      const reserva = { ...mockReservas[0], totalPago: 0 } as any;
      expect(component.getPagamentoStatus(reserva)).toBe('❌ Pendente');
    });

    it('should return correct pagamento class', () => {
      expect(component.getPagamentoClass(mockReservas[1] as any)).toBe('pagamento-quitada');
      expect(component.getPagamentoClass(mockReservas[0] as any)).toBe('pagamento-parcial');
    });

    it('should return correct data class', () => {
      expect(component.getDataClass(yesterday.toISOString().split('T')[0])).toBe('data-passada');
      expect(component.getDataClass(tomorrow.toISOString().split('T')[0])).toBe('data-futura');
    });
  });

  describe('totals methods', () => {
    beforeEach(() => {
      component.reservasFiltradas = mockReservas as any;
    });

    it('should calculate total reservas', () => {
      expect(component.getTotalReservas()).toBe(2);
    });

    it('should calculate valor total reservas', () => {
      expect(component.getValorTotalReservas()).toBe(1500);
    });

    it('should calculate valor total pago', () => {
      expect(component.getValorTotalPago()).toBe(1000);
    });

    it('should calculate saldo pendente', () => {
      expect(component.getSaldoPendente()).toBe(500);
    });
  });
});

