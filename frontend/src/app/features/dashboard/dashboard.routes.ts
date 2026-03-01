import { Routes } from '@angular/router';
import { roleGuard } from '../../core/guards/role.guard';

export const DASHBOARD_ROUTES: Routes = [
  {
    path: 'admin',
    canActivate: [roleGuard('ADMIN')],
    loadComponent: () => import('./admin-dashboard/admin-dashboard.component').then(c => c.AdminDashboardComponent)
  },
  {
    path: 'barber',
    canActivate: [roleGuard('BARBER')],
    loadComponent: () => import('./barber-dashboard/barber-dashboard.component').then(c => c.BarberDashboardComponent)
  },
  {
    path: '',
    redirectTo: 'barber',
    pathMatch: 'full'
  }
];