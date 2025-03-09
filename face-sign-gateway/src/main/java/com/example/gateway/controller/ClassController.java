package com.example.gateway.controller;

import com.example.entity.Class;
import com.example.service.IClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/classes")
public class ClassController {
    @Autowired
    private IClassService classService;

    @PostMapping
    public ResponseEntity<Class> addClass(@RequestBody Class classEntity) {
        return ResponseEntity.ok(classService.addClass(classEntity));
    }

    @GetMapping("/{classId}")
    public ResponseEntity<Class> getClassById(@PathVariable Long classId) {
        return ResponseEntity.ok(classService.getClassById(classId));
    }

    @PutMapping("/{classId}")
    public ResponseEntity<Class> updateClass(@PathVariable Long classId, @RequestBody Class classEntity) {
        classEntity.setClassId(classId);
        return ResponseEntity.ok(classService.updateClass(classEntity));
    }

    @DeleteMapping("/{classId}")
    public ResponseEntity<Void> deleteClass(@PathVariable Long classId) {
        classService.deleteClass(classId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Class>> getAllClasses() {
        return ResponseEntity.ok(classService.getAllClasses());
    }
}