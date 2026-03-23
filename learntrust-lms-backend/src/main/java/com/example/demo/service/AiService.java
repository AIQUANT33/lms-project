package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
/*Its job is to connect your Spring Boot backend
 to the Google Gemini AI model,
  send prompts to the AI, 
  and return the AI-generated responses */
@Service
public class AiService {

    @Value("${gemini.api.key}")
    private String apiKey;
    
    //rest endpoint for google gemini ai
    private final String GEMINI_URL =
    "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=";

    private String callGemini(String prompt) {
        /*This function sends a prompt to Gemini AI and returns the generated response.*/

        RestTemplate restTemplate = new RestTemplate();

        //text : actual prompt send to ai
        Map<String, Object> text = new HashMap<>();
        text.put("text", prompt);

        //wrap inside parts
        Map<String, Object> part = new HashMap<>();
        part.put("parts", List.of(text));


        //wrap inside content
        Map<String, Object> body = new HashMap<>();
        body.put("contents", List.of(part));
        
        //tells the server this request body is JSON
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        //combinrd json body and http headers into single request object
        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(body, headers);
        
        //sends http request to gemini ai server
        ResponseEntity<String> response =
                restTemplate.exchange(
                        GEMINI_URL + apiKey,
                        HttpMethod.POST,
                        request,
                        String.class
                );

        return response.getBody(); //receive ai response
    }

    public String askAI(String question) {
        return callGemini(question);
    }

    public String generateQuiz(String content, int numQuestions) {

        String prompt =
                "Generate " + numQuestions +
                " multiple choice quiz questions from the following content:\n" +
                content;

        return callGemini(prompt);
    }

    public String generateProgressSummary(String progressData) {

        String prompt =
                """
                You are an AI learning assistant inside an LMS platform.

                Analyze the student learning data and generate a short progress report.

                Format the response using these sections:

                 Learning Overview
                 Progress Insights
                 Suggested Next Step

                Keep the tone encouraging and concise.

                Student Data:
                """ + progressData;

        return callGemini(prompt);
    }
}

/* this service is used for
AI chat
quiz generation
summarizing
 */