package com.example.Education_for_everyone.controller;


import com.example.Education_for_everyone.dtos.GetGroupDto;
import com.example.Education_for_everyone.dtos.GetStudentDto;
import com.example.Education_for_everyone.service.GroupOfStudentsService;
import com.example.Education_for_everyone.utils.Helper;
import com.example.Education_for_everyone.utils.SuccessDto;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/groupOfStudents")
public class GroupOfStudentsController {

    GroupOfStudentsService groupOfStudentsService;

    @Autowired
    public GroupOfStudentsController(GroupOfStudentsService groupOfStudentsService) {
        this.groupOfStudentsService = groupOfStudentsService;
    }

    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR')")
    @PostMapping("/register")
    public ResponseEntity<SuccessDto> addStudentInGroup(@RequestParam Long studentId, @RequestParam Long groupId, Authentication authentication)
    {
        groupOfStudentsService.addStudentInGroup(studentId,groupId,Helper.getKeycloakUser(authentication));

        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR')")
    @DeleteMapping("")
    @SneakyThrows
    public ResponseEntity<SuccessDto> deleteStudentFromGroup(@RequestParam Long studentId,@RequestParam Long groupId,Authentication authentication)
    {
        groupOfStudentsService.removeStudentFromGroup(studentId,groupId,Helper.getKeycloakUser(authentication));

        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR','STUDENT')")
    @GetMapping("/showStudentsByGroupId")
    public List<GetStudentDto> getAllStudentsByGroupId(@RequestParam Long groupId) {

        return groupOfStudentsService.getAllStudentsByGroupId(groupId);
    }

}
