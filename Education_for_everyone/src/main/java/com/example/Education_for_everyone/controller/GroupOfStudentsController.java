package com.example.Education_for_everyone.controller;


import com.example.Education_for_everyone.service.GroupOfStudentsService;
import com.example.Education_for_everyone.utils.SuccessDto;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/groupOfStudents")
public class GroupOfStudentsController {

    GroupOfStudentsService groupOfStudentsService;

    @Autowired
    public GroupOfStudentsController(GroupOfStudentsService groupOfStudentsService) {
        this.groupOfStudentsService = groupOfStudentsService;
    }

    @PostMapping("/register")
    public ResponseEntity<SuccessDto> addStudentInGroup(@RequestParam Long studentId,@RequestParam Long groupId)
    {
        groupOfStudentsService.addStudentInGroup(studentId,groupId);

        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }

    @DeleteMapping("")
    @SneakyThrows
    public ResponseEntity<SuccessDto> deleteStudentFromGroup(@RequestParam Long studentId,@RequestParam Long groupId)
    {
        groupOfStudentsService.removeStudentFromGroup(studentId,groupId);

        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }


}
