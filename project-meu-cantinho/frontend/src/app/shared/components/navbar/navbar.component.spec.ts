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
    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should initialize with null currentUser', () => {
      expect(component.currentUser).toBeNull();
    });

    it('should initialize isAdmin as false', () => {
      expect(component.isAdmin).toBe(false);
    });

    it('should initialize isFuncionario as false', () => {
      expect(component.isFuncionario).toBe(false);
    });

    it('should initialize isCliente as false', () => {
      expect(component.isCliente).toBe(false);
    });
  });

  describe('ngOnInit', () => {
    it('should subscribe to currentUser$', () => {
      authService.currentUser$ = of(mockUser);
      authService.isAdmin.mockReturnValue(true);
      authService.isFuncionario.mockReturnValue(false);
      authService.isCliente.mockReturnValue(false);

      component.ngOnInit();

      expect(component.currentUser).toEqual(mockUser);
      expect(component.isAdmin).toBe(true);
    });

    it('should update user roles when user changes', () => {
      authService.currentUser$ = of(mockUser);
      authService.isAdmin.mockReturnValue(true);
      authService.isFuncionario.mockReturnValue(false);
      authService.isCliente.mockReturnValue(false);

      component.ngOnInit();

      expect(authService.isAdmin).toHaveBeenCalled();
      expect(authService.isFuncionario).toHaveBeenCalled();
      expect(authService.isCliente).toHaveBeenCalled();
    });

    it('should set isFuncionario when user is funcionario', () => {
      const funcionarioUser = { ...mockUser, perfil: PerfilUsuario.FUNCIONARIO };
      authService.currentUser$ = of(funcionarioUser);
      authService.isAdmin.mockReturnValue(false);
      authService.isFuncionario.mockReturnValue(true);
      authService.isCliente.mockReturnValue(false);

      component.ngOnInit();

      expect(component.isFuncionario).toBe(true);
      expect(component.isAdmin).toBe(false);
    });

    it('should set isCliente when user is cliente', () => {
      const clienteUser = { ...mockUser, perfil: PerfilUsuario.CLIENTE };
      authService.currentUser$ = of(clienteUser);
      authService.isAdmin.mockReturnValue(false);
      authService.isFuncionario.mockReturnValue(false);
      authService.isCliente.mockReturnValue(true);

      component.ngOnInit();

      expect(component.isCliente).toBe(true);
      expect(component.isAdmin).toBe(false);
    });

    it('should handle null user', () => {
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
    it('should complete destroy$ subject', () => {
      const nextSpy = jest.spyOn(component['destroy$'], 'next');
      const completeSpy = jest.spyOn(component['destroy$'], 'complete');

      component.ngOnDestroy();

      expect(nextSpy).toHaveBeenCalled();
      expect(completeSpy).toHaveBeenCalled();
    });

    it('should unsubscribe from currentUser$', () => {
      component.ngOnInit();
      const destroySpy = jest.spyOn(component['destroy$'], 'next');

      component.ngOnDestroy();

      expect(destroySpy).toHaveBeenCalled();
    });
  });

  describe('logout', () => {
    it('should call authService.logout', () => {
      component.logout();

      expect(authService.logout).toHaveBeenCalled();
    });

    it('should navigate to login page', () => {
      const navigateSpy = jest.spyOn(component['router'], 'navigate');

      component.logout();

      expect(navigateSpy).toHaveBeenCalledWith(['/login']);
    });

    it('should call logout and navigate in sequence', () => {
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
    it('should update state when user logs in', () => {
      authService.currentUser$ = of(mockUser);
      authService.isAdmin.mockReturnValue(true);
      authService.isFuncionario.mockReturnValue(false);
      authService.isCliente.mockReturnValue(false);

      component.ngOnInit();
      fixture.detectChanges();

      expect(component.currentUser).toEqual(mockUser);
      expect(component.isAdmin).toBe(true);
    });

    it('should update state when user logs out', () => {
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
