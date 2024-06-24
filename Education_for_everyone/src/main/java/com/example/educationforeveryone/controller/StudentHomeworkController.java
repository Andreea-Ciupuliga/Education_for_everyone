package com.example.educationforeveryone.controller;

import com.example.educationforeveryone.service.StudentHomeworkService;
import com.example.educationforeveryone.utils.Helper;
import com.example.educationforeveryone.utils.SuccessDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/student-homework")
@Tag(name = "/student-homework", description = "student homework controller")
public class StudentHomeworkController {

    private final StudentHomeworkService studentHomeworkService;

    public StudentHomeworkController(StudentHomeworkService studentHomeworkService) {
        this.studentHomeworkService = studentHomeworkService;
    }

    @Operation(summary = "Assign homework to a student", description = "Assign homework to a student")
    @PreAuthorize("hasAnyRole('PROFESSOR')")
    @PostMapping("/assign-homework/{studentId}/{homeworkId}")
    public ResponseEntity<SuccessDto> assignHomework(
            @PathVariable Long studentId,
            @PathVariable Long homeworkId,
            Authentication authentication) {
        studentHomeworkService.registerStudentHomework(studentId, homeworkId, Helper.getKeycloakUser(authentication));
        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }

    @Operation(summary = "Assign a score to a student's homework", description = "Assign a score to a student's homework")
    @PreAuthorize("hasAnyRole('PROFESSOR')")
    @PostMapping("/assign-score/{studentId}/{homeworkId}/{score}")
    public ResponseEntity<SuccessDto> assignScore(
            @PathVariable Long studentId,
            @PathVariable Long homeworkId,
            @PathVariable Long score,
            Authentication authentication) {
        studentHomeworkService.assignScoreToStudent(studentId, homeworkId, score, Helper.getKeycloakUser(authentication));
        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }

    @Operation(summary = "Remove homework from a student", description = "Remove homework from a student")
    @PreAuthorize("hasAnyRole('PROFESSOR')")
    @DeleteMapping("/{studentId}/{homeworkId}")
    public ResponseEntity<SuccessDto> removeHomework(
            @PathVariable Long studentId,
            @PathVariable Long homeworkId,
            Authentication authentication) {
        studentHomeworkService.removeStudentHomework(studentId, homeworkId, Helper.getKeycloakUser(authentication));
        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }

    @Operation(summary = "Get average student grades for a student", description = "Get average student grades for a student")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR','STUDENT')")
    @GetMapping("/average-student-grades/{studentId}")
    public ResponseEntity<Double> getAverageStudentGrades(
            @PathVariable Long studentId,
            Authentication authentication) {
        return new ResponseEntity<>(studentHomeworkService.getAverageStudentGrades(studentId, Helper.getKeycloakUser(authentication)), HttpStatus.OK);
    }

    @Operation(summary = "Get all grades for a student", description = "Get all grades for a student id")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR','STUDENT')")
    @GetMapping("/grades/{studentId}")
    public ResponseEntity<List<String>> getStudentGrades(
            Authentication authentication,
            @PathVariable Long studentId) {
        return new ResponseEntity<>(studentHomeworkService.getStudentGrades(Helper.getKeycloakUser(authentication), studentId), HttpStatus.OK);
    }
}
