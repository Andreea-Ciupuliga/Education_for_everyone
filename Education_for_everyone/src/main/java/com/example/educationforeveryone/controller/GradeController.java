package com.example.educationforeveryone.controller;

import com.example.educationforeveryone.service.GradeService;
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
@RequestMapping("/grades")
@Tag(name = "/grades", description = "grade controller")
public class GradeController {

    private final GradeService gradeService;

    public GradeController(GradeService gradeService) {
        this.gradeService = gradeService;
    }

    @Operation(summary = "Assign homework to a student", description = "Assign homework to a student")
    @PreAuthorize("hasAnyRole('PROFESSOR')")
    @PostMapping("/assign-homework")
    public ResponseEntity<SuccessDto> assignHomework(@RequestParam Long studentId,
                                                     @RequestParam Long homeworkId,
                                                     Authentication authentication) {
        gradeService.assignHomeworkToStudent(studentId, homeworkId, Helper.getKeycloakUser(authentication));
        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }

    @Operation(summary = "Assign a score to a student's homework", description = "Assign a score to a student's homework")
    @PreAuthorize("hasAnyRole('PROFESSOR')")
    @PostMapping("/assign-score")
    public ResponseEntity<SuccessDto> assignScore(@RequestParam Long studentId,
                                                  @RequestParam Long homeworkId,
                                                  @RequestParam Long score,
                                                  Authentication authentication) {
        gradeService.assignScoreToStudent(studentId, homeworkId, score, Helper.getKeycloakUser(authentication));
        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('PROFESSOR')")
    @DeleteMapping("/remove-homework")
    public ResponseEntity<SuccessDto> removeHomework(@RequestParam Long studentId,
                                                     @RequestParam Long homeworkId,
                                                     Authentication authentication) {
        gradeService.removeHomeworkFromStudent(studentId, homeworkId, Helper.getKeycloakUser(authentication));
        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR','STUDENT')")
    @GetMapping("/average-student-grades")
    public ResponseEntity<Double> averageStudentGrades(@RequestParam Long studentId,
                                                       Authentication authentication) {
        return new ResponseEntity<>(gradeService.averageStudentGrades(studentId, Helper.getKeycloakUser(authentication)), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR','STUDENT')")
    @GetMapping("/student-grades")
    public ResponseEntity<List<String>> getAllStudentsGrades(Authentication authentication,
                                                             @RequestParam Long studentId) {

        return new ResponseEntity<>(gradeService.showGrades(Helper.getKeycloakUser(authentication), studentId), HttpStatus.OK);
    }

}
