import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CourseService } from '../../services/course';
import { NavbarComponent } from '../../shared/navbar/navbar';

@Component({
  selector: 'app-trainer-completions',
  standalone: true,
  imports: [CommonModule, NavbarComponent],
  templateUrl: './trainer-completions.component.html',
  styleUrls: ['./trainer-completions.component.css'] 
})
export class TrainerCompletionsComponent implements OnInit {

  enrollments: any[] = [];

  constructor(
    private courseService: CourseService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadAll();
  }

  loadAll(): void {

    const user = JSON.parse(localStorage.getItem('user') || '{}');
    const trainerId = user?.userId || user?.id;

    if (!trainerId) {
      console.error("Trainer ID missing");
      return;
    }

    this.courseService.getTrainerRequests(trainerId)
      .subscribe({
        next: (res: any[]) => {

          console.log("ALL DATA:", res);

      
          this.enrollments = [...res];

          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error("Error loading requests", err);
        }
      });
  }

  approve(e: any): void {

    this.courseService.approveCompletion(e.enrollmentId)
      .subscribe({
        next: () => {

          alert("Approved successfully");

          
          const item = this.enrollments.find(
            i => i.enrollmentId === e.enrollmentId
          );

          if (item) {
            item.completionStatus = 'APPROVED';
          }

          this.cdr.detectChanges();
        },
        error: () => {
          alert("Approval failed");
        }
      });
  }
}