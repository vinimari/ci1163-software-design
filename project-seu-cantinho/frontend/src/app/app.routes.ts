import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';
import { LoginComponent } from './features/auth/login/login.component';
import { UnauthorizedComponent } from './features/auth/unauthorized/unauthorized.component';
import { HomeComponent } from './features/home/home.component';
import { LayoutComponent } from './shared/components/layout/layout.component';
import { PerfilUsuario } from './core/models';

export const routes: Routes = [
  {
    path: 'login',
    component: LoginComponent
  },
  {
    path: 'register',
    loadComponent: () => import('./features/register/register.component').then(m => m.RegisterComponent)
  },
  {
    path: 'unauthorized',
    component: UnauthorizedComponent
  },
  {
    path: '',
    component: LayoutComponent,
    canActivate: [authGuard],
    children: [
      {
        path: '',
        component: HomeComponent
      },
      {
        path: 'espacos',
        loadChildren: () => import('./features/espacos/espacos.routes').then(m => m.ESPACOS_ROUTES)
      },
      {
        path: 'reservas',
        canActivate: [roleGuard([PerfilUsuario.CLIENTE])],
        loadChildren: () => import('./features/reservas/reservas.routes').then(m => m.RESERVAS_ROUTES)
      },
      {
        path: 'admin',
        canActivate: [roleGuard([PerfilUsuario.ADMIN])],
        loadChildren: () => import('./features/admin/admin.routes').then(m => m.ADMIN_ROUTES)
      },
      {
        path: 'funcionario',
        canActivate: [roleGuard([PerfilUsuario.FUNCIONARIO])],
        children: [
          {
            path: 'espacos',
            loadComponent: () => import('./features/admin/espacos/espacos-admin/espacos-admin.component')
              .then(m => m.EspacosAdminComponent)
          },
          {
            path: 'clientes',
            loadComponent: () => import('./features/admin/admin.routes')
              .then(() => import('./features/auth/login/login.component').then(m => m.LoginComponent)) // Placeholder
          },
          {
            path: 'reservas',
            loadComponent: () => import('./features/admin/reservas/reservas-admin/reservas-admin.component')
              .then(m => m.ReservasAdminComponent)
          },
          {
            path: 'reservas/:id',
            loadComponent: () => import('./features/admin/reservas/reserva-detail/reserva-detail.component')
              .then(m => m.ReservaDetailComponent)
          }
        ]
      }
    ]
  },
  {
    path: '**',
    redirectTo: ''
  }
];
