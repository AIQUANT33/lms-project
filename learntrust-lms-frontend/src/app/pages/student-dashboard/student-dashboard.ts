import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from '../../shared/navbar/navbar';
import { CourseService } from '../../services/course';
import { RouterModule, Router } from '@angular/router';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { HttpClient } from '@angular/common/http';
import { Chart } from 'chart.js/auto';

@Component({
  selector: 'app-student-dashboard',
  standalone: true,
  imports: [CommonModule, NavbarComponent, RouterModule],
  templateUrl: './student-dashboard.html',
  styleUrls: ['./student-dashboard.css']
})
export class StudentDashboard implements OnInit {

  loading = true;

  enrolledCourses: any[] = [];
  completedCourses: any[] = [];
  availableCourses: any[] = [];

  userName = '';

  totalCourses = 0;
  completedCount = 0;
  activeCount = 0;

  aiSummary = '';
  aiLoading = false;

  analytics: any = {};
  chartsLoaded = false;

  lineChart?: Chart;
  pieChart?: Chart;
  barChart?: Chart;

  profileImage: string | null = null;

  isMyLearningPage = false;

  constructor(
    private courseService: CourseService,
    private cdr: ChangeDetectorRef,
    private router: Router,
    private http: HttpClient
  ) {}

  ngOnInit(): void {

    const user = JSON.parse(localStorage.getItem('user') || '{}');

    this.userName = user?.name || user?.email || 'Student';
    this.profileImage = localStorage.getItem("profileImage");

    this.checkPage();
    this.loadCourses();

    if(!this.isMyLearningPage){
      this.loadAnalytics(user?.userId || user?.id);
    }
  }

  checkPage() {
    const url = this.router.url;
    this.isMyLearningPage = url.includes("my-learning");
  }

  //profile image

  uploadImage(event: any){

    const file = event.target.files[0];
    if(!file) return;

    const reader = new FileReader();

    reader.onload = () => {

      this.profileImage = reader.result as string;
      localStorage.setItem("profileImage", this.profileImage);
      this.cdr.detectChanges();
    };

    reader.readAsDataURL(file);
  }

  removeImage(){

    this.profileImage = null;
    localStorage.removeItem("profileImage");
    this.cdr.detectChanges();
  }

  // load courses

  loadCourses(): void {

    const user = JSON.parse(localStorage.getItem('user') || '{}');
    const userId = user?.userId || user?.id;

    const published$ = this.courseService
      .getPublishedCourses()
      .pipe(catchError(() => of([])));

    forkJoin({

      enrollments: this.courseService
        .getMyCourses(userId)
        .pipe(catchError(() => of([]))),

      published: published$

    }).subscribe({

      next: ({ enrollments, published }: any) => {

        const enrollList = Array.isArray(enrollments) ? enrollments : [];
this.enrolledCourses = enrollList
  .filter((e:any)=> e.completionStatus === 'IN_PROGRESS')
  .map((e:any)=> ({
    ...e.course,
    progress: e.progressPercentage || 0,
    completionStatus: e.completionStatus
  }));

this.completedCourses = enrollList
  .filter((e:any)=> 
    e.completionStatus === 'COMPLETED' ||
    e.completionStatus === 'PENDING_APPROVAL' ||
    e.completionStatus === 'APPROVED'   
  )
  .map((e:any)=> ({
    ...e.course,
   
    completionStatus: e.completionStatus
  }));
        const allCourses = Array.isArray(published)
          ? published
          : published?.content || [];

        const enrolledIds = enrollList.map((e:any)=> e.course?.courseId);

        this.availableCourses = allCourses.filter(
          (c:any)=> !enrolledIds.includes(c.courseId)
        );

        this.activeCount = this.enrolledCourses.length;
        this.completedCount = this.completedCourses.length;
        this.totalCourses = this.activeCount + this.completedCount;

        this.loading = false;

        this.cdr.detectChanges();
      }

    });

  }

 canDownload(course:any){
  return course.completionStatus === 'APPROVED'; 
}

  isPending(course:any)
  {
    return course.completionStatus==='PENDING_APPROVAL';
  }

  // analytics

  loadAnalytics(userId:number){

    this.http
    .get(`http://localhost:8080/users/${userId}/analytics`)
    .subscribe({

      next:(data:any)=>{

        this.analytics = data;

        this.chartsLoaded = true;

        this.cdr.detectChanges();

        // ensure DOM rendered before charts
        setTimeout(()=>{
          this.renderCharts();
        },50);

      },

      error:()=>{
        console.log("Analytics load failed");
      }

    });

  }

  renderCharts(){

    const learningCanvas = document.getElementById("learningChart") as HTMLCanvasElement;
    const performanceCanvas = document.getElementById("performanceChart") as HTMLCanvasElement;
    const studyCanvas = document.getElementById("studyChart") as HTMLCanvasElement;

    if(!learningCanvas || !performanceCanvas || !studyCanvas){
      console.log("Chart canvas not ready");
      return;
    }

    this.lineChart?.destroy();
    this.pieChart?.destroy();
    this.barChart?.destroy();

    // line charts

    this.lineChart = new Chart(learningCanvas,{
      type:'line',
      data:{
        labels:["Courses","Modules","Lessons"],
        datasets:[{
          label:"Learning Progress",
          data:[
            this.analytics.totalCourses || 0,
            this.analytics.modulesCompleted || 0,
            this.analytics.lessonsCompleted || 0
          ],
          borderColor:"#6366f1",
          backgroundColor:"rgba(99,102,241,0.2)",
          fill:true
        }]
      },
      options:{
        responsive:true,
        scales:{
          y:{
            beginAtZero:true
          }
        }
      }
    });

    // pie charts

    this.pieChart = new Chart(performanceCanvas,{
      type:'doughnut',
      data:{
        labels:["Completed","Active"],
        datasets:[{
          data:[
            this.analytics.completedCourses || 0,
            this.analytics.activeCourses || 0
          ],
          backgroundColor:["#22c55e","#f59e0b"]
        }]
      }
    });

    //bar chart

    this.barChart = new Chart(studyCanvas,{
      type:'bar',
      data:{
        labels:["Mon","Tue","Wed","Thu","Fri","Sat","Sun"],
        datasets:[{
          label:"Study Activity",
          data:Array.from(this.analytics.weeklyStudyActivity || [0,0,0,0,0,0,0]),
          backgroundColor:"#6366f1"
        }]
      },
      options:{
        responsive:true,
        scales:{
          y:{
            beginAtZero:true
          }
        }
      }
    });

  }

  // enroll

  handleEnroll(courseId:number){

    const user = JSON.parse(localStorage.getItem('user') || '{}');
    const userId = user?.userId || user?.id;

    this.courseService.enroll(courseId,userId).subscribe(()=>{

      alert("Enrolled successfully");

      this.loadCourses();

    });

  }

//  REQUEST CERTIFICATE
requestCertificate(courseId: number){

  const user = JSON.parse(localStorage.getItem('user') || '{}');
  const studentId = user?.userId || user?.id;

  this.courseService.requestCertificate(studentId, courseId)
    .subscribe(() => {

      alert("Request sent to trainer");

      this.loadCourses(); // refresh UI
    });
}


//  DOWNLOAD CERTIFICATE
downloadCertificate(courseId: number){

  const user = JSON.parse(localStorage.getItem('user') || '{}');
  const studentId = user?.userId || user?.id;

  this.courseService.downloadCertificate(studentId, courseId)
    .subscribe((blob: Blob) => {

      const url = window.URL.createObjectURL(blob);
      window.open(url);

    });
}

  //ai report

  generateAIReport(){

    const user = JSON.parse(localStorage.getItem('user') || '{}');
    const userId = user?.userId || user?.id;

    if(!userId) return;

    this.aiLoading = true;
    this.aiSummary = '';

    this.http
    .get(`http://localhost:8080/users/${userId}/ai-progress-summary`)
    .subscribe({

      next:(res:any)=>{

        let answer='';

        try{

          answer =
          res?.candidates?.[0]?.content?.parts?.[0]?.text ||
          res?.summary ||
          res?.message ||
          '';

        }catch{
          answer='AI summary unavailable';
        }

        answer = answer
        .replace(/\*\*(.*?)\*\*/g,'<b>$1</b>')
        .replace(/\n/g,'<br>')
        .replace(/\* /g,'• ');

        this.aiSummary = answer;

        this.aiLoading = false;

        this.cdr.detectChanges();

      },

      error:()=>{
        this.aiSummary="AI summary failed";
        this.aiLoading=false;
      }

    });

  }

}