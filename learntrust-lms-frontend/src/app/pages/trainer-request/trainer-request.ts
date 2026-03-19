import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { NavbarComponent } from '../../shared/navbar/navbar';

@Component({
  selector: 'app-trainer-request',
  standalone: true,
  imports: [CommonModule, FormsModule, NavbarComponent],
  templateUrl: './trainer-request.html',
  styleUrls: ['./trainer-request.css']
})
export class TrainerRequestComponent {

  isSubmitting = false;
  successMsg = '';
  errorMsg = '';

  form = {
    fullName: '',
    email: '',
    expertise: '',
    message: ''
  };

  constructor(
    private http: HttpClient,
    private router: Router
  ) {}

  submitRequest() {
    if (this.isSubmitting) return;

    this.isSubmitting = true;
    this.successMsg = '';
    this.errorMsg = '';

    this.http.post('http://localhost:8080/trainer-requests', this.form)
      .subscribe({
        next: () => {
          this.isSubmitting = false;

          // show success message first
          this.successMsg = '✅ Application submitted! Our team will review it.';

         
          setTimeout(() => {
            this.router.navigate(['/trainer-request-status']);
          }, 1500);
        },
        error: (err) => {
          console.error('Trainer request failed:', err);
          this.isSubmitting = false;
          this.errorMsg = '❌ Submission failed. Please try again.';
        }
      });
  }
}