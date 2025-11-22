import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router, ActivatedRoute } from '@angular/router';
import { of, throwError } from 'rxjs';
import { ClienteDetailComponent } from './cliente-detail.component';
import { ClienteService, ReservaService } from '../../../../core/services';
import { ClienteResponse, ReservaResponse, PerfilUsuario, StatusReserva } from '../../../../core/models';

describe('ClienteDetailComponent', () => {
  let component: ClienteDetailComponent;
  let fixture: ComponentFixture<ClienteDetailComponent>;
  let clienteService: jest.Mocked<ClienteService>;
  let reservaService: jest.Mocked<ReservaService>;
  let router: jest.Mocked<Router>;
  let activatedRoute: any;

  const mockCliente: ClienteResponse = {
    id: 1,
    nome: 'João Silva',
    email: 'joao@email.com',
    cpf: '12345678900',
    telefone: '41999999999',
    ativo: true,
    perfil: PerfilUsuario.CLIENTE,
    dataCadastro: '2024-01-01'
  };

  const mockReservas: ReservaResponse[] = [
    {
      id: 1,
      usuarioId: 1,
      espacoId: 1,
      espacoNome: 'Salão de Festas',
      dataEvento: '2024-12-25',
      dataCriacao: '2024-11-01',
      status: StatusReserva.CONFIRMADA,
      valorTotal: 1500.00,
      totalPago: 1500.00,
      saldo: 0
    },
    {
      id: 2,
      usuarioId: 1,
      espacoId: 2,
      espacoNome: 'Quadra Poliesportiva',
      dataEvento: '2024-12-31',
      dataCriacao: '2024-11-15',
      status: StatusReserva.AGUARDANDO_SINAL,
      valorTotal: 800.00,
      totalPago: 400.00,
      saldo: 400.00
    }
  ];

  beforeEach(async () => {
    const clienteServiceMock = {
      getById: jest.fn().mockReturnValue(of(mockCliente)),
      getAll: jest.fn(),
      create: jest.fn(),
      update: jest.fn(),
      delete: jest.fn(),
      toggleAtivo: jest.fn()
    };

    const reservaServiceMock = {
      getByUsuarioId: jest.fn().mockReturnValue(of(mockReservas)),
      getAll: jest.fn(),
      getById: jest.fn(),
      create: jest.fn(),
      update: jest.fn(),
      delete: jest.fn(),
      updateStatus: jest.fn()
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
      imports: [ClienteDetailComponent],
      providers: [
        { provide: ClienteService, useValue: clienteServiceMock },
        { provide: ReservaService, useValue: reservaServiceMock },
        { provide: Router, useValue: routerMock },
        { provide: ActivatedRoute, useValue: activatedRouteMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ClienteDetailComponent);
    component = fixture.componentInstance;
    clienteService = TestBed.inject(ClienteService) as jest.Mocked<ClienteService>;
    reservaService = TestBed.inject(ReservaService) as jest.Mocked<ReservaService>;
    router = TestBed.inject(Router) as jest.Mocked<Router>;
    activatedRoute = TestBed.inject(ActivatedRoute);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load cliente on init', () => {
    fixture.detectChanges();
    expect(clienteService.getById).toHaveBeenCalledWith(1);
    expect(component.cliente).toEqual(mockCliente);
    expect(component.loading).toBe(false);
  });

  it('should load reservas on init', () => {
    fixture.detectChanges();
    expect(reservaService.getByUsuarioId).toHaveBeenCalledWith(1);
    expect(component.reservas).toEqual(mockReservas);
    expect(component.loadingReservas).toBe(false);
  });

  it('should handle error when loading cliente fails', () => {
    const consoleErrorSpy = jest.spyOn(console, 'error').mockImplementation();
    clienteService.getById.mockReturnValue(throwError(() => new Error('Error')));

    component.loadCliente(1);

    expect(component.error).toBe('Erro ao carregar cliente');
    expect(component.loading).toBe(false);
    consoleErrorSpy.mockRestore();
  });

  it('should handle error when loading reservas fails', () => {
    const consoleErrorSpy = jest.spyOn(console, 'error').mockImplementation();
    reservaService.getByUsuarioId.mockReturnValue(throwError(() => new Error('Error')));

    component.loadReservas(1);

    expect(component.loadingReservas).toBe(false);
    consoleErrorSpy.mockRestore();
  });

  it('should navigate back when goBack is called', () => {
    component.goBack();
    expect(router.navigate).toHaveBeenCalledWith(['/admin/clientes']);
  });

  it('should navigate to reserva detail when viewReserva is called', () => {
    component.viewReserva(1);
    expect(router.navigate).toHaveBeenCalledWith(['/admin/reservas', 1]);
  });

  it('should return correct status class for AGUARDANDO_SINAL', () => {
    expect(component.getStatusClass('AGUARDANDO_SINAL')).toBe('status-pendente');
  });

  it('should return correct status class for CONFIRMADA', () => {
    expect(component.getStatusClass('CONFIRMADA')).toBe('status-confirmada');
  });

  it('should return correct status class for CANCELADA', () => {
    expect(component.getStatusClass('CANCELADA')).toBe('status-cancelada');
  });

  it('should return correct status class for CONCLUIDA', () => {
    expect(component.getStatusClass('CONCLUIDA')).toBe('status-concluida');
  });

  it('should return empty string for unknown status', () => {
    expect(component.getStatusClass('UNKNOWN')).toBe('');
  });

  it('should return correct status icon for AGUARDANDO_SINAL', () => {
    expect(component.getStatusIcon('AGUARDANDO_SINAL')).toBe('⏳');
  });

  it('should return correct status icon for CONFIRMADA', () => {
    expect(component.getStatusIcon('CONFIRMADA')).toBe('✅');
  });

  it('should return correct status icon for CANCELADA', () => {
    expect(component.getStatusIcon('CANCELADA')).toBe('❌');
  });

  it('should return correct status icon for CONCLUIDA', () => {
    expect(component.getStatusIcon('CONCLUIDA')).toBe('🎉');
  });

  it('should return default icon for unknown status', () => {
    expect(component.getStatusIcon('UNKNOWN')).toBe('📋');
  });

  it('should return total number of reservas', () => {
    component.reservas = mockReservas;
    expect(component.getTotalReservas()).toBe(2);
  });

  it('should return zero when no reservas', () => {
    component.reservas = [];
    expect(component.getTotalReservas()).toBe(0);
  });

  it('should calculate total value of all reservas', () => {
    component.reservas = mockReservas;
    expect(component.getValorTotalReservas()).toBe(2300.00);
  });

  it('should return zero when calculating total with no reservas', () => {
    component.reservas = [];
    expect(component.getValorTotalReservas()).toBe(0);
  });

  it('should not load cliente or reservas when id is null', () => {
    activatedRoute.snapshot.paramMap.get = jest.fn().mockReturnValue(null);
    
    component.ngOnInit();

    expect(clienteService.getById).not.toHaveBeenCalled();
    expect(reservaService.getByUsuarioId).not.toHaveBeenCalled();
  });

  it('should set loading to true when loading cliente', () => {
    let loadingDuringCall = false;
    
    clienteService.getById.mockImplementation(() => {
      loadingDuringCall = component.loading;
      return of(mockCliente);
    });
    
    component.loadCliente(1);
    expect(loadingDuringCall).toBe(true);
  });

  it('should set loadingReservas to true when loading reservas', () => {
    let loadingDuringCall = false;
    
    reservaService.getByUsuarioId.mockImplementation(() => {
      loadingDuringCall = component.loadingReservas;
      return of(mockReservas);
    });
    
    component.loadReservas(1);
    expect(loadingDuringCall).toBe(true);
  });

  it('should handle cliente without CPF', () => {
    const clienteSemCpf = { ...mockCliente, cpf: undefined };
    clienteService.getById.mockReturnValue(of(clienteSemCpf));
    
    component.loadCliente(1);

    expect(component.cliente?.cpf).toBeUndefined();
  });

  it('should handle cliente without telefone', () => {
    const clienteSemTelefone = { ...mockCliente, telefone: undefined };
    clienteService.getById.mockReturnValue(of(clienteSemTelefone));
    
    component.loadCliente(1);

    expect(component.cliente?.telefone).toBeUndefined();
  });

  it('should handle reservas without saldo defined', () => {
    const reservaSemSaldo: ReservaResponse = {
      id: 3,
      usuarioId: 1,
      espacoId: 3,
      espacoNome: 'Churrasqueira',
      dataEvento: '2025-01-10',
      dataCriacao: '2024-11-20',
      status: StatusReserva.CONFIRMADA,
      valorTotal: 500.00,
      totalPago: undefined,
      saldo: undefined
    };

    component.reservas = [reservaSemSaldo];
    
    expect(component.getValorTotalReservas()).toBe(500.00);
  });
});
