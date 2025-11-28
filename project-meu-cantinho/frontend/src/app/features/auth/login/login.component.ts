import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  loginForm: FormGroup;
  loading = false;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      senha: ['', [Validators.required]]
    });
  }

  onSubmit(): void {
    if (this.loginForm.invalid) return;

    this.loading = true;
    this.errorMessage = '';

    const { email, senha } = this.loginForm.value;

    this.authService.login({ email, senha }).subscribe({
      next: () => {
        this.loading = false;

        if (this.authService.isAdmin()) {
          this.router.navigate(['/admin/reservas']);
        } else if (this.authService.isFuncionario()) {
          this.router.navigate(['/funcionario/reservas']);
        } else {
          this.router.navigate(['/home']);
        }
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = 'Email ou senha inválidos';
        console.error('Login error:', err);
      }
    });
  }
}
