package com.example.educationforeveryone.service;

import com.example.educationforeveryone.dtos.GetGroupDto;
import com.example.educationforeveryone.dtos.RegisterGroupDto;
import com.example.educationforeveryone.exceptions.alreadyExistException.GroupAlreadyExistException;
import com.example.educationforeveryone.exceptions.notFoundException.GroupNotFoundException;
import com.example.educationforeveryone.exceptions.notFoundException.UserNotFoundException;
import com.example.educationforeveryone.models.Group;
import com.example.educationforeveryone.models.Professor;
import com.example.educationforeveryone.repository.GroupRepository;
import com.example.educationforeveryone.repository.ProfessorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final ProfessorRepository professorRepository;

    public GroupService(GroupRepository groupRepository, ProfessorRepository professorRepository) {
        this.groupRepository = groupRepository;
        this.professorRepository = professorRepository;
    }

    public void registerGroup(RegisterGroupDto registerGroupDto, String username) {
        if (groupRepository.findByGroupName(registerGroupDto.getGroupName()).isPresent()) {
            throw new GroupAlreadyExistException("Group already exist!");
        }
        Professor professor = findProfessorByUsernameOrThrowException(username);
        Group savedGroup = groupRepository.save(buildGroup(registerGroupDto, professor));
        log.info("Successfully saved group with id: {}", savedGroup.getId());
    }

    public void removeGroup(Long groupId, String username) {
        Group group = findGroupByIdOrThrowException(groupId);
        if (username.equals("admin") || isProfessorInGroup(username, groupId)) {
            groupRepository.delete(group);
            log.info("Successfully removed group with id: {}", groupId);
        } else {
            throw new UserNotFoundException("Professor not in this group. You cannot delete a group you are not part of!");
        }
    }

    public GetGroupDto getGroupById(Long groupId) {
        Group group = findGroupByIdOrThrowException(groupId);
        return buildGetGroupDto(group);
    }

    public void updateGroup(Long groupId, RegisterGroupDto newRegisterGroupDto, String username) {
        Group group = findGroupByIdOrThrowException(groupId);
        Professor professor = findProfessorByUsernameOrThrowException(username);
        if (groupRepository.findByProfessorIdAndGroupId(professor.getId(), groupId).isEmpty()) {
            throw new UserNotFoundException("Professor not in this group. You cannot edit a group you are not part of!");
        }
        setFieldsIfNotNull(newRegisterGroupDto, group);
        groupRepository.save(group);
        log.info("Successfully updated group with id: {}", groupId);
    }

    public List<GetGroupDto> getAllGroups() {
        List<GetGroupDto> allGroups = groupRepository.findAllGroups();
        if (allGroups.isEmpty()) {
            throw new GroupNotFoundException("There are no groups to display");
        }
        return allGroups;
    }

    public List<GetGroupDto> getAllGroupsBySubject(String subject) {
        List<GetGroupDto> groupsBySubject = groupRepository.findAllBySubjectContains(subject);
        if (groupsBySubject.isEmpty()) {
            throw new GroupNotFoundException("Subject Not Found");
        }
        return groupsBySubject;
    }

    public List<GetGroupDto> getAllGroupsByProfessorName(String professorName) {
        List<GetGroupDto> groupsByProfessorName = groupRepository.findAllByProfessorNameContains(professorName);
        if (groupsByProfessorName.isEmpty()) {
            throw new UserNotFoundException("Professor Not Found");
        }
        return groupsByProfessorName;
    }

    private boolean isProfessorInGroup(String username, Long groupId) {
        Professor professor = findProfessorByUsernameOrThrowException(username);
        return groupRepository.findByProfessorIdAndGroupId(professor.getId(), groupId).isPresent();
    }

    private Group findGroupByIdOrThrowException(Long groupId) {
        return groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException("Group not found"));
    }

    private Professor findProfessorByUsernameOrThrowException(String username) {
        return professorRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("professor not found"));
    }

    private void setFieldsIfNotNull(RegisterGroupDto newRegisterGroupDto, Group group) {
        if (newRegisterGroupDto.getGroupName() != null) {
            group.setGroupName(newRegisterGroupDto.getGroupName());
        }
        if (newRegisterGroupDto.getSubject() != null) {
            group.setSubject(newRegisterGroupDto.getSubject());
        }
        if (newRegisterGroupDto.getYearOfStudy() != null) {
            group.setYearOfStudy(newRegisterGroupDto.getYearOfStudy());
        }
        if (newRegisterGroupDto.getAvailableSeats() != null) {
            group.setAvailableSeats(newRegisterGroupDto.getAvailableSeats());
        }
        if (newRegisterGroupDto.getTotalSeats() != null) {
            group.setTotalSeats(newRegisterGroupDto.getTotalSeats());
        }
    }

    private Group buildGroup(RegisterGroupDto registerGroupDto, Professor professor) {
        return Group.builder()
                .professor(Professor.builder().id(professor.getId()).build())
                .groupName(registerGroupDto.getGroupName())
                .subject(registerGroupDto.getSubject())
                .yearOfStudy(registerGroupDto.getYearOfStudy())
                .availableSeats(registerGroupDto.getTotalSeats()) // At the beginning, the available seats should be the same as the total number of seats because if the group has just been created, no one has occupied any yet
                .totalSeats(registerGroupDto.getTotalSeats()).build();
    }

    private GetGroupDto buildGetGroupDto(Group group) {
        return GetGroupDto.builder()
                .groupName(group.getGroupName())
                .subject(group.getSubject())
                .yearOfStudy(group.getYearOfStudy())
                .availableSeats(group.getAvailableSeats()).build();
    }
}
