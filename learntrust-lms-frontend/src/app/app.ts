import { Component, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { AiAssistant } from './shared/ai-assistant/ai-assistant';
import { HttpClientModule } from '@angular/common/http';
import { UiService } from './services/ui.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, AiAssistant, HttpClientModule],
  templateUrl: './app.html', 
  styleUrls: ['./app.css']
})
export class AppComponent implements OnInit {

  constructor(private uiService: UiService) {}

  ngOnInit() {
    this.applyThemeFromBackend();
  }
   
       // Sets CSS variables on the ROOT html element
          // This makes them available to EVERY component
  applyThemeFromBackend() {
    this.uiService.getTheme().subscribe({
      next: (theme) => {
        if (theme) {
          document.documentElement.style.setProperty('--primary', theme.primaryColor);
          document.documentElement.style.setProperty('--secondary', theme.secondaryColor);
          document.documentElement.style.setProperty('--accent', theme.accentColor);
          document.documentElement.style.setProperty('--bg', theme.backgroundColor);
          document.documentElement.style.setProperty('--text', theme.textColor);
        }
      },
      error: () => {
        console.log('Using default theme');
      }
    });
  }
}