import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { EspacoRequest, EspacoResponse } from '../models';

@Injectable({
  providedIn: 'root'
})
export class EspacoService {
  private readonly apiUrl = `${environment.apiUrl}/espacos`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<EspacoResponse[]> {
    return this.http.get<EspacoResponse[]>(this.apiUrl);
  }

  getById(id: number): Observable<EspacoResponse> {
    return this.http.get<EspacoResponse>(`${this.apiUrl}/${id}`);
  }

  getByFilialId(filialId: number): Observable<EspacoResponse[]> {
    return this.http.get<EspacoResponse[]>(`${this.apiUrl}/filial/${filialId}`);
  }

  getAtivos(): Observable<EspacoResponse[]> {
    return this.http.get<EspacoResponse[]>(`${this.apiUrl}/ativos`);
  }

  getDisponiveis(data: string): Observable<EspacoResponse[]> {
    return this.http.get<EspacoResponse[]>(`${this.apiUrl}/disponiveis`, {
      params: { data }
    });
  }

  create(espaco: EspacoRequest): Observable<EspacoResponse> {
    return this.http.post<EspacoResponse>(this.apiUrl, espaco);
  }

  update(id: number, espaco: EspacoRequest): Observable<EspacoResponse> {
    return this.http.put<EspacoResponse>(`${this.apiUrl}/${id}`, espaco);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
