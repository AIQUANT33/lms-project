import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CourseService } from '../../services/course';
import { FormsModule } from '@angular/forms';
import { NavbarComponent } from '../../shared/navbar/navbar';
import { ChangeDetectorRef } from '@angular/core'; 

@Component({
  selector: 'app-admin-users',
  standalone: true,
  imports: [CommonModule, FormsModule, NavbarComponent],
  templateUrl: './admin-users.component.html',
  styleUrls: ['./admin-users.component.css']
})
export class AdminUsersComponent implements OnInit {

  users: any[] = [];
  loading = true;

  constructor(
    private courseService: CourseService,
    private cdr: ChangeDetectorRef 
  ) {}

  ngOnInit() {
    console.log("Component Loaded"); 
    this.loadUsers();
  }

loadUsers() {
  console.log("Calling API...");

  this.courseService.getAllUsers().subscribe({
    next: (res) => {
      console.log("USERS:", res);

      this.users = Array.isArray(res)
        ? res.filter(user =>
            user.role === 'TRAINER' || user.role === 'STUDENT'
          )
        : [];

      this.loading = false;
      this.cdr.detectChanges();
    },
    error: (err) => {
      console.error("ERROR:", err);
      this.loading = false;
      this.cdr.detectChanges();
    }
  });
}
  toggleStatus(user: any) {
    this.courseService.toggleUserStatus(user.userId)
      .subscribe(() => {
        user.status =
          user.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE';
      });
  }

  updateRole(user: any) {
    this.courseService.updateUserRole(user.userId, user.role)
      .subscribe();
  }
}