import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { PerfilUsuario } from '../models';

export const roleGuard = (allowedRoles: PerfilUsuario[]): CanActivateFn => {
  return () => {
    const authService = inject(AuthService);
    const router = inject(Router);
    const currentUser = authService.getCurrentUser();

    if (!currentUser) {
      router.navigate(['/login']);
      return false;
    }

    if (allowedRoles.includes(currentUser.perfil)) {
      return true;
    }

    router.navigate(['/unauthorized']);
    return false;
  };
};
