import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from '../../shared/navbar/navbar';
import { CourseService } from '../../services/course';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-trainer-dashboard',
  standalone: true,
  imports: [CommonModule, NavbarComponent, RouterModule],
  templateUrl: './trainer-dashboard.html',
  styleUrls: ['./trainer-dashboard.css'],
})
export class TrainerDashboard implements OnInit {

  courses: any[] = [];
  loading = false;
  errorMsg = '';
  trainerId!: number;
  trainerName: string = '';

  get publishedCount(): number {
    return this.courses.filter(c => c?.status === 'PUBLISHED').length;
  }

  get draftCount(): number {
    return this.courses.filter(c => c?.status === 'DRAFT').length;
  }

  constructor(
    private courseService: CourseService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    this.trainerId   = user?.id || user?.userId;
    this.trainerName = user?.name || user?.username || user?.email || 'Trainer';

    if (!this.trainerId) {
      this.errorMsg = 'Trainer not found';
      return;
    }

    this.loadCourses();
  }

  loadCourses() {
    this.loading = true;
    this.errorMsg = '';

    this.courseService.getTrainerCourses(this.trainerId).subscribe({
      next: (res: any) => {
        this.courses = [...(res || [])];
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error(err);
        this.errorMsg = 'Failed to load courses';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  submitForReview(courseId: number) {
    this.courseService.submitForReview(courseId).subscribe({
      next: () => this.loadCourses(),
      error: () => alert('❌ Failed to submit course')
    });
  }

  deleteCourse(courseId: number, courseTitle: string) {
    const confirmed = confirm(
      `Are you sure you want to delete "${courseTitle}"?\n\nThis will permanently delete all modules, content and quizzes.`
    );
    if (!confirmed) return;

    this.courseService.deleteCourse(courseId).subscribe({
      next: () => {
        this.loadCourses();
      },
      error: (err) => {
        console.error(err);
        const msg = err?.error || err?.message || 'Failed to delete course';
        alert('❌ ' + msg);
      }
    });
  }

  getStatusClass(status: string) {
    switch (status) {
      case 'DRAFT':     return 'badge draft';
      case 'PENDING':   return 'badge pending';
      case 'PUBLISHED': return 'badge published';
      default:          return 'badge';
    }
  }
}