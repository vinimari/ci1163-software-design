import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { of, throwError } from 'rxjs';
import { EspacosAdminComponent } from './espacos-admin.component';
import { EspacoService, AuthService, FilialService } from '../../../../core/services';
import { EspacoResponse, EspacoRequest, UsuarioResponse } from '../../../../core/models';
import { PerfilUsuario } from '../../../../core/models/enums';

describe('EspacosAdminComponent', () => {
  let component: EspacosAdminComponent;
  let fixture: ComponentFixture<EspacosAdminComponent>;
  let espacoService: jest.Mocked<EspacoService>;
  let authService: jest.Mocked<AuthService>;
  let filialService: jest.Mocked<FilialService>;

  const mockEspacoResponse: EspacoResponse = {
    id: 1,
    nome: 'Salão Principal',
    descricao: 'Espaço amplo',
    capacidade: 100,
    precoDiaria: 500.00,
    ativo: true,
    urlFotoPrincipal: 'https://example.com/foto.jpg',
    filial: {
      id: 1,
      nome: 'Filial Centro',
      cidade: 'São Paulo',
      estado: 'SP'
    }
  };

  const mockAdminUser: UsuarioResponse = {
    id: 1,
    nome: 'Admin',
    email: 'admin@test.com',
    perfil: PerfilUsuario.ADMIN,
    ativo: true,
    dataCadastro: '2025-01-01T00:00:00Z'
  };

  const mockFuncionarioUser: UsuarioResponse = {
    id: 2,
    nome: 'Funcionario',
    email: 'func@test.com',
    perfil: PerfilUsuario.FUNCIONARIO,
    ativo: true,
    dataCadastro: '2025-01-01T00:00:00Z'
  };

  beforeEach(async () => {
    const espacoServiceMock = {
      getAll: jest.fn(),
      getById: jest.fn(),
      getByFilialId: jest.fn(),
      getAtivos: jest.fn(),
      create: jest.fn(),
      update: jest.fn(),
      delete: jest.fn()
    };

    const authServiceMock = {
      getCurrentUser: jest.fn(),
      isAdmin: jest.fn(),
      isFuncionario: jest.fn(),
      isCliente: jest.fn(),
      currentUser$: of(null)
    };

    const filialServiceMock = {
      getAll: jest.fn(),
      getById: jest.fn(),
      getAtivas: jest.fn(),
      create: jest.fn(),
      update: jest.fn(),
      delete: jest.fn()
    };

    await TestBed.configureTestingModule({
      imports: [
        EspacosAdminComponent,
        HttpClientTestingModule,
        RouterTestingModule
      ],
      providers: [
        { provide: EspacoService, useValue: espacoServiceMock },
        { provide: AuthService, useValue: authServiceMock },
        { provide: FilialService, useValue: filialServiceMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(EspacosAdminComponent);
    component = fixture.componentInstance;
    espacoService = TestBed.inject(EspacoService) as jest.Mocked<EspacoService>;
    authService = TestBed.inject(AuthService) as jest.Mocked<AuthService>;
    filialService = TestBed.inject(FilialService) as jest.Mocked<FilialService>;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('ngOnInit()', () => {
    it('should load espacos for admin user', () => {
      authService.getCurrentUser.mockReturnValue(mockAdminUser);
      authService.isAdmin.mockReturnValue(true);
      authService.isFuncionario.mockReturnValue(false);
      espacoService.getAll.mockReturnValue(of([mockEspacoResponse]));

      component.ngOnInit();

      expect(authService.isFuncionario).toHaveBeenCalled();
      expect(espacoService.getAll).toHaveBeenCalled();
      expect(component.espacos).toEqual([mockEspacoResponse]);
      expect(component.loading).toBe(false);
    });

    it('should load espacos for funcionario user', () => {
      authService.getCurrentUser.mockReturnValue(mockFuncionarioUser);
      authService.isFuncionario.mockReturnValue(true);
      espacoService.getByFilialId.mockReturnValue(of([mockEspacoResponse]));

      component.ngOnInit();

      expect(authService.isFuncionario).toHaveBeenCalled();
      expect(component.filialIdFuncionario).toBe(1); // Mock value
      expect(espacoService.getByFilialId).toHaveBeenCalledWith(1);
    });

    it('should handle error when loading espacos', () => {
      authService.isAdmin.mockReturnValue(true);
      authService.isFuncionario.mockReturnValue(false);
      espacoService.getAll.mockReturnValue(throwError(() => new Error('Server error')));
      const consoleError = jest.spyOn(console, 'error').mockImplementation();

      component.ngOnInit();

      expect(component.loading).toBe(false);
      expect(consoleError).toHaveBeenCalled();
      consoleError.mockRestore();
    });
  });

  describe('aplicarFiltros()', () => {
    beforeEach(() => {
      component.espacos = [
        mockEspacoResponse,
        {
          ...mockEspacoResponse,
          id: 2,
          nome: 'Sala de Reuniões',
          ativo: false,
          filial: { ...mockEspacoResponse.filial, nome: 'Filial Norte' }
        }
      ];
    });

    it('should filter by search term', () => {
      component.searchTerm = 'Salão';
      component.aplicarFiltros();

      expect(component.espacosFiltrados.length).toBe(1);
      expect(component.espacosFiltrados[0].nome).toBe('Salão Principal');
    });

    it('should filter by filial name', () => {
      component.searchTerm = 'Norte';
      component.aplicarFiltros();

      expect(component.espacosFiltrados.length).toBe(1);
      expect(component.espacosFiltrados[0].filial.nome).toBe('Filial Norte');
    });

    it('should filter by status ativo', () => {
      component.filtroStatus = 'ativo';
      component.aplicarFiltros();

      expect(component.espacosFiltrados.length).toBe(1);
      expect(component.espacosFiltrados[0].ativo).toBe(true);
    });

    it('should filter by status inativo', () => {
      component.filtroStatus = 'inativo';
      component.aplicarFiltros();

      expect(component.espacosFiltrados.length).toBe(1);
      expect(component.espacosFiltrados[0].ativo).toBe(false);
    });

    it('should show all when no filters applied', () => {
      component.searchTerm = '';
      component.filtroStatus = '';
      component.aplicarFiltros();

      expect(component.espacosFiltrados.length).toBe(2);
    });
  });

  describe('novoEspaco()', () => {
    it('should set showForm to true and clear espacoSelecionado', () => {
      component.espacoSelecionado = mockEspacoResponse;

      component.novoEspaco();

      expect(component.showForm).toBe(true);
      expect(component.espacoSelecionado).toBeUndefined();
    });
  });

  describe('editarEspaco()', () => {
    it('should set espacoSelecionado and show form', () => {
      component.editarEspaco(mockEspacoResponse);

      expect(component.showForm).toBe(true);
      expect(component.espacoSelecionado).toEqual(mockEspacoResponse);
    });
  });

  describe('onSubmitForm()', () => {
    const mockEspacoRequest: EspacoRequest = {
      nome: 'Salão Principal',
      descricao: 'Espaço amplo',
      capacidade: 100,
      precoDiaria: 500.00,
      ativo: true,
      urlFotoPrincipal: 'https://example.com/foto.jpg',
      filialId: 1
    };

    it('should create new espaco when espacoSelecionado is undefined', () => {
      authService.isAdmin.mockReturnValue(true);
      authService.isFuncionario.mockReturnValue(false);
      espacoService.create.mockReturnValue(of(mockEspacoResponse));
      espacoService.getAll.mockReturnValue(of([mockEspacoResponse]));
      const alertSpy = jest.spyOn(window, 'alert').mockImplementation();

      component.espacoSelecionado = undefined;
      component.onSubmitForm(mockEspacoRequest);

      expect(espacoService.create).toHaveBeenCalledWith(mockEspacoRequest);
      expect(component.showForm).toBe(false);
      expect(alertSpy).toHaveBeenCalledWith('Espaço criado com sucesso!');
      alertSpy.mockRestore();
    });

    it('should update existing espaco when espacoSelecionado is set', () => {
      authService.isAdmin.mockReturnValue(true);
      authService.isFuncionario.mockReturnValue(false);
      espacoService.update.mockReturnValue(of(mockEspacoResponse));
      espacoService.getAll.mockReturnValue(of([mockEspacoResponse]));
      const alertSpy = jest.spyOn(window, 'alert').mockImplementation();

      component.espacoSelecionado = mockEspacoResponse;
      component.onSubmitForm(mockEspacoRequest);

      expect(espacoService.update).toHaveBeenCalledWith(mockEspacoResponse.id, mockEspacoRequest);
      expect(component.showForm).toBe(false);
      expect(alertSpy).toHaveBeenCalledWith('Espaço atualizado com sucesso!');
      alertSpy.mockRestore();
    });

    it('should handle create error with backend message', () => {
      const errorResponse = {
        error: {
          message: 'Capacidade não pode exceder 1000 pessoas por questões de segurança'
        }
      };
      espacoService.create.mockReturnValue(throwError(() => errorResponse));
      const alertSpy = jest.spyOn(window, 'alert').mockImplementation();
      const consoleError = jest.spyOn(console, 'error').mockImplementation();

      component.espacoSelecionado = undefined;
      component.onSubmitForm(mockEspacoRequest);

      expect(alertSpy).toHaveBeenCalledWith('Capacidade não pode exceder 1000 pessoas por questões de segurança');
      alertSpy.mockRestore();
      consoleError.mockRestore();
    });

    it('should handle create error without backend message', () => {
      espacoService.create.mockReturnValue(throwError(() => new Error('Create error')));
      const alertSpy = jest.spyOn(window, 'alert').mockImplementation();
      const consoleError = jest.spyOn(console, 'error').mockImplementation();

      component.espacoSelecionado = undefined;
      component.onSubmitForm(mockEspacoRequest);

      expect(alertSpy).toHaveBeenCalledWith('Erro ao criar espaço. Tente novamente.');
      alertSpy.mockRestore();
      consoleError.mockRestore();
    });

    it('should handle update error with backend message', () => {
      const errorResponse = {
        error: {
          message: 'Capacidade não pode exceder 1000 pessoas por questões de segurança'
        }
      };
      espacoService.update.mockReturnValue(throwError(() => errorResponse));
      const alertSpy = jest.spyOn(window, 'alert').mockImplementation();
      const consoleError = jest.spyOn(console, 'error').mockImplementation();

      component.espacoSelecionado = mockEspacoResponse;
      component.onSubmitForm(mockEspacoRequest);

      expect(alertSpy).toHaveBeenCalledWith('Capacidade não pode exceder 1000 pessoas por questões de segurança');
      alertSpy.mockRestore();
      consoleError.mockRestore();
    });

    it('should handle update error without backend message', () => {
      espacoService.update.mockReturnValue(throwError(() => new Error('Update error')));
      const alertSpy = jest.spyOn(window, 'alert').mockImplementation();
      const consoleError = jest.spyOn(console, 'error').mockImplementation();

      component.espacoSelecionado = mockEspacoResponse;
      component.onSubmitForm(mockEspacoRequest);

      expect(alertSpy).toHaveBeenCalledWith('Erro ao atualizar espaço. Tente novamente.');
      alertSpy.mockRestore();
      consoleError.mockRestore();
    });
  });

  describe('onCancelForm()', () => {
    it('should hide form and clear espacoSelecionado', () => {
      component.showForm = true;
      component.espacoSelecionado = mockEspacoResponse;

      component.onCancelForm();

      expect(component.showForm).toBe(false);
      expect(component.espacoSelecionado).toBeUndefined();
    });
  });

  describe('toggleAtivo()', () => {
    it('should toggle espaco status to inactive', () => {
      authService.isAdmin.mockReturnValue(true);
      authService.isFuncionario.mockReturnValue(false);
      const inactiveEspaco = { ...mockEspacoResponse, ativo: false };
      espacoService.update.mockReturnValue(of(inactiveEspaco));
      espacoService.getAll.mockReturnValue(of([inactiveEspaco]));
      const alertSpy = jest.spyOn(window, 'alert').mockImplementation();

      component.toggleAtivo(mockEspacoResponse);

      expect(espacoService.update).toHaveBeenCalledWith(
        mockEspacoResponse.id,
        expect.objectContaining({ ativo: false })
      );
      expect(alertSpy).toHaveBeenCalledWith('Espaço desativado com sucesso!');
      alertSpy.mockRestore();
    });

    it('should toggle espaco status to active', () => {
      authService.isAdmin.mockReturnValue(true);
      authService.isFuncionario.mockReturnValue(false);
      const inactiveEspaco = { ...mockEspacoResponse, ativo: false };
      const activeEspaco = { ...mockEspacoResponse, ativo: true };
      espacoService.update.mockReturnValue(of(activeEspaco));
      espacoService.getAll.mockReturnValue(of([activeEspaco]));
      const alertSpy = jest.spyOn(window, 'alert').mockImplementation();

      component.toggleAtivo(inactiveEspaco);

      expect(espacoService.update).toHaveBeenCalledWith(
        inactiveEspaco.id,
        expect.objectContaining({ ativo: true })
      );
      expect(alertSpy).toHaveBeenCalledWith('Espaço ativado com sucesso!');
      alertSpy.mockRestore();
    });

    it('should handle toggle error with backend message', () => {
      const errorResponse = {
        error: {
          message: 'Não é possível desativar espaço com reservas ativas'
        }
      };
      espacoService.update.mockReturnValue(throwError(() => errorResponse));
      const alertSpy = jest.spyOn(window, 'alert').mockImplementation();
      const consoleError = jest.spyOn(console, 'error').mockImplementation();

      component.toggleAtivo(mockEspacoResponse);

      expect(alertSpy).toHaveBeenCalledWith('Não é possível desativar espaço com reservas ativas');
      alertSpy.mockRestore();
      consoleError.mockRestore();
    });

    it('should handle toggle error without backend message', () => {
      espacoService.update.mockReturnValue(throwError(() => new Error('Toggle error')));
      const alertSpy = jest.spyOn(window, 'alert').mockImplementation();
      const consoleError = jest.spyOn(console, 'error').mockImplementation();

      component.toggleAtivo(mockEspacoResponse);

      expect(alertSpy).toHaveBeenCalledWith('Erro ao alterar status do espaço.');
      alertSpy.mockRestore();
      consoleError.mockRestore();
    });
  });

  describe('excluirEspaco()', () => {
    it('should delete espaco when confirmed', () => {
      authService.isAdmin.mockReturnValue(true);
      authService.isFuncionario.mockReturnValue(false);
      espacoService.delete.mockReturnValue(of(null));
      espacoService.getAll.mockReturnValue(of([]));
      const confirmSpy = jest.spyOn(window, 'confirm').mockReturnValue(true);
      const alertSpy = jest.spyOn(window, 'alert').mockImplementation();

      component.excluirEspaco(mockEspacoResponse);

      expect(confirmSpy).toHaveBeenCalledWith('Tem certeza que deseja excluir o espaço "Salão Principal"?');
      expect(espacoService.delete).toHaveBeenCalledWith(mockEspacoResponse.id);
      expect(alertSpy).toHaveBeenCalledWith('Espaço excluído com sucesso!');
      confirmSpy.mockRestore();
      alertSpy.mockRestore();
    });

    it('should not delete espaco when cancelled', () => {
      const confirmSpy = jest.spyOn(window, 'confirm').mockReturnValue(false);

      component.excluirEspaco(mockEspacoResponse);

      expect(confirmSpy).toHaveBeenCalled();
      expect(espacoService.delete).not.toHaveBeenCalled();
      confirmSpy.mockRestore();
    });

    it('should handle delete error with backend message', () => {
      const errorResponse = {
        error: {
          message: 'Espaço não pode ser excluído pois possui reservas vinculadas'
        }
      };
      espacoService.delete.mockReturnValue(throwError(() => errorResponse));
      const confirmSpy = jest.spyOn(window, 'confirm').mockReturnValue(true);
      const alertSpy = jest.spyOn(window, 'alert').mockImplementation();
      const consoleError = jest.spyOn(console, 'error').mockImplementation();

      component.excluirEspaco(mockEspacoResponse);

      expect(alertSpy).toHaveBeenCalledWith('Espaço não pode ser excluído pois possui reservas vinculadas');
      confirmSpy.mockRestore();
      alertSpy.mockRestore();
      consoleError.mockRestore();
    });

    it('should handle delete error without backend message', () => {
      espacoService.delete.mockReturnValue(throwError(() => new Error('Delete error')));
      const confirmSpy = jest.spyOn(window, 'confirm').mockReturnValue(true);
      const alertSpy = jest.spyOn(window, 'alert').mockImplementation();
      const consoleError = jest.spyOn(console, 'error').mockImplementation();

      component.excluirEspaco(mockEspacoResponse);

      expect(alertSpy).toHaveBeenCalledWith('Erro ao excluir espaço. Verifique se não há reservas associadas.');
      confirmSpy.mockRestore();
      alertSpy.mockRestore();
      consoleError.mockRestore();
    });
  });

  describe('onSearchChange()', () => {
    it('should update searchTerm and apply filters', () => {
      component.espacos = [mockEspacoResponse];
      const applySpy = jest.spyOn(component, 'aplicarFiltros');

      component.onSearchChange('Salão');

      expect(component.searchTerm).toBe('Salão');
      expect(applySpy).toHaveBeenCalled();
    });
  });

  describe('onStatusFilterChange()', () => {
    it('should update filtroStatus and apply filters', () => {
      component.espacos = [mockEspacoResponse];
      const applySpy = jest.spyOn(component, 'aplicarFiltros');

      component.onStatusFilterChange('ativo');

      expect(component.filtroStatus).toBe('ativo');
      expect(applySpy).toHaveBeenCalled();
    });
  });
});
