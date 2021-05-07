package com.example.Education_for_everyone.controller;

import com.example.Education_for_everyone.dtos.GetGroupDto;
import com.example.Education_for_everyone.dtos.RegisterGroupDto;
import com.example.Education_for_everyone.models.Group;
import com.example.Education_for_everyone.service.GroupService;
import com.example.Education_for_everyone.utils.SuccessDto;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/register")
    @SneakyThrows
    public ResponseEntity<SuccessDto> registerGroup(@RequestBody RegisterGroupDto registerGroupDto)
    {
        groupService.registerGroup(registerGroupDto);

        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }

    @DeleteMapping()
    @SneakyThrows
    public ResponseEntity<SuccessDto>removeGroup(@RequestParam Long groupId)
    {
        groupService.removeGroup(groupId);

        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }


    @GetMapping()
    @SneakyThrows
    public ResponseEntity<GetGroupDto>getGroup(@RequestParam Long groupId)
    {

        return new ResponseEntity<>(groupService.getGroup(groupId), HttpStatus.OK);
    }


    @PutMapping()
    @SneakyThrows
    public  ResponseEntity<SuccessDto> putGroup(@RequestParam Long groupId,@RequestBody RegisterGroupDto registerGroupDto)
    {
        groupService.putGroup(groupId,registerGroupDto);

        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }


    @GetMapping("/showGroupsBySubject")
    public List<GetGroupDto> getAllGroups(@RequestParam String subject) {

        return groupService.getAllGroupsBySubject(subject);
    }


}
