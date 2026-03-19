import { Component, ElementRef, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AiService } from '../../services/ai.service';
 
//stores conversation messages
interface ChatMessage {
  sender: 'user' | 'ai';
  text: string;
}

@Component({
  selector: 'app-ai-assistant',
  standalone: true, //This means the component does not need to be declared inside an Angular module.
  imports: [CommonModule, FormsModule],
  templateUrl: './ai-assistant.html',
  styleUrls: ['./ai-assistant.css']
})
export class AiAssistant {

  isOpen = false;
  isFullscreen = false;
  lightTheme = false;

  question = '';
  loading = false;

  messages: ChatMessage[] = [];

  @ViewChild('chatBody') chatBody!: ElementRef; //access the chat contrainer in html
  //used for automatic scrolling when new messages arrive 

  constructor(private aiService: AiService) {}

  toggle() { //open or close the chatbot
    this.isOpen = !this.isOpen;
  }

  toggleFullscreen() { //full screen 
    this.isFullscreen = !this.isFullscreen;
  }

  sendMessage() {

    if (!this.question.trim()) return; //prevents empty messages

    const userMessage = this.question; //store user message

    //add user message to chat
    this.messages.push({
      sender: 'user',
      text: userMessage
    });

    this.question = '';
    this.loading = true;

    setTimeout(() => this.scrollToBottom()); //to ensure that the newest msg is visible

    this.aiService.askAI(userMessage).subscribe({ //sends request to backend ai
       
      //runs when ai returns a response
      next: (res:any) => {

      let answer = '';

if (typeof res === 'string') {
  answer = res;
} else {
  answer = res?.candidates?.[0]?.content?.parts?.[0]?.text || 'AI response failed';
}
        answer = answer
          .replace(/\*\*(.*?)\*\*/g, '<b>$1</b>')
          .replace(/\n/g, '<br>')
          .replace(/\* /g, '• ');

        this.messages.push({
          sender: 'ai',
          text: answer
        });

        this.loading = false;

        setTimeout(() => this.scrollToBottom());

      },

      error: () => {
        this.messages.push({
          sender: 'ai',
          text: 'AI service failed.'
        });

        this.loading = false;
      }

    });

  }
  
  //ensure ai response is visible
  scrollToBottom() {
    if (!this.chatBody) return;
    this.chatBody.nativeElement.scrollTop =
      this.chatBody.nativeElement.scrollHeight;
  }

}