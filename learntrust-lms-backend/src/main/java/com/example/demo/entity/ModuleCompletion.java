package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "module_completions")
public class ModuleCompletion {

    /* 
       -primary key id
       - auto-increment
        - each completion row gets unique id.
    */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne //many completions for one student
    @JoinColumn(name = "student_id", nullable = false) //fk column is student_id
    private User student; 



//many students can complete one module
    @ManyToOne //many completions for one module
    @JoinColumn(name = "module_id", nullable = false)  //fk  = module_id
    private Module module;

    private LocalDateTime completedAt; //Stores timestamp when completion happened.

    public ModuleCompletion() { //default constructor
        this.completedAt = LocalDateTime.now();

        /* 
        
        whenever a new completion record is created, 
        we set completedAt to current timestamp.
         This allows us to track when the student completed the module.


         no need to send from frontend, backend will set it automatically when creating the record.

        
        
        */

    }

    // getters and setters
}
