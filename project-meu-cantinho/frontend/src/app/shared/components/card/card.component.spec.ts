import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CardComponent } from './card.component';

describe('CardComponent', () => {
  let component: CardComponent;
  let fixture: ComponentFixture<CardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CardComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(CardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  describe('Component Initialization', () => {
    it('deve criar', () => {
      expect(component).toBeTruthy();
    });

    it('deve inicializar with undefined title', () => {
      expect(component.title).toBeUndefined();
    });

    it('deve inicializar hover as false', () => {
      expect(component.hover).toBe(false);
    });

    it('deve inicializar hasFooter as false', () => {
      expect(component.hasFooter).toBe(false);
    });
  });

  describe('Input Properties', () => {
    it('deve accept title input', () => {
      component.title = 'Test Title';
      expect(component.title).toBe('Test Title');
    });

    it('deve accept hover input', () => {
      component.hover = true;
      expect(component.hover).toBe(true);
    });

    it('deve accept hasFooter input', () => {
      component.hasFooter = true;
      expect(component.hasFooter).toBe(true);
    });

    it('deve handle empty title', () => {
      component.title = '';
      expect(component.title).toBe('');
    });
  });
});
