import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { FiliaisListComponent } from './filiais-list.component';
import { FilialService } from '../../../../core/services';

describe('FiliaisListComponent', () => {
  let component: FiliaisListComponent;
  let fixture: ComponentFixture<FiliaisListComponent>;
  let filialService: jest.Mocked<FilialService>;

  beforeEach(async () => {
    const filialServiceMock = {
      getAll: jest.fn().mockReturnValue(of([])),
      delete: jest.fn().mockReturnValue(of(undefined))
    };

    await TestBed.configureTestingModule({
      imports: [FiliaisListComponent, RouterTestingModule],
      providers: [
        { provide: FilialService, useValue: filialServiceMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(FiliaisListComponent);
    component = fixture.componentInstance;
    filialService = TestBed.inject(FilialService) as jest.Mocked<FilialService>;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
