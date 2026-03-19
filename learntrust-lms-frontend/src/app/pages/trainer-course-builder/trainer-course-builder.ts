import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { CourseService } from '../../services/course';
import { NavbarComponent } from '../../shared/navbar/navbar';
import { FormsModule } from '@angular/forms';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { AiService } from '../../services/ai.service';

interface QuizQuestion {
  id?: number;
  questionText: string;
  type: 'MCQ' | 'DESCRIPTIVE';
  options: string[];
  correctAnswer: number | null;
  descriptiveAnswer?: string;
}

@Component({
  selector: 'app-trainer-course-builder',
  standalone: true,
  imports: [CommonModule, NavbarComponent, FormsModule],
  templateUrl: './trainer-course-builder.html',
  styleUrls: ['./trainer-course-builder.css']
})
export class TrainerCourseBuilder implements OnInit {

  courseId!: number;
  modules: any[] = [];
  loading = false;
  newModuleTitle = '';

  constructor(
    private route: ActivatedRoute,
    private courseService: CourseService,
    private sanitizer: DomSanitizer,
    private aiService: AiService,
    private cdr: ChangeDetectorRef  
  ) {}

  ngOnInit(): void {
    
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.courseId = Number(id);
        this.loadModules();
      }
    });
  }

  //  MODULE 
  addModule() {
    if (!this.newModuleTitle.trim()) return;

    this.courseService.createModule(this.courseId, {
      title: this.newModuleTitle
    }).subscribe(() => {
      this.newModuleTitle = '';
      this.loadModules();
    });
  }

  loadModules() {
    this.loading = true;

    this.courseService.getModulesByCourse(this.courseId).subscribe({
      next: (res: any[]) => {
        this.modules = (res || []).map((m: any) => {
          const moduleObj = {
            ...m,
            expanded: false,
            contents: [],
            quizzes: [],
            newContentTitle: '',
            newContentType: 'VIDEO',
            newContentUrl: '',
            newContentText: '',
            quizTitle: '',
            questions: [] as QuizQuestion[]
          };

          this.courseService.getContentsByModule(m.moduleId)
            .subscribe((c: any[]) => {
              moduleObj.contents = (c || []).map((content: any) => ({
                ...content,
                expanded: false
              }));
              this.cdr.detectChanges(); 
            });

          this.courseService.getModuleQuizzes(m.moduleId)
            .subscribe((q: any[]) => {
              moduleObj.quizzes = (q || []).map((qq: any) => ({
                ...qq,
                assessmentId: qq.id,
                assessmentData: qq.assessmentData,
                expanded: false
              }));
              this.cdr.detectChanges(); 
            });

          return moduleObj;
        });

        this.loading = false;
        this.cdr.detectChanges(); 
      },
      error: () => {
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  // AI QUIZ GENERATOR
  generateQuizWithAI(module: any) {
    if (!module.contents?.length) {
      alert('Add module content first.');
      return;
    }

    const contentText = module.contents
      .map((c: any) => c.contentText || c.title)
      .join('\n');

    this.aiService.generateQuiz(contentText, 5).subscribe({
      next: (res: any) => {
        let aiText = '';
        try {
          aiText = res?.candidates?.[0]?.content?.parts?.[0]?.text || '';
        } catch {
          alert('AI response parsing failed');
          return;
        }

        const lines = aiText.split('\n').filter((l: string) => l.trim());
        lines.forEach((line: string) => {
          module.questions.push({
            id: Date.now() + Math.random(),
            questionText: line,
            type: 'MCQ',
            options: ['', '', '', ''],
            correctAnswer: null
          });
        });

        alert('AI questions generated. Please edit answers.');
        this.cdr.detectChanges();
      },
      error: () => alert('AI quiz generation failed')
    });
  }

  //  QUIZ 
  addQuestion(module: any) {
    module.questions.push({
      id: Date.now(),
      questionText: '',
      type: 'MCQ',
      options: ['', '', '', ''],
      correctAnswer: null,
      descriptiveAnswer: ''
    });
  }

  trackByOption(index: number, option: any) {
    return index;
  }

  removeOption(question: any, index: number) {
    if (question.options.length <= 2) {
      alert('At least 2 options required');
      return;
    }
    question.options.splice(index, 1);
    if (question.correctAnswer === index) {
      question.correctAnswer = null;
    }
  }

  removeQuestion(module: any, index: number) {
    module.questions.splice(index, 1);
  }

  createModuleQuiz(module: any) {
    if (!module.quizTitle?.trim()) {
      alert('Enter quiz title');
      return;
    }
    if (!module.questions?.length) {
      alert('Add questions');
      return;
    }

    const payload = {
      moduleId: module.moduleId,
      title: module.quizTitle,
      type: 'QUIZ',
      questionsJson: JSON.stringify(module.questions),
      passingScore: 40
    };

    this.courseService.createQuiz(payload).subscribe({
      next: (res: any) => {
        module.quizzes = module.quizzes || [];
        module.quizzes.push({
          ...res,
          assessmentId: res.id,
          expanded: false
        });
        module.quizTitle = '';
        module.questions = [];
        alert('Quiz saved successfully');
        this.cdr.detectChanges();
      },
      error: () => alert('Quiz save failed')
    });
  }

  
  safeUrl(url: string): SafeResourceUrl {
    return url;
  }

  safePdfUrl(url: string): SafeResourceUrl {
    return 'https://docs.google.com/gview?url=' +
      encodeURIComponent(url) + '&embedded=true';
  }

  //  MODULE ACTIONS 
  toggleModule(module: any) {
    module.expanded = !module.expanded;
  }

  moveUp(module: any) {
    this.courseService.moveModuleUp(module.moduleId)
      .subscribe(() => this.loadModules());
  }

  moveDown(module: any) {
    this.courseService.moveModuleDown(module.moduleId)
      .subscribe(() => this.loadModules());
  }

  deleteModule(module: any) {
    if (!confirm('Delete module?')) return;
    this.courseService.deleteModule(module.moduleId)
      .subscribe(() => this.loadModules());
  }

  // CONTENT ACTIONS
  toggleContent(content: any) {
    content.expanded = !content.expanded;
  }

  addContent(module: any) {
    if (!module.newContentTitle?.trim()) {
      alert('Enter content title');
      return;
    }

    const payload = {
      moduleId: module.moduleId,
      title: module.newContentTitle,
      contentType: module.newContentType,
      contentUrl: module.newContentUrl,
      contentText: module.newContentText,
      sequenceOrder: module.contents.length + 1
    };

    this.courseService.createContent(payload).subscribe(() => {
      this.reloadContents(module);
      module.newContentTitle = '';
      module.newContentUrl = '';
      module.newContentText = '';
    });
  }

  reloadContents(module: any) {
    this.courseService.getContentsByModule(module.moduleId)
      .subscribe((res: any[]) => {
        module.contents = (res || []).map((content: any) => ({
          ...content,
          expanded: false
        }));
        this.cdr.detectChanges();
      });
  }

  updateContent(module: any, content: any) {
    this.courseService.updateContent(content.id, content)
      .subscribe(() => {
        alert('Content updated');
        this.reloadContents(module);
      });
  }

  deleteContent(module: any, content: any) {
    if (!confirm('Delete content?')) return;
    this.courseService.deleteContent(content.id)
      .subscribe(() => {
        module.contents = module.contents.filter((c: any) => c.id !== content.id);
        this.cdr.detectChanges();
      });
  }

  //  FILE UPLOAD 
  uploadFile(event: any, module: any) {
    const file = event.target.files[0];
    if (!file) return;

    const cloudName = 'dnpxg7p4t';
    const uploadPreset = 'learntrust_unsigned';

    const formData = new FormData();
    formData.append('file', file);
    formData.append('upload_preset', uploadPreset);

    const uploadUrl = module.newContentType === 'VIDEO'
      ? `https://api.cloudinary.com/v1_1/${cloudName}/video/upload`
      : `https://api.cloudinary.com/v1_1/${cloudName}/raw/upload`;

    fetch(uploadUrl, { method: 'POST', body: formData })
      .then(res => res.json())
      .then(data => {
        module.newContentUrl = data.secure_url;
        alert('File uploaded successfully');
        this.cdr.detectChanges();
      })
      .catch(() => alert('Upload failed'));
  }

  //  QUIZ ACTIONS 
  deleteQuiz(module: any, quiz: any) {
    if (!confirm('Delete this quiz?')) return;
    const id = quiz.assessmentId || quiz.id;
    this.courseService.deleteQuiz(id)
      .subscribe(() => {
        module.quizzes = module.quizzes.filter((q: any) => q !== quiz);
        this.cdr.detectChanges();
      });
  }

  parseQuestions(json: string) {
    try {
      return json ? JSON.parse(json) : [];
    } catch {
      return [];
    }
  }
}