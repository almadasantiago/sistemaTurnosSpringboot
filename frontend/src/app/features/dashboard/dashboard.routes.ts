import { Routes } from '@angular/router';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { roleGuard } from '../../core/guards/role.guard';
import { AuthService } from '../../core/services/auth.service';

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
    path: 'client',
    canActivate: [roleGuard('CLIENT')],
    loadComponent: () => import('./client-dashboard/client-dashboard').then(c => c.ClientDashboardComponent)
  },
  {
    path: '',
    canActivate: [() => {
      const auth = inject(AuthService);
      const router = inject(Router);
      const role = auth.currentRole();
      if (role === 'ADMIN') return router.createUrlTree(['/dashboard/admin']);
      if (role === 'BARBER') return router.createUrlTree(['/dashboard/barber']);
      if (role === 'CLIENT') return router.createUrlTree(['/dashboard/client']);
      return router.createUrlTree(['/auth/login']);
    }],
    children: []
  }
];