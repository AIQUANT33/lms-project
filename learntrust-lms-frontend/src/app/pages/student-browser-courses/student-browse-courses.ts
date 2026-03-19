import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CourseService, Course } from '../../services/course';
import { NavbarComponent } from '../../shared/navbar/navbar';

@Component({
  selector: 'app-student-browse-courses',
  standalone: true,
  imports: [CommonModule, NavbarComponent],
  templateUrl: './student-browse-courses.html',
  styleUrls: ['./student-browse-courses.css']
})
export class StudentBrowseCourses implements OnInit {

  courses: Course[] = [];
  loading = true;

  enrollingCourseId?: number;

  enrolledCourseIds: number[] = [];

  constructor(
    private courseService: CourseService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadAll();
  }

  loadAll() {

    this.loading = true;

    const raw = localStorage.getItem('user');

    let userId: number | null = null;

    if (raw) {

      try {

        const u = JSON.parse(raw);

        userId = u?.userId ?? u?.id ?? null;

      } catch {

        userId = null;

      }

    }

    this.courseService.getPublishedCourses().subscribe({

      next: (courses) => {

        const allCourses = Array.isArray(courses) ? courses : [];

        if (!userId) {

          this.courses = allCourses;

          this.loading = false;

          this.cdr.detectChanges();

          return;

        }

        this.courseService.getMyCourses(userId).subscribe({

          next: (enr: any[]) => {

            this.enrolledCourseIds = (enr || [])

              .map((e: any) => e?.course?.courseId)

              .filter((id: any) => id != null);

            // remove enrolled courses

            this.courses = allCourses.filter(

              (c: any) => !this.enrolledCourseIds.includes(c.courseId)

            );

            this.loading = false;

            this.cdr.detectChanges();

          },

          error: () => {

            this.enrolledCourseIds = [];

            this.courses = allCourses;

            this.loading = false;

            this.cdr.detectChanges();

          }

        });

      },

      error: () => {

        this.courses = [];

        this.loading = false;

        this.cdr.detectChanges();

      }

    });

  }

  enroll(courseId: number) {

    if (this.enrolledCourseIds.includes(courseId)) {

      alert('⚠️ Already enrolled');

      return;

    }

    const raw = localStorage.getItem('user');

    const user = raw ? JSON.parse(raw) : null;

    const userId = user?.userId || user?.id;

    if (!userId) {

      alert('❌ User not logged in');

      return;

    }

    this.enrollingCourseId = courseId;

    this.courseService.enroll(courseId, userId).subscribe({

      next: () => {

        this.enrollingCourseId = undefined;

        this.enrolledCourseIds.push(courseId);

        this.courses = this.courses.filter(c => c.courseId !== courseId);

        alert('✅ Enrolled successfully');

        this.cdr.detectChanges();

      },

      error: () => {

        this.enrollingCourseId = undefined;

        alert('❌ Enrollment failed');

        this.cdr.detectChanges();

      }

    });

  }

  isEnrolled(courseId: number): boolean {

    return this.enrolledCourseIds.includes(courseId);

  }

}