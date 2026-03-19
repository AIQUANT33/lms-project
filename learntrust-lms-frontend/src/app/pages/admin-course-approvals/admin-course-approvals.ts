import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CourseService, Course } from '../../services/course';
import { HttpClient } from '@angular/common/http';
import { NavbarComponent } from '../../shared/navbar/navbar';
import { ChangeDetectorRef } from '@angular/core';
@Component({
  selector: 'app-admin-course-approvals',
  standalone: true,
  imports: [CommonModule, NavbarComponent],
  templateUrl: './admin-course-approvals.html',
  styleUrls: ['./admin-course-approvals.css']
})
export class AdminCourseApprovalsComponent implements OnInit {

  pendingCourses: Course[] = [];
  pendingTrainerRequests: any[] = [];

  loadingCourses = false;
  loadingTrainers = false;

  private baseUrl = 'http://localhost:8080';

  constructor(
    private courseService: CourseService,
    private http: HttpClient,
      private cdr: ChangeDetectorRef //Manually tells Angular to re-render when nested callbacks change data

  ) {}

 
  ngOnInit(): void {
    this.loadPendingCourses();
    this.loadPendingTrainerRequests();
  }

 
  


loadPendingCourses() {
  this.loadingCourses = true;

  this.courseService.getPendingCourses().subscribe({
    next: (res: any) => {
      //  always create new reference
      this.pendingCourses = [...(res || [])];

      this.loadingCourses = false;

      // ensure UI updates immediately
      this.cdr.detectChanges();
    },
    error: (err) => {
      console.error(err);
      this.loadingCourses = false;
      this.cdr.detectChanges();
    }
  });
}


  loadPendingTrainerRequests() {
  this.loadingTrainers = true;

  this.http
    .get<any[]>(`${this.baseUrl}/trainer-requests/pending`)
    .subscribe({
      next: (res: any) => {
        //  create new reference 
        this.pendingTrainerRequests = [...(res || [])];

        this.loadingTrainers = false;

        
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error(err);
        this.loadingTrainers = false;
        this.cdr.detectChanges();
      }
    });
}

//approve Trainer
  approveTrainer(requestId: number) {
    this.http
      .put(`${this.baseUrl}/trainer-requests/${requestId}/approve`, {})
      .subscribe(() => {
        this.loadPendingTrainerRequests();
      });
  }

  rejectTrainer(requestId: number) {
    this.http
      .put(`${this.baseUrl}/trainer-requests/${requestId}/reject`, {})
      .subscribe(() => {
        this.loadPendingTrainerRequests();
      });
  }

//approve course
  approveCourse(courseId: number) {
    const user = JSON.parse(localStorage.getItem('user') || '{}');

    this.courseService
      .approveCourse(courseId, user.userId)
      .subscribe(() => {
        this.loadPendingCourses();
      });
  }

  rejectCourse(courseId: number) {
    this.courseService.rejectCourse(courseId).subscribe(() => {
      this.loadPendingCourses();
    });
  }
}