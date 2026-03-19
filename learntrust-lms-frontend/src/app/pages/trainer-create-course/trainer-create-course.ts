import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CourseService } from '../../services/course';
import { NavbarComponent } from '../../shared/navbar/navbar';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-trainer-create-course',
  standalone: true,
  imports: [CommonModule, FormsModule, NavbarComponent],
  templateUrl: './trainer-create-course.html',
  styleUrls: ['./trainer-create-course.css']
})
export class TrainerCreateCourse {

  isSubmitting = false; //disables button
  successMsg = '';  //success ui
  errorMsg = '';    //error ui

  // VIDEO STATE
  selectedVideo?: File; //raw file
  videoUrl: string = ''; //cloudinarty url
  uploadingVideo = false; //spinner

  //  PUT YOUR VALUES HERE
  // upload is happening directly form frontend to cloudinary and not via backend
  private cloudName = 'dnpxg7p4t'; 
  private uploadPreset = 'learntrust_unsigned';

  form = {
    title: '',
    description: ''
  };

  constructor(
    private courseService: CourseService,
    private router: Router,
    private http: HttpClient
  ) {}


  // VIDEO SELECT

  onVideoSelected(event: any) {
    const file = event.target.files[0];
    if (!file) return;

    this.selectedVideo = file;
  }


  // UPLOAD TO CLOUDINARY
 
  uploadVideo() {
    if (!this.selectedVideo) return; //prevents empty upload

    this.uploadingVideo = true;

    //creates multipart request
    const formData = new FormData();
    formData.append('file', this.selectedVideo);
    formData.append('upload_preset', this.uploadPreset);

    const url = `https://api.cloudinary.com/v1_1/${this.cloudName}/video/upload`;

    this.http.post<any>(url, formData).subscribe({ //browser sends video to cloudinary
      next: (res) => {
        console.log('✅ Cloudinary upload success', res);

        this.videoUrl = res.secure_url; //public video url (saved in db and played by student)
        this.uploadingVideo = false;

        alert('✅ Video uploaded successfully');
      },
      error: (err) => {
        console.error('❌ Cloudinary upload failed', err);
        this.uploadingVideo = false;
        alert('❌ Video upload failed');
      }
    });
  }

 //create course
  createCourse() {
    if (this.isSubmitting) return;

    const user = JSON.parse(localStorage.getItem('user') || '{}');

    if (!user?.userId) {
      this.errorMsg = 'Trainer not found. Please login again.';
      return;
    }

    this.isSubmitting = true;
    this.successMsg = '';
    this.errorMsg = '';

    const payload = {
      title: this.form.title,
      description: this.form.description,
      status: 'DRAFT',  // Start as DRAFT, requires admin approval
      videoUrl: this.videoUrl, //  SAVE VIDEO URL
      trainer: {
        userId: user.userId
      }
    };

    console.log('🚀 Creating course with payload:', payload);

    this.courseService.createCourse(payload).subscribe({
      next: () => {
        this.isSubmitting = false;
        this.successMsg = '✅ Course saved as draft';

        this.form.title = '';
        this.form.description = '';
        this.videoUrl = '';

        setTimeout(() => {
          this.router.navigate(['/trainer']);
        }, 1000);
      },
      error: (err) => {
        console.error('❌ Create course failed:', err);
        this.isSubmitting = false;
        this.errorMsg = '❌ Failed to create course';
      }
    });
  }

  
}