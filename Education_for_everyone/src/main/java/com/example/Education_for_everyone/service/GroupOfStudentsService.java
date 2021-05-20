package com.example.Education_for_everyone.service;


import com.example.Education_for_everyone.dtos.GetGroupDto;
import com.example.Education_for_everyone.dtos.GetStudentDto;
import com.example.Education_for_everyone.exceptions.*;
import com.example.Education_for_everyone.models.Group;
import com.example.Education_for_everyone.models.Professor;
import com.example.Education_for_everyone.models.Student;
import com.example.Education_for_everyone.models.GroupOfStudents;
import com.example.Education_for_everyone.repository.GroupOfStudentsRepository;
import com.example.Education_for_everyone.repository.GroupRepository;
import com.example.Education_for_everyone.repository.ProfessorRepository;
import com.example.Education_for_everyone.repository.StudentRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupOfStudentsService {

    private StudentRepository studentRepository;
    private ProfessorRepository professorRepository;
    private GroupRepository groupRepository;
    private GroupOfStudentsRepository groupOfStudentsRepository;


    @Autowired
    public GroupOfStudentsService(StudentRepository studentRepository, GroupRepository groupRepository, GroupOfStudentsRepository groupOfStudentsRepository,ProfessorRepository professorRepository)
    {
        this.studentRepository = studentRepository;
        this.groupRepository = groupRepository;
        this.groupOfStudentsRepository = groupOfStudentsRepository;
        this.professorRepository= professorRepository;
    }

    @SneakyThrows
    public void addStudentInGroup(Long studentId,Long groupId,String username)
    {

        //am nevoie de un obiect de tip student pentru FirstName si LastName
        Student student = studentRepository.findById(studentId).orElseThrow(()->new UserNotFoundException("student not found"));

        Group group = groupRepository.findById(groupId).orElseThrow(()->new GroupNotFoundException("group not found"));

        if(groupOfStudentsRepository.findBystudentIdAndgroupId(studentId,groupId).isPresent())
        {
            throw new UserAlreadyExistException("student already in this group");
        }

        //verificam daca mai sunt locuri disponibile
        if(group.getAvailableSeats()<=0)
        {
            throw new NotEnoughAvailableSeatsException("Not Enough Available Seats");
        }

        //daca este admin poate adauga studentul in grup
        if(username.equals("admin"))
        {
            GroupOfStudents groupOfStudents=GroupOfStudents.builder()
                    .student(Student.builder().id(student.getId()).build())
                    .group(Group.builder().id(group.getId()).build())
                    .groupName(group.getGroupName())
                    .studentFirstName(student.getFirstName())
                    .studentLastName(student.getLastName()).build();

            groupOfStudentsRepository.save(groupOfStudents);

            group.setAvailableSeats(group.getAvailableSeats()-1);
            groupRepository.save(group);
        }

        else //daca nu e admin inseamna ca este un profesor
        {
            //Cautam profesorul in baza de date dupa username-ul din token ca sa vedem ca exista profesorul respectiv
            Professor professor = professorRepository.findByUsername(username).orElseThrow(()->new UserNotFoundException("professor not found"));

            //daca profesorul preda la grupul in care vrea sa adauge studentul
            if (groupRepository.findByprofessorIdAndgroupId(professor.getId(),groupId).isPresent())
            {
                GroupOfStudents groupOfStudents=GroupOfStudents.builder()
                        .student(Student.builder().id(student.getId()).build())
                        .group(Group.builder().id(group.getId()).build())
                        .groupName(group.getGroupName())
                        .studentFirstName(student.getFirstName())
                        .studentLastName(student.getLastName()).build();

                groupOfStudentsRepository.save(groupOfStudents);

                group.setAvailableSeats(group.getAvailableSeats()-1);
                groupRepository.save(group);
            }

            else if (groupRepository.findByprofessorIdAndgroupId(professor.getId(), groupId).isEmpty()) {
                throw new UserNotFoundException("Professor not in this group. You cannot add a student in a group you are not part of");
            }
        }
    }

    @SneakyThrows
    public void removeStudentFromGroup(Long studentId,Long groupId,String username) {

        if(studentRepository.findById(studentId).isEmpty())
        {
            throw new UserNotFoundException("student not found");
        }

        Group group = groupRepository.findById(groupId).orElseThrow(()->new GroupNotFoundException("group not found"));

        //vedem daca studentul este in grupul respectiv
        GroupOfStudents groupOfStudents=groupOfStudentsRepository.findBystudentIdAndgroupId(studentId,groupId).orElseThrow(()->new GroupAndStudentDoNotMatchException("Group And Student Do Not Match"));

        //daca e admin poate sterge studentul din grup
        if(username.equals("admin"))
        {
            groupOfStudentsRepository.delete(groupOfStudents);
            group.setAvailableSeats(group.getAvailableSeats() + 1);
            groupRepository.save(group);
        }

        else //inseamna ca nu e admin deci poate fi profesor
        {
            //Cautam profesorul in baza de date dupa username-ul din token ca sa vedem ca exista profesorul respectiv
            Professor professor = professorRepository.findByUsername(username).orElseThrow(()->new UserNotFoundException("professor not found"));

            //daca profesorul preda la grupul din care vrem sa stergem studentul
            if (groupRepository.findByprofessorIdAndgroupId(professor.getId(), groupId).isPresent())
            {
                groupOfStudentsRepository.delete(groupOfStudents);
                group.setAvailableSeats(group.getAvailableSeats() + 1);
                groupRepository.save(group);
            }

            else if (groupRepository.findByprofessorIdAndgroupId(professor.getId(), groupId).isEmpty()) {
                throw new UserNotFoundException("Professor not in this group. You delete a student from a group you are not part of");
            }
        }
    }

    //afisam toti studentii dintr-un grup
    @SneakyThrows
    public List<GetStudentDto> getAllStudentsByGroupId(Long groupId) {
        return groupOfStudentsRepository.findAllStudentsByGroupId(groupId);
    }


}
