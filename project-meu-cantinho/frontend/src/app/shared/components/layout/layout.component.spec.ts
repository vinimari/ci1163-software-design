import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { LayoutComponent } from './layout.component';
import { AuthService } from '../../../core/services/auth.service';
import { of } from 'rxjs';

describe('LayoutComponent', () => {
  let component: LayoutComponent;
  let fixture: ComponentFixture<LayoutComponent>;

  beforeEach(async () => {
    const authServiceMock = {
      currentUser$: of(null),
      isAdmin: jest.fn(),
      isFuncionario: jest.fn(),
      isCliente: jest.fn(),
      isAuthenticated: jest.fn().mockReturnValue(false)
    };

    await TestBed.configureTestingModule({
      imports: [LayoutComponent, RouterTestingModule],
      providers: [
        { provide: AuthService, useValue: authServiceMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LayoutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
