package com.example.educationforeveryone.controller;

import com.example.educationforeveryone.dtos.GetGroupDto;
import com.example.educationforeveryone.dtos.RegisterGroupDto;
import com.example.educationforeveryone.service.GroupService;
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
@RequestMapping("/groups")
@Tag(name = "/groups", description = "group controller")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @Operation(summary = "Register a group", description = "Register a group")
    @PreAuthorize("hasAnyRole('PROFESSOR')")
    @PostMapping("/register")
    public ResponseEntity<SuccessDto> registerGroup(@RequestBody RegisterGroupDto registerGroupDto, Authentication authentication) {
        groupService.registerGroup(registerGroupDto, Helper.getKeycloakUser(authentication));
        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }

    @Operation(summary = "Remove a group", description = "Remove a group")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR')")
    @DeleteMapping("/{group-id}")
    public ResponseEntity<SuccessDto> removeGroup(@PathVariable("group-id") Long groupId, Authentication authentication) {
        groupService.removeGroup(groupId, Helper.getKeycloakUser(authentication));
        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }

    @Operation(summary = "Get a group", description = "Get a group by group id")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR','STUDENT')")
    @GetMapping("/{group-id}")
    public ResponseEntity<GetGroupDto> getGroupById(@PathVariable("group-id") Long groupId) {
        return new ResponseEntity<>(groupService.getGroupById(groupId), HttpStatus.OK);
    }

    @Operation(summary = "Update a group", description = "Update a group")
    @PreAuthorize("hasAnyRole('PROFESSOR')")
    @PutMapping("/{group-id}")
    public ResponseEntity<SuccessDto> updateGroup(@PathVariable("group-id") Long groupId,
                                                  @RequestBody RegisterGroupDto registerGroupDto,
                                                  Authentication authentication) {
        groupService.updateGroup(groupId, registerGroupDto, Helper.getKeycloakUser(authentication));
        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }

    @Operation(summary = "Get all groups", description = "Get all groups")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR','STUDENT')")
    @GetMapping
    public ResponseEntity<List<GetGroupDto>> getAllGroups() {
        return new ResponseEntity<>(groupService.getAllGroups(), HttpStatus.OK);
    }

    @Operation(summary = "Get all groups by subject", description = "Get all groups by subject")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR','STUDENT')")
    @GetMapping("/by-subject/{subject}")
    public ResponseEntity<List<GetGroupDto>> getAllGroupsBySubject(@PathVariable String subject) {
        return new ResponseEntity<>(groupService.getAllGroupsBySubject(subject), HttpStatus.OK);
    }

    @Operation(summary = "Get all groups by professor name", description = "Get all groups by professor name")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR','STUDENT')")
    @GetMapping("/by-professor-name/{professor-name}")
    public ResponseEntity<List<GetGroupDto>> getAllGroupsByProfessorName(@PathVariable("professor-name") String professorName) {
        return new ResponseEntity<>(groupService.getAllGroupsByProfessorName(professorName), HttpStatus.OK);
    }
}
