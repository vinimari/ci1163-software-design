import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { FilialService } from '../../../../core/services';
import { FilialRequest } from '../../../../core/models';
import { LoadingComponent } from '../../../../shared/components/loading/loading.component';
import { ButtonComponent } from '../../../../shared/components/button/button.component';

@Component({
  selector: 'app-filial-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, LoadingComponent, ButtonComponent],
  templateUrl: './filial-form.component.html',
  styleUrls: ['./filial-form.component.scss']
})
export class FilialFormComponent implements OnInit {
  filialForm: FormGroup;
  loading = false;
  error: string | null = null;
  isEditMode = false;
  filialId: number | null = null;

  constructor(
    private fb: FormBuilder,
    private filialService: FilialService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.filialForm = this.fb.group({
      nome: ['', [Validators.required, Validators.minLength(3)]],
      cidade: ['', [Validators.required, Validators.minLength(2)]],
      estado: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(2)]],
      endereco: [''],
      telefone: ['', [Validators.pattern(/^\(\d{2}\)\s?\d{4,5}-\d{4}$/)]]
    });
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id && id !== 'new') {
      this.isEditMode = true;
      this.filialId = +id;
      this.loadFilial(this.filialId);
    }
  }

  loadFilial(id: number): void {
    this.loading = true;
    this.filialService.getById(id).subscribe({
      next: (filial) => {
        this.filialForm.patchValue({
          nome: filial.nome,
          cidade: filial.cidade,
          estado: filial.estado,
          endereco: filial.endereco || '',
          telefone: filial.telefone || ''
        });
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erro ao carregar filial';
        this.loading = false;
        console.error('Erro ao carregar filial:', err);
      }
    });
  }

  onSubmit(): void {
    if (this.filialForm.valid) {
      this.loading = true;
      this.error = null;

      const filialData: FilialRequest = this.filialForm.value;

      const request = this.isEditMode && this.filialId
        ? this.filialService.update(this.filialId, filialData)
        : this.filialService.create(filialData);

      request.subscribe({
        next: () => {
          this.router.navigate(['/admin/filiais']);
        },
        error: (err) => {
          this.error = this.isEditMode
            ? 'Erro ao atualizar filial'
            : 'Erro ao criar filial';
          this.loading = false;
          console.error('Erro ao salvar filial:', err);
        }
      });
    } else {
      this.markFormGroupTouched(this.filialForm);
    }
  }

  markFormGroupTouched(formGroup: FormGroup): void {
    Object.keys(formGroup.controls).forEach(key => {
      const control = formGroup.get(key);
      control?.markAsTouched();
    });
  }

  cancel(): void {
    this.router.navigate(['/admin/filiais']);
  }

  get nome() { return this.filialForm.get('nome'); }
  get cidade() { return this.filialForm.get('cidade'); }
  get estado() { return this.filialForm.get('estado'); }
  get telefone() { return this.filialForm.get('telefone'); }
}
