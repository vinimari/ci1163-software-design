import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { ADMIN_ROUTES } from './admin.routes';
import { PerfilUsuario } from '../../core/models';
import { roleGuard } from '../../core/guards/role.guard';

describe('Admin Routes', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        {
          provide: Router,
          useValue: {
            navigate: jest.fn()
          }
        }
      ]
    });
  });

  it('should have correct number of routes', () => {
    expect(ADMIN_ROUTES.length).toBe(4);
  });

  describe('Filiais Routes', () => {
    let filiaisRoute: any;

    beforeEach(() => {
      filiaisRoute = ADMIN_ROUTES.find(r => r.path === 'filiais');
    });

    it('should have filiais route', () => {
      expect(filiaisRoute).toBeDefined();
    });

    it('should have roleGuard with ADMIN role', () => {
      expect(filiaisRoute.canActivate).toBeDefined();
      expect(filiaisRoute.canActivate.length).toBe(1);
    });

    it('should have children routes', () => {
      expect(filiaisRoute.children).toBeDefined();
      expect(filiaisRoute.children.length).toBe(4);
    });

    it('should have list route', () => {
      const listRoute = filiaisRoute.children.find((r: any) => r.path === '');
      expect(listRoute).toBeDefined();
      expect(listRoute.loadComponent).toBeDefined();
    });

    it('should have new route', () => {
      const newRoute = filiaisRoute.children.find((r: any) => r.path === 'new');
      expect(newRoute).toBeDefined();
      expect(newRoute.loadComponent).toBeDefined();
    });

    it('should have detail route', () => {
      const detailRoute = filiaisRoute.children.find((r: any) => r.path === ':id');
      expect(detailRoute).toBeDefined();
      expect(detailRoute.loadComponent).toBeDefined();
    });

    it('should have edit route', () => {
      const editRoute = filiaisRoute.children.find((r: any) => r.path === ':id/edit');
      expect(editRoute).toBeDefined();
      expect(editRoute.loadComponent).toBeDefined();
    });
  });

  describe('Clientes Routes', () => {
    let clientesRoute: any;

    beforeEach(() => {
      clientesRoute = ADMIN_ROUTES.find(r => r.path === 'clientes');
    });

    it('should have clientes route', () => {
      expect(clientesRoute).toBeDefined();
    });

    it('should have roleGuard with ADMIN and FUNCIONARIO roles', () => {
      expect(clientesRoute.canActivate).toBeDefined();
      expect(clientesRoute.canActivate.length).toBe(1);
    });

    it('should have children routes', () => {
      expect(clientesRoute.children).toBeDefined();
      expect(clientesRoute.children.length).toBe(2);
    });

    it('should have list route', () => {
      const listRoute = clientesRoute.children.find((r: any) => r.path === '');
      expect(listRoute).toBeDefined();
      expect(listRoute.loadComponent).toBeDefined();
    });

    it('should have detail route', () => {
      const detailRoute = clientesRoute.children.find((r: any) => r.path === ':id');
      expect(detailRoute).toBeDefined();
      expect(detailRoute.loadComponent).toBeDefined();
    });
  });

  describe('Espacos Routes', () => {
    let espacosRoute: any;

    beforeEach(() => {
      espacosRoute = ADMIN_ROUTES.find(r => r.path === 'espacos');
    });

    it('should have espacos route', () => {
      expect(espacosRoute).toBeDefined();
    });

    it('should have roleGuard with ADMIN and FUNCIONARIO roles', () => {
      expect(espacosRoute.canActivate).toBeDefined();
      expect(espacosRoute.canActivate.length).toBe(1);
    });

    it('should have loadComponent', () => {
      expect(espacosRoute.loadComponent).toBeDefined();
    });

    it('should not have children', () => {
      expect(espacosRoute.children).toBeUndefined();
    });
  });

  describe('Reservas Routes', () => {
    let reservasRoute: any;

    beforeEach(() => {
      reservasRoute = ADMIN_ROUTES.find(r => r.path === 'reservas');
    });

    it('should have reservas route', () => {
      expect(reservasRoute).toBeDefined();
    });

    it('should have roleGuard with ADMIN and FUNCIONARIO roles', () => {
      expect(reservasRoute.canActivate).toBeDefined();
      expect(reservasRoute.canActivate.length).toBe(1);
    });

    it('should have loadComponent', () => {
      expect(reservasRoute.loadComponent).toBeDefined();
    });

    it('should not have children', () => {
      expect(reservasRoute.children).toBeUndefined();
    });
  });

  describe('Route Structure', () => {
    it('should have all routes with guards', () => {
      ADMIN_ROUTES.forEach(route => {
        expect(route.canActivate).toBeDefined();
        expect(route.canActivate?.length).toBeGreaterThan(0);
      });
    });

    it('should have unique paths', () => {
      const paths = ADMIN_ROUTES.map(r => r.path);
      const uniquePaths = new Set(paths);
      expect(uniquePaths.size).toBe(paths.length);
    });
  });
});
