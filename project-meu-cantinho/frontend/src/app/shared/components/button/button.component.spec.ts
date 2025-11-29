import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ButtonComponent } from './button.component';

describe('ButtonComponent', () => {
  let component: ButtonComponent;
  let fixture: ComponentFixture<ButtonComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ButtonComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(ButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('deve criar', () => {
    expect(component).toBeTruthy();
  });

  it('deve ter default variant as primary', () => {
    expect(component.variant).toBe('primary');
  });

  it('deve ter default size as medium', () => {
    expect(component.size).toBe('medium');
  });

  it('deve ter default type as button', () => {
    expect(component.type).toBe('button');
  });

  it('não deve be disabled by default', () => {
    expect(component.disabled).toBe(false);
  });

  it('não deve be full width by default', () => {
    expect(component.fullWidth).toBe(false);
  });

  it('deve emit clicked event when not disabled', () => {
    const event = new Event('click');
    jest.spyOn(component.clicked, 'emit');

    component.onClick(event);

    expect(component.clicked.emit).toHaveBeenCalledWith(event);
  });

  it('não deve emit clicked event when disabled', () => {
    const event = new Event('click');
    component.disabled = true;
    jest.spyOn(component.clicked, 'emit');

    component.onClick(event);

    expect(component.clicked.emit).not.toHaveBeenCalled();
  });

  it('deve accept icon input', () => {
    component.icon = '🏠';
    fixture.detectChanges();

    expect(component.icon).toBe('🏠');
  });

  it('deve apply correct CSS classes', () => {
    component.variant = 'success';
    component.size = 'large';
    component.fullWidth = true;
    fixture.detectChanges();

    const button = fixture.nativeElement.querySelector('button');
    expect(button.classList.contains('btn')).toBe(true);
    expect(button.classList.contains('btn-success')).toBe(true);
    expect(button.classList.contains('btn-large')).toBe(true);
    expect(button.classList.contains('btn-full-width')).toBe(true);
  });

  it('deve render icon when provided', () => {
    component.icon = '✨';
    fixture.detectChanges();

    const icon = fixture.nativeElement.querySelector('.btn-icon');
    expect(icon).toBeTruthy();
    expect(icon.textContent).toBe('✨');
  });

  it('não deve render icon span when not provided', () => {
    fixture.detectChanges();

    const icon = fixture.nativeElement.querySelector('.btn-icon');
    expect(icon).toBeFalsy();
  });

  it('deve disable button when disabled is true', () => {
    component.disabled = true;
    fixture.detectChanges();

    const button = fixture.nativeElement.querySelector('button');
    expect(button.disabled).toBe(true);
  });
});
