import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private baseUrl = 'http://localhost:8080';

  constructor(private http: HttpClient) {}

  // REGISTER — unchanged
  register(user: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/users`, user);
  }

  // LOGIN — now saves token separately
  login(payload: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/auth/login`, payload).pipe(
      tap((response: any) => {
        if (response && response.token) {
          // Save token separately for HTTP requests
          localStorage.setItem('token', response.token);
          // Save user info (without password)
          const user = {
            userId: response.userId,
            name:   response.name,
            email:  response.email,
            role:   response.role,
            status: response.status
          };
          localStorage.setItem('user', JSON.stringify(user));
        }
      })
    );
  }

  // Get JWT token
  getToken(): string | null {
    return localStorage.getItem('token');
  }

  // User storage — unchanged
  saveUser(user: any) {
    localStorage.setItem('user', JSON.stringify(user));
  }

  getUser() {
    const data = localStorage.getItem('user');
    return data ? JSON.parse(data) : null;
  }

  isLoggedIn(): boolean {
    return !!localStorage.getItem('token'); // check token not user
  }

  getUserRole(): string | null {
    const user = this.getUser();
    return user?.role || null;
  }

  getUserId(): number | null {
    const user = this.getUser();
    return user?.userId || user?.id || null;
  }

  getUserName(): string | null {
    const user = this.getUser();
    return user?.name || null;
  }

  // LOGOUT — clear both token and user
  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  }
}