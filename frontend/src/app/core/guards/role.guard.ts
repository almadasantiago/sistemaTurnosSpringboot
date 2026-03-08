import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { UserRole } from '../models/user.model';

export const roleGuard = (requiredRole: UserRole): CanActivateFn => {
  return () => {
    const authService = inject(AuthService);
    const router = inject(Router);

    if (authService.hasRole(requiredRole)) {
      return true;
    }

    const role = authService.currentRole();
    if (role === 'ADMIN') return router.createUrlTree(['/dashboard/admin']);
    if (role === 'BARBER') return router.createUrlTree(['/dashboard/barber']);
    if (role === 'CLIENT') return router.createUrlTree(['/dashboard/client']);

    return router.createUrlTree(['/auth/login']);
  };
};