import { Component, OnInit, OnDestroy, ChangeDetectorRef, ViewEncapsulation } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { CourseService } from '../../services/course';
import { AuthService } from '../../services/auth.service';
import { NavbarComponent } from '../../shared/navbar/navbar';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-student-course-player',
  standalone: true,
  imports: [CommonModule, FormsModule, NavbarComponent],
  templateUrl: './student-course-player.html',
  styleUrls: ['./student-course-player.css'],
  encapsulation: ViewEncapsulation.None
})
export class StudentCoursePlayer implements OnInit, OnDestroy {

  courseId!: number;
  course: any;
  modules: any[] = [];
  loading = true;
  selectedContent: any = null;
  videoEnded: boolean = false;
  progressPercentage: number = 0;
  completionStatus: string = 'IN_PROGRESS';
  studentAnswers: any = {};
  quizResults: any = {};
  quizAttempted: any = {};
  certificateUnlocked: boolean = false;

  private routeSub?: Subscription;

  constructor(
    private route: ActivatedRoute,
    private courseService: CourseService,
    private authService: AuthService,
    private cdr: ChangeDetectorRef,
    private sanitizer: DomSanitizer
  ) {}

  ngOnInit(): void {
    this.routeSub = this.route.paramMap.subscribe(params => {
      const idParam = params.get('id');
      this.courseId = idParam ? Number(idParam) : 0;
      if (this.courseId) {
        this.loadCourse();
      }
    });
  }

  ngOnDestroy(): void {
    this.routeSub?.unsubscribe();
  }

  loadCourse() {
    this.loading = true;
    this.courseService.getCourseDetails(this.courseId).subscribe({
      next: (data) => {
        this.course = data.course;
        this.modules = data.modules || [];
        this.modules.forEach(m => {
          m.expanded = true;
          m.contents = [];
          m.quizzes = [];
          this.loadContents(m);
          this.loadQuizzes(m);
        });
        this.loading = false;
        this.loadProgress();
        this.cdr.detectChanges();
      },
      error: () => this.loadCourseFallback()
    });
  }

  loadCourseFallback() {
    this.courseService.getCourseById(this.courseId).subscribe({
      next: (course) => {
        this.course = course;
        this.loadModules();
      },
      error: () => {
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  loadModules() {
    this.courseService.getModulesByCourse(this.courseId).subscribe({
      next: (mods) => {
        this.modules = mods || [];
        this.modules.forEach(m => {
          m.expanded = true;
          m.contents = [];
          m.quizzes = [];
          this.loadContents(m);
          this.loadQuizzes(m);
        });
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  loadContents(module: any) {
    this.courseService.getContentsByModule(module.moduleId)
      .subscribe(res => {
        module.contents = res || [];
        if (!this.selectedContent && module.contents.length > 0) {
          this.selectedContent = module.contents[0];
        }
        this.cdr.detectChanges();
      });
  }

  selectContent(content: any) {
    this.selectedContent = content;
    this.videoEnded = false;
  }

  onVideoEnded() {
    this.videoEnded = true;
  }

  loadQuizzes(module: any) {
    this.courseService.getModuleQuizzes(module.moduleId)
      .subscribe((quizRes: any[]) => {
        //  parse questions ONCE here, store in quiz.parsedQuestions
       
        module.quizzes = (quizRes || []).map(q => {
          const parsed = this.parseData(q.assessmentData);
          return {
            ...q,
            id: q.id || q.assessmentId,
            parsedQuestions: parsed,  // ← pre-parsed, used in HTML
            passingScore: q.passingMarks ?? q.passingScore ?? 40
          };
        });
        this.cdr.detectChanges();
      });
  }

  private parseData(data: any): any[] {
    try {
      if (!data) return [];
      if (typeof data === 'string') {
        const parsed = JSON.parse(data);
        return Array.isArray(parsed) ? parsed : [];
      }
      return Array.isArray(data) ? data : [];
    } catch {
      return [];
    }
  }

  toggleModule(module: any) {
    module.expanded = !module.expanded;
  }

  submitQuiz(quiz: any) {
    const questions = quiz.parsedQuestions || [];
    if (!questions.length) return;

    let score = 0;
    questions.forEach((q: any, index: number) => {
      const ans = this.studentAnswers[quiz.id + '_' + index];
      if (ans == q.correctAnswer) score++;
    });

    const total = questions.length;
    const percentage = (score / total) * 100;
    const passed = percentage >= quiz.passingScore;

    this.quizResults[quiz.id] = { score, total, percentage, passed };
    this.quizAttempted[quiz.id] = true;

    if (passed) alert('Quiz passed! Continue learning.');
    this.cdr.detectChanges();
  }

  loadProgress() {
    const studentId = this.authService.getUserId();
    if (!studentId) return;

    this.courseService.getCourseProgress(studentId, this.courseId)
      .subscribe((enrollment) => {
        this.progressPercentage = enrollment?.progressPercentage || 0;
        this.completionStatus   = enrollment?.completionStatus || 'IN_PROGRESS';
        this.certificateUnlocked =
          this.completionStatus === 'COMPLETED' &&
          this.progressPercentage === 100;
        this.cdr.detectChanges();
      });
  }

  markContentComplete() {
    const studentId = this.authService.getUserId();
    if (!studentId || !this.selectedContent?.id) return;

    this.courseService.completeContent({
      studentId: studentId,
      contentId: this.selectedContent.id
    }).subscribe(() => this.loadProgress());
  }

  requestCertificate() {
    const studentId = this.authService.getUserId();
    if (!studentId) return;

    this.courseService.requestCertificate(studentId, this.courseId)
      .subscribe(() => {
        alert('Request sent to trainer');
        this.loadProgress();
      });
  }

  downloadCertificate() {
    const studentId = this.authService.getUserId();
    if (!studentId) return;

    this.courseService.downloadCertificate(studentId, this.courseId)
      .subscribe((blob: Blob) => {
        const url = window.URL.createObjectURL(blob);
        window.open(url);
      });
  }

  safeUrl(url: string): SafeResourceUrl {
    if (!url) return '';
    if (url.endsWith('.pdf')) {
      return 'https://docs.google.com/gview?url=' +
        encodeURIComponent(url) + '&embedded=true';
    }
    return url;
  }
}