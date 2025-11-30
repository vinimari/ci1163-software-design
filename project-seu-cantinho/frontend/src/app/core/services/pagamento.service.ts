import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { PagamentoRequest, PagamentoResponse } from '../models';

@Injectable({
  providedIn: 'root'
})
export class PagamentoService {
  private readonly apiUrl = `${environment.apiUrl}/pagamentos`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<PagamentoResponse[]> {
    return this.http.get<PagamentoResponse[]>(this.apiUrl);
  }

  getById(id: number): Observable<PagamentoResponse> {
    return this.http.get<PagamentoResponse>(`${this.apiUrl}/${id}`);
  }

  getByReservaId(reservaId: number): Observable<PagamentoResponse[]> {
    return this.http.get<PagamentoResponse[]>(`${this.apiUrl}/reserva/${reservaId}`);
  }

  create(pagamento: PagamentoRequest): Observable<PagamentoResponse> {
    return this.http.post<PagamentoResponse>(this.apiUrl, pagamento);
  }

  update(id: number, pagamento: PagamentoRequest): Observable<PagamentoResponse> {
    return this.http.put<PagamentoResponse>(`${this.apiUrl}/${id}`, pagamento);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
