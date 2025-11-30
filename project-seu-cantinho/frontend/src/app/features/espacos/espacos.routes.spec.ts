import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { ESPACOS_ROUTES } from './espacos.routes';

describe('Espacos Routes', () => {
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
    expect(ESPACOS_ROUTES.length).toBe(2);
  });

  describe('List Route', () => {
    let listRoute: any;

    beforeEach(() => {
      listRoute = ESPACOS_ROUTES.find(r => r.path === '');
    });

    it('deve ter list route', () => {
      expect(listRoute).toBeDefined();
    });

    it('deve ter loadComponent for list', () => {
      expect(listRoute.loadComponent).toBeDefined();
    });

    it('deve load EspacosListComponent', async () => {
      const module = await listRoute.loadComponent();
      expect(module).toBeDefined();
    });
  });

  describe('Detail Route', () => {
    let detailRoute: any;

    beforeEach(() => {
      detailRoute = ESPACOS_ROUTES.find((r: any) => r.path === ':id');
    });

    it('deve ter detail route', () => {
      expect(detailRoute).toBeDefined();
    });

    it('deve tar loadComponent for detail', () => {
      expect(detailRoute.loadComponent).toBeDefined();
    });

    it('deve load EspacoDetailComponent', async () => {
      const module = await detailRoute.loadComponent();
      expect(module).toBeDefined();
    });
  });

  describe('Route Structure', () => {
    it('deve ter all routes with loadComponent', () => {
      ESPACOS_ROUTES.forEach(route => {
        expect(route.loadComponent).toBeDefined();
      });
    });

    it('deve ter unique paths', () => {
      const paths = ESPACOS_ROUTES.map(r => r.path);
      const uniquePaths = new Set(paths);
      expect(uniquePaths.size).toBe(paths.length);
    });

    it('não deve have guards on routes', () => {
      ESPACOS_ROUTES.forEach(route => {
        expect(route.canActivate).toBeUndefined();
      });
    });
  });
});
