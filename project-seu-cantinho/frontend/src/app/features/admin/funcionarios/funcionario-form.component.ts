import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { FuncionarioService } from '../../../core/services/funcionario.service';
import { FilialService } from '../../../core/services/filial.service';
import { FuncionarioRequest } from '../../../core/models/funcionario.model';
import { FilialResponse } from '../../../core/models/filial.model';
import { LoadingComponent } from '../../../shared/components/loading/loading.component';

@Component({
  selector: 'app-funcionario-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, LoadingComponent],
  templateUrl: './funcionario-form.component.html',
  styleUrls: ['./funcionario-form.component.scss']
})
export class FuncionarioFormComponent implements OnInit {
  funcionarioForm!: FormGroup;
  filiais: FilialResponse[] = [];
  isEditMode = false;
  funcionarioId: number | null = null;
  loading = false;
  error: string | null = null;

  constructor(
    private fb: FormBuilder,
    private funcionarioService: FuncionarioService,
    private filialService: FilialService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.createForm();
    this.loadFiliais();
    this.checkEditMode();
  }

  createForm(): void {
    this.funcionarioForm = this.fb.group({
      nome: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      senha: [''],
      cpf: [''],
      telefone: [''],
      matricula: ['', [Validators.required, Validators.maxLength(50)]],
      filialId: [null, Validators.required],
      ativo: [true]
    });
  }

  loadFiliais(): void {
    this.filialService.getAll().subscribe({
      next: (filiais) => {
        this.filiais = filiais;
      },
      error: (err) => {
        console.error('Erro ao carregar filiais:', err);
        this.error = 'Erro ao carregar filiais';
      }
    });
  }

  checkEditMode(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id && id !== 'new') {
      this.isEditMode = true;
      this.funcionarioId = +id;
      this.loadFuncionario(this.funcionarioId);
      // Senha não é obrigatória no modo edição
      this.funcionarioForm.get('senha')?.clearValidators();
    } else {
      // Senha obrigatória no modo criação
      this.funcionarioForm.get('senha')?.setValidators([Validators.required, Validators.minLength(6)]);
    }
    this.funcionarioForm.get('senha')?.updateValueAndValidity();
  }

  loadFuncionario(id: number): void {
    this.loading = true;
    this.funcionarioService.findById(id).subscribe({
      next: (funcionario) => {
        this.funcionarioForm.patchValue({
          nome: funcionario.nome,
          email: funcionario.email,
          cpf: funcionario.cpf,
          telefone: funcionario.telefone,
          matricula: funcionario.matricula,
          filialId: funcionario.filial.id,
          ativo: funcionario.ativo
        });
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erro ao carregar funcionário';
        this.loading = false;
        console.error('Erro:', err);
      }
    });
  }

  onSubmit(): void {
    if (this.funcionarioForm.valid) {
      this.loading = true;
      this.error = null;

      const formValue = this.funcionarioForm.value;
      const funcionarioRequest: FuncionarioRequest = {
        nome: formValue.nome,
        email: formValue.email,
        senha: formValue.senha || undefined,
        cpf: formValue.cpf || undefined,
        telefone: formValue.telefone || undefined,
        matricula: formValue.matricula,
        filialId: formValue.filialId,
        ativo: formValue.ativo
      };

      const request = this.isEditMode && this.funcionarioId
        ? this.funcionarioService.update(this.funcionarioId, funcionarioRequest)
        : this.funcionarioService.create(funcionarioRequest);

      request.subscribe({
        next: () => {
          this.router.navigate(['/admin/funcionarios']);
        },
        error: (err) => {
          this.loading = false;
          if (err.status === 409) {
            this.error = 'Email ou matrícula já cadastrados';
          } else {
            this.error = 'Erro ao salvar funcionário';
          }
          console.error('Erro:', err);
        }
      });
    } else {
      this.markFormGroupTouched(this.funcionarioForm);
    }
  }

  markFormGroupTouched(formGroup: FormGroup): void {
    Object.keys(formGroup.controls).forEach(key => {
      const control = formGroup.get(key);
      control?.markAsTouched();
    });
  }

  formatCpf(): void {
    const cpfControl = this.funcionarioForm.get('cpf');
    if (cpfControl?.value) {
      let cpf = cpfControl.value.replace(/\D/g, '');
      if (cpf.length <= 11) {
        cpf = cpf.replace(/(\d{3})(\d)/, '$1.$2');
        cpf = cpf.replace(/(\d{3})(\d)/, '$1.$2');
        cpf = cpf.replace(/(\d{3})(\d{1,2})$/, '$1-$2');
        cpfControl.setValue(cpf, { emitEvent: false });
      }
    }
  }

  formatTelefone(): void {
    const telefoneControl = this.funcionarioForm.get('telefone');
    if (telefoneControl?.value) {
      let telefone = telefoneControl.value.replace(/\D/g, '');
      if (telefone.length <= 11) {
        if (telefone.length <= 10) {
          telefone = telefone.replace(/(\d{2})(\d)/, '($1) $2');
          telefone = telefone.replace(/(\d{4})(\d)/, '$1-$2');
        } else {
          telefone = telefone.replace(/(\d{2})(\d)/, '($1) $2');
          telefone = telefone.replace(/(\d{5})(\d)/, '$1-$2');
        }
        telefoneControl.setValue(telefone, { emitEvent: false });
      }
    }
  }

  cancel(): void {
    this.router.navigate(['/admin/funcionarios']);
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.funcionarioForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched));
  }

  getErrorMessage(fieldName: string): string {
    const field = this.funcionarioForm.get(fieldName);
    if (field?.errors) {
      if (field.errors['required']) return 'Campo obrigatório';
      if (field.errors['email']) return 'Email inválido';
      if (field.errors['minlength']) return `Mínimo de ${field.errors['minlength'].requiredLength} caracteres`;
      if (field.errors['maxlength']) return `Máximo de ${field.errors['maxlength'].requiredLength} caracteres`;
    }
    return '';
  }
}
