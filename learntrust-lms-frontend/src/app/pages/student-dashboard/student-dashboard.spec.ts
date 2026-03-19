import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from '../../shared/navbar/navbar';
import { CourseService } from '../../services/course';
import { Router, RouterModule } from '@angular/router';
import { forkJoin } from 'rxjs';

@Component({
selector: 'app-student-dashboard',
standalone: true,
imports: [
CommonModule,
NavbarComponent,
RouterModule
],
templateUrl: './student-dashboard.html',
styleUrls: ['./student-dashboard.css']
})
export class StudentDashboard implements OnInit {

loading = false;
enrolledCourses: any[] = [];
availableCourses: any[] = [];

constructor(
private courseService: CourseService,
private router: Router
) {}

ngOnInit() {
this.loadCourses();
}
//load courses
loadCourses() {
this.loading = true;


let user: any = null;
try {
  user = JSON.parse(localStorage.getItem('user') || '{}');
} catch {
  user = null;
}

const userId = user?.userId;

// CASE 1 — user missing
if (!userId) {
  console.warn('User missing in localStorage');

  this.courseService.getPublishedCourses().subscribe({
    next: (courses) => {
      this.availableCourses = courses || [];
      this.enrolledCourses = [];
      this.loading = false;
    },
    error: () => {
      this.availableCourses = [];
      this.loading = false;
    }
  });

  return;
}

//  CASE 2 — load both in parallel (BEST PRACTICE)
forkJoin({
  enrollments: this.courseService.getMyCourses(userId),
  published: this.courseService.getPublishedCourses()
}).subscribe({
  next: ({ enrollments, published }) => {
    this.enrolledCourses = (enrollments || []).map(
      (e: any) => e.course
    );

    this.availableCourses = published || [];

    this.loading = false;
  },
  error: (err) => {
    console.error('Dashboard load failed:', err);
    this.enrolledCourses = [];
    this.availableCourses = [];
    this.loading = false;
  }
});


}

//enrollment
handleEnroll(courseId: number) {
const user = JSON.parse(localStorage.getItem('user') || '{}');


this.courseService.enroll(courseId, user.userId).subscribe({
  next: () => {
    alert('Enrolled successfully ✅');
    this.loadCourses();
  },
  error: (err) => {
    if (err?.error?.message?.includes('Already enrolled')) {
      alert('⚠️ Already enrolled');
      this.loadCourses();
    } else {
      alert('Enrollment failed');
    }
  }
});


}

// certificate
downloadCertificate(courseId: number) {
window.open(`http://localhost:8080/certificates/download/${courseId}`);
}
}
