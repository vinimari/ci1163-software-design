import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const authService = inject(AuthService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
        authService.logout();
        router.navigate(['/login']);
      }

      let errorMessage = 'Ocorreu um erro inesperado';

      if (error.error && typeof error.error === 'object') {
        errorMessage = error.error.message || error.error.error || error.statusText;
      } else if (typeof error.error === 'string') {
        errorMessage = error.error;
      } else if (error.message) {
        errorMessage = error.message;
      }

      const enhancedError = new HttpErrorResponse({
        error: { ...error.error, extractedMessage: errorMessage },
        headers: error.headers,
        status: error.status,
        statusText: error.statusText,
        url: error.url || undefined
      });

      return throwError(() => enhancedError);
    })
  );
};
