import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { roleGuard } from './role.guard';
import { AuthService } from '../services/auth.service';
import { PerfilUsuario } from '../models/enums';

describe('roleGuard', () => {
  let authService: jest.Mocked<AuthService>;
  let router: jest.Mocked<Router>;

  beforeEach(() => {
    const authServiceMock = {
      hasRole: jest.fn(),
      getCurrentUser: jest.fn()
    };

    const routerMock = {
      navigate: jest.fn(),
      createUrlTree: jest.fn()
    };

    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        { provide: Router, useValue: routerMock }
      ]
    });

    authService = TestBed.inject(AuthService) as jest.Mocked<AuthService>;
    router = TestBed.inject(Router) as jest.Mocked<Router>;
  });

  it('should allow access when user has required role', () => {
    authService.getCurrentUser.mockReturnValue({ 
      id: 1, 
      nome: 'Admin', 
      email: 'admin@test.com', 
      perfil: PerfilUsuario.ADMIN,
      ativo: true,
      dataCadastro: '2025-01-01'
    });
    const guard = roleGuard([PerfilUsuario.ADMIN]);

    const result = TestBed.runInInjectionContext(() =>
      guard({} as any, {} as any)
    );

    expect(result).toBe(true);
    expect(authService.getCurrentUser).toHaveBeenCalled();
  });

  it('should redirect to unauthorized when user does not have required role', () => {
    authService.getCurrentUser.mockReturnValue({ 
      id: 1, 
      nome: 'Cliente', 
      email: 'cliente@test.com', 
      perfil: PerfilUsuario.CLIENTE,
      ativo: true,
      dataCadastro: '2025-01-01'
    });
    router.navigate.mockReturnValue(Promise.resolve(true));
    const guard = roleGuard([PerfilUsuario.ADMIN]);

    const result = TestBed.runInInjectionContext(() =>
      guard({} as any, {} as any)
    );

    expect(result).toBe(false);
    expect(authService.getCurrentUser).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/unauthorized']);
  });

  it('should work with multiple roles', () => {
    authService.getCurrentUser.mockReturnValue({ 
      id: 1, 
      nome: 'Funcionario', 
      email: 'func@test.com', 
      perfil: PerfilUsuario.FUNCIONARIO,
      ativo: true,
      dataCadastro: '2025-01-01'
    });
    const guard = roleGuard([PerfilUsuario.ADMIN, PerfilUsuario.FUNCIONARIO]);

    const result = TestBed.runInInjectionContext(() =>
      guard({} as any, {} as any)
    );

    expect(result).toBe(true);
    expect(authService.getCurrentUser).toHaveBeenCalled();
  });

  it('should redirect to login when user is not logged in', () => {
    authService.getCurrentUser.mockReturnValue(null);
    router.navigate.mockReturnValue(Promise.resolve(true));
    const guard = roleGuard([PerfilUsuario.ADMIN]);

    const result = TestBed.runInInjectionContext(() =>
      guard({} as any, {} as any)
    );

    expect(result).toBe(false);
    expect(authService.getCurrentUser).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/login']);
  });
});