import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AuthService } from './auth.service';
import { LoginRequest, LoginResponse, UsuarioResponse } from '../models';
import { PerfilUsuario } from '../models/enums';
import { environment } from '../../../environments/environment';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  const mockLoginRequest: LoginRequest = {
    email: 'admin@test.com',
    senha: 'password123'
  };

  const mockLoginResponse: LoginResponse = {
    id: 1,
    nome: 'Admin User',
    email: 'admin@test.com',
    perfil: PerfilUsuario.ADMIN,
    token: 'mock-jwt-token'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
    localStorage.clear();
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  describe('Constructor and Initialization', () => {
    it('should be created', () => {
      expect(service).toBeTruthy();
    });
  });  describe('login()', () => {
    it('should authenticate user and store credentials', (done) => {
      service.login(mockLoginRequest).subscribe(response => {
        expect(response).toEqual(mockLoginResponse);
        expect(localStorage.getItem('token')).toBe(mockLoginResponse.token);

        const storedUser = JSON.parse(localStorage.getItem('user')!);
        expect(storedUser.id).toBe(mockLoginResponse.id);
        expect(storedUser.nome).toBe(mockLoginResponse.nome);
        expect(storedUser.email).toBe(mockLoginResponse.email);
        expect(storedUser.perfil).toBe(mockLoginResponse.perfil);

        done();
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/auth/login`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(mockLoginRequest);
      req.flush(mockLoginResponse);
    });

    it('should update currentUser$ observable on login', (done) => {
      service.login(mockLoginRequest).subscribe(() => {
        service.currentUser$.subscribe(user => {
          if (user) {
            expect(user.id).toBe(mockLoginResponse.id);
            expect(user.perfil).toBe(mockLoginResponse.perfil);
            done();
          }
        });
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/auth/login`);
      req.flush(mockLoginResponse);
    });

    it('should handle login error', (done) => {
      const errorResponse = { status: 401, statusText: 'Unauthorized' };

      service.login(mockLoginRequest).subscribe({
        next: () => fail('should have failed with 401 error'),
        error: (error) => {
          expect(error.status).toBe(401);
          done();
        }
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/auth/login`);
      req.flush('Invalid credentials', errorResponse);
    });
  });

  describe('logout()', () => {
    beforeEach(() => {
      localStorage.setItem('token', 'test-token');
      localStorage.setItem('user', JSON.stringify({
        id: 1,
        nome: 'Test',
        email: 'test@test.com',
        perfil: PerfilUsuario.CLIENTE,
        ativo: true,
        dataCadastro: '2025-01-01T00:00:00Z'
      }));
    });

    it('should clear localStorage', () => {
      service.logout();
      expect(localStorage.getItem('token')).toBeNull();
      expect(localStorage.getItem('user')).toBeNull();
    });

    it('should set currentUser$ to null', (done) => {
      service.logout();
      service.currentUser$.subscribe(user => {
        expect(user).toBeNull();
        done();
      });
    });
  });

  describe('isAuthenticated()', () => {
    it('should return true when token exists', () => {
      localStorage.setItem('token', 'test-token');
      expect(service.isAuthenticated()).toBe(true);
    });

    it('should return false when token does not exist', () => {
      localStorage.removeItem('token');
      expect(service.isAuthenticated()).toBe(false);
    });
  });

  describe('getToken()', () => {
    it('should return token from localStorage', () => {
      const testToken = 'test-jwt-token';
      localStorage.setItem('token', testToken);
      expect(service.getToken()).toBe(testToken);
    });

    it('should return null when no token exists', () => {
      localStorage.removeItem('token');
      expect(service.getToken()).toBeNull();
    });
  });

  describe('getCurrentUser()', () => {
    it('should return current user after login', (done) => {
      service.login(mockLoginRequest).subscribe(() => {
        const user = service.getCurrentUser();
        expect(user).toBeTruthy();
        expect(user?.id).toBe(mockLoginResponse.id);
        expect(user?.perfil).toBe(mockLoginResponse.perfil);
        done();
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/auth/login`);
      req.flush(mockLoginResponse);
    });

    it('should return null when no user exists', () => {
      localStorage.removeItem('user');
      const user = service.getCurrentUser();
      expect(user).toBeNull();
    });
  });  describe('Role Check Methods', () => {
    beforeEach(() => {
      localStorage.clear();
    });

    it('isAdmin() should return true for ADMIN user', (done) => {
      const adminResponse: LoginResponse = {
        id: 1,
        nome: 'Admin',
        email: 'admin@test.com',
        perfil: PerfilUsuario.ADMIN,
        token: 'token123'
      };

      service.login(mockLoginRequest).subscribe(() => {
        expect(service.isAdmin()).toBe(true);
        done();
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/auth/login`);
      req.flush(adminResponse);
    });

    it('isAdmin() should return false for non-ADMIN user', (done) => {
      const clienteResponse: LoginResponse = {
        id: 1,
        nome: 'Cliente',
        email: 'cliente@test.com',
        perfil: PerfilUsuario.CLIENTE,
        token: 'token123'
      };

      service.login(mockLoginRequest).subscribe(() => {
        expect(service.isAdmin()).toBe(false);
        done();
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/auth/login`);
      req.flush(clienteResponse);
    });

    it('isFuncionario() should return true for FUNCIONARIO user', (done) => {
      const funcResponse: LoginResponse = {
        id: 1,
        nome: 'Funcionario',
        email: 'func@test.com',
        perfil: PerfilUsuario.FUNCIONARIO,
        token: 'token123'
      };

      service.login(mockLoginRequest).subscribe(() => {
        expect(service.isFuncionario()).toBe(true);
        done();
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/auth/login`);
      req.flush(funcResponse);
    });

    it('isCliente() should return true for CLIENTE user', (done) => {
      const clienteResponse: LoginResponse = {
        id: 1,
        nome: 'Cliente',
        email: 'cliente@test.com',
        perfil: PerfilUsuario.CLIENTE,
        token: 'token123'
      };

      service.login(mockLoginRequest).subscribe(() => {
        expect(service.isCliente()).toBe(true);
        done();
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/auth/login`);
      req.flush(clienteResponse);
    });

    it('hasRole() should return true for matching role', (done) => {
      const adminResponse: LoginResponse = {
        id: 1,
        nome: 'Admin',
        email: 'admin@test.com',
        perfil: PerfilUsuario.ADMIN,
        token: 'token123'
      };

      service.login(mockLoginRequest).subscribe(() => {
        expect(service.hasRole(['ADMIN'])).toBe(true);
        expect(service.hasRole(['CLIENTE'])).toBe(false);
        done();
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/auth/login`);
      req.flush(adminResponse);
    });

    it('hasRole() should handle array of roles', (done) => {
      const funcResponse: LoginResponse = {
        id: 1,
        nome: 'Funcionario',
        email: 'func@test.com',
        perfil: PerfilUsuario.FUNCIONARIO,
        token: 'token123'
      };

      service.login(mockLoginRequest).subscribe(() => {
        expect(service.hasRole(['ADMIN', 'FUNCIONARIO'])).toBe(true);
        expect(service.hasRole(['ADMIN', 'CLIENTE'])).toBe(false);
        done();
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/auth/login`);
      req.flush(funcResponse);
    });
  });
});
