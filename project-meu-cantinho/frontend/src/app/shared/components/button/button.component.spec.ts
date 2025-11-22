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

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have default variant as primary', () => {
    expect(component.variant).toBe('primary');
  });

  it('should have default size as medium', () => {
    expect(component.size).toBe('medium');
  });

  it('should have default type as button', () => {
    expect(component.type).toBe('button');
  });

  it('should not be disabled by default', () => {
    expect(component.disabled).toBe(false);
  });

  it('should not be full width by default', () => {
    expect(component.fullWidth).toBe(false);
  });

  it('should emit clicked event when not disabled', () => {
    const event = new Event('click');
    jest.spyOn(component.clicked, 'emit');

    component.onClick(event);

    expect(component.clicked.emit).toHaveBeenCalledWith(event);
  });

  it('should not emit clicked event when disabled', () => {
    const event = new Event('click');
    component.disabled = true;
    jest.spyOn(component.clicked, 'emit');

    component.onClick(event);

    expect(component.clicked.emit).not.toHaveBeenCalled();
  });

  it('should accept icon input', () => {
    component.icon = '🏠';
    fixture.detectChanges();

    expect(component.icon).toBe('🏠');
  });

  it('should apply correct CSS classes', () => {
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

  it('should render icon when provided', () => {
    component.icon = '✨';
    fixture.detectChanges();

    const icon = fixture.nativeElement.querySelector('.btn-icon');
    expect(icon).toBeTruthy();
    expect(icon.textContent).toBe('✨');
  });

  it('should not render icon span when not provided', () => {
    fixture.detectChanges();

    const icon = fixture.nativeElement.querySelector('.btn-icon');
    expect(icon).toBeFalsy();
  });

  it('should disable button when disabled is true', () => {
    component.disabled = true;
    fixture.detectChanges();

    const button = fixture.nativeElement.querySelector('button');
    expect(button.disabled).toBe(true);
  });
});
