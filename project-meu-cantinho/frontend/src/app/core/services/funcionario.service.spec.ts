import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { FuncionarioService } from './funcionario.service';
import { FuncionarioRequest, FuncionarioResponse } from '../models/funcionario.model';
import { environment } from '../../../environments/environment';

describe('FuncionarioService', () => {
  let service: FuncionarioService;
  let httpMock: HttpTestingController;
  const apiUrl = `${environment.apiUrl}/funcionarios`;

  const mockFuncionario: FuncionarioResponse = {
    id: 1,
    nome: 'João Silva',
    email: 'joao@example.com',
    cpf: '123.456.789-00',
    telefone: '(11) 98765-4321',
    ativo: true,
    dataCadastro: '2024-01-01T00:00:00',
    matricula: 'F001',
    filial: {
      id: 1,
      nome: 'Filial Centro',
      cidade: 'São Paulo',
      estado: 'SP',
      endereco: 'Rua A, 123',
      telefone: '(11) 1234-5678',
      dataCadastro: '2024-01-01T00:00:00'
    }
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [FuncionarioService]
    });
    service = TestBed.inject(FuncionarioService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('findAll', () => {
    it('should retrieve all funcionarios', () => {
      const mockFuncionarios = [mockFuncionario];

      service.findAll().subscribe(funcionarios => {
        expect(funcionarios).toEqual(mockFuncionarios);
        expect(funcionarios.length).toBe(1);
      });

      const req = httpMock.expectOne(apiUrl);
      expect(req.request.method).toBe('GET');
      req.flush(mockFuncionarios);
    });

    it('should retrieve funcionarios filtered by filialId', () => {
      const filialId = 1;
      const mockFuncionarios = [mockFuncionario];

      service.findAll(filialId).subscribe(funcionarios => {
        expect(funcionarios).toEqual(mockFuncionarios);
      });

      const req = httpMock.expectOne(`${apiUrl}?filialId=${filialId}`);
      expect(req.request.method).toBe('GET');
      req.flush(mockFuncionarios);
    });
  });

  describe('findById', () => {
    it('should retrieve a funcionario by id', () => {
      const id = 1;

      service.findById(id).subscribe(funcionario => {
        expect(funcionario).toEqual(mockFuncionario);
        expect(funcionario.id).toBe(id);
      });

      const req = httpMock.expectOne(`${apiUrl}/${id}`);
      expect(req.request.method).toBe('GET');
      req.flush(mockFuncionario);
    });
  });

  describe('create', () => {
    it('should create a new funcionario', () => {
      const newFuncionario: FuncionarioRequest = {
        nome: 'João Silva',
        email: 'joao@example.com',
        senha: 'senha123',
        matricula: 'F001',
        filialId: 1,
        ativo: true
      };

      service.create(newFuncionario).subscribe(funcionario => {
        expect(funcionario).toEqual(mockFuncionario);
      });

      const req = httpMock.expectOne(apiUrl);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(newFuncionario);
      req.flush(mockFuncionario);
    });
  });

  describe('update', () => {
    it('should update an existing funcionario', () => {
      const id = 1;
      const updatedFuncionario: FuncionarioRequest = {
        nome: 'João Silva Atualizado',
        email: 'joao@example.com',
        matricula: 'F001',
        filialId: 1,
        ativo: true
      };

      service.update(id, updatedFuncionario).subscribe(funcionario => {
        expect(funcionario).toEqual(mockFuncionario);
      });

      const req = httpMock.expectOne(`${apiUrl}/${id}`);
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(updatedFuncionario);
      req.flush(mockFuncionario);
    });
  });

  describe('delete', () => {
    it('should delete a funcionario', () => {
      const id = 1;

      service.delete(id).subscribe(response => {
        expect(response).toBeUndefined();
      });

      const req = httpMock.expectOne(`${apiUrl}/${id}`);
      expect(req.request.method).toBe('DELETE');
      req.flush(null);
    });
  });

  describe('toggleAtivo', () => {
    it('should toggle funcionario active status', () => {
      const id = 1;
      const ativo = false;

      service.toggleAtivo(id, ativo).subscribe(funcionario => {
        expect(funcionario).toEqual(mockFuncionario);
      });

      const req = httpMock.expectOne(`${apiUrl}/${id}/ativo?ativo=${ativo}`);
      expect(req.request.method).toBe('PATCH');
      req.flush(mockFuncionario);
    });
  });

  describe('trocarFilial', () => {
    it('should change funcionario filial', () => {
      const id = 1;
      const filialId = 2;

      service.trocarFilial(id, filialId).subscribe(funcionario => {
        expect(funcionario).toEqual(mockFuncionario);
      });

      const req = httpMock.expectOne(`${apiUrl}/${id}/filial?filialId=${filialId}`);
      expect(req.request.method).toBe('PATCH');
      req.flush(mockFuncionario);
    });
  });
});
