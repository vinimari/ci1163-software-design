import { Routes } from '@angular/router';
import { roleGuard } from '../../core/guards/role.guard';
import { PerfilUsuario } from '../../core/models';

export const ADMIN_ROUTES: Routes = [
  {
    path: 'filiais',
    canActivate: [roleGuard([PerfilUsuario.ADMIN])],
    loadComponent: () => import('./filiais/filiais-list/filiais-list.component').then(m => m.FiliaisListComponent)
  },
  {
    path: 'espacos',
    canActivate: [roleGuard([PerfilUsuario.ADMIN, PerfilUsuario.FUNCIONARIO])],
    loadComponent: () => import('./espacos/espacos-admin/espacos-admin.component').then(m => m.EspacosAdminComponent)
  },
  {
    path: 'reservas',
    canActivate: [roleGuard([PerfilUsuario.ADMIN, PerfilUsuario.FUNCIONARIO])],
    loadComponent: () => import('./reservas/reservas-admin/reservas-admin.component').then(m => m.ReservasAdminComponent)
  }
];
