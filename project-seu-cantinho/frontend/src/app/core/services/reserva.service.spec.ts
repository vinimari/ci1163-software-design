import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ReservaService } from './reserva.service';
import { ReservaRequest, ReservaResponse, StatusReserva } from '../models';
import { environment } from '../../../environments/environment';

describe('ReservaService', () => {
  let service: ReservaService;
  let httpMock: HttpTestingController;
  const apiUrl = `${environment.apiUrl}/reservas`;

  const mockReservaResponse: ReservaResponse = {
    id: 1,
    usuarioId: 1,
    espacoId: 1,
    dataInicio: '2024-01-20',
    dataFim: '2024-01-21',
    status: StatusReserva.CONFIRMADA,
    valorTotal: 500.00,
    observacoes: 'Reserva para evento'
  };

  const mockReservaRequest: ReservaRequest = {
    usuarioId: 1,
    espacoId: 1,
    dataInicio: '2024-01-20',
    dataFim: '2024-01-21',
    status: StatusReserva.CONFIRMADA,
    valorTotal: 500.00,
    observacoes: 'Reserva para evento'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ReservaService]
    });
    service = TestBed.inject(ReservaService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('deve ser created', () => {
    expect(service).toBeTruthy();
  });

  describe('getAll', () => {
    it('deve fetch all reservas', () => {
      const mockReservas = [mockReservaResponse];

      service.getAll().subscribe(reservas => {
        expect(reservas).toEqual(mockReservas);
        expect(reservas.length).toBe(1);
      });

      const req = httpMock.expectOne(apiUrl);
      expect(req.request.method).toBe('GET');
      req.flush(mockReservas);
    });

    it('deve retornar empty array when no reservas', () => {
      service.getAll().subscribe(reservas => {
        expect(reservas).toEqual([]);
        expect(reservas.length).toBe(0);
      });

      const req = httpMock.expectOne(apiUrl);
      req.flush([]);
    });
  });

  describe('getById', () => {
    it('deve fetch reserva by id', () => {
      const id = 1;

      service.getById(id).subscribe(reserva => {
        expect(reserva).toEqual(mockReservaResponse);
        expect(reserva.id).toBe(id);
      });

      const req = httpMock.expectOne(`${apiUrl}/${id}`);
      expect(req.request.method).toBe('GET');
      req.flush(mockReservaResponse);
    });

    it('deve tratar erro when reserva not found', () => {
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

  describe('getByUsuarioId', () => {
    it('deve fetch reservas by usuario id', () => {
      const usuarioId = 1;
      const mockReservas = [mockReservaResponse];

      service.getByUsuarioId(usuarioId).subscribe(reservas => {
        expect(reservas).toEqual(mockReservas);
        expect(reservas[0].usuarioId).toBe(usuarioId);
      });

      const req = httpMock.expectOne(`${apiUrl}/usuario/${usuarioId}`);
      expect(req.request.method).toBe('GET');
      req.flush(mockReservas);
    });

    it('deve retornar empty array when no reservas for usuario', () => {
      const usuarioId = 999;

      service.getByUsuarioId(usuarioId).subscribe(reservas => {
        expect(reservas).toEqual([]);
      });

      const req = httpMock.expectOne(`${apiUrl}/usuario/${usuarioId}`);
      req.flush([]);
    });
  });

  describe('getByEspacoId', () => {
    it('deve fetch reservas by espaco id', () => {
      const espacoId = 1;
      const mockReservas = [mockReservaResponse];

      service.getByEspacoId(espacoId).subscribe(reservas => {
        expect(reservas).toEqual(mockReservas);
        expect(reservas[0].espacoId).toBe(espacoId);
      });

      const req = httpMock.expectOne(`${apiUrl}/espaco/${espacoId}`);
      expect(req.request.method).toBe('GET');
      req.flush(mockReservas);
    });

    it('deve retornar empty array when no reservas for espaco', () => {
      const espacoId = 999;

      service.getByEspacoId(espacoId).subscribe(reservas => {
        expect(reservas).toEqual([]);
      });

      const req = httpMock.expectOne(`${apiUrl}/espaco/${espacoId}`);
      req.flush([]);
    });
  });

  describe('create', () => {
    it('deve criar a new reserva', () => {
      service.create(mockReservaRequest).subscribe(reserva => {
        expect(reserva).toEqual(mockReservaResponse);
        expect(reserva.valorTotal).toBe(mockReservaRequest.valorTotal);
      });

      const req = httpMock.expectOne(apiUrl);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(mockReservaRequest);
      req.flush(mockReservaResponse);
    });

    it('deve handle validation errors', () => {
      service.create(mockReservaRequest).subscribe(
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
    it('deve atualizar an existing reserva', () => {
      const id = 1;
      const updatedReserva = { ...mockReservaResponse, valorTotal: 600.00 };

      service.update(id, mockReservaRequest).subscribe(reserva => {
        expect(reserva).toEqual(updatedReserva);
      });

      const req = httpMock.expectOne(`${apiUrl}/${id}`);
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(mockReservaRequest);
      req.flush(updatedReserva);
    });

    it('deve tratar erro when updating non-existent reserva', () => {
      const id = 999;

      service.update(id, mockReservaRequest).subscribe(
        () => fail('should have failed'),
        error => {
          expect(error.status).toBe(404);
        }
      );

      const req = httpMock.expectOne(`${apiUrl}/${id}`);
      req.flush('Not found', { status: 404, statusText: 'Not Found' });
    });
  });

  describe('updateStatus', () => {
    it('deve atualizar reserva status', () => {
      const id = 1;
      const newStatus = StatusReserva.CANCELADA;
      const updatedReserva = { ...mockReservaResponse, status: newStatus };

      service.updateStatus(id, newStatus).subscribe(reserva => {
        expect(reserva.status).toBe(newStatus);
      });

      const req = httpMock.expectOne(`${apiUrl}/${id}/status?status=${newStatus}`);
      expect(req.request.method).toBe('PATCH');
      expect(req.request.body).toBeNull();
      req.flush(updatedReserva);
    });

    it('deve tratar erro when updating status of non-existent reserva', () => {
      const id = 999;
      const newStatus = StatusReserva.CANCELADA;

      service.updateStatus(id, newStatus).subscribe(
        () => fail('should have failed'),
        error => {
          expect(error.status).toBe(404);
        }
      );

      const req = httpMock.expectOne(`${apiUrl}/${id}/status?status=${newStatus}`);
      req.flush('Not found', { status: 404, statusText: 'Not Found' });
    });
  });

  describe('delete', () => {
    it('deve delete a reserva', () => {
      const id = 1;

      service.delete(id).subscribe(response => {
        expect(response).toBeUndefined();
      });

      const req = httpMock.expectOne(`${apiUrl}/${id}`);
      expect(req.request.method).toBe('DELETE');
      req.flush(null);
    });

    it('deve tratar erro when deleting non-existent reserva', () => {
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
