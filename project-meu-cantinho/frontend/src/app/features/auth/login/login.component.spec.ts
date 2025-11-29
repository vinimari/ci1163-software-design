import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, throwError, delay } from 'rxjs';
import { LoginComponent } from './login.component';
import { AuthService } from '../../../core/services/auth.service';
import { PerfilUsuario } from '../../../core/models/enums';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authService: jest.Mocked<AuthService>;
  let router: jest.Mocked<Router>;

  beforeEach(async () => {
    const authServiceMock = {
      login: jest.fn(),
      isAdmin: jest.fn(),
      isFuncionario: jest.fn()
    };

    await TestBed.configureTestingModule({
      imports: [
        LoginComponent,
        ReactiveFormsModule,
        RouterTestingModule
      ],
      providers: [
        { provide: AuthService, useValue: authServiceMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService) as jest.Mocked<AuthService>;
    router = TestBed.inject(Router) as jest.Mocked<Router>;
    jest.spyOn(router, 'navigate').mockResolvedValue(true);
    fixture.detectChanges();
  });

  describe('Component Initialization', () => {
    it('deve criar', () => {
      expect(component).toBeTruthy();
    });

    it('deve inicializar form with empty values', () => {
      expect(component.loginForm.get('email')?.value).toBe('');
      expect(component.loginForm.get('senha')?.value).toBe('');
    });

    it('deve inicializar loading as false', () => {
      expect(component.loading).toBe(false);
    });

    it('deve inicializar errorMessage as empty string', () => {
      expect(component.errorMessage).toBe('');
    });
  });

  describe('Form Validation', () => {
    it('deve mark form as invalid when empty', () => {
      expect(component.loginForm.valid).toBe(false);
    });

    it('deve require email field', () => {
      const emailControl = component.loginForm.get('email');
      expect(emailControl?.hasError('required')).toBe(true);
    });

    it('deve require valid email format', () => {
      const emailControl = component.loginForm.get('email');
      emailControl?.setValue('invalid-email');
      expect(emailControl?.hasError('email')).toBe(true);
    });

    it('deve accept valid email', () => {
      const emailControl = component.loginForm.get('email');
      emailControl?.setValue('test@example.com');
      expect(emailControl?.hasError('email')).toBe(false);
      expect(emailControl?.valid).toBe(true);
    });

    it('deve require senha field', () => {
      const senhaControl = component.loginForm.get('senha');
      expect(senhaControl?.hasError('required')).toBe(true);
    });

    it('deve accept any senha value', () => {
      const senhaControl = component.loginForm.get('senha');
      senhaControl?.setValue('123');
      expect(senhaControl?.valid).toBe(true);
    });

    it('deve mark form as valid when all fields are filled correctly', () => {
      component.loginForm.patchValue({
        email: 'test@example.com',
        senha: 'password123'
      });
      expect(component.loginForm.valid).toBe(true);
    });
  });

  describe('onSubmit()', () => {
    it('não deve submit when form is invalid', () => {
      component.loginForm.patchValue({
        email: '',
        senha: ''
      });

      component.onSubmit();

      expect(authService.login).not.toHaveBeenCalled();
    });

    it('não deve submit when email is invalid', () => {
      component.loginForm.patchValue({
        email: 'invalid-email',
        senha: 'password123'
      });

      component.onSubmit();

      expect(authService.login).not.toHaveBeenCalled();
    });

    it('não deve submit when senha is empty', () => {
      component.loginForm.patchValue({
        email: 'test@example.com',
        senha: ''
      });

      component.onSubmit();

      expect(authService.login).not.toHaveBeenCalled();
    });

    it('deve definir loading to true when submitting', fakeAsync(() => {
      component.loginForm.patchValue({
        email: 'test@example.com',
        senha: 'password123'
      });

      authService.isAdmin.mockReturnValue(true);
      authService.isFuncionario.mockReturnValue(false);

      authService.login.mockReturnValue(of({
        id: 1,
        nome: 'Test User',
        email: 'test@example.com',
        perfil: PerfilUsuario.ADMIN,
        token: 'test-token'
      }).pipe(delay(10)));

      component.onSubmit();

      expect(component.loading).toBe(true);

      tick(10); // Complete the observable
      expect(component.loading).toBe(false);
    }));

    it('deve clear errorMessage when submitting', () => {
      component.errorMessage = 'Previous error';
      component.loginForm.patchValue({
        email: 'test@example.com',
        senha: 'password123'
      });

      authService.login.mockReturnValue(of({
        id: 1,
        nome: 'Test User',
        email: 'test@example.com',
        perfil: PerfilUsuario.ADMIN,
        token: 'test-token'
      }));

      component.onSubmit();

      expect(component.errorMessage).toBe('');
    });

    it('deve call authService.login with correct credentials', () => {
      component.loginForm.patchValue({
        email: 'test@example.com',
        senha: 'password123'
      });

      authService.login.mockReturnValue(of({
        id: 1,
        nome: 'Test User',
        email: 'test@example.com',
        perfil: PerfilUsuario.ADMIN,
        token: 'test-token'
      }));

      component.onSubmit();

      expect(authService.login).toHaveBeenCalledWith({
        email: 'test@example.com',
        senha: 'password123'
      });
    });

    it('deve navegar to /home on successful login', fakeAsync(() => {
      component.loginForm.patchValue({
        email: 'test@example.com',
        senha: 'password123'
      });

      authService.isAdmin.mockReturnValue(true);
      authService.isFuncionario.mockReturnValue(false);

      authService.login.mockReturnValue(of({
        id: 1,
        nome: 'Test User',
        email: 'test@example.com',
        perfil: PerfilUsuario.ADMIN,
        token: 'test-token'
      }));
      router.navigate.mockReturnValue(Promise.resolve(true));

      component.onSubmit();
      tick();

      expect(router.navigate).toHaveBeenCalledWith(['/admin/reservas']);
      expect(component.loading).toBe(false);
    }));

    it('deve definir loading to false after successful login', fakeAsync(() => {
      component.loginForm.patchValue({
        email: 'test@example.com',
        senha: 'password123'
      });

      authService.isAdmin.mockReturnValue(true);
      authService.isFuncionario.mockReturnValue(false);

      authService.login.mockReturnValue(of({
        id: 1,
        nome: 'Test User',
        email: 'test@example.com',
        perfil: PerfilUsuario.ADMIN,
        token: 'test-token'
      }));

      component.onSubmit();
      tick();

      expect(component.loading).toBe(false);
    }));

    it('deve handle login error', fakeAsync(() => {
      component.loginForm.patchValue({
        email: 'test@example.com',
        senha: 'wrongpassword'
      });

      const errorResponse = {
        status: 401,
        message: 'Unauthorized'
      };

      authService.login.mockReturnValue(throwError(() => errorResponse));
      const consoleError = jest.spyOn(console, 'error').mockImplementation();

      component.onSubmit();
      tick();

      expect(component.loading).toBe(false);
      expect(component.errorMessage).toBe('Email ou senha inválidos');
      expect(router.navigate).not.toHaveBeenCalled();
      expect(consoleError).toHaveBeenCalledWith('Login error:', errorResponse);
      consoleError.mockRestore();
    }));

    it('deve definir loading to false after login error', fakeAsync(() => {
      component.loginForm.patchValue({
        email: 'test@example.com',
        senha: 'wrongpassword'
      });

      authService.login.mockReturnValue(throwError(() => new Error('Network error')));

      component.onSubmit();
      tick();

      expect(component.loading).toBe(false);
    }));

    it('deve display error message on login failure', fakeAsync(() => {
      component.loginForm.patchValue({
        email: 'test@example.com',
        senha: 'wrongpassword'
      });

      authService.login.mockReturnValue(throwError(() => new Error('Login failed')));

      component.onSubmit();
      tick();

      expect(component.errorMessage).toBe('Email ou senha inválidos');
    }));
  });

  describe('Form Controls', () => {
    it('deve atualizar email value when changed', () => {
      const emailControl = component.loginForm.get('email');
      emailControl?.setValue('newemail@example.com');
      expect(emailControl?.value).toBe('newemail@example.com');
    });

    it('deve atualizar senha value when changed', () => {
      const senhaControl = component.loginForm.get('senha');
      senhaControl?.setValue('newpassword');
      expect(senhaControl?.value).toBe('newpassword');
    });

    it('deve allow form reset', () => {
      component.loginForm.patchValue({
        email: 'test@example.com',
        senha: 'password123'
      });

      component.loginForm.reset();

      expect(component.loginForm.get('email')?.value).toBeNull();
      expect(component.loginForm.get('senha')?.value).toBeNull();
    });
  });

  describe('Edge Cases', () => {
    it('deve handle multiple rapid submit attempts', () => {
      component.loginForm.patchValue({
        email: 'test@example.com',
        senha: 'password123'
      });

      authService.login.mockReturnValue(of({
        id: 1,
        nome: 'Test User',
        email: 'test@example.com',
        perfil: PerfilUsuario.ADMIN,
        token: 'test-token'
      }));

      component.onSubmit();
      component.onSubmit();
      component.onSubmit();

      expect(authService.login).toHaveBeenCalledTimes(3);
    });

    it('deve handle email with special characters', () => {
      component.loginForm.patchValue({
        email: 'test+special@example.co.uk',
        senha: 'password123'
      });

      expect(component.loginForm.get('email')?.valid).toBe(true);
    });

    it('deve handle long senha', () => {
      const longPassword = 'a'.repeat(100);
      component.loginForm.patchValue({
        email: 'test@example.com',
        senha: longPassword
      });

      expect(component.loginForm.get('senha')?.valid).toBe(true);
      expect(component.loginForm.get('senha')?.value).toBe(longPassword);
    });
  });
});
