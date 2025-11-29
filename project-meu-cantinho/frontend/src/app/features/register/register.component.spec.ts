import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, throwError } from 'rxjs';
import { RegisterComponent } from './register.component';
import { ClienteService } from '../../core/services';
import { ClienteResponse } from '../../core/models';
import { LoadingComponent } from '../../shared/components/loading/loading.component';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let clienteService: any;
  let router: any;

  const mockCliente: ClienteResponse = {
    id: 1,
    nome: 'João Silva',
    email: 'joao@teste.com',
    perfil: 'CLIENTE' as any,
    cpf: '123.456.789-00',
    telefone: '(11) 98765-4321',
    ativo: true,
    dataCadastro: '2024-01-01T00:00:00'
  };

  beforeEach(async () => {
    const clienteServiceMock = {
      getById: jest.fn().mockReturnValue(of(mockCliente)),
      create: jest.fn().mockReturnValue(of(mockCliente)),
      update: jest.fn().mockReturnValue(of(mockCliente)),
      delete: jest.fn(),
      getAll: jest.fn(),
      toggleAtivo: jest.fn()
    };

    await TestBed.configureTestingModule({
      imports: [
        RegisterComponent,
        ReactiveFormsModule,
        LoadingComponent,
        RouterTestingModule
      ],
      providers: [
        { provide: ClienteService, useValue: clienteServiceMock }
      ]
    }).compileComponents();

    clienteService = TestBed.inject(ClienteService);
    router = TestBed.inject(Router);
    jest.spyOn(router, 'navigate');
    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
  });

  it('deve criar o componente', () => {
    expect(component).toBeTruthy();
  });

  it('deve inicializar o formulário com campos vazios em modo de criação', () => {
    fixture.detectChanges();

    expect(component.clienteForm.get('nome')?.value).toBe('');
    expect(component.clienteForm.get('email')?.value).toBe('');
    expect(component.clienteForm.get('senha')?.value).toBe('');
  });

  it('deve validar campos obrigatórios', () => {
    fixture.detectChanges();

    const nome = component.clienteForm.get('nome');
    const email = component.clienteForm.get('email');
    const senha = component.clienteForm.get('senha');

    expect(nome?.valid).toBe(false);
    expect(email?.valid).toBe(false);
    expect(senha?.valid).toBe(false);

    nome?.setValue('João Silva');
    email?.setValue('joao@teste.com');
    senha?.setValue('senha123');

    expect(nome?.valid).toBe(true);
    expect(email?.valid).toBe(true);
    expect(senha?.valid).toBe(true);
  });

  it('deve validar formato de email', () => {
    const email = component.clienteForm.get('email');

    email?.setValue('email_invalido');
    expect(email?.hasError('email')).toBe(true);

    email?.setValue('email@valido.com');
    expect(email?.hasError('email')).toBe(false);
  });

  it('deve validar tamanho mínimo da senha', () => {
    const senha = component.clienteForm.get('senha');

    senha?.setValue('123');
    expect(senha?.hasError('minlength')).toBe(true);

    senha?.setValue('123456');
    expect(senha?.hasError('minlength')).toBe(false);
  });

  it('deve validar formato do CPF', () => {
    const cpf = component.clienteForm.get('cpf');

    cpf?.setValue('12345678900');
    expect(cpf?.hasError('pattern')).toBe(true);

    cpf?.setValue('123.456.789-00');
    expect(cpf?.hasError('pattern')).toBe(false);
  });

  it('deve validar formato do telefone', () => {
    const telefone = component.clienteForm.get('telefone');

    telefone?.setValue('11987654321');
    expect(telefone?.hasError('pattern')).toBe(true);

    telefone?.setValue('(11) 98765-4321');
    expect(telefone?.hasError('pattern')).toBe(false);
  });

  it('deve criar cliente com sucesso', () => {
    fixture.detectChanges();

    component.clienteForm.patchValue({
      nome: 'João Silva',
      email: 'joao@teste.com',
      senha: 'senha123',
      cpf: '123.456.789-00',
      telefone: '(11) 98765-4321',
      ativo: true
    });

    component.onSubmit();

    expect(clienteService.create).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('deve exibir erro ao falhar na criação', () => {
    const alertSpy = jest.spyOn(window, 'alert').mockImplementation();
    clienteService.create = jest.fn().mockReturnValue(throwError(() => ({ error: { message: 'Erro ao criar' } })));
    fixture.detectChanges();

    component.clienteForm.patchValue({
      nome: 'João Silva',
      email: 'joao@teste.com',
      senha: 'senha123'
    });

    component.onSubmit();

    expect(alertSpy).toHaveBeenCalledWith('Erro ao criar');
    expect(component.loading).toBe(false);
  });

  it('deve marcar campos como touched ao submeter formulário inválido', () => {
    fixture.detectChanges();
    component.onSubmit();

    expect(component.clienteForm.get('nome')?.touched).toBe(true);
    expect(component.clienteForm.get('email')?.touched).toBe(true);
    expect(component.clienteForm.get('senha')?.touched).toBe(true);
  });

  it('deve formatar CPF corretamente', () => {
    const event = {
      target: { value: '12345678900' }
    };

    component.formatCpf(event);

    expect(event.target.value).toBe('123.456.789-00');
  });

  it('deve formatar telefone corretamente (9 dígitos)', () => {
    const event = {
      target: { value: '11987654321' }
    };

    component.formatTelefone(event);

    expect(event.target.value).toBe('(11) 98765-4321');
  });

  it('deve formatar telefone corretamente (8 dígitos)', () => {
    const event = {
      target: { value: '1187654321' }
    };

    component.formatTelefone(event);

    expect(event.target.value).toBe('(11) 8765-4321');
  });
});
