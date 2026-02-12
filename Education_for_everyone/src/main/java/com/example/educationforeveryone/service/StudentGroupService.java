package com.example.educationforeveryone.service;

import com.example.educationforeveryone.dtos.GetStudentDto;
import com.example.educationforeveryone.exceptions.DoNotMatchException;
import com.example.educationforeveryone.exceptions.GroupOfStudentsException;
import com.example.educationforeveryone.exceptions.alreadyExistException.UserAlreadyExistException;
import com.example.educationforeveryone.exceptions.notFoundException.UserNotFoundException;
import com.example.educationforeveryone.models.Group;
import com.example.educationforeveryone.models.Professor;
import com.example.educationforeveryone.models.Student;
import com.example.educationforeveryone.models.StudentGroup;
import com.example.educationforeveryone.repository.GroupRepository;
import com.example.educationforeveryone.repository.StudentGroupRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class StudentGroupService {

    private final StudentService studentService;
    private final ProfessorService professorService;
    private final GroupRepository groupRepository;
    private final GroupService groupService;
    private final StudentGroupRepository studentGroupRepository;

    public StudentGroupService(StudentService studentService,
                               ProfessorService professorService,
                               GroupRepository groupRepository,
                               StudentGroupRepository studentGroupRepository,
                               GroupService groupService) {
        this.studentService = studentService;
        this.professorService = professorService;
        this.groupRepository = groupRepository;
        this.studentGroupRepository = studentGroupRepository;
        this.groupService = groupService;
    }

    public void registerStudentInGroup(Long studentId, Long groupId, String username) {
        Student student = studentService.getStudentByIdOrThrowException(studentId);
        Group group = groupService.findGroupByIdOrThrowException(groupId);

        validateStudentNotInGroup(studentId, groupId);
        validateAvailableSeats(group);

        if (isAdmin(username) || isProfessorTeachingGroup(username, groupId)) {
            saveGroupOfStudentsAndLog(studentId, groupId, student, group);
        } else {
            throw new UserNotFoundException("Professor not in this group. You cannot add a student to a group you are not part of");
        }
    }

    public void removeStudentFromGroup(Long studentId, Long groupId, String username) {
        studentService.checkStudentExistsOrThrowException(studentId);
        Group group = groupService.findGroupByIdOrThrowException(groupId);
        StudentGroup studentGroup = studentGroupRepository.findByStudentIdAndGroupId(studentId, groupId).orElseThrow(() -> new DoNotMatchException("Group and student do not match"));
        if (isAdmin(username) || isProfessorTeachingGroup(username, groupId)) {
            deleteGroupOfStudentsAndLog(studentId, groupId, studentGroup, group);
        } else {
            throw new UserNotFoundException("Professor not in this group. You cannot delete a student from a group you are not part of");
        }
    }

    public List<GetStudentDto> getAllStudentsByGroupId(Long groupId) {
        return studentGroupRepository.findAllStudentsByGroupId(groupId);
    }

    private boolean isAdmin(String username) {
        return "admin".equals(username);
    }

    private boolean isProfessorTeachingGroup(String username, Long groupId) {
        Professor professor = professorService.getProfessorByUsernameOrThrowException(username);
        return groupRepository.findByProfessorIdAndGroupId(professor.getId(), groupId).isPresent();
    }

    private void deleteGroupOfStudentsAndLog(Long studentId, Long groupId, StudentGroup studentGroup, Group group) {
        studentGroupRepository.delete(studentGroup);
        log.info("Successfully deleted group of students with id: {} for student with id: {} into group with id: {} and name: {}", studentGroup.getId(), studentId, groupId, group.getGroupName());
        group.setAvailableSeats(group.getAvailableSeats() + 1);
        groupRepository.save(group);
    }

    private void saveGroupOfStudentsAndLog(Long studentId, Long groupId, Student student, Group group) {
        StudentGroup savedStudentGroup = studentGroupRepository.save(buildGroupOfStudents(student, group));
        log.info("Successfully saved group of students with id: {} for student with id: {} into group with id: {} and name: {}", savedStudentGroup.getId(), studentId, groupId, group.getGroupName());
        group.setAvailableSeats(group.getAvailableSeats() - 1);
        groupRepository.save(group);
    }

    private void validateAvailableSeats(Group group) {
        if (group.getAvailableSeats() <= 0) {
            throw new GroupOfStudentsException("Not Enough Available Seats");
        }
    }

    private void validateStudentNotInGroup(Long studentId, Long groupId) {
        if (studentGroupRepository.findByStudentIdAndGroupId(studentId, groupId).isPresent()) {
            throw new UserAlreadyExistException("Student already in this group");
        }
    }

    private StudentGroup buildGroupOfStudents(Student student, Group group) {
        return StudentGroup.builder()
                .student(Student.builder().id(student.getId()).build())
                .group(Group.builder().id(group.getId()).build())
                .groupName(group.getGroupName())
                .studentFirstName(student.getFirstName())
                .studentLastName(student.getLastName()).build();
    }

}
