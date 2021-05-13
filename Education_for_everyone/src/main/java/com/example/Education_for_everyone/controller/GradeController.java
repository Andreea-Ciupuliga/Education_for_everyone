package com.example.Education_for_everyone.controller;


import com.example.Education_for_everyone.service.GradeService;
import com.example.Education_for_everyone.utils.Helper;
import com.example.Education_for_everyone.utils.SuccessDto;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/grade")
public class GradeController {

    GradeService gradeService;

    @Autowired
    public GradeController(GradeService gradeService) {
        this.gradeService = gradeService;
    }

    @PreAuthorize("hasAnyRole('PROFESSOR')")
    @PostMapping("/register")
    public ResponseEntity<SuccessDto> assignHomeworkToStudent(@RequestParam Long studentId, @RequestParam Long homeworkId)
    {
        gradeService.assignHomeworkToStudent(studentId,homeworkId);

        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }


    @PreAuthorize("hasAnyRole('PROFESSOR')")
    @PostMapping("/assignScore")
    public ResponseEntity<SuccessDto> assignScoreToStudent(@RequestParam Long studentId, @RequestParam Long homeworkId,@RequestParam Long score)
    {
        gradeService.assignScoreToStudent(studentId,homeworkId,score);

        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('PROFESSOR')")
    @DeleteMapping("")
    @SneakyThrows
    public ResponseEntity<SuccessDto> deleteHomeworkFromStudent(@RequestParam Long studentId,@RequestParam Long homeworkId)
    {
        gradeService.removeHomeworkFromStudent(studentId,homeworkId);

        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }

}
