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
    it('deve criar', () => {
      expect(component).toBeTruthy();
    });

    it('deve inicializar with empty espacos array', () => {
      expect(component.espacos).toEqual([]);
    });

    it('deve inicializar with loading false', () => {
      expect(component.loading).toBe(false);
    });

    it('deve inicializar with empty error', () => {
      expect(component.error).toBe('');
    });
  });

  describe('ngOnInit', () => {
    it('deve call loadEspacos on initialization', () => {
      espacoService.getAtivos.mockReturnValue(of(mockEspacos));
      const loadSpy = jest.spyOn(component, 'loadEspacos');

      fixture.detectChanges();

      expect(loadSpy).toHaveBeenCalled();
    });

    it('deve carregar espacos on initialization', () => {
      espacoService.getAtivos.mockReturnValue(of(mockEspacos));

      fixture.detectChanges();

      expect(espacoService.getAtivos).toHaveBeenCalled();
      expect(component.espacos).toEqual(mockEspacos);
    });
  });

  describe('loadEspacos', () => {
    it('deve clear error when loading', () => {
      component.error = 'Previous error';
      espacoService.getAtivos.mockReturnValue(of(mockEspacos));

      component.loadEspacos();

      expect(component.error).toBe('');
    });

    it('deve carregar espacos successfully', () => {
      espacoService.getAtivos.mockReturnValue(of(mockEspacos));

      component.loadEspacos();

      expect(component.espacos).toEqual(mockEspacos);
      expect(component.loading).toBe(false);
      expect(component.error).toBe('');
    });

    it('deve call espacoService.getAtivos', () => {
      espacoService.getAtivos.mockReturnValue(of(mockEspacos));

      component.loadEspacos();

      expect(espacoService.getAtivos).toHaveBeenCalled();
    });

    it('deve definir loading to false after success', () => {
      espacoService.getAtivos.mockReturnValue(of(mockEspacos));

      component.loadEspacos();

      expect(component.loading).toBe(false);
    });

    it('deve handle empty espacos list', () => {
      espacoService.getAtivos.mockReturnValue(of([]));

      component.loadEspacos();

      expect(component.espacos).toEqual([]);
      expect(component.loading).toBe(false);
      expect(component.error).toBe('');
    });

    it('deve tratar erro when loading fails', () => {
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

    it('deve definir loading to false after error', () => {
      espacoService.getAtivos.mockReturnValue(throwError(() => new Error('Error')));

      component.loadEspacos();

      expect(component.loading).toBe(false);
    });

    it('deve definir error message on failure', () => {
      espacoService.getAtivos.mockReturnValue(throwError(() => new Error('Error')));

      component.loadEspacos();

      expect(component.error).toBe('Erro ao carregar espaços.');
    });

    it('não deve modify espacos on error', () => {
      component.espacos = mockEspacos;
      espacoService.getAtivos.mockReturnValue(throwError(() => new Error('Error')));

      component.loadEspacos();

      expect(component.espacos).toEqual(mockEspacos);
    });
  });

  describe('Integration', () => {
    it('deve carregar espacos on initialization', () => {
      espacoService.getAtivos.mockReturnValue(of(mockEspacos));

      fixture.detectChanges();

      expect(component.espacos).toEqual(mockEspacos);
      expect(component.loading).toBe(false);
      expect(component.error).toBe('');
    });

    it('deve tratar erro on initialization', () => {
      const consoleError = jest.spyOn(console, 'error').mockImplementation();
      espacoService.getAtivos.mockReturnValue(throwError(() => new Error('Failed')));

      fixture.detectChanges();

      expect(component.error).toBe('Erro ao carregar espaços.');
      expect(component.loading).toBe(false);
      consoleError.mockRestore();
    });
  });
});
