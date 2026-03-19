import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule], 
  templateUrl: './register.html',
  styleUrls: ['./register.css']
})
export class Register {

  isLoading = false;
  errorMsg = '';

  user = {
    name: '',
    email: '',
    password: '',
    role: 'STUDENT'
  };

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  register() {
    this.isLoading = true;
    this.errorMsg = '';

    this.authService.register(this.user).subscribe({
      next: () => {
        this.isLoading = false;
        alert('Registration successful');
        this.router.navigate(['/login']); 
      },
      error: () => {
        this.isLoading = false;
        this.errorMsg = 'Registration failed';
      }
    });
  }
}