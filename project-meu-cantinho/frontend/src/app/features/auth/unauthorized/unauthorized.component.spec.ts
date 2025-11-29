import { Router } from '@angular/router';
import { UnauthorizedComponent } from './unauthorized.component';

describe('UnauthorizedComponent', () => {
  let component: UnauthorizedComponent;
  let router: jest.Mocked<Router>;

  beforeEach(() => {
    router = {
      navigate: jest.fn()
    } as any;

    component = new UnauthorizedComponent(router);
  });

  it('deve criar', () => {
    expect(component).toBeTruthy();
  });

  describe('goBack', () => {
    it('deve navigate to home when goBack is called', () => {
      component.goBack();
      expect(router.navigate).toHaveBeenCalledWith(['/']);
    });

    it('deve call router.navigate once', () => {
      component.goBack();
      expect(router.navigate).toHaveBeenCalledTimes(1);
    });
  });
});
