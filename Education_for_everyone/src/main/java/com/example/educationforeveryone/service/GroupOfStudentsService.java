package com.example.educationforeveryone.service;

import com.example.educationforeveryone.dtos.GetStudentDto;
import com.example.educationforeveryone.exceptions.DoNotMatchException;
import com.example.educationforeveryone.exceptions.GroupOfStudentsException;
import com.example.educationforeveryone.exceptions.alreadyExistException.UserAlreadyExistException;
import com.example.educationforeveryone.exceptions.notFoundException.GroupNotFoundException;
import com.example.educationforeveryone.exceptions.notFoundException.UserNotFoundException;
import com.example.educationforeveryone.models.Group;
import com.example.educationforeveryone.models.GroupOfStudents;
import com.example.educationforeveryone.models.Professor;
import com.example.educationforeveryone.models.Student;
import com.example.educationforeveryone.repository.GroupOfStudentsRepository;
import com.example.educationforeveryone.repository.GroupRepository;
import com.example.educationforeveryone.repository.ProfessorRepository;
import com.example.educationforeveryone.repository.StudentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class GroupOfStudentsService {

    private final StudentRepository studentRepository;
    private final ProfessorRepository professorRepository;
    private final GroupRepository groupRepository;
    private final GroupOfStudentsRepository groupOfStudentsRepository;

    public GroupOfStudentsService(StudentRepository studentRepository,
                                  GroupRepository groupRepository,
                                  GroupOfStudentsRepository groupOfStudentsRepository,
                                  ProfessorRepository professorRepository) {
        this.studentRepository = studentRepository;
        this.groupRepository = groupRepository;
        this.groupOfStudentsRepository = groupOfStudentsRepository;
        this.professorRepository = professorRepository;
    }

    public void registerStudentInGroup(Long studentId, Long groupId, String username) {
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new UserNotFoundException("Student not found"));
        Group group = findGroupByIdOrThrowException(groupId);

        validateStudentNotInGroup(studentId, groupId);
        validateAvailableSeats(group);

        if (isAdmin(username) || isProfessorTeachingGroup(username, groupId)) {
            saveGroupOfStudentsAndLog(studentId, groupId, student, group);
        } else {
            throw new UserNotFoundException("Professor not in this group. You cannot add a student to a group you are not part of");
        }
    }

    public void removeStudentFromGroup(Long studentId, Long groupId, String username) {
        if (studentRepository.findById(studentId).isEmpty()) {
            throw new UserNotFoundException("student not found");
        }
        Group group = findGroupByIdOrThrowException(groupId);
        GroupOfStudents groupOfStudents = groupOfStudentsRepository.findByStudentIdAndGroupId(studentId, groupId).orElseThrow(() -> new DoNotMatchException("Group snd student do not match"));
        if (isAdmin(username) || isProfessorTeachingGroup(username, groupId)) {
            deleteGroupOfStudentsAndLog(studentId, groupId, groupOfStudents, group);
        } else {
            throw new UserNotFoundException("Professor not in this group. You cannot delete a student from a group you are not part of");
        }
    }

    public List<GetStudentDto> getAllStudentsByGroupId(Long groupId) {
        return groupOfStudentsRepository.findAllStudentsByGroupId(groupId);
    }

    private Group findGroupByIdOrThrowException(Long groupId) {
        return groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException("Group not found"));
    }

    private boolean isAdmin(String username) {
        return "admin".equals(username);
    }

    private boolean isProfessorTeachingGroup(String username, Long groupId) {
        Professor professor = professorRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Professor not found"));

        return groupRepository.findByProfessorIdAndGroupId(professor.getId(), groupId).isPresent();
    }

    private void deleteGroupOfStudentsAndLog(Long studentId, Long groupId, GroupOfStudents groupOfStudents, Group group) {
        groupOfStudentsRepository.delete(groupOfStudents);
        log.info("Successfully deleted group of students with id: {} for student with id: {} into group with id: {} and name: {}", groupOfStudents.getId(), studentId, groupId, group.getGroupName());
        group.setAvailableSeats(group.getAvailableSeats() + 1);
        groupRepository.save(group);
    }

    private void saveGroupOfStudentsAndLog(Long studentId, Long groupId, Student student, Group group) {
        GroupOfStudents savedGroupOfStudents = groupOfStudentsRepository.save(buildGroupOfStudents(student, group));
        log.info("Successfully saved group of students with id: {} for student with id: {} into group with id: {} and name: {}", savedGroupOfStudents.getId(), studentId, groupId, group.getGroupName());
        group.setAvailableSeats(group.getAvailableSeats() - 1);
        groupRepository.save(group);
    }

    private void validateAvailableSeats(Group group) {
        if (group.getAvailableSeats() <= 0) {
            throw new GroupOfStudentsException("Not Enough Available Seats");
        }
    }

    private void validateStudentNotInGroup(Long studentId, Long groupId) {
        if (groupOfStudentsRepository.findByStudentIdAndGroupId(studentId, groupId).isPresent()) {
            throw new UserAlreadyExistException("Student already in this group");
        }
    }

    private GroupOfStudents buildGroupOfStudents(Student student, Group group) {
        return GroupOfStudents.builder()
                .student(Student.builder().id(student.getId()).build())
                .group(Group.builder().id(group.getId()).build())
                .groupName(group.getGroupName())
                .studentFirstName(student.getFirstName())
                .studentLastName(student.getLastName()).build();
    }

}
