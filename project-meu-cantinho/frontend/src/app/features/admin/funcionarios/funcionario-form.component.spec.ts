import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { FuncionarioFormComponent } from './funcionario-form.component';
import { FuncionarioService } from '../../../core/services/funcionario.service';
import { FilialService } from '../../../core/services/filial.service';
import { FuncionarioResponse } from '../../../core/models/funcionario.model';
import { FilialResponse } from '../../../core/models/filial.model';

describe('FuncionarioFormComponent', () => {
  let component: FuncionarioFormComponent;
  let fixture: ComponentFixture<FuncionarioFormComponent>;
  let funcionarioService: jest.Mocked<FuncionarioService>;
  let filialService: jest.Mocked<FilialService>;
  let router: Router;

  const mockFilial: FilialResponse = {
    id: 1,
    nome: 'Filial Centro',
    cidade: 'São Paulo',
    estado: 'SP',
    endereco: 'Rua A, 123',
    telefone: '(11) 1234-5678',
    dataCadastro: '2024-01-01T00:00:00'
  };

  const mockFuncionario: FuncionarioResponse = {
    id: 1,
    nome: 'João Silva',
    email: 'joao@example.com',
    cpf: '123.456.789-00',
    telefone: '(11) 98765-4321',
    ativo: true,
    dataCadastro: '2024-01-01T00:00:00',
    matricula: 'F001',
    filial: mockFilial
  };

  beforeEach(async () => {
    const funcionarioServiceMock = {
      findById: jest.fn(),
      create: jest.fn(),
      update: jest.fn()
    };

    const filialServiceMock = {
      getAll: jest.fn()
    };

    await TestBed.configureTestingModule({
      imports: [
        FuncionarioFormComponent,
        RouterTestingModule,
        HttpClientTestingModule,
        ReactiveFormsModule
      ],
      providers: [
        { provide: FuncionarioService, useValue: funcionarioServiceMock },
        { provide: FilialService, useValue: filialServiceMock },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: {
                get: jest.fn().mockReturnValue('new')
              }
            }
          }
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(FuncionarioFormComponent);
    component = fixture.componentInstance;
    funcionarioService = TestBed.inject(FuncionarioService) as jest.Mocked<FuncionarioService>;
    filialService = TestBed.inject(FilialService) as jest.Mocked<FilialService>;
    router = TestBed.inject(Router);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize form with empty values in create mode', () => {
    filialService.getAll.mockReturnValue(of([mockFilial]));

    component.ngOnInit();

    expect(component.funcionarioForm).toBeDefined();
    expect(component.isEditMode).toBe(false);
    expect(component.funcionarioForm.get('senha')?.hasError('required')).toBe(true);
  });

  it('should load filiais on init', () => {
    filialService.getAll.mockReturnValue(of([mockFilial]));

    component.ngOnInit();

    expect(filialService.getAll).toHaveBeenCalled();
    expect(component.filiais).toEqual([mockFilial]);
  });

  it('should load funcionario in edit mode', () => {
    const route = TestBed.inject(ActivatedRoute);
    jest.spyOn(route.snapshot.paramMap, 'get').mockReturnValue('1');

    filialService.getAll.mockReturnValue(of([mockFilial]));
    funcionarioService.findById.mockReturnValue(of(mockFuncionario));

    component.ngOnInit();

    expect(component.isEditMode).toBe(true);
    expect(component.funcionarioId).toBe(1);
    expect(funcionarioService.findById).toHaveBeenCalledWith(1);
  });

  it('should create funcionario with valid form', () => {
    filialService.getAll.mockReturnValue(of([mockFilial]));
    funcionarioService.create.mockReturnValue(of(mockFuncionario));
    jest.spyOn(router, 'navigate');

    component.ngOnInit();
    component.funcionarioForm.patchValue({
      nome: 'João Silva',
      email: 'joao@example.com',
      senha: 'senha123',
      matricula: 'F001',
      filialId: 1,
      ativo: true
    });

    component.onSubmit();

    expect(funcionarioService.create).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/admin/funcionarios']);
  });

  it('should update funcionario in edit mode', () => {
    const route = TestBed.inject(ActivatedRoute);
    jest.spyOn(route.snapshot.paramMap, 'get').mockReturnValue('1');

    filialService.getAll.mockReturnValue(of([mockFilial]));
    funcionarioService.findById.mockReturnValue(of(mockFuncionario));
    funcionarioService.update.mockReturnValue(of(mockFuncionario));
    jest.spyOn(router, 'navigate');

    component.ngOnInit();
    component.onSubmit();

    expect(funcionarioService.update).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/admin/funcionarios']);
  });

  it('should handle duplicate email/matricula error', () => {
    filialService.getAll.mockReturnValue(of([mockFilial]));
    funcionarioService.create.mockReturnValue(
      throwError(() => ({ status: 409 }))
    );

    component.ngOnInit();
    component.funcionarioForm.patchValue({
      nome: 'João Silva',
      email: 'joao@example.com',
      senha: 'senha123',
      matricula: 'F001',
      filialId: 1
    });

    component.onSubmit();

    expect(component.error).toBe('Email ou matrícula já cadastrados');
  });

  it('should not submit invalid form', () => {
    filialService.getAll.mockReturnValue(of([mockFilial]));

    component.ngOnInit();
    component.onSubmit();

    expect(funcionarioService.create).not.toHaveBeenCalled();
  });

  it('should format CPF correctly', () => {
    filialService.getAll.mockReturnValue(of([mockFilial]));

    component.ngOnInit();
    component.funcionarioForm.get('cpf')?.setValue('12345678900');
    component.formatCpf();

    expect(component.funcionarioForm.get('cpf')?.value).toBe('123.456.789-00');
  });

  it('should format telefone correctly', () => {
    filialService.getAll.mockReturnValue(of([mockFilial]));

    component.ngOnInit();
    component.funcionarioForm.get('telefone')?.setValue('11987654321');
    component.formatTelefone();

    expect(component.funcionarioForm.get('telefone')?.value).toBe('(11) 98765-4321');
  });

  it('should navigate back on cancel', () => {
    jest.spyOn(router, 'navigate');

    component.cancel();

    expect(router.navigate).toHaveBeenCalledWith(['/admin/funcionarios']);
  });

  it('should validate required fields', () => {
    filialService.getAll.mockReturnValue(of([mockFilial]));

    component.ngOnInit();

    expect(component.isFieldInvalid('nome')).toBe(false);

    component.funcionarioForm.get('nome')?.markAsTouched();
    expect(component.isFieldInvalid('nome')).toBe(true);
  });
});
