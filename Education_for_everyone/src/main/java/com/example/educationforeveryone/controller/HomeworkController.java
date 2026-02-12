package com.example.educationforeveryone.controller;

import com.example.educationforeveryone.models.Homework;
import com.example.educationforeveryone.service.HomeworkService;
import com.example.educationforeveryone.utils.SuccessDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/homework")
@Tag(name = "/homework", description = "homework controller")
public class HomeworkController {

    private final HomeworkService homeworkService;

    public HomeworkController(HomeworkService homeworkService) {
        this.homeworkService = homeworkService;
    }

    @Operation(summary = "Register a homework", description = "Register a homework")
    @PreAuthorize("hasRole('PROFESSOR')")
    @PostMapping("/register")
    public ResponseEntity<SuccessDto> registerHomework(@RequestBody Homework homework) {
        homeworkService.registerHomework(homework);
        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }

    @Operation(summary = "Get homework by id", description = "Get homework by its id")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR')")
    @GetMapping("/{homeworkId}")
    public ResponseEntity<Homework> getHomeworkById(@PathVariable Long homeworkId) {
        return new ResponseEntity<>(homeworkService.getHomeworkByIdOrThrowException(homeworkId), HttpStatus.OK);
    }

    @Operation(summary = "Delete homework by id", description = "Delete homework by its id")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR')")
    @DeleteMapping("/{homeworkId}")
    public ResponseEntity<SuccessDto> removeHomework(@PathVariable Long homeworkId) {
        homeworkService.removeHomework(homeworkId);
        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }

    @Operation(summary = "Update homework", description = "Update homework")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR')")
    @PutMapping("/{homeworkId}")
    public ResponseEntity<SuccessDto> updateHomework(@PathVariable Long homeworkId, @RequestBody Homework homework) {
        homeworkService.updateHomework(homeworkId, homework);
        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }
}
