import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RESERVAS_ROUTES } from './reservas.routes';

describe('Reservas Routes', () => {
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

  it('deve ter correct number of routes', () => {
    expect(RESERVAS_ROUTES.length).toBe(3);
  });

  describe('List Route', () => {
    let listRoute: any;

    beforeEach(() => {
      listRoute = RESERVAS_ROUTES.find(r => r.path === '');
    });

    it('deve ter list route', () => {
      expect(listRoute).toBeDefined();
    });

    it('deve ter loadComponent for list', () => {
      expect(listRoute.loadComponent).toBeDefined();
    });

    it('deve load ReservasListComponent', async () => {
      const module = await listRoute.loadComponent();
      expect(module).toBeDefined();
    });
  });

  describe('New Reserva Route', () => {
    let newRoute: any;

    beforeEach(() => {
      newRoute = RESERVAS_ROUTES.find(r => r.path === 'nova');
    });

    it('deve ter new route', () => {
      expect(newRoute).toBeDefined();
    });

    it('deve ter loadComponent for new', () => {
      expect(newRoute.loadComponent).toBeDefined();
    });

    it('deve load ReservaFormComponent', async () => {
      const module = await newRoute.loadComponent();
      expect(module).toBeDefined();
    });
  });

  describe('Detail Route', () => {
    let detailRoute: any;

    beforeEach(() => {
      detailRoute = RESERVAS_ROUTES.find((r: any) => r.path === ':id');
    });

    it('deve ter detail route', () => {
      expect(detailRoute).toBeDefined();
    });

    it('deve ter loadComponent for detail', () => {
      expect(detailRoute.loadComponent).toBeDefined();
    });

    it('deve load ReservaDetailComponent', async () => {
      const module = await detailRoute.loadComponent();
      expect(module).toBeDefined();
    });
  });

  describe('Route Structure', () => {
    it('deve ter all routes with loadComponent', () => {
      RESERVAS_ROUTES.forEach(route => {
        expect(route.loadComponent).toBeDefined();
      });
    });

    it('deve ter unique paths', () => {
      const paths = RESERVAS_ROUTES.map(r => r.path);
      const uniquePaths = new Set(paths);
      expect(uniquePaths.size).toBe(paths.length);
    });

    it('não deve have guards on routes', () => {
      RESERVAS_ROUTES.forEach(route => {
        expect(route.canActivate).toBeUndefined();
      });
    });

    it('deve ter nova route for creating new reserva', () => {
      const novaRoute = RESERVAS_ROUTES.find(r => r.path === 'nova');
      expect(novaRoute).toBeDefined();
    });
  });
});
