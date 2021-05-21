package com.example.Education_for_everyone.controller;
import com.example.Education_for_everyone.dtos.GetGroupDto;
import com.example.Education_for_everyone.dtos.RegisterGroupDto;
import com.example.Education_for_everyone.service.GroupService;
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


@RequestMapping("/group")
public class GroupController {

    private GroupService groupService;

    @Autowired
    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }


    @PreAuthorize("hasAnyRole('PROFESSOR')")
    @PostMapping("/register")
    @SneakyThrows
    public ResponseEntity<SuccessDto> registerGroup(@RequestBody RegisterGroupDto registerGroupDto,Authentication authentication)
    {
        groupService.registerGroup(registerGroupDto,Helper.getKeycloakUser(authentication));
        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR')")
    @DeleteMapping()
    @SneakyThrows
    public ResponseEntity<SuccessDto>removeGroup(@RequestParam Long groupId,Authentication authentication)
    {

        groupService.removeGroup(groupId,Helper.getKeycloakUser(authentication));
        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }


    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR','STUDENT')")
    @GetMapping()
    @SneakyThrows
    public ResponseEntity<GetGroupDto>getGroup(@RequestParam Long groupId)
    {
        return new ResponseEntity<>(groupService.getGroup(groupId), HttpStatus.OK);
    }


    @PreAuthorize("hasAnyRole('PROFESSOR')")
    @PutMapping()
    @SneakyThrows
    public  ResponseEntity<SuccessDto> putGroup(@RequestParam Long groupId,@RequestBody RegisterGroupDto registerGroupDto,Authentication authentication)
    {
        groupService.putGroup(groupId,registerGroupDto,Helper.getKeycloakUser(authentication));
        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR','STUDENT')")
    @GetMapping("/showGroups")
    public ResponseEntity<List<GetGroupDto>> getAllGroups() {

        return new ResponseEntity<>(groupService.getAllGroups(), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR','STUDENT')")
    @GetMapping("/showGroupsBySubject")
    public ResponseEntity<List<GetGroupDto>> getAllGroupsBySubject(@RequestParam String subject) {

        return new ResponseEntity<>(groupService.getAllGroupsBySubject(subject), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR','STUDENT')")
    @GetMapping("/showGroupsByProfessorName")
    public ResponseEntity<List<GetGroupDto>> getAllGroupsByProfessorName(@RequestParam String professorName) {

        return new ResponseEntity<>(groupService.getAllGroupsByProfessorName(professorName), HttpStatus.OK);
    }

}
