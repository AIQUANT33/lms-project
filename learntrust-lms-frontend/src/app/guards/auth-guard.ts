import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = () => {
  const router      = inject(Router);
  const authService = inject(AuthService);

  //  checks for TOKEN 
  const token = authService.getToken();

  if (!token) {
    router.navigate(['/login']);
    return false;
  }

  // Also verify user data exists
  const user = authService.getUser();
  if (!user || !user.role) {
    authService.logout();
    router.navigate(['/login']);
    return false;
  }

  return true;
};


/* authGuard runs first — it checks if a token exists in localStorage. If no token → redirect to login. If token exists → pass through to roleGuard.*/