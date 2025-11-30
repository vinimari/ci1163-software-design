import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { FuncionarioRequest, FuncionarioResponse } from '../models/funcionario.model';

@Injectable({
  providedIn: 'root'
})
export class FuncionarioService {
  private apiUrl = `${environment.apiUrl}/funcionarios`;

  constructor(private http: HttpClient) {}

  findAll(filialId?: number): Observable<FuncionarioResponse[]> {
    let params = new HttpParams();
    if (filialId) {
      params = params.set('filialId', filialId.toString());
    }
    return this.http.get<FuncionarioResponse[]>(this.apiUrl, { params });
  }

  findById(id: number): Observable<FuncionarioResponse> {
    return this.http.get<FuncionarioResponse>(`${this.apiUrl}/${id}`);
  }

  create(funcionario: FuncionarioRequest): Observable<FuncionarioResponse> {
    return this.http.post<FuncionarioResponse>(this.apiUrl, funcionario);
  }

  update(id: number, funcionario: FuncionarioRequest): Observable<FuncionarioResponse> {
    return this.http.put<FuncionarioResponse>(`${this.apiUrl}/${id}`, funcionario);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  toggleAtivo(id: number, ativo: boolean): Observable<FuncionarioResponse> {
    return this.http.patch<FuncionarioResponse>(
      `${this.apiUrl}/${id}/ativo`,
      null,
      { params: { ativo: ativo.toString() } }
    );
  }

  trocarFilial(id: number, filialId: number): Observable<FuncionarioResponse> {
    return this.http.patch<FuncionarioResponse>(
      `${this.apiUrl}/${id}/filial`,
      null,
      { params: { filialId: filialId.toString() } }
    );
  }
}
