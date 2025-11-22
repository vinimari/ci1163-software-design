import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of, throwError } from 'rxjs';
import { EspacosListComponent } from './espacos-list.component';
import { EspacoService } from '../../../core/services/espaco.service';
import { EspacoResponse } from '../../../core/models';

describe('EspacosListComponent', () => {
  let component: EspacosListComponent;
  let fixture: ComponentFixture<EspacosListComponent>;
  let espacoService: jest.Mocked<EspacoService>;

  const mockEspacos: EspacoResponse[] = [
    {
      id: 1,
      nome: 'Sala de Reunião',
      descricao: 'Sala ampla',
      capacidade: 10,
      precoDiaria: 150.00,
      ativo: true,
      urlFotoPrincipal: 'https://example.com/foto1.jpg',
      filial: { id: 1, nome: 'Filial Centro', cidade: 'Curitiba', estado: 'PR' }
    },
    {
      id: 2,
      nome: 'Auditório',
      descricao: 'Auditório grande',
      capacidade: 50,
      precoDiaria: 500.00,
      ativo: true,
      urlFotoPrincipal: 'https://example.com/foto2.jpg',
      filial: { id: 2, nome: 'Filial Norte', cidade: 'São Paulo', estado: 'SP' }
    }
  ];

  beforeEach(async () => {
    const espacoServiceMock = {
      getAtivos: jest.fn()
    };

    const activatedRouteMock = {
      snapshot: {
        paramMap: {
          get: jest.fn()
        }
      }
    };

    await TestBed.configureTestingModule({
      imports: [EspacosListComponent],
      providers: [
        { provide: EspacoService, useValue: espacoServiceMock },
        { provide: ActivatedRoute, useValue: activatedRouteMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(EspacosListComponent);
    component = fixture.componentInstance;
    espacoService = TestBed.inject(EspacoService) as jest.Mocked<EspacoService>;
  });

  describe('Component Initialization', () => {
    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should initialize with empty espacos array', () => {
      expect(component.espacos).toEqual([]);
    });

    it('should initialize with loading false', () => {
      expect(component.loading).toBe(false);
    });

    it('should initialize with empty error', () => {
      expect(component.error).toBe('');
    });
  });

  describe('ngOnInit', () => {
    it('should call loadEspacos on initialization', () => {
      espacoService.getAtivos.mockReturnValue(of(mockEspacos));
      const loadSpy = jest.spyOn(component, 'loadEspacos');

      fixture.detectChanges();

      expect(loadSpy).toHaveBeenCalled();
    });

    it('should load espacos on initialization', () => {
      espacoService.getAtivos.mockReturnValue(of(mockEspacos));

      fixture.detectChanges();

      expect(espacoService.getAtivos).toHaveBeenCalled();
      expect(component.espacos).toEqual(mockEspacos);
    });
  });

  describe('loadEspacos', () => {
    it('should clear error when loading', () => {
      component.error = 'Previous error';
      espacoService.getAtivos.mockReturnValue(of(mockEspacos));

      component.loadEspacos();

      expect(component.error).toBe('');
    });

    it('should load espacos successfully', () => {
      espacoService.getAtivos.mockReturnValue(of(mockEspacos));

      component.loadEspacos();

      expect(component.espacos).toEqual(mockEspacos);
      expect(component.loading).toBe(false);
      expect(component.error).toBe('');
    });

    it('should call espacoService.getAtivos', () => {
      espacoService.getAtivos.mockReturnValue(of(mockEspacos));

      component.loadEspacos();

      expect(espacoService.getAtivos).toHaveBeenCalled();
    });

    it('should set loading to false after success', () => {
      espacoService.getAtivos.mockReturnValue(of(mockEspacos));

      component.loadEspacos();

      expect(component.loading).toBe(false);
    });

    it('should handle empty espacos list', () => {
      espacoService.getAtivos.mockReturnValue(of([]));

      component.loadEspacos();

      expect(component.espacos).toEqual([]);
      expect(component.loading).toBe(false);
      expect(component.error).toBe('');
    });

    it('should handle error when loading fails', () => {
      const consoleError = jest.spyOn(console, 'error').mockImplementation();
      const error = new Error('Network error');
      espacoService.getAtivos.mockReturnValue(throwError(() => error));

      component.loadEspacos();

      expect(component.loading).toBe(false);
      expect(component.error).toBe('Erro ao carregar espaços.');
      expect(component.espacos).toEqual([]);
      expect(consoleError).toHaveBeenCalledWith('Error loading espacos:', error);
      consoleError.mockRestore();
    });

    it('should set loading to false after error', () => {
      espacoService.getAtivos.mockReturnValue(throwError(() => new Error('Error')));

      component.loadEspacos();

      expect(component.loading).toBe(false);
    });

    it('should set error message on failure', () => {
      espacoService.getAtivos.mockReturnValue(throwError(() => new Error('Error')));

      component.loadEspacos();

      expect(component.error).toBe('Erro ao carregar espaços.');
    });

    it('should not modify espacos on error', () => {
      component.espacos = mockEspacos;
      espacoService.getAtivos.mockReturnValue(throwError(() => new Error('Error')));

      component.loadEspacos();

      expect(component.espacos).toEqual(mockEspacos);
    });
  });

  describe('Integration', () => {
    it('should load espacos on initialization', () => {
      espacoService.getAtivos.mockReturnValue(of(mockEspacos));

      fixture.detectChanges();

      expect(component.espacos).toEqual(mockEspacos);
      expect(component.loading).toBe(false);
      expect(component.error).toBe('');
    });

    it('should handle error on initialization', () => {
      const consoleError = jest.spyOn(console, 'error').mockImplementation();
      espacoService.getAtivos.mockReturnValue(throwError(() => new Error('Failed')));

      fixture.detectChanges();

      expect(component.error).toBe('Erro ao carregar espaços.');
      expect(component.loading).toBe(false);
      consoleError.mockRestore();
    });
  });
});
