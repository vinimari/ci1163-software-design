import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router, ActivatedRoute } from '@angular/router';
import { of, throwError } from 'rxjs';
import { FilialDetailComponent } from './filial-detail.component';
import { FilialService } from '../../../../core/services';
import { FilialResponse } from '../../../../core/models';

describe('FilialDetailComponent', () => {
  let component: FilialDetailComponent;
  let fixture: ComponentFixture<FilialDetailComponent>;
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
      delete: jest.fn().mockReturnValue(of(undefined)),
      getAll: jest.fn(),
      create: jest.fn(),
      update: jest.fn()
    };

    const routerMock = {
      navigate: jest.fn()
    };

    const activatedRouteMock = {
      snapshot: {
        paramMap: {
          get: jest.fn().mockReturnValue('1')
        }
      }
    };

    await TestBed.configureTestingModule({
      imports: [FilialDetailComponent],
      providers: [
        { provide: FilialService, useValue: filialServiceMock },
        { provide: Router, useValue: routerMock },
        { provide: ActivatedRoute, useValue: activatedRouteMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(FilialDetailComponent);
    component = fixture.componentInstance;
    filialService = TestBed.inject(FilialService) as jest.Mocked<FilialService>;
    router = TestBed.inject(Router) as jest.Mocked<Router>;
    activatedRoute = TestBed.inject(ActivatedRoute);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load filial on init', () => {
    fixture.detectChanges();
    expect(filialService.getById).toHaveBeenCalledWith(1);
    expect(component.filial).toEqual(mockFilial);
    expect(component.loading).toBe(false);
  });

  it('should handle error when loading filial fails', () => {
    const consoleErrorSpy = jest.spyOn(console, 'error').mockImplementation();
    filialService.getById.mockReturnValue(throwError(() => new Error('Error')));

    component.loadFilial(1);

    expect(component.error).toBe('Erro ao carregar filial');
    expect(component.loading).toBe(false);
    consoleErrorSpy.mockRestore();
  });

  it('should navigate to edit when editFilial is called', () => {
    component.filial = mockFilial;
    component.editFilial();
    expect(router.navigate).toHaveBeenCalledWith(['/admin/filiais', 1, 'edit']);
  });

  it('should not navigate to edit when filial is null', () => {
    component.filial = null;
    component.editFilial();
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('should navigate back when goBack is called', () => {
    component.goBack();
    expect(router.navigate).toHaveBeenCalledWith(['/admin/filiais']);
  });

  it('should delete filial when confirmed', () => {
    const confirmSpy = jest.spyOn(window, 'confirm').mockReturnValue(true);
    component.filial = mockFilial;

    component.deleteFilial();

    expect(confirmSpy).toHaveBeenCalledWith('Tem certeza que deseja excluir esta filial?');
    expect(filialService.delete).toHaveBeenCalledWith(1);
    expect(router.navigate).toHaveBeenCalledWith(['/admin/filiais']);
    confirmSpy.mockRestore();
  });

  it('should not delete filial when not confirmed', () => {
    const confirmSpy = jest.spyOn(window, 'confirm').mockReturnValue(false);
    component.filial = mockFilial;

    component.deleteFilial();

    expect(filialService.delete).not.toHaveBeenCalled();
    confirmSpy.mockRestore();
  });

  it('should not delete when filial is null', () => {
    const confirmSpy = jest.spyOn(window, 'confirm').mockReturnValue(true);
    component.filial = null;

    component.deleteFilial();

    expect(filialService.delete).not.toHaveBeenCalled();
    confirmSpy.mockRestore();
  });

  it('should handle error when delete fails', () => {
    const consoleErrorSpy = jest.spyOn(console, 'error').mockImplementation();
    const confirmSpy = jest.spyOn(window, 'confirm').mockReturnValue(true);
    component.filial = mockFilial;
    filialService.delete.mockReturnValue(throwError(() => new Error('Error')));

    component.deleteFilial();

    expect(component.error).toBe('Erro ao excluir filial');
    expect(component.loading).toBe(false);
    expect(router.navigate).not.toHaveBeenCalled();
    consoleErrorSpy.mockRestore();
    confirmSpy.mockRestore();
  });

  it('should display error message when error is set', () => {
    component.error = 'Test error';
    fixture.detectChanges();

    const errorElement = fixture.nativeElement.querySelector('.alert-danger');
    expect(errorElement).toBeTruthy();
    expect(errorElement.textContent).toContain('Test error');
  });

  it('should display filial details when loaded', () => {
    component.filial = mockFilial;
    component.loading = false;
    fixture.detectChanges();

    const card = fixture.nativeElement.querySelector('.filial-card');
    expect(card).toBeTruthy();
    expect(card.textContent).toContain('Filial Centro');
  });

  it('should not load filial if id is not provided', () => {
    activatedRoute.snapshot.paramMap.get.mockReturnValue(null);
    const newFixture = TestBed.createComponent(FilialDetailComponent);
    newFixture.detectChanges();

    expect(filialService.getById).not.toHaveBeenCalled();
  });
});
