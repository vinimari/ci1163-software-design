import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of, throwError } from 'rxjs';
import { ReservaFormComponent } from './reserva-form.component';
import { AuthService } from '../../../core/services/auth.service';
import { ReservaService } from '../../../core/services/reserva.service';
import { ClienteService } from '../../../core/services/cliente.service';
import { EspacoService } from '../../../core/services/espaco.service';
import { PagamentoService } from '../../../core/services/pagamento.service';
import { PerfilUsuario, TipoPagamento } from '../../../core/models';

describe('ReservaFormComponent', () => {
  let component: ReservaFormComponent;
  let fixture: ComponentFixture<ReservaFormComponent>;
  let authService: jest.Mocked<AuthService>;
  let reservaService: jest.Mocked<ReservaService>;
  let clienteService: jest.Mocked<ClienteService>;
  let espacoService: jest.Mocked<EspacoService>;
  let pagamentoService: jest.Mocked<PagamentoService>;
  let router: jest.Mocked<Router>;

  const mockUser = {
    id: 1,
    nome: 'Test User',
    email: 'test@example.com',
    perfil: PerfilUsuario.CLIENTE,
    ativo: true,
    dataCadastro: '2024-01-01',
    token: 'test-token'
  };

  const mockEspacos = [
    {
      id: 1,
      nome: 'Salão de Festas',
      descricao: 'Espaço amplo',
      capacidade: 100,
      precoDiaria: 1000,
      ativo: true,
      filialId: 1,
      filial: { id: 1, nome: 'Filial Centro', endereco: 'Rua A', telefone: '123456789', ativo: true }
    },
    {
      id: 2,
      nome: 'Quadra',
      descricao: 'Espaço esportivo',
      capacidade: 50,
      precoDiaria: 500,
      ativo: true,
      filialId: 1,
      filial: { id: 1, nome: 'Filial Centro', endereco: 'Rua A', telefone: '123456789', ativo: true }
    }
  ];

  const mockClientes = [
    {
      id: 1,
      nome: 'Cliente 1',
      email: 'cliente1@example.com',
      cpf: '12345678900',
      telefone: '999999999',
      ativo: true,
      perfil: PerfilUsuario.CLIENTE,
      dataCadastro: '2024-01-01'
    }
  ];

  beforeEach(async () => {
    const activatedRouteMock = {
      snapshot: {
        paramMap: {
          get: jest.fn()
        },
        queryParamMap: {
          get: jest.fn()
        }
      }
    };

    const authServiceMock = {
      getCurrentUser: jest.fn(),
      isAdmin: jest.fn(),
      isFuncionario: jest.fn(),
      isCliente: jest.fn()
    };

    const reservaServiceMock = {
      create: jest.fn()
    };

    const clienteServiceMock = {
      getAll: jest.fn()
    };

    const espacoServiceMock = {
      getAll: jest.fn()
    };

    const pagamentoServiceMock = {
      create: jest.fn()
    };

    const routerMock = {
      navigate: jest.fn()
    };

    await TestBed.configureTestingModule({
      imports: [ReservaFormComponent, HttpClientTestingModule],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRouteMock },
        { provide: AuthService, useValue: authServiceMock },
        { provide: ReservaService, useValue: reservaServiceMock },
        { provide: ClienteService, useValue: clienteServiceMock },
        { provide: EspacoService, useValue: espacoServiceMock },
        { provide: PagamentoService, useValue: pagamentoServiceMock },
        { provide: Router, useValue: routerMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ReservaFormComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService) as jest.Mocked<AuthService>;
    reservaService = TestBed.inject(ReservaService) as jest.Mocked<ReservaService>;
    clienteService = TestBed.inject(ClienteService) as jest.Mocked<ClienteService>;
    espacoService = TestBed.inject(EspacoService) as jest.Mocked<EspacoService>;
    pagamentoService = TestBed.inject(PagamentoService) as jest.Mocked<PagamentoService>;
    router = TestBed.inject(Router) as jest.Mocked<Router>;

    authService.getCurrentUser.mockReturnValue(mockUser);
    authService.isCliente.mockReturnValue(true);
    authService.isAdmin.mockReturnValue(false);
    authService.isFuncionario.mockReturnValue(false);
    espacoService.getAll.mockReturnValue(of(mockEspacos));
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('ngOnInit', () => {
    it('should initialize with user permissions', () => {
      component.ngOnInit();

      expect(component.isCliente).toBe(true);
      expect(component.reserva.usuarioId).toBe(1);
    });

    it('should load espacos on init', () => {
      component.ngOnInit();

      expect(espacoService.getAll).toHaveBeenCalled();
    });

    it('should set espacoId from query params', () => {
      const route = TestBed.inject(ActivatedRoute);
      (route.snapshot.queryParamMap.get as jest.Mock).mockReturnValue('2');

      component.ngOnInit();

      expect(component.reserva.espacoId).toBe(2);
    });

    it('should load clientes for admin users', () => {
      authService.isAdmin.mockReturnValue(true);
      authService.isCliente.mockReturnValue(false);
      clienteService.getAll.mockReturnValue(of(mockClientes));

      component.ngOnInit();

      expect(clienteService.getAll).toHaveBeenCalled();
    });
  });

  describe('checkUserPermissions', () => {
    it('should redirect to login if no user', () => {
      authService.getCurrentUser.mockReturnValue(null);

      component.checkUserPermissions();

      expect(router.navigate).toHaveBeenCalledWith(['/login']);
    });

    it('should set cliente usuario id automatically', () => {
      component.checkUserPermissions();

      expect(component.reserva.usuarioId).toBe(1);
    });
  });

  describe('loadInitialData', () => {
    it('should load espacos successfully', () => {
      component.loadInitialData();

      expect(component.loading).toBe(false);
      expect(component.espacos.length).toBe(2);
    });

    it('should handle error when loading espacos', () => {
      espacoService.getAll.mockReturnValue(throwError(() => new Error('Error')));

      component.loadInitialData();

      expect(component.error).toBe('Erro ao carregar espaços');
    });

    it('should set valorTotal if espacoId is preset', () => {
      component.reserva.espacoId = 1;

      component.loadInitialData();

      expect(component.reserva.valorTotal).toBe(1000);
    });
  });

  describe('onEspacoChange', () => {
    beforeEach(() => {
      component.espacos = mockEspacos;
    });

    it('should update valorTotal when espaco changes', () => {
      component.reserva.espacoId = 1;

      component.onEspacoChange();

      expect(component.reserva.valorTotal).toBe(1000);
    });

    it('should handle string espacoId', () => {
      component.reserva.espacoId = '2' as any;

      component.onEspacoChange();

      expect(component.reserva.espacoId).toBe(2);
      expect(component.reserva.valorTotal).toBe(500);
    });
  });

  describe('validateForm', () => {
    beforeEach(() => {
      const tomorrow = new Date();
      tomorrow.setDate(tomorrow.getDate() + 1);
      component.reserva = {
        dataEvento: tomorrow.toISOString().split('T')[0],
        valorTotal: 1000,
        observacoes: '',
        usuarioId: 1,
        espacoId: 1
      };
      component.formaPagamento = 'PIX';
    });

    it('should validate successfully with valid data', () => {
      expect(component.validateForm()).toBe(true);
    });

    it('should fail if usuarioId is missing', () => {
      component.reserva.usuarioId = 0;

      expect(component.validateForm()).toBe(false);
      expect(component.error).toBe('Selecione um cliente');
    });

    it('should fail if espacoId is missing', () => {
      component.reserva.espacoId = 0;

      expect(component.validateForm()).toBe(false);
      expect(component.error).toBe('Selecione um espaço');
    });

    it('should fail if dataEvento is missing', () => {
      component.reserva.dataEvento = '';

      expect(component.validateForm()).toBe(false);
      expect(component.error).toBe('Selecione a data do evento');
    });

    it('should fail if dataEvento is in the past', () => {
      const yesterday = new Date();
      yesterday.setDate(yesterday.getDate() - 1);
      component.reserva.dataEvento = yesterday.toISOString().split('T')[0];

      expect(component.validateForm()).toBe(false);
      expect(component.error).toBe('A data do evento não pode ser no passado');
    });

    it('should fail if valorTotal is zero', () => {
      component.reserva.valorTotal = 0;

      expect(component.validateForm()).toBe(false);
      expect(component.error).toBe('Valor total deve ser maior que zero');
    });

    it('should fail if formaPagamento is missing', () => {
      component.formaPagamento = '';

      expect(component.validateForm()).toBe(false);
      expect(component.error).toBe('Selecione a forma de pagamento');
    });
  });

  describe('onSubmit', () => {
    beforeEach(() => {
      const tomorrow = new Date();
      tomorrow.setDate(tomorrow.getDate() + 1);
      component.reserva = {
        dataEvento: tomorrow.toISOString().split('T')[0],
        valorTotal: 1000,
        observacoes: '',
        usuarioId: 1,
        espacoId: 1
      };
      component.formaPagamento = 'PIX';
      component.tipoPagamento = TipoPagamento.SINAL;
      component.isCliente = true;
    });

    it('should create reserva and pagamento successfully for cliente', () => {
      const mockReserva = { ...component.reserva, id: 1, status: 'AGUARDANDO_SINAL' };
      reservaService.create.mockReturnValue(of(mockReserva as any));
      pagamentoService.create.mockReturnValue(of({} as any));

      component.onSubmit();

      expect(reservaService.create).toHaveBeenCalled();
      expect(pagamentoService.create).toHaveBeenCalled();
      expect(router.navigate).toHaveBeenCalledWith(['/reservas']);
    });

    it('should navigate to admin/reservas for admin users', () => {
      component.isCliente = false;
      const mockReserva = { ...component.reserva, id: 1, status: 'AGUARDANDO_SINAL' };
      reservaService.create.mockReturnValue(of(mockReserva as any));
      pagamentoService.create.mockReturnValue(of({} as any));

      component.onSubmit();

      expect(router.navigate).toHaveBeenCalledWith(['/admin/reservas']);
    });

    it('should calculate 50% for SINAL payment', () => {
      const mockReserva = { ...component.reserva, id: 1, status: 'AGUARDANDO_SINAL' };
      reservaService.create.mockReturnValue(of(mockReserva as any));
      pagamentoService.create.mockReturnValue(of({} as any));

      component.onSubmit();

      expect(pagamentoService.create).toHaveBeenCalledWith(expect.objectContaining({
        valor: 500,
        tipo: TipoPagamento.SINAL
      }));
    });

    it('should calculate 100% for TOTAL payment', () => {
      component.tipoPagamento = TipoPagamento.TOTAL;
      const mockReserva = { ...component.reserva, id: 1, status: 'AGUARDANDO_SINAL' };
      reservaService.create.mockReturnValue(of(mockReserva as any));
      pagamentoService.create.mockReturnValue(of({} as any));

      component.onSubmit();

      expect(pagamentoService.create).toHaveBeenCalledWith(expect.objectContaining({
        valor: 1000,
        tipo: TipoPagamento.TOTAL
      }));
    });

    it('should handle error when creating reserva', () => {
      reservaService.create.mockReturnValue(throwError(() => ({ error: { message: 'Erro ao criar' } })));

      component.onSubmit();

      expect(component.error).toBe('Erro ao criar');
      expect(component.loading).toBe(false);
    });

    it('should handle error when creating pagamento', () => {
      const mockReserva = { ...component.reserva, id: 1, status: 'AGUARDANDO_SINAL' };
      reservaService.create.mockReturnValue(of(mockReserva as any));
      pagamentoService.create.mockReturnValue(throwError(() => ({ error: { message: 'Erro pagamento' } })));

      component.onSubmit();

      expect(component.error).toContain('Reserva criada mas erro ao registrar pagamento:');
    });

    it('should not submit if validation fails', () => {
      component.reserva.usuarioId = 0;

      component.onSubmit();

      expect(reservaService.create).not.toHaveBeenCalled();
    });
  });

  describe('getValorPagamento', () => {
    beforeEach(() => {
      component.reserva.valorTotal = 1000;
    });

    it('should return 50% for SINAL', () => {
      component.tipoPagamento = TipoPagamento.SINAL;

      expect(component.getValorPagamento()).toBe(500);
    });

    it('should return 100% for TOTAL', () => {
      component.tipoPagamento = TipoPagamento.TOTAL;

      expect(component.getValorPagamento()).toBe(1000);
    });
  });

  describe('cancel', () => {
    it('should navigate to espacos for cliente', () => {
      component.isCliente = true;

      component.cancel();

      expect(router.navigate).toHaveBeenCalledWith(['/espacos']);
    });

    it('should navigate to admin/reservas for admin', () => {
      component.isCliente = false;

      component.cancel();

      expect(router.navigate).toHaveBeenCalledWith(['/admin/reservas']);
    });
  });

  describe('getClienteNome', () => {
    beforeEach(() => {
      component.clientes = mockClientes;
    });

    it('should return cliente nome', () => {
      expect(component.getClienteNome(1)).toBe('Cliente 1');
    });

    it('should return empty string for non-existent cliente', () => {
      expect(component.getClienteNome(999)).toBe('');
    });
  });

  describe('getEspacoInfo', () => {
    beforeEach(() => {
      component.espacos = mockEspacos;
    });

    it('should return espaco info', () => {
      const info = component.getEspacoInfo(1);

      expect(info).toEqual({
        nome: 'Salão de Festas',
        filial: 'Filial Centro',
        capacidade: 100,
        preco: 1000
      });
    });

    it('should return null for non-existent espaco', () => {
      expect(component.getEspacoInfo(999)).toBeNull();
    });
  });

  describe('getMinDate', () => {
    it('should return today date in YYYY-MM-DD format', () => {
      const minDate = component.getMinDate();
      const today = new Date().toISOString().split('T')[0];

      expect(minDate).toBe(today);
    });
  });
});
