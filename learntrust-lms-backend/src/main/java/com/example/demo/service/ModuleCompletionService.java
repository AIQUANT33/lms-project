package com.example.demo.service;

import com.example.demo.dto.ModuleCompletionRequest;
import com.example.demo.entity.Module;
import com.example.demo.entity.ModuleCompletion;
import com.example.demo.entity.User;
import com.example.demo.repository.ModuleCompletionRepository;
import com.example.demo.repository.ModuleRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class ModuleCompletionService {

    private final ModuleCompletionRepository completionRepository;
    private final UserRepository userRepository;
    private final ModuleRepository moduleRepository;

    public ModuleCompletionService(ModuleCompletionRepository completionRepository,
                                   UserRepository userRepository,
                                   ModuleRepository moduleRepository) {
        this.completionRepository = completionRepository;
        this.userRepository = userRepository;
        this.moduleRepository = moduleRepository;
    }

    public ModuleCompletion completeModule(ModuleCompletionRequest request) {
         
        //select * from users where id = request.getStudentId()
        // throw error if not found 
        User student = userRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));
        

        //select * from modules where id = request.getModuleId()
        Module module = moduleRepository.findById(request.getModuleId())
                .orElseThrow(() -> new RuntimeException("Module not found"));

        completionRepository.findByStudentAndModule(student, module)
                .ifPresent(c -> {
                    throw new RuntimeException("Module already completed");
                });


                /*
                
                this sets
                student_id 
                module_id
                completed_at (auto)
             */
        ModuleCompletion completion = new ModuleCompletion();
        completion.setStudent(student);
        completion.setModule(module);

        return completionRepository.save(completion); //save to db and return the saved entity with id and completedAt populated
    }
}


//marks a module as completed by a student, prevents duplicate completions