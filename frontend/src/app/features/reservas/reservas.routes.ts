import { Routes } from '@angular/router';

export const RESERVAS_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () => import('./reservas-list/reservas-list.component').then(m => m.ReservasListComponent)
  },
  {
    path: 'nova',
    loadComponent: () => import('./reserva-form/reserva-form.component').then(m => m.ReservaFormComponent)
  },
  {
    path: ':id',
    loadComponent: () => import('./reserva-detail/reserva-detail.component').then(m => m.ReservaDetailComponent)
  }
];
