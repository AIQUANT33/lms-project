import { Component, OnInit } from '@angular/core';
import { RouterModule, Router, NavigationEnd } from '@angular/router';
import { CommonModule } from '@angular/common';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.html',
  styleUrls: ['./navbar.css']
})
export class NavbarComponent implements OnInit {

  role = '';
  userName = '';
  isLoggedIn = false;

  darkMode = false;

  constructor(private router: Router) {}

  ngOnInit() {

    this.loadUser(); /** -reads user from localStorage
                         -sets role, userName, isLoggedIn */

    this.darkMode = localStorage.getItem('darkMode') === 'true';

    if (this.darkMode) {
      document.documentElement.classList.add('dark-theme');
    }

    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe(() => {
        this.loadUser();
      });
  }

  loadUser() {
    const user = JSON.parse(localStorage.getItem('user') || '{}');

    if (user && user.role) {
      this.role = user.role;
      this.userName = user.name || user.email || '';
      this.isLoggedIn = true;
    } else {
      this.role = '';
      this.userName = '';
      this.isLoggedIn = false;
    }
  }

  logout() {
    localStorage.removeItem('user');
    this.router.navigate(['/login']);
  }

  toggleTheme() {

    this.darkMode = !this.darkMode;

    localStorage.setItem('darkMode', String(this.darkMode));

    const root = document.documentElement;

    if (this.darkMode) {
      root.classList.add('dark-theme');
    } else {
      root.classList.remove('dark-theme');
    }

  }

  handleTeachClick() {
    this.router.navigate(['/become-trainer']);
  }

  forceNavigate(url: string) {
    if (this.router.url === url) {
      this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
        this.router.navigate([url]);
      });
    }
  }
}












/*

Reads user from localStorage
→ sets role (STUDENT/TRAINER/ADMIN)
→ shows correct nav links for that role
→ shows username + logout if logged in
→ shows login + signup if not logged in
→ re-checks user on every route change
*/