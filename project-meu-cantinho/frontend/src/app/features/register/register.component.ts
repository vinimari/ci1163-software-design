import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { ClienteService } from '../../core/services';
import { ClienteRequest } from '../../core/models';
import { LoadingComponent } from '../../shared/components/loading/loading.component';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, LoadingComponent, RouterModule],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {
  clienteForm: FormGroup;
  loading = false;

  constructor(
    private fb: FormBuilder,
    private clienteService: ClienteService,
    private router: Router
  ) {
    this.clienteForm = this.fb.group({
      nome: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(150)]],
      email: ['', [Validators.required, Validators.email, Validators.maxLength(150)]],
      senha: ['', [Validators.required, Validators.minLength(6)]],
      cpf: ['', [Validators.pattern(/^\d{3}\.\d{3}\.\d{3}-\d{2}$/)]],
      telefone: ['', [Validators.pattern(/^\(\d{2}\)\s?\d{4,5}-\d{4}$/)]
]
    });
  }

  ngOnInit(): void {
    // Componente de registro público - não precisa carregar dados
  }

  onSubmit(): void {
    if (this.clienteForm.valid) {
      this.loading = true;

      const clienteData: ClienteRequest = {
        nome: this.clienteForm.value.nome,
        email: this.clienteForm.value.email,
        senha: this.clienteForm.value.senha,
        cpf: this.clienteForm.value.cpf || undefined,
        telefone: this.clienteForm.value.telefone || undefined,
        ativo: true // Novo cliente sempre começa ativo
      };

      this.clienteService.create(clienteData).subscribe({
        next: () => {
          alert('Cadastro realizado com sucesso! Faça login para continuar.');
          this.router.navigate(['/login']);
        },
        error: (err) => {
          this.loading = false;
          const errorMessage = err.error?.message || 'Erro ao realizar cadastro';
          alert(errorMessage);
          console.error('Erro ao cadastrar cliente:', err);
        }
      });
    } else {
      this.markFormGroupTouched(this.clienteForm);
    }
  }

  markFormGroupTouched(formGroup: FormGroup): void {
    Object.keys(formGroup.controls).forEach(key => {
      const control = formGroup.get(key);
      control?.markAsTouched();
    });
  }

  cancel(): void {
    this.router.navigate(['/login']);
  }

  // Máscaras de formatação
  formatCpf(event: any): void {
    let value = event.target.value.replace(/\D/g, '');
    if (value.length <= 11) {
      value = value.replace(/(\d{3})(\d)/, '$1.$2');
      value = value.replace(/(\d{3})(\d)/, '$1.$2');
      value = value.replace(/(\d{3})(\d{1,2})$/, '$1-$2');
      event.target.value = value;
      this.clienteForm.patchValue({ cpf: value });
    }
  }

  formatTelefone(event: any): void {
    let value = event.target.value.replace(/\D/g, '');
    if (value.length <= 11) {
      if (value.length <= 10) {
        value = value.replace(/(\d{2})(\d)/, '($1) $2');
        value = value.replace(/(\d{4})(\d)/, '$1-$2');
      } else {
        value = value.replace(/(\d{2})(\d)/, '($1) $2');
        value = value.replace(/(\d{5})(\d)/, '$1-$2');
      }
      event.target.value = value;
      this.clienteForm.patchValue({ telefone: value });
    }
  }

  get nome() { return this.clienteForm.get('nome'); }
  get email() { return this.clienteForm.get('email'); }
  get senha() { return this.clienteForm.get('senha'); }
  get cpf() { return this.clienteForm.get('cpf'); }
  get telefone() { return this.clienteForm.get('telefone'); }
}
