import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from '../../shared/navbar/navbar';

@Component({
  selector: 'app-trainer-request-status',
  standalone: true,
  imports: [CommonModule, NavbarComponent],
  templateUrl: './trainer-request-status.html',
  styleUrls: ['./trainer-request-status.css']
})
export class TrainerRequestStatusComponent {}