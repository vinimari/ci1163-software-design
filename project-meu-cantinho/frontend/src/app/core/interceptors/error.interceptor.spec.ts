import { TestBed } from '@angular/core/testing';
import { HttpClient, HttpErrorResponse, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { Router } from '@angular/router';
import { errorInterceptor } from './error.interceptor';
import { AuthService } from '../services/auth.service';

describe('errorInterceptor', () => {
  let httpClient: HttpClient;
  let httpMock: HttpTestingController;
  let authService: jest.Mocked<AuthService>;
  let router: jest.Mocked<Router>;

  beforeEach(() => {
    const authServiceMock = {
      logout: jest.fn()
    };

    const routerMock = {
      navigate: jest.fn()
    };

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([errorInterceptor])),
        provideHttpClientTesting(),
        { provide: AuthService, useValue: authServiceMock },
        { provide: Router, useValue: routerMock }
      ]
    });

    httpClient = TestBed.inject(HttpClient);
    httpMock = TestBed.inject(HttpTestingController);
    authService = TestBed.inject(AuthService) as jest.Mocked<AuthService>;
    router = TestBed.inject(Router) as jest.Mocked<Router>;
  });

  afterEach(() => {
    httpMock.verify();
  });

  describe('401 Unauthorized Errors', () => {
    it('should call logout when receiving 401 error', () => {
      const testUrl = '/api/test';

      httpClient.get(testUrl).subscribe({
        next: () => fail('should have failed with 401 error'),
        error: (error: HttpErrorResponse) => {
          expect(error.status).toBe(401);
        }
      });

      const req = httpMock.expectOne(testUrl);
      req.flush('Unauthorized', { status: 401, statusText: 'Unauthorized' });

      expect(authService.logout).toHaveBeenCalled();
    });

    it('should navigate to /login when receiving 401 error', () => {
      const testUrl = '/api/test';

      httpClient.get(testUrl).subscribe({
        next: () => fail('should have failed with 401 error'),
        error: (error: HttpErrorResponse) => {
          expect(error.status).toBe(401);
        }
      });

      const req = httpMock.expectOne(testUrl);
      req.flush('Unauthorized', { status: 401, statusText: 'Unauthorized' });

      expect(router.navigate).toHaveBeenCalledWith(['/login']);
    });

    it('should call logout before navigating to login', () => {
      const testUrl = '/api/test';
      const callOrder: string[] = [];

      authService.logout.mockImplementation(() => {
        callOrder.push('logout');
      });

      router.navigate.mockImplementation(() => {
        callOrder.push('navigate');
        return Promise.resolve(true);
      });

      httpClient.get(testUrl).subscribe({
        next: () => fail('should have failed with 401 error'),
        error: (error: HttpErrorResponse) => {
          expect(error.status).toBe(401);
        }
      });

      const req = httpMock.expectOne(testUrl);
      req.flush('Unauthorized', { status: 401, statusText: 'Unauthorized' });

      expect(callOrder).toEqual(['logout', 'navigate']);
    });

    it('should still throw the error after handling 401', () => {
      const testUrl = '/api/test';
      let errorThrown = false;

      httpClient.get(testUrl).subscribe({
        next: () => fail('should have failed with 401 error'),
        error: (error: HttpErrorResponse) => {
          errorThrown = true;
          expect(error.status).toBe(401);
          expect(error.statusText).toBe('Unauthorized');
        }
      });

      const req = httpMock.expectOne(testUrl);
      req.flush('Unauthorized', { status: 401, statusText: 'Unauthorized' });

      expect(errorThrown).toBe(true);
      expect(authService.logout).toHaveBeenCalled();
      expect(router.navigate).toHaveBeenCalledWith(['/login']);
    });

    it('should handle 401 error with custom error message', () => {
      const testUrl = '/api/test';
      const errorMessage = 'Token expired';

      httpClient.get(testUrl).subscribe({
        next: () => fail('should have failed with 401 error'),
        error: (error: HttpErrorResponse) => {
          expect(error.status).toBe(401);
          expect(error.error).toBe(errorMessage);
        }
      });

      const req = httpMock.expectOne(testUrl);
      req.flush(errorMessage, { status: 401, statusText: 'Unauthorized' });

      expect(authService.logout).toHaveBeenCalled();
      expect(router.navigate).toHaveBeenCalledWith(['/login']);
    });

    it('should handle 401 error with JSON response', () => {
      const testUrl = '/api/test';
      const errorResponse = { message: 'Session expired', code: 'AUTH_001' };

      httpClient.get(testUrl).subscribe({
        next: () => fail('should have failed with 401 error'),
        error: (error: HttpErrorResponse) => {
          expect(error.status).toBe(401);
          expect(error.error).toEqual(errorResponse);
        }
      });

      const req = httpMock.expectOne(testUrl);
      req.flush(errorResponse, { status: 401, statusText: 'Unauthorized' });

      expect(authService.logout).toHaveBeenCalled();
      expect(router.navigate).toHaveBeenCalledWith(['/login']);
    });
  });

  describe('Other HTTP Errors', () => {
    it('should not call logout for 400 Bad Request', () => {
      const testUrl = '/api/test';

      httpClient.get(testUrl).subscribe({
        next: () => fail('should have failed with 400 error'),
        error: (error: HttpErrorResponse) => {
          expect(error.status).toBe(400);
        }
      });

      const req = httpMock.expectOne(testUrl);
      req.flush('Bad Request', { status: 400, statusText: 'Bad Request' });

      expect(authService.logout).not.toHaveBeenCalled();
      expect(router.navigate).not.toHaveBeenCalled();
    });

    it('should not call logout for 403 Forbidden', () => {
      const testUrl = '/api/test';

      httpClient.get(testUrl).subscribe({
        next: () => fail('should have failed with 403 error'),
        error: (error: HttpErrorResponse) => {
          expect(error.status).toBe(403);
        }
      });

      const req = httpMock.expectOne(testUrl);
      req.flush('Forbidden', { status: 403, statusText: 'Forbidden' });

      expect(authService.logout).not.toHaveBeenCalled();
      expect(router.navigate).not.toHaveBeenCalled();
    });

    it('should not call logout for 404 Not Found', () => {
      const testUrl = '/api/test';

      httpClient.get(testUrl).subscribe({
        next: () => fail('should have failed with 404 error'),
        error: (error: HttpErrorResponse) => {
          expect(error.status).toBe(404);
        }
      });

      const req = httpMock.expectOne(testUrl);
      req.flush('Not Found', { status: 404, statusText: 'Not Found' });

      expect(authService.logout).not.toHaveBeenCalled();
      expect(router.navigate).not.toHaveBeenCalled();
    });

    it('should not call logout for 500 Internal Server Error', () => {
      const testUrl = '/api/test';

      httpClient.get(testUrl).subscribe({
        next: () => fail('should have failed with 500 error'),
        error: (error: HttpErrorResponse) => {
          expect(error.status).toBe(500);
        }
      });

      const req = httpMock.expectOne(testUrl);
      req.flush('Internal Server Error', { status: 500, statusText: 'Internal Server Error' });

      expect(authService.logout).not.toHaveBeenCalled();
      expect(router.navigate).not.toHaveBeenCalled();
    });

    it('should still throw error for non-401 status codes', () => {
      const testUrl = '/api/test';
      let errorThrown = false;

      httpClient.get(testUrl).subscribe({
        next: () => fail('should have failed with 500 error'),
        error: (error: HttpErrorResponse) => {
          errorThrown = true;
          expect(error.status).toBe(500);
          expect(error.statusText).toBe('Internal Server Error');
        }
      });

      const req = httpMock.expectOne(testUrl);
      req.flush('Server error', { status: 500, statusText: 'Internal Server Error' });

      expect(errorThrown).toBe(true);
    });
  });

  describe('Successful Requests', () => {
    it('should not interfere with successful requests', () => {
      const testUrl = '/api/test';
      const testData = { id: 1, name: 'Test' };

      httpClient.get(testUrl).subscribe({
        next: (data) => {
          expect(data).toEqual(testData);
        },
        error: () => fail('should not have failed')
      });

      const req = httpMock.expectOne(testUrl);
      req.flush(testData);

      expect(authService.logout).not.toHaveBeenCalled();
      expect(router.navigate).not.toHaveBeenCalled();
    });

    it('should pass through successful POST requests', () => {
      const testUrl = '/api/test';
      const postData = { name: 'New Item' };
      const responseData = { id: 1, ...postData };

      httpClient.post(testUrl, postData).subscribe({
        next: (data) => {
          expect(data).toEqual(responseData);
        },
        error: () => fail('should not have failed')
      });

      const req = httpMock.expectOne(testUrl);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(postData);
      req.flush(responseData);

      expect(authService.logout).not.toHaveBeenCalled();
      expect(router.navigate).not.toHaveBeenCalled();
    });

    it('should pass through successful PUT requests', () => {
      const testUrl = '/api/test/1';
      const putData = { id: 1, name: 'Updated Item' };

      httpClient.put(testUrl, putData).subscribe({
        next: (data) => {
          expect(data).toEqual(putData);
        },
        error: () => fail('should not have failed')
      });

      const req = httpMock.expectOne(testUrl);
      expect(req.request.method).toBe('PUT');
      req.flush(putData);

      expect(authService.logout).not.toHaveBeenCalled();
      expect(router.navigate).not.toHaveBeenCalled();
    });

    it('should pass through successful DELETE requests', () => {
      const testUrl = '/api/test/1';

      httpClient.delete(testUrl).subscribe({
        next: () => {
          expect(true).toBe(true);
        },
        error: () => fail('should not have failed')
      });

      const req = httpMock.expectOne(testUrl);
      expect(req.request.method).toBe('DELETE');
      req.flush(null);

      expect(authService.logout).not.toHaveBeenCalled();
      expect(router.navigate).not.toHaveBeenCalled();
    });
  });

  describe('Multiple Requests', () => {
    it('should handle multiple 401 errors correctly', () => {
      const testUrl1 = '/api/test1';
      const testUrl2 = '/api/test2';

      httpClient.get(testUrl1).subscribe({
        next: () => fail('should have failed'),
        error: (error: HttpErrorResponse) => {
          expect(error.status).toBe(401);
        }
      });

      httpClient.get(testUrl2).subscribe({
        next: () => fail('should have failed'),
        error: (error: HttpErrorResponse) => {
          expect(error.status).toBe(401);
        }
      });

      const req1 = httpMock.expectOne(testUrl1);
      const req2 = httpMock.expectOne(testUrl2);

      req1.flush('Unauthorized', { status: 401, statusText: 'Unauthorized' });
      req2.flush('Unauthorized', { status: 401, statusText: 'Unauthorized' });

      expect(authService.logout).toHaveBeenCalledTimes(2);
      expect(router.navigate).toHaveBeenCalledTimes(2);
    });

    it('should handle mixed success and error requests', () => {
      const successUrl = '/api/success';
      const errorUrl = '/api/error';
      const testData = { id: 1, name: 'Test' };

      httpClient.get(successUrl).subscribe({
        next: (data) => {
          expect(data).toEqual(testData);
        },
        error: () => fail('should not have failed')
      });

      httpClient.get(errorUrl).subscribe({
        next: () => fail('should have failed'),
        error: (error: HttpErrorResponse) => {
          expect(error.status).toBe(401);
        }
      });

      const successReq = httpMock.expectOne(successUrl);
      const errorReq = httpMock.expectOne(errorUrl);

      successReq.flush(testData);
      errorReq.flush('Unauthorized', { status: 401, statusText: 'Unauthorized' });

      expect(authService.logout).toHaveBeenCalledTimes(1);
      expect(router.navigate).toHaveBeenCalledTimes(1);
    });
  });

  describe('Edge Cases', () => {
    it('should handle 401 error with empty response body', () => {
      const testUrl = '/api/test';

      httpClient.get(testUrl).subscribe({
        next: () => fail('should have failed with 401 error'),
        error: (error: HttpErrorResponse) => {
          expect(error.status).toBe(401);
          expect(error.error).toBe('');
        }
      });

      const req = httpMock.expectOne(testUrl);
      req.flush('', { status: 401, statusText: 'Unauthorized' });

      expect(authService.logout).toHaveBeenCalled();
      expect(router.navigate).toHaveBeenCalledWith(['/login']);
    });

    it('should handle 401 error with null response', () => {
      const testUrl = '/api/test';

      httpClient.get(testUrl).subscribe({
        next: () => fail('should have failed with 401 error'),
        error: (error: HttpErrorResponse) => {
          expect(error.status).toBe(401);
        }
      });

      const req = httpMock.expectOne(testUrl);
      req.flush(null, { status: 401, statusText: 'Unauthorized' });

      expect(authService.logout).toHaveBeenCalled();
      expect(router.navigate).toHaveBeenCalledWith(['/login']);
    });

    it('should preserve error details when re-throwing', () => {
      const testUrl = '/api/test';
      const errorMessage = 'Custom error message';
      const errorHeaders = { 'X-Error-Code': 'AUTH_FAILED' };

      httpClient.get(testUrl).subscribe({
        next: () => fail('should have failed with 401 error'),
        error: (error: HttpErrorResponse) => {
          expect(error.status).toBe(401);
          expect(error.error).toBe(errorMessage);
          expect(error.headers.get('X-Error-Code')).toBe('AUTH_FAILED');
        }
      });

      const req = httpMock.expectOne(testUrl);
      req.flush(errorMessage, {
        status: 401,
        statusText: 'Unauthorized',
        headers: errorHeaders
      });

      expect(authService.logout).toHaveBeenCalled();
      expect(router.navigate).toHaveBeenCalledWith(['/login']);
    });
  });
});
