import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { UserRole } from '../models/user.model';

// Protege rutas que requieren un rol específico
// Uso: canActivate: [() => roleGuard('ADMIN')]
export const roleGuard = (requiredRole: UserRole): CanActivateFn => {
  return () => {
    const authService = inject(AuthService);
    const router = inject(Router);

    if (authService.hasRole(requiredRole)) {
      return true;
    }

    // Si está autenticado pero no tiene el rol, lo mandamos al inicio
    // No al login, porque ya está logueado
    
    return router.createUrlTree(['/']);
  };
};