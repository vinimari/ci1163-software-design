import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { of, throwError } from 'rxjs';
import { EspacoFormComponent } from './espaco-form.component';
import { FilialService } from '../../../../core/services';
import { EspacoResponse, FilialResponse } from '../../../../core/models';

describe('EspacoFormComponent', () => {
  let component: EspacoFormComponent;
  let fixture: ComponentFixture<EspacoFormComponent>;
  let filialService: jest.Mocked<FilialService>;

  const mockFiliais: FilialResponse[] = [
    { id: 1, nome: 'Filial Centro', cidade: 'Curitiba', estado: 'PR', endereco: 'Rua A', telefone: '1234-5678', dataCadastro: '2024-01-01' },
    { id: 2, nome: 'Filial Norte', cidade: 'São Paulo', estado: 'SP', endereco: 'Rua B', telefone: '8765-4321', dataCadastro: '2024-01-02' }
  ];

  const mockEspaco: EspacoResponse = {
    id: 1,
    nome: 'Sala de Reunião',
    descricao: 'Sala ampla com projetor',
    capacidade: 10,
    precoDiaria: 150.00,
    ativo: true,
    urlFotoPrincipal: 'https://example.com/foto.jpg',
    filial: {
      id: 1,
      nome: 'Filial Centro',
      cidade: 'Curitiba',
      estado: 'PR'
    }
  };

  beforeEach(async () => {
    const filialServiceMock = {
      getAll: jest.fn(),
      getById: jest.fn()
    };

    await TestBed.configureTestingModule({
      imports: [EspacoFormComponent, ReactiveFormsModule],
      providers: [
        { provide: FilialService, useValue: filialServiceMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(EspacoFormComponent);
    component = fixture.componentInstance;
    filialService = TestBed.inject(FilialService) as jest.Mocked<FilialService>;
  });

  describe('Component Initialization', () => {
    it('deve criar', () => {
      expect(component).toBeTruthy();
    });

    it('deve inicializar in create mode when no espaco is provided', () => {
      filialService.getAll.mockReturnValue(of(mockFiliais));

      fixture.detectChanges();

      expect(component.isEditMode).toBe(false);
      expect(component.espacoForm).toBeDefined();
    });

    it('deve inicializar in edit mode when espaco is provided', () => {
      component.espaco = mockEspaco;
      filialService.getAll.mockReturnValue(of(mockFiliais));

      fixture.detectChanges();

      expect(component.isEditMode).toBe(true);
    });

    it('deve carregar all filiais for admin (no filialIdFuncionario)', () => {
      filialService.getAll.mockReturnValue(of(mockFiliais));

      fixture.detectChanges();

      expect(filialService.getAll).toHaveBeenCalled();
      expect(component.filiais).toEqual(mockFiliais);
    });

    it('deve carregar only funcionario filial when filialIdFuncionario is provided', () => {
      component.filialIdFuncionario = 1;
      filialService.getById.mockReturnValue(of(mockFiliais[0]));

      fixture.detectChanges();

      expect(filialService.getById).toHaveBeenCalledWith(1);
      expect(component.filiais).toEqual([mockFiliais[0]]);
    });

    it('deve tratar erro when loading filiais', () => {
      const consoleError = jest.spyOn(console, 'error').mockImplementation();
      const error = new Error('Network error');
      filialService.getAll.mockReturnValue(throwError(() => error));

      fixture.detectChanges();

      expect(consoleError).toHaveBeenCalledWith('Erro ao carregar filiais:', error);
      consoleError.mockRestore();
    });

    it('deve tratar erro when loading filial by id', () => {
      component.filialIdFuncionario = 1;
      const consoleError = jest.spyOn(console, 'error').mockImplementation();
      const error = new Error('Network error');
      filialService.getById.mockReturnValue(throwError(() => error));

      fixture.detectChanges();

      expect(consoleError).toHaveBeenCalledWith('Erro ao carregar filial:', error);
      consoleError.mockRestore();
    });
  });

  describe('Form Initialization', () => {
    beforeEach(() => {
      filialService.getAll.mockReturnValue(of(mockFiliais));
    });

    it('deve inicializar form with empty values in create mode', () => {
      fixture.detectChanges();

      expect(component.espacoForm.get('nome')?.value).toBe('');
      expect(component.espacoForm.get('descricao')?.value).toBe('');
      expect(component.espacoForm.get('capacidade')?.value).toBe('');
      expect(component.espacoForm.get('precoDiaria')?.value).toBe('');
      expect(component.espacoForm.get('ativo')?.value).toBe(true);
      expect(component.espacoForm.get('urlFotoPrincipal')?.value).toBe('');
      expect(component.espacoForm.get('filialId')?.value).toBe('');
    });

    it('deve inicializar form with espaco values in edit mode', () => {
      component.espaco = mockEspaco;

      fixture.detectChanges();

      expect(component.espacoForm.get('nome')?.value).toBe(mockEspaco.nome);
      expect(component.espacoForm.get('descricao')?.value).toBe(mockEspaco.descricao);
      expect(component.espacoForm.get('capacidade')?.value).toBe(mockEspaco.capacidade);
      expect(component.espacoForm.get('precoDiaria')?.value).toBe(mockEspaco.precoDiaria);
      expect(component.espacoForm.get('ativo')?.value).toBe(mockEspaco.ativo);
      expect(component.espacoForm.get('urlFotoPrincipal')?.value).toBe(mockEspaco.urlFotoPrincipal);
      expect(component.espacoForm.get('filialId')?.value).toBe(mockEspaco.filial.id);
    });

    it('deve disable filialId field when filialIdFuncionario is provided', () => {
      component.filialIdFuncionario = 1;
      filialService.getById.mockReturnValue(of(mockFiliais[0]));

      fixture.detectChanges();

      expect(component.espacoForm.get('filialId')?.disabled).toBe(true);
    });

    it('deve enable filialId field when filialIdFuncionario is not provided', () => {
      fixture.detectChanges();

      expect(component.espacoForm.get('filialId')?.disabled).toBe(false);
    });

    it('deve definir filialId to filialIdFuncionario when provided', () => {
      component.filialIdFuncionario = 1;
      filialService.getById.mockReturnValue(of(mockFiliais[0]));

      fixture.detectChanges();

      expect(component.espacoForm.get('filialId')?.value).toBe(1);
    });
  });

  describe('Form Validation', () => {
    beforeEach(() => {
      filialService.getAll.mockReturnValue(of(mockFiliais));
      fixture.detectChanges();
    });

    it('deve mark form as invalid when empty', () => {
      expect(component.espacoForm.valid).toBe(false);
    });

    it('deve require nome field', () => {
      const nomeControl = component.espacoForm.get('nome');
      expect(nomeControl?.hasError('required')).toBe(true);
    });

    it('deve validar nome maxLength', () => {
      const nomeControl = component.espacoForm.get('nome');
      nomeControl?.setValue('a'.repeat(151));
      expect(nomeControl?.hasError('maxlength')).toBe(true);
    });

    it('deve accept valid nome', () => {
      const nomeControl = component.espacoForm.get('nome');
      nomeControl?.setValue('Sala de Reunião');
      expect(nomeControl?.valid).toBe(true);
    });

    it('não deve require descricao field', () => {
      const descricaoControl = component.espacoForm.get('descricao');
      expect(descricaoControl?.hasError('required')).toBe(false);
    });

    it('deve require capacidade field', () => {
      const capacidadeControl = component.espacoForm.get('capacidade');
      expect(capacidadeControl?.hasError('required')).toBe(true);
    });

    it('deve validar capacidade minimum value', () => {
      const capacidadeControl = component.espacoForm.get('capacidade');
      capacidadeControl?.setValue(0);
      expect(capacidadeControl?.hasError('min')).toBe(true);
    });

    it('deve accept valid capacidade', () => {
      const capacidadeControl = component.espacoForm.get('capacidade');
      capacidadeControl?.setValue(10);
      expect(capacidadeControl?.valid).toBe(true);
    });

    it('deve require precoDiaria field', () => {
      const precoDiariaControl = component.espacoForm.get('precoDiaria');
      expect(precoDiariaControl?.hasError('required')).toBe(true);
    });

    it('deve validar precoDiaria minimum value', () => {
      const precoDiariaControl = component.espacoForm.get('precoDiaria');
      precoDiariaControl?.setValue(-1);
      expect(precoDiariaControl?.hasError('min')).toBe(true);
    });

    it('deve accept zero as valid precoDiaria', () => {
      const precoDiariaControl = component.espacoForm.get('precoDiaria');
      precoDiariaControl?.setValue(0);
      expect(precoDiariaControl?.hasError('min')).toBe(false);
    });

    it('deve validar urlFotoPrincipal pattern', () => {
      const urlControl = component.espacoForm.get('urlFotoPrincipal');
      urlControl?.setValue('invalid-url');
      expect(urlControl?.hasError('pattern')).toBe(true);
    });

    it('deve accept valid http URL', () => {
      const urlControl = component.espacoForm.get('urlFotoPrincipal');
      urlControl?.setValue('http://example.com/photo.jpg');
      expect(urlControl?.valid).toBe(true);
    });

    it('deve accept valid https URL', () => {
      const urlControl = component.espacoForm.get('urlFotoPrincipal');
      urlControl?.setValue('https://example.com/photo.jpg');
      expect(urlControl?.valid).toBe(true);
    });

    it('deve accept empty urlFotoPrincipal', () => {
      const urlControl = component.espacoForm.get('urlFotoPrincipal');
      urlControl?.setValue('');
      expect(urlControl?.valid).toBe(true);
    });

    it('deve require filialId field', () => {
      const filialIdControl = component.espacoForm.get('filialId');
      expect(filialIdControl?.hasError('required')).toBe(true);
    });

    it('deve mark form as valid when all required fields are filled', () => {
      component.espacoForm.patchValue({
        nome: 'Sala de Reunião',
        descricao: 'Sala ampla',
        capacidade: 10,
        precoDiaria: 150.00,
        ativo: true,
        urlFotoPrincipal: 'https://example.com/photo.jpg',
        filialId: 1
      });

      expect(component.espacoForm.valid).toBe(true);
    });
  });

  describe('onSubmit()', () => {
    beforeEach(() => {
      filialService.getAll.mockReturnValue(of(mockFiliais));
      fixture.detectChanges();
    });

    it('deve emit submitForm when form is valid', () => {
      const submitSpy = jest.spyOn(component.submitForm, 'emit');

      component.espacoForm.patchValue({
        nome: 'Sala de Reunião',
        descricao: 'Sala ampla',
        capacidade: 10,
        precoDiaria: 150.00,
        ativo: true,
        urlFotoPrincipal: 'https://example.com/photo.jpg',
        filialId: 1
      });

      component.onSubmit();

      expect(submitSpy).toHaveBeenCalledWith({
        nome: 'Sala de Reunião',
        descricao: 'Sala ampla',
        capacidade: 10,
        precoDiaria: 150.00,
        ativo: true,
        urlFotoPrincipal: 'https://example.com/photo.jpg',
        filialId: 1
      });
    });

    it('deve definir loading to true when submitting', () => {
      component.espacoForm.patchValue({
        nome: 'Sala de Reunião',
        capacidade: 10,
        precoDiaria: 150.00,
        filialId: 1
      });

      component.onSubmit();

      expect(component.loading).toBe(true);
    });

    it('não deve emit submitForm when form is invalid', () => {
      const submitSpy = jest.spyOn(component.submitForm, 'emit');

      component.espacoForm.patchValue({
        nome: '', // Invalid - required
        capacidade: 10,
        precoDiaria: 150.00
      });

      component.onSubmit();

      expect(submitSpy).not.toHaveBeenCalled();
      expect(component.loading).toBe(false);
    });

    it('deve mark all fields as touched when form is invalid', () => {
      component.onSubmit();

      expect(component.espacoForm.get('nome')?.touched).toBe(true);
      expect(component.espacoForm.get('capacidade')?.touched).toBe(true);
      expect(component.espacoForm.get('precoDiaria')?.touched).toBe(true);
      expect(component.espacoForm.get('filialId')?.touched).toBe(true);
    });

    it('deve include disabled fields in submission using getRawValue', () => {
      component.filialIdFuncionario = 1;
      filialService.getById.mockReturnValue(of(mockFiliais[0]));

      component.ngOnInit();

      const submitSpy = jest.spyOn(component.submitForm, 'emit');
      component.espacoForm.patchValue({
        nome: 'Sala de Reunião',
        descricao: 'Sala ampla',
        capacidade: 10,
        precoDiaria: 150.00,
        ativo: true,
        urlFotoPrincipal: 'https://example.com/photo.jpg'
      });

      component.onSubmit();

      expect(submitSpy).toHaveBeenCalled();
      expect(submitSpy.mock.calls.length).toBeGreaterThan(0);
      const emittedValue = submitSpy.mock.calls[0][0];
      expect(emittedValue).toBeDefined();
      if (emittedValue) {
        expect(emittedValue.filialId).toBe(1);
      }
    });
  });

  describe('onCancel()', () => {
    beforeEach(() => {
      filialService.getAll.mockReturnValue(of(mockFiliais));
      fixture.detectChanges();
    });

    it('deve emit cancel event', () => {
      const cancelSpy = jest.spyOn(component.cancel, 'emit');

      component.onCancel();

      expect(cancelSpy).toHaveBeenCalled();
    });
  });

  describe('isFieldInvalid()', () => {
    beforeEach(() => {
      filialService.getAll.mockReturnValue(of(mockFiliais));
      fixture.detectChanges();
    });

    it('deve retornar false for untouched invalid field', () => {
      expect(component.isFieldInvalid('nome')).toBe(false);
    });

    it('deve retornar true for touched invalid field', () => {
      const nomeControl = component.espacoForm.get('nome');
      nomeControl?.markAsTouched();

      expect(component.isFieldInvalid('nome')).toBe(true);
    });

    it('deve retornar true for dirty invalid field', () => {
      const nomeControl = component.espacoForm.get('nome');
      nomeControl?.markAsDirty();

      expect(component.isFieldInvalid('nome')).toBe(true);
    });

    it('deve retornar false for valid field', () => {
      const nomeControl = component.espacoForm.get('nome');
      nomeControl?.setValue('Sala de Reunião');
      nomeControl?.markAsTouched();

      expect(component.isFieldInvalid('nome')).toBe(false);
    });

    it('deve retornar false for non-existent field', () => {
      expect(component.isFieldInvalid('nonExistent')).toBe(false);
    });
  });

  describe('getErrorMessage()', () => {
    beforeEach(() => {
      filialService.getAll.mockReturnValue(of(mockFiliais));
      fixture.detectChanges();
    });

    it('deve retornar required error message', () => {
      const nomeControl = component.espacoForm.get('nome');
      nomeControl?.markAsTouched();

      expect(component.getErrorMessage('nome')).toBe('Campo obrigatório');
    });

    it('deve retornar min error message', () => {
      const capacidadeControl = component.espacoForm.get('capacidade');
      capacidadeControl?.setValue(0);
      capacidadeControl?.markAsTouched();

      expect(component.getErrorMessage('capacidade')).toBe('Valor mínimo: 1');
    });

    it('deve retornar maxlength error message', () => {
      const nomeControl = component.espacoForm.get('nome');
      nomeControl?.setValue('a'.repeat(151));
      nomeControl?.markAsTouched();

      expect(component.getErrorMessage('nome')).toBe('Máximo 150 caracteres');
    });

    it('deve retornar pattern error message for invalid URL', () => {
      const urlControl = component.espacoForm.get('urlFotoPrincipal');
      urlControl?.setValue('invalid-url');
      urlControl?.markAsTouched();

      expect(component.getErrorMessage('urlFotoPrincipal')).toBe('URL inválida');
    });

    it('deve retornar empty string for valid field', () => {
      const nomeControl = component.espacoForm.get('nome');
      nomeControl?.setValue('Sala de Reunião');
      nomeControl?.markAsTouched();

      expect(component.getErrorMessage('nome')).toBe('');
    });

    it('deve retornar empty string for non-existent field', () => {
      expect(component.getErrorMessage('nonExistent')).toBe('');
    });

    it('deve retornar empty string for field without errors', () => {
      expect(component.getErrorMessage('descricao')).toBe('');
    });
  });

  describe('Edit Mode Scenarios', () => {
    beforeEach(() => {
      component.espaco = mockEspaco;
      filialService.getAll.mockReturnValue(of(mockFiliais));
      fixture.detectChanges();
    });

    it('deve populate form with existing espaco data', () => {
      expect(component.espacoForm.get('nome')?.value).toBe(mockEspaco.nome);
      expect(component.espacoForm.get('capacidade')?.value).toBe(mockEspaco.capacidade);
      expect(component.espacoForm.get('precoDiaria')?.value).toBe(mockEspaco.precoDiaria);
    });

    it('deve handle espaco with ativo = false', () => {
      const inactiveEspaco = { ...mockEspaco, ativo: false };
      component.espaco = inactiveEspaco;
      component.ngOnInit();

      expect(component.espacoForm.get('ativo')?.value).toBe(false);
    });

    it('deve submit updated values', () => {
      const submitSpy = jest.spyOn(component.submitForm, 'emit');

      component.espacoForm.patchValue({
        nome: 'Sala Atualizada',
        capacidade: 20
      });

      component.onSubmit();

      expect(submitSpy).toHaveBeenCalled();
      const emittedValue = submitSpy.mock.calls[0][0];
      expect(emittedValue).toBeDefined();
      if (emittedValue) {
        expect(emittedValue.nome).toBe('Sala Atualizada');
        expect(emittedValue.capacidade).toBe(20);
      }
    });
  });

  describe('Edge Cases', () => {
    beforeEach(() => {
      filialService.getAll.mockReturnValue(of(mockFiliais));
      fixture.detectChanges();
    });

    it('deve handle espaco without urlFotoPrincipal', () => {
      const espacoWithoutUrl = { ...mockEspaco, urlFotoPrincipal: '' };
      component.espaco = espacoWithoutUrl;
      component.ngOnInit();

      expect(component.espacoForm.get('urlFotoPrincipal')?.value).toBe('');
    });

    it('deve handle espaco without descricao', () => {
      const espacoWithoutDesc = { ...mockEspaco, descricao: '' };
      component.espaco = espacoWithoutDesc;
      component.ngOnInit();

      expect(component.espacoForm.get('descricao')?.value).toBe('');
    });

    it('deve handle form with all optional fields empty', () => {
      component.espacoForm.patchValue({
        nome: 'Sala Mínima',
        descricao: '',
        capacidade: 1,
        precoDiaria: 0,
        ativo: true,
        urlFotoPrincipal: '',
        filialId: 1
      });

      expect(component.espacoForm.valid).toBe(true);
    });

    it('deve handle large capacity values', () => {
      const capacidadeControl = component.espacoForm.get('capacidade');
      capacidadeControl?.setValue(1000);

      expect(capacidadeControl?.valid).toBe(true);
    });

    it('deve handle large price values', () => {
      const precoControl = component.espacoForm.get('precoDiaria');
      precoControl?.setValue(99999.99);

      expect(precoControl?.valid).toBe(true);
    });

    it('deve handle decimal capacity values', () => {
      const capacidadeControl = component.espacoForm.get('capacidade');
      capacidadeControl?.setValue(10.5);

      expect(capacidadeControl?.valid).toBe(true);
    });
  });

  describe('Funcionario Restrictions', () => {
    beforeEach(() => {
      component.filialIdFuncionario = 1;
      filialService.getById.mockReturnValue(of(mockFiliais[0]));
      fixture.detectChanges();
    });

    it('deve only load funcionario filial', () => {
      expect(filialService.getById).toHaveBeenCalledWith(1);
      expect(filialService.getAll).not.toHaveBeenCalled();
      expect(component.filiais.length).toBe(1);
    });

    it('deve disable filialId selection', () => {
      expect(component.espacoForm.get('filialId')?.disabled).toBe(true);
    });

    it('deve pre-select funcionario filial', () => {
      expect(component.espacoForm.get('filialId')?.value).toBe(1);
    });
  });
});
