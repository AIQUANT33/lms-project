import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ //makes this class available across angular
  providedIn: 'root' //singlr global service instance
})
export class AiService {

  private baseUrl = 'http://localhost:8080/api/ai'; //springboot ai controller (all ai request will go here)

  constructor(private http: HttpClient) {}

  //this sends request : POST /api/ai/ask
  
  askAI(question: string): Observable<any> {
    return this.http.post(`${this.baseUrl}/ask`, { question });
  }

  //request : POST /api/ai/generate-quiz
  generateQuiz(content: string, numQuestions: number): Observable<any> {
    return this.http.post(`${this.baseUrl}/generate-quiz`, {
      content,
      numQuestions
    });
  }

  //request : POST /api/ai/progress-summary
  generateProgressSummary(progressData: string): Observable<any> {
    return this.http.post(`${this.baseUrl}/progress-summary`, {
      progressData
    });
  }
}