package com.example.educationforeveryone.controller;

import com.example.educationforeveryone.dtos.GetStudentDto;
import com.example.educationforeveryone.service.StudentGroupService;
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
@RequestMapping("/group-of-students")
@Tag(name = "/group-of-students", description = "group-of-students controller")
public class StudentGroupController {

    private final StudentGroupService studentGroupService;

    public StudentGroupController(StudentGroupService studentGroupService) {
        this.studentGroupService = studentGroupService;
    }

    @Operation(summary = "Register a student into a group", description = "Register a student into a group using student id and group id")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR')")
    @PostMapping("/register/{studentId}/{groupId}")
    public ResponseEntity<SuccessDto> registerStudentInGroup(@PathVariable Long studentId, @PathVariable Long groupId, Authentication authentication) {
        studentGroupService.registerStudentInGroup(studentId, groupId, Helper.getKeycloakUser(authentication));
        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }

    @Operation(summary = "Remove a student from a group", description = "Remove a student from a group using student id and group id")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR')")
    @DeleteMapping("/{studentId}/{groupId}")
    public ResponseEntity<SuccessDto> removeStudentFromGroup(@PathVariable Long studentId, @PathVariable Long groupId, Authentication authentication) {
        studentGroupService.removeStudentFromGroup(studentId, groupId, Helper.getKeycloakUser(authentication));
        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }

    @Operation(summary = "Get all students from a group", description = "Get all students from a group using group id")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR','STUDENT')")
    @GetMapping("/{groupId}/students")
    public List<GetStudentDto> getAllStudentsByGroupId(@PathVariable Long groupId) {
        return studentGroupService.getAllStudentsByGroupId(groupId);
    }
}

