import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { EspacoService } from './espaco.service';
import { EspacoRequest, EspacoResponse } from '../models';
import { environment } from '../../../environments/environment';

describe('EspacoService', () => {
  let service: EspacoService;
  let httpMock: HttpTestingController;
  const apiUrl = `${environment.apiUrl}/espacos`;

  const mockEspacoResponse: EspacoResponse = {
    id: 1,
    nome: 'Salão Principal',
    descricao: 'Espaço amplo para eventos',
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

  const mockEspacoRequest: EspacoRequest = {
    nome: 'Salão Principal',
    descricao: 'Espaço amplo para eventos',
    capacidade: 100,
    precoDiaria: 500.00,
    ativo: true,
    urlFotoPrincipal: 'https://example.com/foto.jpg',
    filialId: 1
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [EspacoService]
    });
    service = TestBed.inject(EspacoService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getAll()', () => {
    it('should retrieve all espacos', (done) => {
      const mockEspacos = [mockEspacoResponse];

      service.getAll().subscribe(espacos => {
        expect(espacos).toEqual(mockEspacos);
        expect(espacos.length).toBe(1);
        done();
      });

      const req = httpMock.expectOne(apiUrl);
      expect(req.request.method).toBe('GET');
      req.flush(mockEspacos);
    });

    it('should handle empty list', (done) => {
      service.getAll().subscribe(espacos => {
        expect(espacos).toEqual([]);
        expect(espacos.length).toBe(0);
        done();
      });

      const req = httpMock.expectOne(apiUrl);
      req.flush([]);
    });

    it('should handle error', (done) => {
      service.getAll().subscribe({
        next: () => fail('should have failed'),
        error: (error) => {
          expect(error.status).toBe(500);
          done();
        }
      });

      const req = httpMock.expectOne(apiUrl);
      req.flush('Server error', { status: 500, statusText: 'Internal Server Error' });
    });
  });

  describe('getById()', () => {
    it('should retrieve a single espaco by id', (done) => {
      const espacoId = 1;

      service.getById(espacoId).subscribe(espaco => {
        expect(espaco).toEqual(mockEspacoResponse);
        expect(espaco.id).toBe(espacoId);
        done();
      });

      const req = httpMock.expectOne(`${apiUrl}/${espacoId}`);
      expect(req.request.method).toBe('GET');
      req.flush(mockEspacoResponse);
    });

    it('should handle not found error', (done) => {
      const espacoId = 999;

      service.getById(espacoId).subscribe({
        next: () => fail('should have failed'),
        error: (error) => {
          expect(error.status).toBe(404);
          done();
        }
      });

      const req = httpMock.expectOne(`${apiUrl}/${espacoId}`);
      req.flush('Not found', { status: 404, statusText: 'Not Found' });
    });
  });

  describe('getByFilialId()', () => {
    it('should retrieve espacos by filial id', (done) => {
      const filialId = 1;
      const mockEspacos = [mockEspacoResponse];

      service.getByFilialId(filialId).subscribe(espacos => {
        expect(espacos).toEqual(mockEspacos);
        expect(espacos[0].filial.id).toBe(filialId);
        done();
      });

      const req = httpMock.expectOne(`${apiUrl}/filial/${filialId}`);
      expect(req.request.method).toBe('GET');
      req.flush(mockEspacos);
    });

    it('should return empty array when filial has no espacos', (done) => {
      const filialId = 999;

      service.getByFilialId(filialId).subscribe(espacos => {
        expect(espacos).toEqual([]);
        done();
      });

      const req = httpMock.expectOne(`${apiUrl}/filial/${filialId}`);
      req.flush([]);
    });
  });

  describe('getAtivos()', () => {
    it('should retrieve only active espacos', (done) => {
      const activeEspacos = [
        { ...mockEspacoResponse, ativo: true },
        { ...mockEspacoResponse, id: 2, ativo: true }
      ];

      service.getAtivos().subscribe(espacos => {
        expect(espacos).toEqual(activeEspacos);
        expect(espacos.every(e => e.ativo)).toBe(true);
        done();
      });

      const req = httpMock.expectOne(`${apiUrl}/ativos`);
      expect(req.request.method).toBe('GET');
      req.flush(activeEspacos);
    });
  });

  describe('create()', () => {
    it('should create a new espaco', (done) => {
      service.create(mockEspacoRequest).subscribe(espaco => {
        expect(espaco).toEqual(mockEspacoResponse);
        expect(espaco.nome).toBe(mockEspacoRequest.nome);
        done();
      });

      const req = httpMock.expectOne(apiUrl);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(mockEspacoRequest);
      req.flush(mockEspacoResponse);
    });

    it('should handle validation error', (done) => {
      const invalidRequest = { ...mockEspacoRequest, nome: '' };

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
    it('should update an existing espaco', (done) => {
      const espacoId = 1;
      const updatedEspaco = { ...mockEspacoResponse, nome: 'Nome Atualizado' };

      service.update(espacoId, mockEspacoRequest).subscribe(espaco => {
        expect(espaco).toEqual(updatedEspaco);
        done();
      });

      const req = httpMock.expectOne(`${apiUrl}/${espacoId}`);
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(mockEspacoRequest);
      req.flush(updatedEspaco);
    });

    it('should handle not found on update', (done) => {
      const espacoId = 999;

      service.update(espacoId, mockEspacoRequest).subscribe({
        next: () => fail('should have failed'),
        error: (error) => {
          expect(error.status).toBe(404);
          done();
        }
      });

      const req = httpMock.expectOne(`${apiUrl}/${espacoId}`);
      req.flush('Not found', { status: 404, statusText: 'Not Found' });
    });
  });

  describe('delete()', () => {
    it('should delete an espaco', (done) => {
      const espacoId = 1;

      service.delete(espacoId).subscribe(response => {
        expect(response).toBeNull();
        done();
      });

      const req = httpMock.expectOne(`${apiUrl}/${espacoId}`);
      expect(req.request.method).toBe('DELETE');
      req.flush(null);
    });

    it('should handle conflict when deleting espaco with reservas', (done) => {
      const espacoId = 1;

      service.delete(espacoId).subscribe({
        next: () => fail('should have failed'),
        error: (error) => {
          expect(error.status).toBe(409);
          done();
        }
      });

      const req = httpMock.expectOne(`${apiUrl}/${espacoId}`);
      req.flush('Cannot delete espaco with reservas', { status: 409, statusText: 'Conflict' });
    });

    it('should handle not found on delete', (done) => {
      const espacoId = 999;

      service.delete(espacoId).subscribe({
        next: () => fail('should have failed'),
        error: (error) => {
          expect(error.status).toBe(404);
          done();
        }
      });

      const req = httpMock.expectOne(`${apiUrl}/${espacoId}`);
      req.flush('Not found', { status: 404, statusText: 'Not Found' });
    });
  });
});
