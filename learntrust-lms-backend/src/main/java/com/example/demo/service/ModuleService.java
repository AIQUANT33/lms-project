package com.example.demo.service;

import com.example.demo.entity.Course;
import com.example.demo.entity.Module;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.ModuleRepository;
import com.example.demo.repository.ModuleCompletionRepository;
import com.example.demo.repository.ContentCompletionRepository;
import com.example.demo.repository.ModuleContentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ModuleService {

    private final ModuleRepository moduleRepository;
    private final CourseRepository courseRepository;
    private final ModuleCompletionRepository moduleCompletionRepository;
    private final ContentCompletionRepository contentCompletionRepository;
    private final ModuleContentRepository moduleContentRepository;

    public ModuleService(ModuleRepository moduleRepository,
                         CourseRepository courseRepository,
                         ModuleCompletionRepository moduleCompletionRepository,
                         ContentCompletionRepository contentCompletionRepository,
                         ModuleContentRepository moduleContentRepository) {
        this.moduleRepository = moduleRepository;
        this.courseRepository = courseRepository;
        this.moduleCompletionRepository = moduleCompletionRepository;
        this.contentCompletionRepository = contentCompletionRepository;
        this.moduleContentRepository = moduleContentRepository;
    }

    // CREATE MODULE
    public Module createModule(Long courseId, Module module) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        int nextOrder = (int) moduleRepository.countByCourse(course) + 1;
        module.setModuleOrder(nextOrder);
        module.setSequenceNo(nextOrder);
        module.setCourse(course);

        return moduleRepository.save(module);
    }

    // GET MODULES
    public List<Module> getModulesByCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        return moduleRepository.findByCourseOrderByModuleOrderAsc(course);
    }

    // MOVE UP
    public void moveModuleUp(Long moduleId) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Module not found"));

        if (module.getModuleOrder() == 1) return;

        Module prev = moduleRepository
                .findByCourseOrderByModuleOrderAsc(module.getCourse())
                .stream()
                .filter(m -> m.getModuleOrder() == module.getModuleOrder() - 1)
                .findFirst()
                .orElse(null);

        if (prev != null) {
            int temp = module.getModuleOrder();
            module.setModuleOrder(prev.getModuleOrder());
            prev.setModuleOrder(temp);
            moduleRepository.save(prev);
            moduleRepository.save(module);
        }
    }

    // MOVE DOWN
    public void moveModuleDown(Long moduleId) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Module not found"));

        int maxOrder = (int) moduleRepository.countByCourse(module.getCourse());
        if (module.getModuleOrder() >= maxOrder) return;

        Module next = moduleRepository
                .findByCourseOrderByModuleOrderAsc(module.getCourse())
                .stream()
                .filter(m -> m.getModuleOrder() == module.getModuleOrder() + 1)
                .findFirst()
                .orElse(null);

        if (next != null) {
            int temp = module.getModuleOrder();
            module.setModuleOrder(next.getModuleOrder());
            next.setModuleOrder(temp);
            moduleRepository.save(next);
            moduleRepository.save(module);
        }
    }

    // DELETE MODULE
    // deletes in correct order to avoid FK constraint errors
    @Transactional
    public void deleteModule(Long moduleId) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Module not found"));

        Course course = module.getCourse();

        // Step 1: get all contents of this module
        var contents = moduleContentRepository.findByModule(module);

        // Step 2: delete content completions for each content
        contents.forEach(content ->
            contentCompletionRepository.deleteByContent(content)
        );

        // Step 3: delete module completions
        moduleCompletionRepository.deleteByModule(module);

        // Step 4: delete the module (cascade removes contents + assessments)
        moduleRepository.delete(module);

        // Step 5: normalize order after delete
        List<Module> modules =
                moduleRepository.findByCourseOrderByModuleOrderAsc(course);

        int order = 1;
        for (Module m : modules) {
            m.setModuleOrder(order);
            m.setSequenceNo(order);
            order++;
        }

        moduleRepository.saveAll(modules);
    }
}
