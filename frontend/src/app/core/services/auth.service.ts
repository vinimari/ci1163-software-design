import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { LoginRequest, LoginResponse, UsuarioResponse } from '../models';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly apiUrl = `${environment.apiUrl}/auth`;
  private currentUserSubject = new BehaviorSubject<UsuarioResponse | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {
    this.loadUserFromStorage();
  }

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap(response => {
        this.setSession(response);
      })
    );
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    this.currentUserSubject.next(null);
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  getCurrentUser(): UsuarioResponse | null {
    return this.currentUserSubject.value;
  }

  isAdmin(): boolean {
    return this.currentUserSubject.value?.perfil === 'ADMIN';
  }

  isFuncionario(): boolean {
    return this.currentUserSubject.value?.perfil === 'FUNCIONARIO';
  }

  isCliente(): boolean {
    return this.currentUserSubject.value?.perfil === 'CLIENTE';
  }

  hasRole(roles: string[]): boolean {
    const userPerfil = this.currentUserSubject.value?.perfil;
    return userPerfil ? roles.includes(userPerfil) : false;
  }

  private setSession(response: LoginResponse): void {
    localStorage.setItem('token', response.token);
    const user: UsuarioResponse = {
      id: response.id,
      nome: response.nome,
      email: response.email,
      perfil: response.perfil,
      ativo: true,
      dataCadastro: new Date().toISOString()
    };
    localStorage.setItem('user', JSON.stringify(user));
    this.currentUserSubject.next(user);
  }

  private loadUserFromStorage(): void {
    const userJson = localStorage.getItem('user');
    if (userJson) {
      try {
        const user = JSON.parse(userJson);
        this.currentUserSubject.next(user);
      } catch (error) {
        console.error('Error parsing user from localStorage', error);
      }
    }
  }
}
