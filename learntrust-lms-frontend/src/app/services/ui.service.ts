import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UiService {

  private apiUrl = 'http://localhost:8080/api/ui/theme';

  constructor(private http: HttpClient) {}

  getTheme(): Observable<any> {
    return this.http.get<any>(this.apiUrl);
  }
}