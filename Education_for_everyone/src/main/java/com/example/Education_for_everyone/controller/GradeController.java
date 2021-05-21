package com.example.Education_for_everyone.controller;
import com.example.Education_for_everyone.service.GradeService;
import com.example.Education_for_everyone.utils.Helper;
import com.example.Education_for_everyone.utils.SuccessDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/grade")
public class GradeController {

    GradeService gradeService;

    @Autowired
    public GradeController(GradeService gradeService) {
        this.gradeService = gradeService;
    }

    @PreAuthorize("hasAnyRole('PROFESSOR')")
    @PostMapping("/assignHomework")
    public ResponseEntity<SuccessDto> assignHomeworkToStudent(@RequestParam Long studentId, @RequestParam Long homeworkId,Authentication authentication)
    {
        gradeService.assignHomeworkToStudent(studentId,homeworkId,Helper.getKeycloakUser(authentication));

        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }


    @PreAuthorize("hasAnyRole('PROFESSOR')")
    @PostMapping("/assignScore")
    public ResponseEntity<SuccessDto> assignScoreToStudent(@RequestParam Long studentId, @RequestParam Long homeworkId,@RequestParam Long score,Authentication authentication)
    {
        gradeService.assignScoreToStudent(studentId,homeworkId,score,Helper.getKeycloakUser(authentication));

        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('PROFESSOR')")
    @DeleteMapping("")
    public ResponseEntity<SuccessDto> deleteHomeworkFromStudent(@RequestParam Long studentId,@RequestParam Long homeworkId,Authentication authentication)
    {
        gradeService.removeHomeworkFromStudent(studentId,homeworkId,Helper.getKeycloakUser(authentication));

        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR','STUDENT')")
    @GetMapping("/averageStudentGrades")
    public ResponseEntity<Double> averageStudentGrades(@RequestParam Long studentId,Authentication authentication)
    {
        return new ResponseEntity<>(gradeService.averageStudentGrades(studentId,Helper.getKeycloakUser(authentication)), HttpStatus.OK);
    }


    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR','STUDENT')")
    @GetMapping("/showStudentsGrades")
    public ResponseEntity<List<String>> getAllStudentsGrades(Authentication authentication,@RequestParam Long studentId) {

        return new ResponseEntity<>(gradeService.showGrades(Helper.getKeycloakUser(authentication),studentId), HttpStatus.OK);
    }

}
