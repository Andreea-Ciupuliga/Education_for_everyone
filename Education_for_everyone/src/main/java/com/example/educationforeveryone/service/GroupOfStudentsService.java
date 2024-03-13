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
import org.springframework.stereotype.Service;

import java.util.List;

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

    public void addStudentInGroup(Long studentId, Long groupId, String username) {

        //am nevoie de un obiect de tip student pentru FirstName si LastName
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new UserNotFoundException("student not found"));

        Group group = groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException("group not found"));

        if (groupOfStudentsRepository.findByStudentIdAndGroupId(studentId, groupId).isPresent()) {
            throw new UserAlreadyExistException("student already in this group");
        }

        //verificam daca mai sunt locuri disponibile
        if (group.getAvailableSeats() <= 0) {
            throw new GroupOfStudentsException("Not Enough Available Seats");
        }


        //daca este admin poate adauga studentul in grup
        if (username.equals("admin")) {
            GroupOfStudents groupOfStudents = buildGroupOfStudents(student, group);

            groupOfStudentsRepository.save(groupOfStudents);

            group.setAvailableSeats(group.getAvailableSeats() - 1);
            groupRepository.save(group);
        } else //daca nu e admin inseamna ca este un profesor
        {
            //Cautam profesorul in baza de date dupa username-ul din token ca sa vedem ca exista profesorul respectiv
            Professor professor = professorRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("professor not found"));

            //daca profesorul preda la grupul in care vrea sa adauge studentul
            if (groupRepository.findByProfessorIdAndGroupId(professor.getId(), groupId).isPresent()) {
                GroupOfStudents groupOfStudents = buildGroupOfStudents(student, group);

                groupOfStudentsRepository.save(groupOfStudents);

                group.setAvailableSeats(group.getAvailableSeats() - 1);
                groupRepository.save(group);
            } else if (groupRepository.findByProfessorIdAndGroupId(professor.getId(), groupId).isEmpty()) {
                throw new UserNotFoundException("Professor not in this group. You cannot add a student in a group you are not part of");
            }
        }
    }


    public void removeStudentFromGroup(Long studentId, Long groupId, String username) {

        if (studentRepository.findById(studentId).isEmpty()) {
            throw new UserNotFoundException("student not found");
        }

        Group group = groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException("group not found"));

        //vedem daca studentul este in grupul respectiv
        GroupOfStudents groupOfStudents = groupOfStudentsRepository.findByStudentIdAndGroupId(studentId, groupId).orElseThrow(() -> new DoNotMatchException("Group And Student Do Not Match"));

        //daca e admin poate sterge studentul din grup
        if (username.equals("admin")) {
            groupOfStudentsRepository.delete(groupOfStudents);
            group.setAvailableSeats(group.getAvailableSeats() + 1);
            groupRepository.save(group);
        } else //inseamna ca nu e admin deci poate fi profesor
        {
            //Cautam profesorul in baza de date dupa username-ul din token ca sa vedem ca exista profesorul respectiv
            Professor professor = professorRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("professor not found"));

            //daca profesorul preda la grupul din care vrem sa stergem studentul
            if (groupRepository.findByProfessorIdAndGroupId(professor.getId(), groupId).isPresent()) {
                groupOfStudentsRepository.delete(groupOfStudents);
                group.setAvailableSeats(group.getAvailableSeats() + 1);
                groupRepository.save(group);
            } else if (groupRepository.findByProfessorIdAndGroupId(professor.getId(), groupId).isEmpty()) {
                throw new UserNotFoundException("Professor not in this group. You delete a student from a group you are not part of");
            }
        }
    }

    public List<GetStudentDto> getAllStudentsByGroupId(Long groupId) {
        return groupOfStudentsRepository.findAllStudentsByGroupId(groupId);
    }

    private static GroupOfStudents buildGroupOfStudents(Student student, Group group) {
        return GroupOfStudents.builder()
                .student(Student.builder().id(student.getId()).build())
                .group(Group.builder().id(group.getId()).build())
                .groupName(group.getGroupName())
                .studentFirstName(student.getFirstName())
                .studentLastName(student.getLastName()).build();
    }

}
