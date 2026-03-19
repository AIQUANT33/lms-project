import { inject } from '@angular/core';
import { CanActivateFn, Router, ActivatedRouteSnapshot } from '@angular/router'; //to read route data (

export const roleGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {  //angular automatically sends route.data
  const router = inject(Router); //needed for navigation

  const userData = localStorage.getItem('user'); //checks if login session still exists

  // not logged in then redirect to login
  if (!userData) {
    router.navigate(['/login']);
    return false;
  }

  try {
    const user = JSON.parse(userData);
    const expectedRole = route.data['role'];

    //  normalize role 
    const userRole = (user.role || '').toUpperCase();
    const requiredRole = (expectedRole || '').toUpperCase();
    

    /* 
     guard allows navigation
     page loads
     if userrole = student
     requiredrole = student
     then , acess granted
    */
    if (userRole === requiredRole) {
      return true;
    }

    //  role mismatch → send to their dashboard
    if (userRole === 'STUDENT') {
      router.navigate(['/student']);
    } else if (userRole === 'TRAINER') {
      router.navigate(['/trainer']);
    } else if (userRole === 'ADMIN') {
      router.navigate(['/admin']);
    } else {
      router.navigate(['/login']);
    }

    return false;
  } catch (e) {
    localStorage.removeItem('user'); //remove corrupted sessions
    router.navigate(['/login']); //send to login & block route
    return false;
  }
};


//COMPLETE RUNTINE FLOW 
/* 
Check localStorage :
   if 
   missing → redirect login
   else
     try parse →
        if role matches → allow
        if role mismatch → redirect dashboard
        if parse fails → cleanup + login
        
        */