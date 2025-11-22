import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { FilialRequest, FilialResponse } from '../models';

@Injectable({
  providedIn: 'root'
})
export class FilialService {
  private readonly apiUrl = `${environment.apiUrl}/filiais`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<FilialResponse[]> {
    return this.http.get<FilialResponse[]>(this.apiUrl);
  }

  getById(id: number): Observable<FilialResponse> {
    return this.http.get<FilialResponse>(`${this.apiUrl}/${id}`);
  }

  getAtivas(): Observable<FilialResponse[]> {
    return this.http.get<FilialResponse[]>(`${this.apiUrl}/ativas`);
  }

  create(filial: FilialRequest): Observable<FilialResponse> {
    return this.http.post<FilialResponse>(this.apiUrl, filial);
  }

  update(id: number, filial: FilialRequest): Observable<FilialResponse> {
    return this.http.put<FilialResponse>(`${this.apiUrl}/${id}`, filial);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
