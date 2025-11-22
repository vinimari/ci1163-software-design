import { Routes } from '@angular/router';

export const ESPACOS_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () => import('./espacos-list/espacos-list.component').then(m => m.EspacosListComponent)
  },
  {
    path: ':id',
    loadComponent: () => import('./espaco-detail/espaco-detail.component').then(m => m.EspacoDetailComponent)
  }
];
