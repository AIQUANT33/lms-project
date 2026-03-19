import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Course {
  courseId: number;
  title: string;
  description: string;
  status: string;
  videoUrl?: string;
}

@Injectable({
  providedIn: 'root'
})
export class CourseService {

  private baseUrl = 'http://localhost:8080';

  constructor(private http: HttpClient) {}

  //student

  getMyCourses(studentId: number): Observable<any[]> {
    return this.http.get<any[]>(
      `${this.baseUrl}/enrollments/my-courses?studentId=${studentId}`
    );
  }

  getCourseProgress(studentId: number, courseId: number): Observable<any> {
    return this.http.get<any>(
      `${this.baseUrl}/enrollments/progress?studentId=${studentId}&courseId=${courseId}`
    );
  }

  completeCourse(courseId: number, studentId: number): Observable<any> {
    return this.http.put<any>(
      `${this.baseUrl}/enrollments/complete?studentId=${studentId}&courseId=${courseId}`,
      {}
    );
  }

  getPublishedCourses(): Observable<Course[]> {
    return this.http.get<Course[]>(`${this.baseUrl}/courses/published`);
  }

  getCourseById(courseId: number): Observable<Course> {
    return this.http.get<Course>(`${this.baseUrl}/courses/${courseId}`);
  }

  enroll(courseId: number, studentId: number): Observable<any> {
    return this.http.post(
      `${this.baseUrl}/enrollments?studentId=${studentId}&courseId=${courseId}`,
      {}
    );
  }

  getCourseDetails(courseId: number): Observable<any> {
    return this.http.get<any>(
      `${this.baseUrl}/courses/${courseId}/details`
    );
  }

  // trainer

  createCourse(payload: any): Observable<Course> {
    return this.http.post<Course>(`${this.baseUrl}/courses`, payload);
  }

  getTrainerCourses(trainerId: number): Observable<Course[]> {
    return this.http.get<Course[]>(
      `${this.baseUrl}/courses/trainer/${trainerId}`
    );
  }

  submitForReview(courseId: number): Observable<Course> {
    return this.http.put<Course>(
      `${this.baseUrl}/courses/${courseId}/submit`,
      {}
    );
  }

  //admin

  getPendingCourses(): Observable<Course[]> {
    return this.http.get<Course[]>(`${this.baseUrl}/courses/pending`);
  }

  approveCourse(courseId: number, adminId: number): Observable<Course> {
    return this.http.put<Course>(
      `${this.baseUrl}/courses/${courseId}/approve/${adminId}`,
      {}
    );
  }

  rejectCourse(courseId: number): Observable<Course> {
    return this.http.put<Course>(
      `${this.baseUrl}/courses/${courseId}/reject`,
      {}
    );
  }

  // modules

  createModule(courseId: number, payload: any): Observable<any> {
    return this.http.post(
      `${this.baseUrl}/modules?courseId=${courseId}`,
      payload
    );
  }

  getModulesByCourse(courseId: number): Observable<any[]> {
    return this.http.get<any[]>(
      `${this.baseUrl}/modules/course/${courseId}`
    );
  }

  deleteModule(moduleId: number) {
    return this.http.delete(`${this.baseUrl}/modules/${moduleId}`);
  }

  moveModuleUp(moduleId: number) {
    return this.http.put(`${this.baseUrl}/modules/${moduleId}/move-up`, {});
  }

  moveModuleDown(moduleId: number) {
    return this.http.put(`${this.baseUrl}/modules/${moduleId}/move-down`, {});
  }

  updateModule(moduleId: number, payload: any) {
    return this.http.put(
      `${this.baseUrl}/modules/${moduleId}`,
      payload
    );
  }

  //content

  createContent(payload: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/module-contents`, payload);
  }

  getContentsByModule(moduleId: number): Observable<any[]> {
    return this.http.get<any[]>(
      `${this.baseUrl}/module-contents/module/${moduleId}`
    );
  }

  updateContent(contentId: number, payload: any) {
    return this.http.put(
      `${this.baseUrl}/module-contents/${contentId}`,
      payload
    );
  }

  deleteContent(contentId: number) {
    return this.http.delete(
      `${this.baseUrl}/module-contents/${contentId}`
    );
  }

  uploadFile(formData: FormData) {
    return this.http.post(
      `${this.baseUrl}/files/upload`,
      formData,
      { responseType: 'text' }
    );
  }

  // quiz

  createQuiz(payload: any) {
    return this.http.post(`${this.baseUrl}/assessments`, payload);
  }

  deleteQuiz(assessmentId: number) {
    return this.http.delete(`${this.baseUrl}/assessments/${assessmentId}`);
  }

  getModuleQuizzes(moduleId: number): Observable<any[]> {
    return this.http.get<any[]>(
      `${this.baseUrl}/assessments/module/${moduleId}`
    );
  }



  createFinalAssessment(payload: any) {
    return this.http.post(`${this.baseUrl}/assessments`, payload);
  }

  // content completion

  completeContent(payload: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/content-completions`, payload);
  }

  // certificate

  issueCertificate(studentId:number, courseId:number){

    return this.http.post(
      `${this.baseUrl}/certificates/issue?studentId=${studentId}&courseId=${courseId}`,
      {}
    );

  }

// admin user management

getAllUsers() {
  return this.http.get<any[]>(`${this.baseUrl}/admin/users`);
}

toggleUserStatus(userId: number) {
  return this.http.put(
    `${this.baseUrl}/admin/users/${userId}/toggle-status`,
    {}
  );
}

updateUserRole(userId: number, role: string) {
  return this.http.put(
    `${this.baseUrl}/admin/users/${userId}/role?role=${role}`,
    {}
  );
}



// submissions

getSubmissionsByQuiz(quizId: number) {
  return this.http.get<any[]>(
    `${this.baseUrl}/submissions/quiz/${quizId}`
  );
}

reviewSubmission(submissionId: number, status: string) {
  return this.http.put(
    `${this.baseUrl}/submissions/${submissionId}/review?status=${status}`,
    {}
  );
}

// enrollments

getAllEnrollments() {
  return this.http.get<any[]>(`${this.baseUrl}/enrollments`);
}

approveCompletion(enrollmentId: number) {
  return this.http.put(
    `${this.baseUrl}/enrollments/approve/${enrollmentId}`,
    {}
  );
}




// STUDENT REQUEST
requestCertificate(studentId: number, courseId: number) {
  return this.http.put(
    `${this.baseUrl}/enrollments/complete?studentId=${studentId}&courseId=${courseId}`,
    {}
  );
}

// DOWNLOAD PDF
downloadCertificate(studentId: number, courseId: number) {
  return this.http.get(
    `${this.baseUrl}/certificates/download?studentId=${studentId}&courseId=${courseId}`,
    { responseType: 'blob' }
  );
}

getTrainerRequests(trainerId: number) {
  return this.http.get<any[]>(
    `${this.baseUrl}/enrollments/trainer/${trainerId}`
  );

}


// TRAINER: DELETE COURSE
deleteCourse(courseId: number): Observable<any> {
  return this.http.delete(
    `${this.baseUrl}/courses/${courseId}`,
    { responseType: 'text' }
  );
}
}