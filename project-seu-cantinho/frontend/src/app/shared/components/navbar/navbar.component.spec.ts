import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { NavbarComponent } from './navbar.component';
import { AuthService } from '../../../core/services/auth.service';
import { PerfilUsuario } from '../../../core/models';

describe('NavbarComponent', () => {
  let component: NavbarComponent;
  let fixture: ComponentFixture<NavbarComponent>;
  let authService: jest.Mocked<AuthService>;
  let router: jest.Mocked<Router>;

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
      currentUser$: of(null),
      isAdmin: jest.fn(),
      isFuncionario: jest.fn(),
      isCliente: jest.fn(),
      logout: jest.fn(),
      getCurrentUser: jest.fn(),
      isAuthenticated: jest.fn().mockReturnValue(false)
    };

    const routerMock = {
      navigate: jest.fn()
    };

    await TestBed.configureTestingModule({
      imports: [NavbarComponent, RouterTestingModule],
      providers: [
        { provide: AuthService, useValue: authServiceMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(NavbarComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService) as jest.Mocked<AuthService>;
    router = TestBed.inject(Router) as jest.Mocked<Router>;
  });

  describe('Component Initialization', () => {
    it('deve criar', () => {
      expect(component).toBeTruthy();
    });

    it('deve inicializar with null currentUser', () => {
      expect(component.currentUser).toBeNull();
    });

    it('deve inicializar isAdmin as false', () => {
      expect(component.isAdmin).toBe(false);
    });

    it('deve inicializar isFuncionario as false', () => {
      expect(component.isFuncionario).toBe(false);
    });

    it('deve inicializar isCliente as false', () => {
      expect(component.isCliente).toBe(false);
    });
  });

  describe('ngOnInit', () => {
    it('deve subscribe to currentUser$', () => {
      authService.currentUser$ = of(mockUser);
      authService.isAdmin.mockReturnValue(true);
      authService.isFuncionario.mockReturnValue(false);
      authService.isCliente.mockReturnValue(false);

      component.ngOnInit();

      expect(component.currentUser).toEqual(mockUser);
      expect(component.isAdmin).toBe(true);
    });

    it('deve atualizar user roles when user changes', () => {
      authService.currentUser$ = of(mockUser);
      authService.isAdmin.mockReturnValue(true);
      authService.isFuncionario.mockReturnValue(false);
      authService.isCliente.mockReturnValue(false);

      component.ngOnInit();

      expect(authService.isAdmin).toHaveBeenCalled();
      expect(authService.isFuncionario).toHaveBeenCalled();
      expect(authService.isCliente).toHaveBeenCalled();
    });

    it('deve definir isFuncionario when user is funcionario', () => {
      const funcionarioUser = { ...mockUser, perfil: PerfilUsuario.FUNCIONARIO };
      authService.currentUser$ = of(funcionarioUser);
      authService.isAdmin.mockReturnValue(false);
      authService.isFuncionario.mockReturnValue(true);
      authService.isCliente.mockReturnValue(false);

      component.ngOnInit();

      expect(component.isFuncionario).toBe(true);
      expect(component.isAdmin).toBe(false);
    });

    it('deve definir isCliente when user is cliente', () => {
      const clienteUser = { ...mockUser, perfil: PerfilUsuario.CLIENTE };
      authService.currentUser$ = of(clienteUser);
      authService.isAdmin.mockReturnValue(false);
      authService.isFuncionario.mockReturnValue(false);
      authService.isCliente.mockReturnValue(true);

      component.ngOnInit();

      expect(component.isCliente).toBe(true);
      expect(component.isAdmin).toBe(false);
    });

    it('deve handle null user', () => {
      authService.currentUser$ = of(null);
      authService.isAdmin.mockReturnValue(false);
      authService.isFuncionario.mockReturnValue(false);
      authService.isCliente.mockReturnValue(false);

      component.ngOnInit();

      expect(component.currentUser).toBeNull();
      expect(component.isAdmin).toBe(false);
    });
  });

  describe('ngOnDestroy', () => {
    it('deve complete destroy$ subject', () => {
      const nextSpy = jest.spyOn(component['destroy$'], 'next');
      const completeSpy = jest.spyOn(component['destroy$'], 'complete');

      component.ngOnDestroy();

      expect(nextSpy).toHaveBeenCalled();
      expect(completeSpy).toHaveBeenCalled();
    });

    it('deve unsubscribe from currentUser$', () => {
      component.ngOnInit();
      const destroySpy = jest.spyOn(component['destroy$'], 'next');

      component.ngOnDestroy();

      expect(destroySpy).toHaveBeenCalled();
    });
  });

  describe('logout', () => {
    it('deve call authService.logout', () => {
      component.logout();

      expect(authService.logout).toHaveBeenCalled();
    });

    it('deve navegar to login page', () => {
      const navigateSpy = jest.spyOn(component['router'], 'navigate');

      component.logout();

      expect(navigateSpy).toHaveBeenCalledWith(['/login']);
    });

    it('deve call logout and navigate in sequence', () => {
      const callOrder: string[] = [];
      
      authService.logout.mockImplementation(() => {
        callOrder.push('logout');
      });
      
      const navigateSpy = jest.spyOn(component['router'], 'navigate')
        .mockImplementation(() => {
          callOrder.push('navigate');
          return Promise.resolve(true);
        });

      component.logout();

      expect(callOrder).toEqual(['logout', 'navigate']);
    });
  });

  describe('Integration', () => {
    it('deve atualizar state when user logs in', () => {
      authService.currentUser$ = of(mockUser);
      authService.isAdmin.mockReturnValue(true);
      authService.isFuncionario.mockReturnValue(false);
      authService.isCliente.mockReturnValue(false);

      component.ngOnInit();
      fixture.detectChanges();

      expect(component.currentUser).toEqual(mockUser);
      expect(component.isAdmin).toBe(true);
    });

    it('deve atualizar state when user logs out', () => {
      authService.currentUser$ = of(null);
      authService.isAdmin.mockReturnValue(false);
      authService.isFuncionario.mockReturnValue(false);
      authService.isCliente.mockReturnValue(false);

      component.ngOnInit();
      fixture.detectChanges();

      expect(component.currentUser).toBeNull();
      expect(component.isAdmin).toBe(false);
    });
  });
});
