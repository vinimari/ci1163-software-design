import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormsModule } from '@angular/forms';
import { of, throwError } from 'rxjs';
import { FuncionariosListComponent } from './funcionarios-list.component';
import { FuncionarioService } from '../../../core/services/funcionario.service';
import { FilialService } from '../../../core/services/filial.service';
import { FuncionarioResponse } from '../../../core/models/funcionario.model';
import { FilialResponse } from '../../../core/models/filial.model';

describe('FuncionariosListComponent', () => {
  let component: FuncionariosListComponent;
  let fixture: ComponentFixture<FuncionariosListComponent>;
  let funcionarioService: jest.Mocked<FuncionarioService>;
  let filialService: jest.Mocked<FilialService>;

  const mockFilial: FilialResponse = {
    id: 1,
    nome: 'Filial Centro',
    cidade: 'São Paulo',
    estado: 'SP',
    endereco: 'Rua A, 123',
    telefone: '(11) 1234-5678',
    dataCadastro: '2024-01-01T00:00:00'
  };

  const mockFuncionario: FuncionarioResponse = {
    id: 1,
    nome: 'João Silva',
    email: 'joao@example.com',
    cpf: '123.456.789-00',
    telefone: '(11) 98765-4321',
    ativo: true,
    dataCadastro: '2024-01-01T00:00:00',
    matricula: 'F001',
    filial: mockFilial
  };

  beforeEach(async () => {
    const funcionarioServiceMock = {
      findAll: jest.fn(),
      toggleAtivo: jest.fn(),
      delete: jest.fn()
    };

    const filialServiceMock = {
      getAll: jest.fn()
    };

    await TestBed.configureTestingModule({
      imports: [
        FuncionariosListComponent,
        RouterTestingModule,
        HttpClientTestingModule,
        FormsModule
      ],
      providers: [
        { provide: FuncionarioService, useValue: funcionarioServiceMock },
        { provide: FilialService, useValue: filialServiceMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(FuncionariosListComponent);
    component = fixture.componentInstance;
    funcionarioService = TestBed.inject(FuncionarioService) as jest.Mocked<FuncionarioService>;
    filialService = TestBed.inject(FilialService) as jest.Mocked<FilialService>;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load filiais and funcionarios on init', () => {
    filialService.getAll.mockReturnValue(of([mockFilial]));
    funcionarioService.findAll.mockReturnValue(of([mockFuncionario]));

    component.ngOnInit();

    expect(filialService.getAll).toHaveBeenCalled();
    expect(funcionarioService.findAll).toHaveBeenCalled();
    expect(component.filiais).toEqual([mockFilial]);
    expect(component.funcionarios).toEqual([mockFuncionario]);
    expect(component.loading).toBe(false);
  });

  it('should handle error when loading funcionarios', () => {
    filialService.getAll.mockReturnValue(of([mockFilial]));
    funcionarioService.findAll.mockReturnValue(throwError(() => new Error('Error')));

    component.ngOnInit();

    expect(component.error).toBe('Erro ao carregar funcionários');
    expect(component.loading).toBe(false);
  });

  it('should filter funcionarios by filial', () => {
    funcionarioService.findAll.mockReturnValue(of([mockFuncionario]));

    component.selectedFilialId = 1;
    component.onFilialFilterChange();

    expect(funcionarioService.findAll).toHaveBeenCalledWith(1);
  });

  it('should clear filter', () => {
    funcionarioService.findAll.mockReturnValue(of([mockFuncionario]));

    component.selectedFilialId = 1;
    component.clearFilter();

    expect(component.selectedFilialId).toBeNull();
    expect(funcionarioService.findAll).toHaveBeenCalled();
  });

  it('should toggle funcionario active status', () => {
    jest.spyOn(window, 'confirm').mockReturnValue(true);
    funcionarioService.toggleAtivo.mockReturnValue(of(mockFuncionario));
    funcionarioService.findAll.mockReturnValue(of([mockFuncionario]));

    component.toggleAtivo(mockFuncionario);

    expect(window.confirm).toHaveBeenCalled();
    expect(funcionarioService.toggleAtivo).toHaveBeenCalledWith(1, false);
  });

  it('should not toggle if user cancels', () => {
    jest.spyOn(window, 'confirm').mockReturnValue(false);

    component.toggleAtivo(mockFuncionario);

    expect(funcionarioService.toggleAtivo).not.toHaveBeenCalled();
  });

  it('should delete funcionario', () => {
    jest.spyOn(window, 'confirm').mockReturnValue(true);
    funcionarioService.delete.mockReturnValue(of(undefined));
    funcionarioService.findAll.mockReturnValue(of([]));

    component.deleteFuncionario(mockFuncionario);

    expect(window.confirm).toHaveBeenCalled();
    expect(funcionarioService.delete).toHaveBeenCalledWith(1);
  });

  it('should not delete if user cancels', () => {
    jest.spyOn(window, 'confirm').mockReturnValue(false);

    component.deleteFuncionario(mockFuncionario);

    expect(funcionarioService.delete).not.toHaveBeenCalled();
  });

  it('should return correct status class', () => {
    expect(component.getStatusClass(true)).toBe('status-ativo');
    expect(component.getStatusClass(false)).toBe('status-inativo');
  });

  it('should return correct status text', () => {
    expect(component.getStatusText(true)).toBe('Ativo');
    expect(component.getStatusText(false)).toBe('Inativo');
  });
});
