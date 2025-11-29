import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { ClientesListComponent } from './clientes-list.component';
import { ClienteService } from '../../../../core/services';
import { ClienteResponse, PerfilUsuario } from '../../../../core/models';

describe('ClientesListComponent', () => {
  let component: ClientesListComponent;
  let fixture: ComponentFixture<ClientesListComponent>;
  let clienteService: jest.Mocked<ClienteService>;
  let router: jest.Mocked<Router>;

  const mockClientes: ClienteResponse[] = [
    {
      id: 1,
      nome: 'João Silva',
      email: 'joao@email.com',
      cpf: '12345678900',
      telefone: '41999999999',
      ativo: true,
      perfil: PerfilUsuario.CLIENTE,
      dataCadastro: '2024-01-01'
    },
    {
      id: 2,
      nome: 'Maria Santos',
      email: 'maria@email.com',
      cpf: '98765432100',
      telefone: '41988888888',
      ativo: false,
      perfil: PerfilUsuario.CLIENTE,
      dataCadastro: '2024-01-02'
    }
  ];

  beforeEach(async () => {
    const clienteServiceMock = {
      getAll: jest.fn().mockReturnValue(of(mockClientes)),
      toggleAtivo: jest.fn().mockReturnValue(of(mockClientes[0])),
      getById: jest.fn(),
      create: jest.fn(),
      update: jest.fn(),
      delete: jest.fn()
    };

    const routerMock = {
      navigate: jest.fn()
    };

    await TestBed.configureTestingModule({
      imports: [ClientesListComponent],
      providers: [
        { provide: ClienteService, useValue: clienteServiceMock },
        { provide: Router, useValue: routerMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ClientesListComponent);
    component = fixture.componentInstance;
    clienteService = TestBed.inject(ClienteService) as jest.Mocked<ClienteService>;
    router = TestBed.inject(Router) as jest.Mocked<Router>;
  });

  it('deve criar', () => {
    expect(component).toBeTruthy();
  });

  it('deve carregar clientes on init', () => {
    fixture.detectChanges();
    expect(clienteService.getAll).toHaveBeenCalled();
    expect(component.clientes).toEqual(mockClientes);
    expect(component.clientesFiltrados).toEqual(mockClientes);
    expect(component.loading).toBe(false);
  });

  it('deve tratar erro when loading clientes fails', () => {
    const consoleErrorSpy = jest.spyOn(console, 'error').mockImplementation();
    clienteService.getAll.mockReturnValue(throwError(() => new Error('Error')));

    component.loadClientes();

    expect(component.error).toBe('Erro ao carregar clientes');
    expect(component.loading).toBe(false);
    consoleErrorSpy.mockRestore();
  });

  it('deve navegar to cliente detail when viewCliente is called', () => {
    component.viewCliente(1);
    expect(router.navigate).toHaveBeenCalledWith(['/admin/clientes', 1]);
  });

  it('deve filter clientes by name', () => {
    component.clientes = mockClientes;
    component.onSearchChange('joão');
    component.filterClientes();

    expect(component.clientesFiltrados).toHaveLength(1);
    expect(component.clientesFiltrados[0].nome).toBe('João Silva');
  });

  it('deve filter clientes by email', () => {
    component.clientes = mockClientes;
    component.onSearchChange('maria@email.com');
    component.filterClientes();

    expect(component.clientesFiltrados).toHaveLength(1);
    expect(component.clientesFiltrados[0].email).toBe('maria@email.com');
  });

  it('deve filter clientes by CPF', () => {
    component.clientes = mockClientes;
    component.onSearchChange('123456');
    component.filterClientes();

    expect(component.clientesFiltrados).toHaveLength(1);
    expect(component.clientesFiltrados[0].cpf).toBe('12345678900');
  });

  it('deve retornar all clientes when search term is empty', () => {
    component.clientes = mockClientes;
    component.onSearchChange('');
    component.filterClientes();

    expect(component.clientesFiltrados).toEqual(mockClientes);
  });

  it('deve retornar empty array when no cliente matches search', () => {
    component.clientes = mockClientes;
    component.onSearchChange('inexistente');
    component.filterClientes();

    expect(component.clientesFiltrados).toHaveLength(0);
  });

  it('deve toggle cliente to inactive when confirmed', () => {
    const confirmSpy = jest.spyOn(window, 'confirm').mockReturnValue(true);
    const cliente = { ...mockClientes[0], ativo: true };

    component.toggleAtivo(cliente);

    expect(confirmSpy).toHaveBeenCalledWith('Tem certeza que deseja desativar este cliente?');
    expect(clienteService.toggleAtivo).toHaveBeenCalledWith(1, false);
    expect(clienteService.getAll).toHaveBeenCalled();
    confirmSpy.mockRestore();
  });

  it('deve toggle cliente to active when confirmed', () => {
    const confirmSpy = jest.spyOn(window, 'confirm').mockReturnValue(true);
    const cliente = { ...mockClientes[1], ativo: false };

    component.toggleAtivo(cliente);

    expect(confirmSpy).toHaveBeenCalledWith('Tem certeza que deseja ativar este cliente?');
    expect(clienteService.toggleAtivo).toHaveBeenCalledWith(2, true);
    expect(clienteService.getAll).toHaveBeenCalled();
    confirmSpy.mockRestore();
  });

  it('não deve toggle cliente when not confirmed', () => {
    const confirmSpy = jest.spyOn(window, 'confirm').mockReturnValue(false);
    const cliente = mockClientes[0];

    component.toggleAtivo(cliente);

    expect(clienteService.toggleAtivo).not.toHaveBeenCalled();
    confirmSpy.mockRestore();
  });

  it('deve tratar erro when toggle cliente fails', () => {
    const consoleErrorSpy = jest.spyOn(console, 'error').mockImplementation();
    const confirmSpy = jest.spyOn(window, 'confirm').mockReturnValue(true);
    clienteService.toggleAtivo.mockReturnValue(throwError(() => new Error('Error')));

    component.toggleAtivo(mockClientes[0]);

    expect(component.error).toBe('Erro ao atualizar cliente');
    expect(component.loading).toBe(false);
    consoleErrorSpy.mockRestore();
    confirmSpy.mockRestore();
  });

  it('deve definir loading to true when toggling cliente status', () => {
    const confirmSpy = jest.spyOn(window, 'confirm').mockReturnValue(true);
    let loadingDuringCall = false;

    clienteService.toggleAtivo.mockImplementation(() => {
      loadingDuringCall = component.loading;
      return of(mockClientes[0]);
    });

    component.toggleAtivo(mockClientes[0]);

    expect(loadingDuringCall).toBe(true);
    confirmSpy.mockRestore();
  });

  it('deve handle clientes without CPF when filtering', () => {
    const clienteSemCpf: ClienteResponse = {
      id: 3,
      nome: 'Pedro Costa',
      email: 'pedro@email.com',
      cpf: undefined,
      telefone: '41977777777',
      ativo: true,
      perfil: PerfilUsuario.CLIENTE,
      dataCadastro: '2024-01-03'
    };

    component.clientes = [...mockClientes, clienteSemCpf];
    component.onSearchChange('123456');
    component.filterClientes();

    expect(component.clientesFiltrados).toHaveLength(1);
    expect(component.clientesFiltrados[0].id).toBe(1);
  });
});
