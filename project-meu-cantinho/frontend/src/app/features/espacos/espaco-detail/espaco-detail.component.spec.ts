import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of, throwError } from 'rxjs';
import { EspacoDetailComponent } from './espaco-detail.component';
import { EspacoService } from '../../../core/services/espaco.service';
import { EspacoResponse } from '../../../core/models';

describe('EspacoDetailComponent', () => {
  let component: EspacoDetailComponent;
  let fixture: ComponentFixture<EspacoDetailComponent>;
  let espacoService: jest.Mocked<EspacoService>;
  let activatedRoute: any;

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
    const espacoServiceMock = {
      getById: jest.fn()
    };

    activatedRoute = {
      snapshot: {
        paramMap: {
          get: jest.fn()
        }
      }
    };

    await TestBed.configureTestingModule({
      imports: [EspacoDetailComponent],
      providers: [
        { provide: EspacoService, useValue: espacoServiceMock },
        { provide: ActivatedRoute, useValue: activatedRoute }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(EspacoDetailComponent);
    component = fixture.componentInstance;
    espacoService = TestBed.inject(EspacoService) as jest.Mocked<EspacoService>;
  });

  describe('Component Initialization', () => {
    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should initialize with null espaco', () => {
      expect(component.espaco).toBeNull();
    });

    it('should initialize with loading false', () => {
      expect(component.loading).toBe(false);
    });

    it('should initialize with empty error', () => {
      expect(component.error).toBe('');
    });
  });

  describe('ngOnInit', () => {
    it('should load espaco when id is present in route', () => {
      activatedRoute.snapshot.paramMap.get.mockReturnValue('1');
      espacoService.getById.mockReturnValue(of(mockEspaco));

      fixture.detectChanges();

      expect(activatedRoute.snapshot.paramMap.get).toHaveBeenCalledWith('id');
      expect(espacoService.getById).toHaveBeenCalledWith(1);
    });

    it('should not load espaco when id is not present', () => {
      activatedRoute.snapshot.paramMap.get.mockReturnValue(null);

      fixture.detectChanges();

      expect(espacoService.getById).not.toHaveBeenCalled();
    });

    it('should convert string id to number', () => {
      activatedRoute.snapshot.paramMap.get.mockReturnValue('42');
      espacoService.getById.mockReturnValue(of(mockEspaco));

      fixture.detectChanges();

      expect(espacoService.getById).toHaveBeenCalledWith(42);
    });
  });

  describe('loadEspaco', () => {
    it('should set loading to true when loading', () => {
      espacoService.getById.mockReturnValue(of(mockEspaco));

      component.loadEspaco(1);

      expect(component.loading).toBe(false); // já completou sincronamente
    });

    it('should clear error when loading', () => {
      component.error = 'Previous error';
      espacoService.getById.mockReturnValue(of(mockEspaco));

      component.loadEspaco(1);

      expect(component.error).toBe('');
    });

    it('should load espaco successfully', () => {
      espacoService.getById.mockReturnValue(of(mockEspaco));

      component.loadEspaco(1);

      expect(component.espaco).toEqual(mockEspaco);
      expect(component.loading).toBe(false);
      expect(component.error).toBe('');
    });

    it('should call espacoService.getById with correct id', () => {
      espacoService.getById.mockReturnValue(of(mockEspaco));

      component.loadEspaco(123);

      expect(espacoService.getById).toHaveBeenCalledWith(123);
    });

    it('should handle error when loading fails', () => {
      const consoleError = jest.spyOn(console, 'error').mockImplementation();
      const error = new Error('Network error');
      espacoService.getById.mockReturnValue(throwError(() => error));

      component.loadEspaco(1);

      expect(component.loading).toBe(false);
      expect(component.error).toBe('Erro ao carregar detalhes do espaço.');
      expect(component.espaco).toBeNull();
      expect(consoleError).toHaveBeenCalledWith('Error loading espaco:', error);
      consoleError.mockRestore();
    });

    it('should set loading to false after error', () => {
      espacoService.getById.mockReturnValue(throwError(() => new Error('Error')));

      component.loadEspaco(1);

      expect(component.loading).toBe(false);
    });

    it('should set error message on failure', () => {
      espacoService.getById.mockReturnValue(throwError(() => new Error('Error')));

      component.loadEspaco(1);

      expect(component.error).toBe('Erro ao carregar detalhes do espaço.');
    });
  });

  describe('Integration', () => {
    it('should load espaco on initialization with valid id', () => {
      activatedRoute.snapshot.paramMap.get.mockReturnValue('1');
      espacoService.getById.mockReturnValue(of(mockEspaco));

      fixture.detectChanges();

      expect(component.espaco).toEqual(mockEspaco);
      expect(component.loading).toBe(false);
      expect(component.error).toBe('');
    });

    it('should handle error on initialization', () => {
      const consoleError = jest.spyOn(console, 'error').mockImplementation();
      activatedRoute.snapshot.paramMap.get.mockReturnValue('999');
      espacoService.getById.mockReturnValue(throwError(() => new Error('Not found')));

      fixture.detectChanges();

      expect(component.error).toBe('Erro ao carregar detalhes do espaço.');
      expect(component.loading).toBe(false);
      consoleError.mockRestore();
    });
  });
});
