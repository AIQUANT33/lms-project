import { Routes } from '@angular/router';

import { LoginComponent } from './pages/login/login';
import { Register } from './pages/register/register';
import { StudentDashboard } from './pages/student-dashboard/student-dashboard';
import { TrainerDashboard } from './pages/trainer-dashboard/trainer-dashboard';
import { AdminDashboard } from './pages/admin-dashboard/admin-dashboard';
import { TrainerCreateCourse } from './pages/trainer-create-course/trainer-create-course';
import { TrainerRequestComponent } from './pages/trainer-request/trainer-request';
import { TrainerRequestStatusComponent } from './pages/trainer-request/trainer-request-status';
import { AdminCourseApprovalsComponent } from './pages/admin-course-approvals/admin-course-approvals';
import { HomeComponent } from './pages/home/home';

import { authGuard } from './guards/auth-guard';
import { roleGuard } from './guards/role.guard';

import { StudentBrowseCourses } from './pages/student-browser-courses/student-browse-courses';
import { StudentCoursePlayer } from './pages/student-course-player/student-course-player';
import { AdminUsersComponent } from './pages/admin-users/admin-users.component';

export const routes: Routes = [

  { path: '', component: HomeComponent },

  { path: 'login', component: LoginComponent },
  { path: 'register', component: Register },

  // STUDENT
  {
    path: 'student',
    component: StudentDashboard,
    canActivate: [authGuard, roleGuard],
    data: { role: 'STUDENT' }
  },
  { path: 'my-learning', component: StudentDashboard },
  {
    path: 'courses',
    component: StudentBrowseCourses,
    canActivate: [authGuard, roleGuard],
    data: { role: 'STUDENT' }
  },

  // TRAINER
  {
    path: 'trainer',
    component: TrainerDashboard,
    canActivate: [authGuard, roleGuard],
    data: { role: 'TRAINER' }
  },
  {
    path: 'trainer/create-course',
    component: TrainerCreateCourse,
    canActivate: [authGuard, roleGuard],
    data: { role: 'TRAINER' }
  },

  //  TRAINER FEATURES 
  {
    path: 'trainer/submissions',
    loadComponent: () =>
      import('./review/trainer-submissions.component')
        .then(m => m.TrainerSubmissionsComponent),
    canActivate: [authGuard, roleGuard],
    data: { role: 'TRAINER' }
  },

  {
    path: 'trainer/completions',
    loadComponent: () =>
      import('./pages/review-completion/trainer-completions.component')
        .then(m => m.TrainerCompletionsComponent),
    canActivate: [authGuard, roleGuard],
    data: { role: 'TRAINER' }
  },

  // ADMIN
  {
    path: 'admin',
    component: AdminDashboard,
    canActivate: [authGuard, roleGuard],
    data: { role: 'ADMIN' }
  },
  {
    path: 'admin/users',
    component: AdminUsersComponent,
    canActivate: [authGuard, roleGuard],
    data: { role: 'ADMIN' }
  },
  {
    path: 'admin/course-approvals',
    component: AdminCourseApprovalsComponent,
    canActivate: [authGuard, roleGuard],
    data: { role: 'ADMIN' }
  },

  {
    path: 'become-trainer',
    component: TrainerRequestComponent
  },
  {
    path: 'trainer-request-status',
    component: TrainerRequestStatusComponent
  },

  {
    path: 'learn/:id',
    component: StudentCoursePlayer,
    canActivate: [authGuard, roleGuard],
    data: { role: 'STUDENT' }
  },

  {
    path: 'trainer/course/:id',
    loadComponent: () =>
      import('./pages/trainer-course-builder/trainer-course-builder')
        .then(m => m.TrainerCourseBuilder),
    canActivate: [authGuard, roleGuard],
    data: { role: 'TRAINER' }
  },

  { path: '**', redirectTo: 'login' }
];