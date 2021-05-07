package com.example.Education_for_everyone.service;


import com.example.Education_for_everyone.dtos.GetGroupDto;
import com.example.Education_for_everyone.dtos.RegisterGroupDto;
import com.example.Education_for_everyone.exceptions.BookNotFoundException;
import com.example.Education_for_everyone.exceptions.GroupAlreadyExistException;
import com.example.Education_for_everyone.exceptions.GroupNotFoundException;
import com.example.Education_for_everyone.models.Group;
import com.example.Education_for_everyone.models.Professor;
import com.example.Education_for_everyone.repository.GroupRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GroupService {

    private GroupRepository groupRepository;

    @Autowired
    public GroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @SneakyThrows
    public void registerGroup(RegisterGroupDto registerGroupDto)
    {

        if(groupRepository.findByGroupName(registerGroupDto.getGroupName()).isPresent())
        {

            throw new GroupAlreadyExistException("Group Already Exist");

        }

        Group group = Group.builder()
                .professor(Professor.builder().id(registerGroupDto.getProfessorId()).build())
                .groupName(registerGroupDto.getGroupName())
                .subject(registerGroupDto.getSubject())
                .yearOfStudy(registerGroupDto.getYearOfStudy())
                .availableSeats(registerGroupDto.getTotalSeats()) // la inceput locurile libere ar trebui sa fie acelasi numar ca locurile totale doarece daca abia s-a creat grupul nu a ocupat nimeni nimic
                .totalSeats(registerGroupDto.getTotalSeats()).build();

        groupRepository.save(group);

    }


    @SneakyThrows
    public void removeGroup(Long groupId)
    {
        Group group = groupRepository.findById(groupId).orElseThrow(()->new GroupNotFoundException("group not found"));
        groupRepository.delete(group);
    }

    @SneakyThrows
    public GetGroupDto getGroup(Long groupId)
    {

        Group group = groupRepository.findById(groupId).orElseThrow(()->new GroupNotFoundException("group not found"));

        GetGroupDto getGrouptDto=  GetGroupDto.builder()
                .groupName(group.getGroupName())
                .subject(group.getSubject())
                .yearOfStudy(group.getYearOfStudy())
                .availableSeats(group.getAvailableSeats()).build();

        return getGrouptDto;
    }

    @SneakyThrows
    public void putGroup(Long groupId,RegisterGroupDto newRegisterGroupDto)
    {
        Group group = groupRepository.findById(groupId).orElseThrow(()->new GroupNotFoundException("group not found"));

        if(newRegisterGroupDto.getGroupName()!=null)
            group.setGroupName(newRegisterGroupDto.getGroupName());

        if(newRegisterGroupDto.getSubject()!=null)
            group.setSubject(newRegisterGroupDto.getSubject());

        if(newRegisterGroupDto.getYearOfStudy()!=null)
            group.setYearOfStudy(newRegisterGroupDto.getYearOfStudy());

        if(newRegisterGroupDto.getAvailableSeats()!=null)
            group.setAvailableSeats(newRegisterGroupDto.getAvailableSeats());

        if(newRegisterGroupDto.getTotalSeats()!=null)
            group.setTotalSeats(newRegisterGroupDto.getTotalSeats());


        groupRepository.save(group);


    }


    @SneakyThrows
    public List<GetGroupDto> getAllGroupsBySubject(String subject) {

        if(groupRepository.findAllBySubjectContains(subject).isEmpty())
        {
            throw new GroupNotFoundException("Subject Not Found");
        }
        return groupRepository.findAllBySubjectContains(subject);
    }



}
