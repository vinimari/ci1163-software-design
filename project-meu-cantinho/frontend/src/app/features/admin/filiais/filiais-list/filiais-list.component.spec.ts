import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FiliaisListComponent } from './filiais-list.component';

describe('FiliaisListComponent', () => {
  let component: FiliaisListComponent;
  let fixture: ComponentFixture<FiliaisListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FiliaisListComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(FiliaisListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
