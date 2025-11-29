import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router, ActivatedRoute } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { of, throwError } from 'rxjs';
import { FilialFormComponent } from './filial-form.component';
import { FilialService } from '../../../../core/services';
import { FilialResponse, FilialRequest } from '../../../../core/models';

describe('FilialFormComponent', () => {
  let component: FilialFormComponent;
  let fixture: ComponentFixture<FilialFormComponent>;
  let filialService: jest.Mocked<FilialService>;
  let router: jest.Mocked<Router>;
  let activatedRoute: any;

  const mockFilial: FilialResponse = {
    id: 1,
    nome: 'Filial Centro',
    cidade: 'São Paulo',
    estado: 'SP',
    endereco: 'Rua A, 123',
    telefone: '(11) 1234-5678',
    dataCadastro: '2024-01-01'
  };

  beforeEach(async () => {
    const filialServiceMock = {
      getById: jest.fn().mockReturnValue(of(mockFilial)),
      create: jest.fn().mockReturnValue(of(mockFilial)),
      update: jest.fn().mockReturnValue(of(mockFilial)),
      delete: jest.fn(),
      getAll: jest.fn()
    };

    const routerMock = {
      navigate: jest.fn()
    };

    const activatedRouteMock = {
      snapshot: {
        paramMap: {
          get: jest.fn().mockReturnValue(null)
        }
      }
    };

    await TestBed.configureTestingModule({
      imports: [FilialFormComponent, ReactiveFormsModule],
      providers: [
        { provide: FilialService, useValue: filialServiceMock },
        { provide: Router, useValue: routerMock },
        { provide: ActivatedRoute, useValue: activatedRouteMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(FilialFormComponent);
    component = fixture.componentInstance;
    filialService = TestBed.inject(FilialService) as jest.Mocked<FilialService>;
    router = TestBed.inject(Router) as jest.Mocked<Router>;
    activatedRoute = TestBed.inject(ActivatedRoute);
  });

  it('deve criar', () => {
    expect(component).toBeTruthy();
  });

  it('deve inicializar form with empty values in create mode', () => {
    fixture.detectChanges();
    expect(component.isEditMode).toBe(false);
    expect(component.filialForm.value).toEqual({
      nome: '',
      cidade: '',
      estado: '',
      endereco: '',
      telefone: ''
    });
  });

  it('deve carregar filial in edit mode', () => {
    activatedRoute.snapshot.paramMap.get.mockReturnValue('1');
    fixture.detectChanges();

    expect(component.isEditMode).toBe(true);
    expect(component.filialId).toBe(1);
    expect(filialService.getById).toHaveBeenCalledWith(1);
  });

  it('deve patch form values when loading filial', () => {
    component.loadFilial(1);

    expect(component.filialForm.value).toEqual({
      nome: 'Filial Centro',
      cidade: 'São Paulo',
      estado: 'SP',
      endereco: 'Rua A, 123',
      telefone: '(11) 1234-5678'
    });
    expect(component.loading).toBe(false);
  });

  it('deve tratar erro when loading filial fails', () => {
    const consoleErrorSpy = jest.spyOn(console, 'error').mockImplementation();
    filialService.getById.mockReturnValue(throwError(() => new Error('Error')));

    component.loadFilial(1);

    expect(component.error).toBe('Erro ao carregar filial');
    expect(component.loading).toBe(false);
    consoleErrorSpy.mockRestore();
  });

  it('deve validar required fields', () => {
    expect(component.filialForm.valid).toBe(false);

    component.filialForm.patchValue({
      nome: 'Test',
      cidade: 'SP',
      estado: 'SP'
    });

    expect(component.filialForm.valid).toBe(true);
  });

  it('deve validar nome minLength', () => {
    const nomeControl = component.filialForm.get('nome');
    nomeControl?.setValue('AB');
    expect(nomeControl?.hasError('minlength')).toBe(true);

    nomeControl?.setValue('ABC');
    expect(nomeControl?.hasError('minlength')).toBe(false);
  });

  it('deve validar estado length', () => {
    const estadoControl = component.filialForm.get('estado');
    estadoControl?.setValue('S');
    expect(estadoControl?.valid).toBe(false);

    estadoControl?.setValue('SP');
    expect(estadoControl?.valid).toBe(true);

    estadoControl?.setValue('SPX');
    expect(estadoControl?.valid).toBe(false);
  });

  it('deve validar telefone pattern', () => {
    const telefoneControl = component.filialForm.get('telefone');
    telefoneControl?.setValue('123456789');
    expect(telefoneControl?.hasError('pattern')).toBe(true);

    telefoneControl?.setValue('(11) 1234-5678');
    expect(telefoneControl?.hasError('pattern')).toBe(false);

    telefoneControl?.setValue('(11) 91234-5678');
    expect(telefoneControl?.hasError('pattern')).toBe(false);
  });

  it('deve criar filial when form is valid in create mode', () => {
    component.filialForm.patchValue({
      nome: 'Nova Filial',
      cidade: 'Rio de Janeiro',
      estado: 'RJ',
      endereco: 'Av B, 456',
      telefone: '(21) 9876-5432'
    });

    component.onSubmit();

    const expectedData: FilialRequest = {
      nome: 'Nova Filial',
      cidade: 'Rio de Janeiro',
      estado: 'RJ',
      endereco: 'Av B, 456',
      telefone: '(21) 9876-5432'
    };

    expect(filialService.create).toHaveBeenCalledWith(expectedData);
    expect(router.navigate).toHaveBeenCalledWith(['/admin/filiais']);
  });

  it('deve atualizar filial when form is valid in edit mode', () => {
    component.isEditMode = true;
    component.filialId = 1;
    component.filialForm.patchValue({
      nome: 'Filial Atualizada',
      cidade: 'São Paulo',
      estado: 'SP',
      endereco: 'Rua C, 789',
      telefone: '(11) 9999-8888'
    });

    component.onSubmit();

    const expectedData: FilialRequest = {
      nome: 'Filial Atualizada',
      cidade: 'São Paulo',
      estado: 'SP',
      endereco: 'Rua C, 789',
      telefone: '(11) 9999-8888'
    };

    expect(filialService.update).toHaveBeenCalledWith(1, expectedData);
    expect(router.navigate).toHaveBeenCalledWith(['/admin/filiais']);
  });

  it('não deve submit when form is invalid', () => {
    component.onSubmit();

    expect(filialService.create).not.toHaveBeenCalled();
    expect(filialService.update).not.toHaveBeenCalled();
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('deve mark all fields as touched when submitting invalid form', () => {
    component.onSubmit();

    expect(component.filialForm.get('nome')?.touched).toBe(true);
    expect(component.filialForm.get('cidade')?.touched).toBe(true);
    expect(component.filialForm.get('estado')?.touched).toBe(true);
  });

  it('deve tratar erro when create fails', () => {
    const consoleErrorSpy = jest.spyOn(console, 'error').mockImplementation();
    filialService.create.mockReturnValue(throwError(() => new Error('Error')));

    component.filialForm.patchValue({
      nome: 'Nova Filial',
      cidade: 'São Paulo',
      estado: 'SP'
    });

    component.onSubmit();

    expect(component.error).toBe('Erro ao criar filial');
    expect(component.loading).toBe(false);
    consoleErrorSpy.mockRestore();
  });

  it('deve tratar erro when update fails', () => {
    const consoleErrorSpy = jest.spyOn(console, 'error').mockImplementation();
    component.isEditMode = true;
    component.filialId = 1;
    filialService.update.mockReturnValue(throwError(() => new Error('Error')));

    component.filialForm.patchValue({
      nome: 'Filial Atualizada',
      cidade: 'São Paulo',
      estado: 'SP'
    });

    component.onSubmit();

    expect(component.error).toBe('Erro ao atualizar filial');
    expect(component.loading).toBe(false);
    consoleErrorSpy.mockRestore();
  });

  it('deve navegar back when cancel is called', () => {
    component.cancel();
    expect(router.navigate).toHaveBeenCalledWith(['/admin/filiais']);
  });

  it('deve ter nome getter', () => {
    expect(component.nome).toBe(component.filialForm.get('nome'));
  });

  it('deve ter cidade getter', () => {
    expect(component.cidade).toBe(component.filialForm.get('cidade'));
  });

  it('deve ter estado getter', () => {
    expect(component.estado).toBe(component.filialForm.get('estado'));
  });

  it('deve ter telefone getter', () => {
    expect(component.telefone).toBe(component.filialForm.get('telefone'));
  });

  it('deve display error message when error is set', () => {
    component.error = 'Test error';
    fixture.detectChanges();

    const errorElement = fixture.nativeElement.querySelector('.alert-danger');
    expect(errorElement).toBeTruthy();
    expect(errorElement.textContent).toContain('Test error');
  });

  it('deve show correct title in edit mode', () => {
    component.isEditMode = true;
    fixture.detectChanges();

    const title = fixture.nativeElement.querySelector('h2');
    expect(title.textContent).toContain('Editar Filial');
  });

  it('deve show correct title in create mode', () => {
    component.isEditMode = false;
    fixture.detectChanges();

    const title = fixture.nativeElement.querySelector('h2');
    expect(title.textContent).toContain('Nova Filial');
  });

  it('não deve enter edit mode when id is "new"', () => {
    activatedRoute.snapshot.paramMap.get.mockReturnValue('new');
    const newFixture = TestBed.createComponent(FilialFormComponent);
    const newComponent = newFixture.componentInstance;
    newFixture.detectChanges();

    expect(newComponent.isEditMode).toBe(false);
    expect(filialService.getById).not.toHaveBeenCalled();
  });
});
