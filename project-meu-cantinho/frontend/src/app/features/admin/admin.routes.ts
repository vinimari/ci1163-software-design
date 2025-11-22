import { Routes } from '@angular/router';
import { roleGuard } from '../../core/guards/role.guard';
import { PerfilUsuario } from '../../core/models';

export const ADMIN_ROUTES: Routes = [
  {
    path: 'filiais',
    canActivate: [roleGuard([PerfilUsuario.ADMIN])],
    children: [
      {
        path: '',
        loadComponent: () => import('./filiais/filiais-list/filiais-list.component').then(m => m.FiliaisListComponent)
      },
      {
        path: 'new',
        loadComponent: () => import('./filiais/filial-form/filial-form.component').then(m => m.FilialFormComponent)
      },
      {
        path: ':id',
        loadComponent: () => import('./filiais/filial-detail/filial-detail.component').then(m => m.FilialDetailComponent)
      },
      {
        path: ':id/edit',
        loadComponent: () => import('./filiais/filial-form/filial-form.component').then(m => m.FilialFormComponent)
      }
    ]
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
