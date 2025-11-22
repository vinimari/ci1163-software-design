import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { FilialService } from './filial.service';
import { FilialRequest, FilialResponse } from '../models';
import { environment } from '../../../environments/environment';

describe('FilialService', () => {
  let service: FilialService;
  let httpMock: HttpTestingController;
  const apiUrl = `${environment.apiUrl}/filiais`;

  const mockFilialResponse: FilialResponse = {
    id: 1,
    nome: 'Filial Centro',
    endereco: 'Rua Principal, 100',
    cidade: 'São Paulo',
    estado: 'SP',
    telefone: '(11) 1234-5678',
    ativo: true
  };

  const mockFilialRequest: FilialRequest = {
    nome: 'Filial Centro',
    endereco: 'Rua Principal, 100',
    cidade: 'São Paulo',
    estado: 'SP',
    telefone: '(11) 1234-5678',
    ativo: true
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [FilialService]
    });
    service = TestBed.inject(FilialService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getAll()', () => {
    it('should retrieve all filiais', (done) => {
      const mockFiliais = [mockFilialResponse];

      service.getAll().subscribe(filiais => {
        expect(filiais).toEqual(mockFiliais);
        expect(filiais.length).toBe(1);
        done();
      });

      const req = httpMock.expectOne(apiUrl);
      expect(req.request.method).toBe('GET');
      req.flush(mockFiliais);
    });

    it('should handle empty list', (done) => {
      service.getAll().subscribe(filiais => {
        expect(filiais).toEqual([]);
        done();
      });

      const req = httpMock.expectOne(apiUrl);
      req.flush([]);
    });
  });

  describe('getById()', () => {
    it('should retrieve a single filial by id', (done) => {
      const filialId = 1;

      service.getById(filialId).subscribe(filial => {
        expect(filial).toEqual(mockFilialResponse);
        expect(filial.id).toBe(filialId);
        done();
      });

      const req = httpMock.expectOne(`${apiUrl}/${filialId}`);
      expect(req.request.method).toBe('GET');
      req.flush(mockFilialResponse);
    });

    it('should handle not found error', (done) => {
      const filialId = 999;

      service.getById(filialId).subscribe({
        next: () => fail('should have failed'),
        error: (error) => {
          expect(error.status).toBe(404);
          done();
        }
      });

      const req = httpMock.expectOne(`${apiUrl}/${filialId}`);
      req.flush('Not found', { status: 404, statusText: 'Not Found' });
    });
  });

  describe('getAtivas()', () => {
    it('should retrieve only active filiais', (done) => {
      const activeFiliais = [
        { ...mockFilialResponse, ativo: true },
        { ...mockFilialResponse, id: 2, nome: 'Filial Norte', ativo: true }
      ];

      service.getAtivas().subscribe(filiais => {
        expect(filiais).toEqual(activeFiliais);
        expect(filiais.every(f => f.ativo)).toBe(true);
        done();
      });

      const req = httpMock.expectOne(`${apiUrl}/ativas`);
      expect(req.request.method).toBe('GET');
      req.flush(activeFiliais);
    });
  });

  describe('create()', () => {
    it('should create a new filial', (done) => {
      service.create(mockFilialRequest).subscribe(filial => {
        expect(filial).toEqual(mockFilialResponse);
        expect(filial.nome).toBe(mockFilialRequest.nome);
        done();
      });

      const req = httpMock.expectOne(apiUrl);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(mockFilialRequest);
      req.flush(mockFilialResponse);
    });

    it('should handle validation error', (done) => {
      const invalidRequest = { ...mockFilialRequest, nome: '' };

      service.create(invalidRequest).subscribe({
        next: () => fail('should have failed'),
        error: (error) => {
          expect(error.status).toBe(400);
          done();
        }
      });

      const req = httpMock.expectOne(apiUrl);
      req.flush('Validation error', { status: 400, statusText: 'Bad Request' });
    });
  });

  describe('update()', () => {
    it('should update an existing filial', (done) => {
      const filialId = 1;
      const updatedFilial = { ...mockFilialResponse, nome: 'Nome Atualizado' };

      service.update(filialId, mockFilialRequest).subscribe(filial => {
        expect(filial).toEqual(updatedFilial);
        done();
      });

      const req = httpMock.expectOne(`${apiUrl}/${filialId}`);
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(mockFilialRequest);
      req.flush(updatedFilial);
    });
  });

  describe('delete()', () => {
    it('should delete a filial', (done) => {
      const filialId = 1;

      service.delete(filialId).subscribe(response => {
        expect(response).toBeNull();
        done();
      });

      const req = httpMock.expectOne(`${apiUrl}/${filialId}`);
      expect(req.request.method).toBe('DELETE');
      req.flush(null);
    });

    it('should handle conflict when deleting filial with espacos', (done) => {
      const filialId = 1;

      service.delete(filialId).subscribe({
        next: () => fail('should have failed'),
        error: (error) => {
          expect(error.status).toBe(409);
          done();
        }
      });

      const req = httpMock.expectOne(`${apiUrl}/${filialId}`);
      req.flush('Cannot delete filial with espacos', { status: 409, statusText: 'Conflict' });
    });
  });
});
