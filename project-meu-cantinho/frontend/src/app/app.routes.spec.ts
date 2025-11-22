import { TestBed } from '@angular/core/testing';
import { Router, provideRouter } from '@angular/router';
import { Location } from '@angular/common';
import { routes } from './app.routes';
import { AuthService } from './core/services/auth.service';
import { of } from 'rxjs';
import { PerfilUsuario } from './core/models';

describe('App Routes', () => {
  let router: Router;
  let location: Location;
  let authService: jest.Mocked<AuthService>;

  const mockUser = {
    id: 1,
    nome: 'Test User',
    email: 'test@example.com',
    perfil: PerfilUsuario.ADMIN,
    ativo: true,
    dataCadastro: '2024-01-01',
    token: 'test-token'
  };

  beforeEach(async () => {
    const authServiceMock = {
      currentUser$: of(mockUser),
      getCurrentUser: jest.fn().mockReturnValue(mockUser),
      isAuthenticated: jest.fn().mockReturnValue(true),
      isAdmin: jest.fn().mockReturnValue(true),
      isFuncionario: jest.fn().mockReturnValue(false),
      isCliente: jest.fn().mockReturnValue(false),
      logout: jest.fn()
    };

    await TestBed.configureTestingModule({
      providers: [
        provideRouter(routes),
        { provide: AuthService, useValue: authServiceMock }
      ]
    });

    router = TestBed.inject(Router);
    location = TestBed.inject(Location);
    authService = TestBed.inject(AuthService) as jest.Mocked<AuthService>;
  });

  describe('Public Routes', () => {
    it('should have login route', () => {
      const route = routes.find(r => r.path === 'login');
      expect(route).toBeDefined();
      expect(route?.component?.name).toBe('LoginComponent');
    });

    it('should have unauthorized route', () => {
      const route = routes.find(r => r.path === 'unauthorized');
      expect(route).toBeDefined();
      expect(route?.component?.name).toBe('UnauthorizedComponent');
    });

    it('should redirect unknown routes to home', () => {
      const route = routes.find(r => r.path === '**');
      expect(route).toBeDefined();
      expect(route?.redirectTo).toBe('');
    });
  });

  describe('Protected Routes', () => {
    it('should have layout component with auth guard', () => {
      const route = routes.find(r => r.path === '' && r.component);
      expect(route).toBeDefined();
      expect(route?.component?.name).toBe('LayoutComponent');
      expect(route?.canActivate).toBeDefined();
      expect(route?.canActivate?.length).toBeGreaterThan(0);
    });

    it('should have home route as child of layout', () => {
      const layoutRoute = routes.find(r => r.path === '' && r.component);
      const homeRoute = layoutRoute?.children?.find(r => r.path === '');
      expect(homeRoute).toBeDefined();
      expect(homeRoute?.component?.name).toBe('HomeComponent');
    });

    it('should have espacos route with lazy loading', () => {
      const layoutRoute = routes.find(r => r.path === '' && r.component);
      const espacosRoute = layoutRoute?.children?.find(r => r.path === 'espacos');
      expect(espacosRoute).toBeDefined();
      expect(espacosRoute?.loadChildren).toBeDefined();
    });

    it('should have reservas route with role guard for CLIENTE', () => {
      const layoutRoute = routes.find(r => r.path === '' && r.component);
      const reservasRoute = layoutRoute?.children?.find(r => r.path === 'reservas');
      expect(reservasRoute).toBeDefined();
      expect(reservasRoute?.canActivate).toBeDefined();
      expect(reservasRoute?.loadChildren).toBeDefined();
    });
  });

  describe('Admin Routes', () => {
    it('should have admin route with role guard', () => {
      const layoutRoute = routes.find(r => r.path === '' && r.component);
      const adminRoute = layoutRoute?.children?.find(r => r.path === 'admin');
      expect(adminRoute).toBeDefined();
      expect(adminRoute?.canActivate).toBeDefined();
      expect(adminRoute?.loadChildren).toBeDefined();
    });
  });

  describe('Funcionario Routes', () => {
    it('should have funcionario route with role guard', () => {
      const layoutRoute = routes.find(r => r.path === '' && r.component);
      const funcRoute = layoutRoute?.children?.find(r => r.path === 'funcionario');
      expect(funcRoute).toBeDefined();
      expect(funcRoute?.canActivate).toBeDefined();
      expect(funcRoute?.children).toBeDefined();
    });

    it('should have espacos as child of funcionario route', () => {
      const layoutRoute = routes.find(r => r.path === '' && r.component);
      const funcRoute = layoutRoute?.children?.find(r => r.path === 'funcionario');
      const espacosRoute = funcRoute?.children?.find(r => r.path === 'espacos');
      expect(espacosRoute).toBeDefined();
      expect(espacosRoute?.loadComponent).toBeDefined();
    });

    it('should have clientes as child of funcionario route', () => {
      const layoutRoute = routes.find(r => r.path === '' && r.component);
      const funcRoute = layoutRoute?.children?.find(r => r.path === 'funcionario');
      const clientesRoute = funcRoute?.children?.find(r => r.path === 'clientes');
      expect(clientesRoute).toBeDefined();
      expect(clientesRoute?.loadComponent).toBeDefined();
    });

    it('should have reservas as child of funcionario route', () => {
      const layoutRoute = routes.find(r => r.path === '' && r.component);
      const funcRoute = layoutRoute?.children?.find(r => r.path === 'funcionario');
      const reservasRoute = funcRoute?.children?.find(r => r.path === 'reservas');
      expect(reservasRoute).toBeDefined();
      expect(reservasRoute?.loadComponent).toBeDefined();
    });

    it('should have pagamentos as child of funcionario route', () => {
      const layoutRoute = routes.find(r => r.path === '' && r.component);
      const funcRoute = layoutRoute?.children?.find(r => r.path === 'funcionario');
      const pagamentosRoute = funcRoute?.children?.find(r => r.path === 'pagamentos');
      expect(pagamentosRoute).toBeDefined();
      expect(pagamentosRoute?.loadComponent).toBeDefined();
    });
  });

  describe('Route Structure', () => {
    it('should have correct number of top-level routes', () => {
      expect(routes.length).toBe(4);
    });

    it('should have layout route with children', () => {
      const layoutRoute = routes.find(r => r.path === '' && r.component);
      expect(layoutRoute?.children).toBeDefined();
      expect(layoutRoute?.children?.length).toBeGreaterThan(0);
    });
  });
});
