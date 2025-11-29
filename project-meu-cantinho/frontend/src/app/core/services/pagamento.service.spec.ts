import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { PagamentoService } from './pagamento.service';
import { PagamentoRequest, PagamentoResponse, TipoPagamento } from '../models';
import { environment } from '../../../environments/environment';

describe('PagamentoService', () => {
  let service: PagamentoService;
  let httpMock: HttpTestingController;
  const apiUrl = `${environment.apiUrl}/pagamentos`;

  const mockPagamentoResponse: PagamentoResponse = {
    id: 1,
    reservaId: 1,
    valor: 500.00,
    dataPagamento: '2024-01-15',
    tipo: TipoPagamento.TOTAL,
    formaPagamento: 'Cartão de Crédito'
  };

  const mockPagamentoRequest: PagamentoRequest = {
    reservaId: 1,
    valor: 500.00,
    tipo: TipoPagamento.TOTAL,
    formaPagamento: 'Cartão de Crédito'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [PagamentoService]
    });
    service = TestBed.inject(PagamentoService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('deve ser created', () => {
    expect(service).toBeTruthy();
  });

  describe('getAll', () => {
    it('deve fetch all pagamentos', () => {
      const mockPagamentos = [mockPagamentoResponse];

      service.getAll().subscribe(pagamentos => {
        expect(pagamentos).toEqual(mockPagamentos);
        expect(pagamentos.length).toBe(1);
      });

      const req = httpMock.expectOne(apiUrl);
      expect(req.request.method).toBe('GET');
      req.flush(mockPagamentos);
    });

    it('deve retornar empty array when no pagamentos', () => {
      service.getAll().subscribe(pagamentos => {
        expect(pagamentos).toEqual([]);
        expect(pagamentos.length).toBe(0);
      });

      const req = httpMock.expectOne(apiUrl);
      req.flush([]);
    });
  });

  describe('getById', () => {
    it('deve fetch pagamento by id', () => {
      const id = 1;

      service.getById(id).subscribe(pagamento => {
        expect(pagamento).toEqual(mockPagamentoResponse);
        expect(pagamento.id).toBe(id);
      });

      const req = httpMock.expectOne(`${apiUrl}/${id}`);
      expect(req.request.method).toBe('GET');
      req.flush(mockPagamentoResponse);
    });

    it('deve tratar erro when pagamento not found', () => {
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

  describe('getByReservaId', () => {
    it('deve fetch pagamentos by reserva id', () => {
      const reservaId = 1;
      const mockPagamentos = [mockPagamentoResponse];

      service.getByReservaId(reservaId).subscribe(pagamentos => {
        expect(pagamentos).toEqual(mockPagamentos);
        expect(pagamentos[0].reservaId).toBe(reservaId);
      });

      const req = httpMock.expectOne(`${apiUrl}/reserva/${reservaId}`);
      expect(req.request.method).toBe('GET');
      req.flush(mockPagamentos);
    });

    it('deve retornar empty array when no pagamentos for reserva', () => {
      const reservaId = 999;

      service.getByReservaId(reservaId).subscribe(pagamentos => {
        expect(pagamentos).toEqual([]);
      });

      const req = httpMock.expectOne(`${apiUrl}/reserva/${reservaId}`);
      req.flush([]);
    });
  });

  describe('create', () => {
    it('deve criar a new pagamento', () => {
      service.create(mockPagamentoRequest).subscribe(pagamento => {
        expect(pagamento).toEqual(mockPagamentoResponse);
        expect(pagamento.valor).toBe(mockPagamentoRequest.valor);
      });

      const req = httpMock.expectOne(apiUrl);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(mockPagamentoRequest);
      req.flush(mockPagamentoResponse);
    });

    it('deve handle validation errors', () => {
      service.create(mockPagamentoRequest).subscribe(
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
    it('deve atualizar an existing pagamento', () => {
      const id = 1;
      const updatedPagamento = { ...mockPagamentoResponse, valor: 600.00 };

      service.update(id, mockPagamentoRequest).subscribe(pagamento => {
        expect(pagamento).toEqual(updatedPagamento);
      });

      const req = httpMock.expectOne(`${apiUrl}/${id}`);
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(mockPagamentoRequest);
      req.flush(updatedPagamento);
    });

    it('deve tratar erro when updating non-existent pagamento', () => {
      const id = 999;

      service.update(id, mockPagamentoRequest).subscribe(
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
    it('deve delete a pagamento', () => {
      const id = 1;

      service.delete(id).subscribe(response => {
        expect(response).toBeUndefined();
      });

      const req = httpMock.expectOne(`${apiUrl}/${id}`);
      expect(req.request.method).toBe('DELETE');
      req.flush(null);
    });

    it('deve tratar erro when deleting non-existent pagamento', () => {
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
