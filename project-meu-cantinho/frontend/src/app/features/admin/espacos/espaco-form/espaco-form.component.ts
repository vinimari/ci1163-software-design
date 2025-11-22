import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { EspacoRequest, EspacoResponse, FilialResponse } from '../../../../core/models';
import { FilialService } from '../../../../core/services';

@Component({
  selector: 'app-espaco-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './espaco-form.component.html',
  styleUrl: './espaco-form.component.scss'
})
export class EspacoFormComponent implements OnInit {
  @Input() espaco?: EspacoResponse;
  @Input() filialIdFuncionario?: number; // Filial do funcionário logado
  @Output() submitForm = new EventEmitter<EspacoRequest>();
  @Output() cancel = new EventEmitter<void>();

  espacoForm!: FormGroup;
  filiais: FilialResponse[] = [];
  loading = false;
  isEditMode = false;

  constructor(
    private fb: FormBuilder,
    private filialService: FilialService
  ) {}

  ngOnInit(): void {
    this.isEditMode = !!this.espaco;
    this.initForm();
    this.loadFiliais();
  }

  private initForm(): void {
    this.espacoForm = this.fb.group({
      nome: [this.espaco?.nome || '', [Validators.required, Validators.maxLength(150)]],
      descricao: [this.espaco?.descricao || ''],
      capacidade: [this.espaco?.capacidade || '', [Validators.required, Validators.min(1)]],
      precoDiaria: [this.espaco?.precoDiaria || '', [Validators.required, Validators.min(0)]],
      ativo: [this.espaco?.ativo !== undefined ? this.espaco.ativo : true],
      urlFotoPrincipal: [this.espaco?.urlFotoPrincipal || '', [Validators.pattern('https?://.+')]],
      filialId: [
        { value: this.espaco?.filialId || this.filialIdFuncionario || '', disabled: !!this.filialIdFuncionario },
        [Validators.required]
      ]
    });
  }

  private loadFiliais(): void {
    if (this.filialIdFuncionario) {
      // Funcionário só vê sua filial
      this.filialService.getById(this.filialIdFuncionario).subscribe({
        next: (filial) => {
          this.filiais = [filial];
        },
        error: (error) => console.error('Erro ao carregar filial:', error)
      });
    } else {
      // Admin vê todas as filiais
      this.filialService.getAll().subscribe({
        next: (filiais) => {
          this.filiais = filiais;
        },
        error: (error) => console.error('Erro ao carregar filiais:', error)
      });
    }
  }

  onSubmit(): void {
    if (this.espacoForm.valid) {
      this.loading = true;
      const formValue = this.espacoForm.getRawValue(); // getRawValue inclui campos disabled
      this.submitForm.emit(formValue);
    } else {
      Object.keys(this.espacoForm.controls).forEach(key => {
        this.espacoForm.get(key)?.markAsTouched();
      });
    }
  }

  onCancel(): void {
    this.cancel.emit();
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.espacoForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched));
  }

  getErrorMessage(fieldName: string): string {
    const field = this.espacoForm.get(fieldName);
    if (!field || !field.errors) return '';

    if (field.errors['required']) return 'Campo obrigatório';
    if (field.errors['min']) return `Valor mínimo: ${field.errors['min'].min}`;
    if (field.errors['maxLength']) return `Máximo ${field.errors['maxLength'].requiredLength} caracteres`;
    if (field.errors['pattern']) return 'URL inválida';

    return 'Campo inválido';
  }
}
