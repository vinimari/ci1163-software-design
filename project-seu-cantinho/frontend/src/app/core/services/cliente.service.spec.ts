import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ClienteService } from './cliente.service';
import { ClienteRequest, ClienteResponse } from '../models';
import { environment } from '../../../environments/environment';

describe('ClienteService', () => {
  let service: ClienteService;
  let httpMock: HttpTestingController;
  const apiUrl = `${environment.apiUrl}/clientes`;

  const mockClienteResponse: ClienteResponse = {
    id: 1,
    nome: 'João Silva',
    cpf: '12345678901',
    telefone: '11999999999',
    email: 'joao@example.com',
    dataCadastro: '2024-01-01'
  };

  const mockClienteRequest: ClienteRequest = {
    nome: 'João Silva',
    cpf: '12345678901',
    telefone: '11999999999',
    email: 'joao@example.com'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ClienteService]
    });
    service = TestBed.inject(ClienteService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('deve ser created', () => {
    expect(service).toBeTruthy();
  });

  describe('getAll', () => {
    it('deve fetch all clientes', () => {
      const mockClientes = [mockClienteResponse];

      service.getAll().subscribe(clientes => {
        expect(clientes).toEqual(mockClientes);
        expect(clientes.length).toBe(1);
      });

      const req = httpMock.expectOne(apiUrl);
      expect(req.request.method).toBe('GET');
      req.flush(mockClientes);
    });

    it('deve retornar empty array when no clientes', () => {
      service.getAll().subscribe(clientes => {
        expect(clientes).toEqual([]);
        expect(clientes.length).toBe(0);
      });

      const req = httpMock.expectOne(apiUrl);
      req.flush([]);
    });
  });

  describe('getById', () => {
    it('deve fetch cliente by id', () => {
      const id = 1;

      service.getById(id).subscribe(cliente => {
        expect(cliente).toEqual(mockClienteResponse);
        expect(cliente.id).toBe(id);
      });

      const req = httpMock.expectOne(`${apiUrl}/${id}`);
      expect(req.request.method).toBe('GET');
      req.flush(mockClienteResponse);
    });

    it('deve tratar erro when cliente not found', () => {
      const id = 999;

      service.getById(id).subscribe(
        () => fail('should have failed'),
        error => {
          expect(error.status).toBe(404);
        }
      );

      const req = httpMock.expectOne(`${apiUrl}/${id}`);
      req.flush('Not found', { status: 404, statusText: 'Not Found' });
    });
  });

  describe('create', () => {
    it('deve criar a new cliente', () => {
      service.create(mockClienteRequest).subscribe(cliente => {
        expect(cliente).toEqual(mockClienteResponse);
        expect(cliente.nome).toBe(mockClienteRequest.nome);
      });

      const req = httpMock.expectOne(apiUrl);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(mockClienteRequest);
      req.flush(mockClienteResponse);
    });

    it('deve handle validation errors', () => {
      service.create(mockClienteRequest).subscribe(
        () => fail('should have failed'),
        error => {
          expect(error.status).toBe(400);
        }
      );

      const req = httpMock.expectOne(apiUrl);
      req.flush('Validation error', { status: 400, statusText: 'Bad Request' });
    });
  });

  describe('update', () => {
    it('deve atualizar an existing cliente', () => {
      const id = 1;
      const updatedCliente = { ...mockClienteResponse, nome: 'João Updated' };

      service.update(id, mockClienteRequest).subscribe(cliente => {
        expect(cliente).toEqual(updatedCliente);
      });

      const req = httpMock.expectOne(`${apiUrl}/${id}`);
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(mockClienteRequest);
      req.flush(updatedCliente);
    });

    it('deve tratar erro when updating non-existent cliente', () => {
      const id = 999;

      service.update(id, mockClienteRequest).subscribe(
        () => fail('should have failed'),
        error => {
          expect(error.status).toBe(404);
        }
      );

      const req = httpMock.expectOne(`${apiUrl}/${id}`);
      req.flush('Not found', { status: 404, statusText: 'Not Found' });
    });
  });

  describe('delete', () => {
    it('deve delete a cliente', () => {
      const id = 1;

      service.delete(id).subscribe(response => {
        expect(response).toBeUndefined();
      });

      const req = httpMock.expectOne(`${apiUrl}/${id}`);
      expect(req.request.method).toBe('DELETE');
      req.flush(null);
    });

    it('deve tratar erro when deleting non-existent cliente', () => {
      const id = 999;

      service.delete(id).subscribe(
        () => fail('should have failed'),
        error => {
          expect(error.status).toBe(404);
        }
      );

      const req = httpMock.expectOne(`${apiUrl}/${id}`);
      req.flush('Not found', { status: 404, statusText: 'Not Found' });
    });
  });
});
