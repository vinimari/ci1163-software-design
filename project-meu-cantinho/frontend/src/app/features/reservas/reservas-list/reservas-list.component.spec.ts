import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of, throwError } from 'rxjs';
import { ReservasListComponent } from './reservas-list.component';
import { ReservaService } from '../../../core/services/reserva.service';
import { AuthService } from '../../../core/services/auth.service';
import { ReservaResponse, StatusReserva, PerfilUsuario } from '../../../core/models';

describe('ReservasListComponent', () => {
  let component: ReservasListComponent;
  let fixture: ComponentFixture<ReservasListComponent>;
  let reservaService: jest.Mocked<ReservaService>;
  let authService: jest.Mocked<AuthService>;

  const mockUser = {
    id: 1,
    nome: 'Test User',
    email: 'test@example.com',
    perfil: PerfilUsuario.CLIENTE,
    ativo: true,
    dataCadastro: '2024-01-01',
    token: 'test-token'
  };

  const mockReservas: ReservaResponse[] = [
    {
      id: 1,
      dataCriacao: '2024-01-10',
      dataEvento: '2024-01-15',
      valorTotal: 150.00,
      status: StatusReserva.CONFIRMADA,
      usuarioId: 1,
      usuarioNome: 'Test User',
      espacoId: 1,
      espacoNome: 'Sala de Reunião',
      totalPago: 150.00,
      saldo: 0,
      espaco: {
        id: 1,
        nome: 'Sala de Reunião'
      }
    },
    {
      id: 2,
      dataCriacao: '2024-02-05',
      dataEvento: '2024-02-10',
      valorTotal: 300.00,
      status: StatusReserva.AGUARDANDO_SINAL,
      usuarioId: 1,
      usuarioNome: 'Test User',
      espacoId: 2,
      espacoNome: 'Auditório',
      totalPago: 0,
      saldo: 300.00,
      espaco: {
        id: 2,
        nome: 'Auditório'
      }
    }
  ];

  beforeEach(async () => {
    window.alert = jest.fn();

    const reservaServiceMock = {
      getByUsuarioId: jest.fn()
    };

    const authServiceMock = {
      getCurrentUser: jest.fn()
    };

    const activatedRouteMock = {
      snapshot: {
        paramMap: {
          get: jest.fn()
        }
      }
    };

    await TestBed.configureTestingModule({
      imports: [ReservasListComponent],
      providers: [
        { provide: ReservaService, useValue: reservaServiceMock },
        { provide: AuthService, useValue: authServiceMock },
        { provide: ActivatedRoute, useValue: activatedRouteMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ReservasListComponent);
    component = fixture.componentInstance;
    reservaService = TestBed.inject(ReservaService) as jest.Mocked<ReservaService>;
    authService = TestBed.inject(AuthService) as jest.Mocked<AuthService>;
  });

  describe('Component Initialization', () => {
    it('deve criar', () => {
      expect(component).toBeTruthy();
    });

    it('deve inicializar with empty reservas array', () => {
      expect(component.reservas).toEqual([]);
    });

    it('deve inicializar with loading false', () => {
      expect(component.loading).toBe(false);
    });

    it('deve inicializar with empty error', () => {
    });
  });

  describe('ngOnInit', () => {
    it('deve call loadReservas on initialization', () => {
      authService.getCurrentUser.mockReturnValue(mockUser);
      reservaService.getByUsuarioId.mockReturnValue(of(mockReservas));
      const loadSpy = jest.spyOn(component, 'loadReservas');

      fixture.detectChanges();

      expect(loadSpy).toHaveBeenCalled();
    });

    it('deve carregar reservas on initialization when user exists', () => {
      authService.getCurrentUser.mockReturnValue(mockUser);
      reservaService.getByUsuarioId.mockReturnValue(of(mockReservas));

      fixture.detectChanges();

      expect(reservaService.getByUsuarioId).toHaveBeenCalledWith(1);
      expect(component.reservas).toEqual(mockReservas);
    });

    it('não deve load reservas when user is null', () => {
      authService.getCurrentUser.mockReturnValue(null);

      fixture.detectChanges();

      expect(reservaService.getByUsuarioId).not.toHaveBeenCalled();
    });
  });

  describe('loadReservas', () => {
    it('deve retornar early if no user is logged in', () => {
      authService.getCurrentUser.mockReturnValue(null);

      component.loadReservas();

      expect(reservaService.getByUsuarioId).not.toHaveBeenCalled();
      expect(component.loading).toBe(false);
    });

    it('deve clear error when loading', () => {
      authService.getCurrentUser.mockReturnValue(mockUser);
      reservaService.getByUsuarioId.mockReturnValue(of(mockReservas));
      const alertSpy = jest.spyOn(window, 'alert');

      component.loadReservas();

    });

    it('deve carregar reservas successfully', () => {
      authService.getCurrentUser.mockReturnValue(mockUser);
      reservaService.getByUsuarioId.mockReturnValue(of(mockReservas));

      component.loadReservas();

      expect(component.reservas).toEqual(mockReservas);
      expect(component.loading).toBe(false);
    });

    it('deve call reservaService.getByUsuarioId with user id', () => {
      authService.getCurrentUser.mockReturnValue(mockUser);
      reservaService.getByUsuarioId.mockReturnValue(of(mockReservas));

      component.loadReservas();

      expect(reservaService.getByUsuarioId).toHaveBeenCalledWith(1);
    });

    it('deve handle empty reservas list', () => {
      authService.getCurrentUser.mockReturnValue(mockUser);
      reservaService.getByUsuarioId.mockReturnValue(of([]));

      component.loadReservas();

      expect(component.reservas).toEqual([]);
      expect(component.loading).toBe(false);
    });

    it('deve tratar erro when loading fails', () => {
      const consoleError = jest.spyOn(console, 'error').mockImplementation();
      const error = new Error('Network error');
      authService.getCurrentUser.mockReturnValue(mockUser);
      reservaService.getByUsuarioId.mockReturnValue(throwError(() => error));

      component.loadReservas();

      expect(component.loading).toBe(false);
      expect(consoleError).toHaveBeenCalledWith('Error loading reservas:', error);
      consoleError.mockRestore();
    });

    it('deve definir loading to false after error', () => {
      authService.getCurrentUser.mockReturnValue(mockUser);
      reservaService.getByUsuarioId.mockReturnValue(throwError(() => new Error('Error')));

      component.loadReservas();

      expect(component.loading).toBe(false);
    });

    it('deve definir error message on failure', () => {
      authService.getCurrentUser.mockReturnValue(mockUser);
      reservaService.getByUsuarioId.mockReturnValue(throwError(() => new Error('Error')));

      component.loadReservas();

    });
  });

  describe('getStatusLabel', () => {
    it('deve retornar "Aguardando Sinal" for AGUARDANDO_SINAL', () => {
      expect(component.getStatusLabel(StatusReserva.AGUARDANDO_SINAL)).toBe('Aguardando Sinal');
    });

    it('deve retornar "Confirmada" for CONFIRMADA', () => {
      expect(component.getStatusLabel(StatusReserva.CONFIRMADA)).toBe('Confirmada');
    });

    it('deve retornar "Quitada" for QUITADA', () => {
      expect(component.getStatusLabel(StatusReserva.QUITADA)).toBe('Quitada');
    });

    it('deve retornar "Finalizada" for FINALIZADA', () => {
      expect(component.getStatusLabel(StatusReserva.FINALIZADA)).toBe('Finalizada');
    });

    it('deve retornar "Cancelada" for CANCELADA', () => {
      expect(component.getStatusLabel(StatusReserva.CANCELADA)).toBe('Cancelada');
    });

    it('deve retornar status value for unknown status', () => {
      const unknownStatus = 'UNKNOWN' as StatusReserva;
      expect(component.getStatusLabel(unknownStatus)).toBe('UNKNOWN');
    });
  });

  describe('Integration', () => {
    it('deve carregar reservas on initialization with authenticated user', () => {
      authService.getCurrentUser.mockReturnValue(mockUser);
      reservaService.getByUsuarioId.mockReturnValue(of(mockReservas));

      fixture.detectChanges();

      expect(component.reservas).toEqual(mockReservas);
      expect(component.loading).toBe(false);
    });

    it('deve tratar erro on initialization', () => {
      const consoleError = jest.spyOn(console, 'error').mockImplementation();
      authService.getCurrentUser.mockReturnValue(mockUser);
      reservaService.getByUsuarioId.mockReturnValue(throwError(() => new Error('Failed')));

      fixture.detectChanges();

      expect(component.loading).toBe(false);
      consoleError.mockRestore();
    });

    it('não deve load reservas when user is not authenticated', () => {
      authService.getCurrentUser.mockReturnValue(null);

      fixture.detectChanges();

      expect(reservaService.getByUsuarioId).not.toHaveBeenCalled();
      expect(component.reservas).toEqual([]);
    });
  });
});
