package com.example.Education_for_everyone.controller;

import com.example.Education_for_everyone.dtos.GetBookDto;
import com.example.Education_for_everyone.dtos.RegisterBookDto;
import com.example.Education_for_everyone.models.Homework;
import com.example.Education_for_everyone.service.HomeworkService;
import com.example.Education_for_everyone.utils.SuccessDto;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/homework")
public class HomeworkController {

    private HomeworkService homeworkService;

    @Autowired
    public HomeworkController(HomeworkService homeworkService) {
        this.homeworkService = homeworkService;
    }


    @PreAuthorize("hasRole('PROFESSOR')")
    @PostMapping("/register")
    @SneakyThrows
    public ResponseEntity<SuccessDto> registerHomework(@RequestBody Homework homework)
    {
        homeworkService.registerHomework(homework);
        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR')")
    @GetMapping()
    @SneakyThrows
    public ResponseEntity<Homework>getHomework(@RequestParam Long homeworkId)
    {
        return new ResponseEntity<>(homeworkService.getHomework(homeworkId), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR')")
    @DeleteMapping()
    @SneakyThrows
    public ResponseEntity<SuccessDto>removeHomework(@RequestParam Long homeworkId)
    {
        homeworkService.removeHomework(homeworkId);

        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR')")
    @PutMapping()
    @SneakyThrows
    public ResponseEntity<SuccessDto>putHomework(@RequestParam Long homeworkId,@RequestBody Homework homework)
    {
        homeworkService.putHomework(homeworkId,homework);
        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }
}
