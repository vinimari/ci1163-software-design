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
      hasRole: jest.fn()
    };

    const routerMock = {
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
    authService.hasRole.mockReturnValue(true);
    const guard = roleGuard([PerfilUsuario.ADMIN]);

    const result = TestBed.runInInjectionContext(() =>
      guard({} as any, {} as any)
    );

    expect(result).toBe(true);
    expect(authService.hasRole).toHaveBeenCalledWith([PerfilUsuario.ADMIN]);
  });

  it('should redirect to unauthorized when user does not have required role', () => {
    authService.hasRole.mockReturnValue(false);
    const urlTree = {} as any;
    router.createUrlTree.mockReturnValue(urlTree);
    const guard = roleGuard([PerfilUsuario.ADMIN]);

    const result = TestBed.runInInjectionContext(() =>
      guard({} as any, {} as any)
    );

    expect(result).toBe(urlTree);
    expect(authService.hasRole).toHaveBeenCalledWith([PerfilUsuario.ADMIN]);
    expect(router.createUrlTree).toHaveBeenCalledWith(['/unauthorized']);
  });

  it('should work with multiple roles', () => {
    authService.hasRole.mockReturnValue(true);
    const guard = roleGuard([PerfilUsuario.ADMIN, PerfilUsuario.FUNCIONARIO]);

    const result = TestBed.runInInjectionContext(() =>
      guard({} as any, {} as any)
    );

    expect(result).toBe(true);
    expect(authService.hasRole).toHaveBeenCalledWith([PerfilUsuario.ADMIN, PerfilUsuario.FUNCIONARIO]);
  });
});
