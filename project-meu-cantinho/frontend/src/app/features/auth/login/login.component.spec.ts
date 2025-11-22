import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
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
      login: jest.fn()
    };

    const routerMock = {
      navigate: jest.fn()
    };

    await TestBed.configureTestingModule({
      imports: [LoginComponent, ReactiveFormsModule],
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        { provide: Router, useValue: routerMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService) as jest.Mocked<AuthService>;
    router = TestBed.inject(Router) as jest.Mocked<Router>;
    fixture.detectChanges();
  });

  describe('Component Initialization', () => {
    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should initialize form with empty values', () => {
      expect(component.loginForm.get('email')?.value).toBe('');
      expect(component.loginForm.get('senha')?.value).toBe('');
    });

    it('should initialize loading as false', () => {
      expect(component.loading).toBe(false);
    });

    it('should initialize errorMessage as empty string', () => {
      expect(component.errorMessage).toBe('');
    });
  });

  describe('Form Validation', () => {
    it('should mark form as invalid when empty', () => {
      expect(component.loginForm.valid).toBe(false);
    });

    it('should require email field', () => {
      const emailControl = component.loginForm.get('email');
      expect(emailControl?.hasError('required')).toBe(true);
    });

    it('should require valid email format', () => {
      const emailControl = component.loginForm.get('email');
      emailControl?.setValue('invalid-email');
      expect(emailControl?.hasError('email')).toBe(true);
    });

    it('should accept valid email', () => {
      const emailControl = component.loginForm.get('email');
      emailControl?.setValue('test@example.com');
      expect(emailControl?.hasError('email')).toBe(false);
      expect(emailControl?.valid).toBe(true);
    });

    it('should require senha field', () => {
      const senhaControl = component.loginForm.get('senha');
      expect(senhaControl?.hasError('required')).toBe(true);
    });

    it('should accept any senha value', () => {
      const senhaControl = component.loginForm.get('senha');
      senhaControl?.setValue('123');
      expect(senhaControl?.valid).toBe(true);
    });

    it('should mark form as valid when all fields are filled correctly', () => {
      component.loginForm.patchValue({
        email: 'test@example.com',
        senha: 'password123'
      });
      expect(component.loginForm.valid).toBe(true);
    });
  });

  describe('onSubmit()', () => {
    it('should not submit when form is invalid', () => {
      component.loginForm.patchValue({
        email: '',
        senha: ''
      });

      component.onSubmit();

      expect(authService.login).not.toHaveBeenCalled();
    });

    it('should not submit when email is invalid', () => {
      component.loginForm.patchValue({
        email: 'invalid-email',
        senha: 'password123'
      });

      component.onSubmit();

      expect(authService.login).not.toHaveBeenCalled();
    });

    it('should not submit when senha is empty', () => {
      component.loginForm.patchValue({
        email: 'test@example.com',
        senha: ''
      });

      component.onSubmit();

      expect(authService.login).not.toHaveBeenCalled();
    });

    it('should set loading to true when submitting', fakeAsync(() => {
      component.loginForm.patchValue({
        email: 'test@example.com',
        senha: 'password123'
      });

      // Use delay to make observable async
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

    it('should clear errorMessage when submitting', () => {
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

    it('should call authService.login with correct credentials', () => {
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

    it('should navigate to /home on successful login', fakeAsync(() => {
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
      router.navigate.mockReturnValue(Promise.resolve(true));

      component.onSubmit();
      tick();

      expect(router.navigate).toHaveBeenCalledWith(['/home']);
      expect(component.loading).toBe(false);
    }));

    it('should set loading to false after successful login', fakeAsync(() => {
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
      tick();

      expect(component.loading).toBe(false);
    }));

    it('should handle login error', fakeAsync(() => {
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

    it('should set loading to false after login error', fakeAsync(() => {
      component.loginForm.patchValue({
        email: 'test@example.com',
        senha: 'wrongpassword'
      });

      authService.login.mockReturnValue(throwError(() => new Error('Network error')));

      component.onSubmit();
      tick();

      expect(component.loading).toBe(false);
    }));

    it('should display error message on login failure', fakeAsync(() => {
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
    it('should update email value when changed', () => {
      const emailControl = component.loginForm.get('email');
      emailControl?.setValue('newemail@example.com');
      expect(emailControl?.value).toBe('newemail@example.com');
    });

    it('should update senha value when changed', () => {
      const senhaControl = component.loginForm.get('senha');
      senhaControl?.setValue('newpassword');
      expect(senhaControl?.value).toBe('newpassword');
    });

    it('should allow form reset', () => {
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
    it('should handle multiple rapid submit attempts', () => {
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

      // Should still only call once per submit
      expect(authService.login).toHaveBeenCalledTimes(3);
    });

    it('should handle email with special characters', () => {
      component.loginForm.patchValue({
        email: 'test+special@example.co.uk',
        senha: 'password123'
      });

      expect(component.loginForm.get('email')?.valid).toBe(true);
    });

    it('should handle long senha', () => {
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
