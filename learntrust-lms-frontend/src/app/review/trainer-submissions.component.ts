import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CourseService } from '../services/course';   
import { FormsModule } from '@angular/forms';
import { NavbarComponent } from '../shared/navbar/navbar'; 

@Component({
  selector: 'app-trainer-submissions',
  standalone: true,
  imports: [CommonModule, FormsModule, NavbarComponent],
  templateUrl: './trainer-submissions.component.html',
  styleUrl: './trainer-submissions.component.css'
})
export class TrainerSubmissionsComponent {

  quizId!: number;
  submissions: any[] = [];

  constructor(private courseService: CourseService) {}

  loadSubmissions() {
    if (!this.quizId) {
      alert("Enter quiz ID");
      return;
    }

    this.courseService.getSubmissionsByQuiz(this.quizId)
      .subscribe(res => {
        this.submissions = res;
      });
  }

  review(sub: any, status: string) {
    this.courseService.reviewSubmission(sub.submissionId, status)
      .subscribe(() => {
        sub.reviewStatus = status;
        alert("Updated");
      });
  }
}