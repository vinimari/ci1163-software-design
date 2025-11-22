import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { FiliaisListComponent } from './filiais-list.component';
import { FilialService } from '../../../../core/services';
import { FilialResponse } from '../../../../core/models';

describe('FiliaisListComponent', () => {
  let component: FiliaisListComponent;
  let fixture: ComponentFixture<FiliaisListComponent>;
  let filialService: jest.Mocked<FilialService>;
  let router: jest.Mocked<Router>;

  const mockFiliais: FilialResponse[] = [
    {
      id: 1,
      nome: 'Filial Centro',
      cidade: 'São Paulo',
      estado: 'SP',
      endereco: 'Rua A, 123',
      telefone: '(11) 1234-5678',
      dataCadastro: '2024-01-01'
    },
    {
      id: 2,
      nome: 'Filial Norte',
      cidade: 'Rio de Janeiro',
      estado: 'RJ',
      endereco: 'Av B, 456',
      telefone: '(21) 9876-5432',
      dataCadastro: '2024-01-02'
    }
  ];

  beforeEach(async () => {
    const filialServiceMock = {
      getAll: jest.fn().mockReturnValue(of(mockFiliais)),
      delete: jest.fn().mockReturnValue(of(undefined)),
      getById: jest.fn(),
      create: jest.fn(),
      update: jest.fn()
    };

    const routerMock = {
      navigate: jest.fn()
    };

    await TestBed.configureTestingModule({
      imports: [FiliaisListComponent],
      providers: [
        { provide: FilialService, useValue: filialServiceMock },
        { provide: Router, useValue: routerMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(FiliaisListComponent);
    component = fixture.componentInstance;
    filialService = TestBed.inject(FilialService) as jest.Mocked<FilialService>;
    router = TestBed.inject(Router) as jest.Mocked<Router>;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load filiais on init', () => {
    fixture.detectChanges();
    expect(filialService.getAll).toHaveBeenCalled();
    expect(component.filiais).toEqual(mockFiliais);
    expect(component.loading).toBe(false);
  });

  it('should handle error when loading filiais fails', () => {
    const consoleErrorSpy = jest.spyOn(console, 'error').mockImplementation();
    filialService.getAll.mockReturnValue(throwError(() => new Error('Error')));
    
    component.loadFiliais();
    
    expect(component.error).toBe('Erro ao carregar filiais');
    expect(component.loading).toBe(false);
    consoleErrorSpy.mockRestore();
  });

  it('should navigate to filial detail when viewFilial is called', () => {
    component.viewFilial(1);
    expect(router.navigate).toHaveBeenCalledWith(['/admin/filiais', 1]);
  });

  it('should navigate to filial edit when editFilial is called', () => {
    component.editFilial(1);
    expect(router.navigate).toHaveBeenCalledWith(['/admin/filiais', 1, 'edit']);
  });

  it('should navigate to create filial when createFilial is called', () => {
    component.createFilial();
    expect(router.navigate).toHaveBeenCalledWith(['/admin/filiais/new']);
  });

  it('should delete filial when confirmed', () => {
    const confirmSpy = jest.spyOn(window, 'confirm').mockReturnValue(true);
    filialService.delete.mockReturnValue(of(undefined));
    
    component.deleteFilial(1);
    
    expect(confirmSpy).toHaveBeenCalledWith('Tem certeza que deseja excluir esta filial?');
    expect(filialService.delete).toHaveBeenCalledWith(1);
    expect(filialService.getAll).toHaveBeenCalled();
    confirmSpy.mockRestore();
  });

  it('should not delete filial when not confirmed', () => {
    const confirmSpy = jest.spyOn(window, 'confirm').mockReturnValue(false);
    
    component.deleteFilial(1);
    
    expect(filialService.delete).not.toHaveBeenCalled();
    confirmSpy.mockRestore();
  });

  it('should handle error when delete fails', () => {
    const consoleErrorSpy = jest.spyOn(console, 'error').mockImplementation();
    const confirmSpy = jest.spyOn(window, 'confirm').mockReturnValue(true);
    filialService.delete.mockReturnValue(throwError(() => new Error('Error')));
    
    component.deleteFilial(1);
    
    expect(component.error).toBe('Erro ao excluir filial');
    expect(component.loading).toBe(false);
    consoleErrorSpy.mockRestore();
    confirmSpy.mockRestore();
  });

  it('should display error message when error is set', () => {
    filialService.getAll.mockReturnValue(throwError(() => new Error('Test error')));
    component.loadFiliais();
    fixture.detectChanges();
    
    const compiled = fixture.nativeElement;
    expect(compiled.textContent).toContain('Erro ao carregar filiais');
  });

  it('should display empty state when no filiais', () => {
    filialService.getAll.mockReturnValue(of([]));
    component.loadFiliais();
    fixture.detectChanges();
    
    const emptyState = fixture.nativeElement.querySelector('.empty-state');
    expect(emptyState).toBeTruthy();
  });

  it('should display filiais in grid', () => {
    component.filiais = mockFiliais;
    component.loading = false;
    fixture.detectChanges();
    
    const cards = fixture.nativeElement.querySelectorAll('.filial-card');
    expect(cards.length).toBe(2);
  });
});
