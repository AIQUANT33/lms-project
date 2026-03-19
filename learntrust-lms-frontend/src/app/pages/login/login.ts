import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { Router, RouterModule, ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css'],
})
export class LoginComponent implements OnInit {

  email = ''; //stores user input
  password = '';  //stores password
  errorMsg = '';   //ui error display
  isLoading = false; //to show loading state during login
  intent: string | null = null; //to handle trainer-specific login flow

  constructor(
    private auth: AuthService, //API calls and auth management
    private router: Router,  //navigation after login
    private route: ActivatedRoute  //to read query params for intent-based login
  ) {}

  // AUTO REDIRECT IF ALREADY LOGGED IN
  ngOnInit() {
    /* 
       if the url is /login?intetent=trainer
       then intent = "trainer"
       used for special flow 
    */
     this.intent = this.route.snapshot.queryParamMap.get('intent');
     
     const role = this.auth.getUserRole(); //reads form localstorage
    

     /* If user already logged in:
        login page is skipped
        user goes directly to dashboard*/
    if (role === 'STUDENT') {
      this.router.navigate(['/student']);
    } else if (role === 'TRAINER') {
      this.router.navigate(['/trainer']);
    } else if (role === 'ADMIN') {
      this.router.navigate(['/admin']);
    }
  }


  // NORMAL LOGIN
  //clears old error and starts loading state
  login() {
    this.errorMsg = '';
    this.isLoading = true;
    

    //goes into backend
    const payload = {
      email: this.email,
      password: this.password,
    };
     
    //calls auth servcie
    // frontend->http post->backend
    this.auth.login(payload).subscribe({
      next: (res: any) => { //res = user object from backend
        console.log('LOGIN RESPONSE:', res);
        this.isLoading = false;

        if (!res || !res.role) {
          this.errorMsg = 'Invalid server response';
          return;
        }

        this.auth.saveUser(res); //save user (saves into localStorage.setItem('user', JSON.stringify(user));)

        //  TRAINER FLOW
        if (this.intent === 'trainer') {
          if (res.role === 'TRAINER') {
            this.router.navigate(['/trainer']);
          } else {
            alert('Please login with a trainer account.');
            this.router.navigate(['/login']);
          }
          return;
        }

        // NORMAL FLOW (roles based navigation)
        switch (res.role) {
          case 'STUDENT':
            this.router.navigate(['/student']);
            break;
          case 'TRAINER':
            this.router.navigate(['/trainer']);
            break;
          case 'ADMIN':
            this.router.navigate(['/admin']);
            break;
          default:
            this.errorMsg = 'Unknown user role';
        }
      },
      error: (err: any) => {
        this.isLoading = false;
        const errorMessage = err.error?.error || 'Invalid credentials';
        this.errorMsg = errorMessage;
      },
    });
  }
}



//AUTHENTICATION PIPELINE
/*
login.html
 → login.ts
 → AuthService
 → POST /auth/login
 → AuthController
 → UserService
 → UserRepository
 → Database
 → User returned
 → localStorage saved
 → router.navigate()
 → dashboard opens
 
 */