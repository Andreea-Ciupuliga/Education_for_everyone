package com.example.Education_for_everyone.service;

import com.example.Education_for_everyone.SendEmailService;
import com.example.Education_for_everyone.exceptions.*;
import com.example.Education_for_everyone.models.Grade;
import com.example.Education_for_everyone.models.Homework;
import com.example.Education_for_everyone.models.Professor;
import com.example.Education_for_everyone.models.Student;
import com.example.Education_for_everyone.repository.*;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class GradeService {
    private StudentRepository studentRepository;
    private GradeRepository gradeRepository;
    private HomeworkRepository homeworkRepository;
    private SendEmailService sendEmailService;
    private ProfessorRepository professorRepository;
    private GroupOfStudentsRepository groupOfStudentsRepository;
    private GroupRepository groupRepository;

    @Autowired
    public GradeService(StudentRepository studentRepository, GradeRepository gradeRepository, HomeworkRepository homeworkRepository,SendEmailService sendEmailService,ProfessorRepository professorRepository,GroupOfStudentsRepository groupOfStudentsRepository,GroupRepository groupRepository) {
        this.studentRepository = studentRepository;
        this.gradeRepository = gradeRepository;
        this.homeworkRepository = homeworkRepository;
        this.sendEmailService = sendEmailService;
        this.professorRepository = professorRepository;
        this.groupOfStudentsRepository=groupOfStudentsRepository;
        this.groupRepository=groupRepository;
    }

    @SneakyThrows
    public void assignHomeworkToStudent(Long studentId,Long homeworkId,String username)
    {
        //ca sa verificam daca profesorul este prezent intr-un grup
        Boolean isPresent=false;

        Student student = studentRepository.findById(studentId).orElseThrow(()->new UserNotFoundException("student not found"));

        //vedem daca exista profesorul(dupa username-ul din token) care vrea sa asigneze tema studentului
        Professor professor = professorRepository.findByUsername(username).orElseThrow(()->new UserNotFoundException("professor not found"));

        Homework homework=homeworkRepository.findById(homeworkId).orElseThrow(()->new HomeworkNotFoundException("homework not found"));


        //vedem lista de grupuri din care face parte studentul (id urile grupurilor mai exact)
        ArrayList groupIds=groupOfStudentsRepository.findGroupIdByStudentId(studentId);


        //vedem daca profesorul preda la vreun grup in care este inscris studentul
        for(int i=0;i<groupIds.size();i++)
            if (groupRepository.findByprofessorIdAndgroupId(professor.getId(),(Long)groupIds.get(i)).isPresent())
                isPresent=true;


        //daca profesorul nu preda la niciun grup din care face parte studentul inseamna ca nu ii poate da teme
        if(isPresent==false)
            throw new UserNotFoundException("Professor not in this group. You cannot assign a homework to a student who is not part of your group");

        //daca gasim perechea studentId si homeworkId inseamna ca studentul are deja tema asta
        if(gradeRepository.findBystudentIdAndhomeworkId(studentId,homeworkId).isPresent())
            throw new UserAlreadyExistException("this homework has already been assigned to the student");

        Grade grade=Grade.builder()
                .student(Student.builder().id(student.getId()).build())
                .homework(Homework.builder().id(homework.getId()).build())
                .studentFirstName(student.getFirstName())
                .studentLastName(student.getLastName())
                .build();

        gradeRepository.save(grade);

        //ii trimitem mail studentului cu noua tema
        String body="Hello "+student.getFirstName()+" "+student.getLastName()+" you have a new homework: "+homework.getTask();
        sendEmailService.sendEmail(student.getEmail(),body,"New homework");

    }

    @SneakyThrows
    public void assignScoreToStudent(Long studentId,Long homeworkId,Long score,String username)
    {
        //ca sa verificam daca profeosrul este prezent intr-un grup
        Boolean isPresent=false;

        Student student = studentRepository.findById(studentId).orElseThrow(()->new UserNotFoundException("student not found"));

        //vedem daca exista profesorul(dupa username-ul din token) care vrea sa asigneze nota studentului
        Professor professor = professorRepository.findByUsername(username).orElseThrow(()->new UserNotFoundException("professor not found"));

        //vedem lista de grupuri din care face parte studentul (id urile grupurilor mai exact)
        ArrayList groupIds=groupOfStudentsRepository.findGroupIdByStudentId(studentId);

        //vedem daca profesorul preda la vreun grup in care este inscris studentul
        for(int i=0;i<groupIds.size();i++)
            if (groupRepository.findByprofessorIdAndgroupId(professor.getId(), (Long) groupIds.get(i)).isPresent())
                isPresent=true;

        //daca profesorul nu preda la niciun grup din care face parte studentul inseamna ca nu ii poate da nota
        if(isPresent==false)
            throw new UserNotFoundException("Professor not in this group. You cannot assign a score to a student who is not part of your group");

        Homework homework=homeworkRepository.findById(homeworkId).orElseThrow(()->new HomeworkNotFoundException("homework not found"));


        Grade grade=gradeRepository.findBystudentIdAndhomeworkId(studentId,homeworkId ).orElseThrow(()->new HomeworkAndStudentDoNotMatchException("Homework And Student Do Not Match"));

        //in cazul in care greseste si ii da o nota mai mare decat nr maxim de puncte care pot fi obtinute pt tema asta
        if(score>homework.getPoints())
            throw new ScoreAssignedIncorrectlyException("the score exceeds the points assigned to this homework");

        grade.setScore(score);

        gradeRepository.save(grade);

        //ii trimitem mail studentului cu nota obtinuta
        String body="Hello "+student.getFirstName()+" "+student.getLastName()+" for homework: "+homework.getTask()+" you got the score: "+score;
        sendEmailService.sendEmail(student.getEmail(),body,"Homework score");

    }

    @SneakyThrows
    public void removeHomeworkFromStudent(Long studentId,Long homeworkId,String username)
    {
        //ca sa verificam daca profeosrul este prezent intr-un grup
        Boolean isPresent=false;

        Professor professor = professorRepository.findByUsername(username).orElseThrow(()->new UserNotFoundException("professor not found"));

        if(studentRepository.findById(studentId).isEmpty())
            throw new UserNotFoundException("student not found");

        //vedem lista de grupuri din care face parte studentul (id urile grupurilor mai exact)
        ArrayList groupIds=groupOfStudentsRepository.findGroupIdByStudentId(studentId);

        //vedem daca profesorul preda la vreun grup in care este inscris studentul
        for(int i=0;i<groupIds.size();i++)
            if (groupRepository.findByprofessorIdAndgroupId(professor.getId(), (Long) groupIds.get(i)).isPresent())
                isPresent=true;


        if(isPresent==false)
            throw new UserNotFoundException("Professor not in this group. You cannot remove homework from a student who is not part of your group");


        if(homeworkRepository.findById(homeworkId).isEmpty())
            throw new HomeworkNotFoundException("homework not found");

        //verificam daca se potriveste perechea studentId si homeworkId
        Grade grade=gradeRepository.findBystudentIdAndhomeworkId(studentId,homeworkId).orElseThrow(()->new HomeworkAndStudentDoNotMatchException("Homework And Student Do Not Match"));

        gradeRepository.delete(grade);
    }

    @SneakyThrows
    public List<String> showGrades(String username,Long studentId)
    {
        Student student = studentRepository.findById(studentId).orElseThrow(()->new UserNotFoundException("student not found"));

        //adminul si profesorii pot vedea notele tuturor studentilor. studentii nu isi pot vedea notele unii altora. tu ca student poti sa vezi doar notele tale

        //daca nu esti admin, nici profesor si nici acelasi student care vrea sa isi vada notele atunci nu poti vedea notele
        if(!username.equals("admin") && professorRepository.findByUsername(username).isEmpty() && !student.getUsername().equals(username))
            throw new UserNotFoundException("you can't see this student's grades");


        List<String>grades = gradeRepository.showScoreByStudentid(studentId);
        return grades;

    }



    @SneakyThrows
    public Double averageStudentGrades(Long studentId,String username)
    {
        //verificam daca exista studentul pt care vrem sa calculam media
        Student student = studentRepository.findById(studentId).orElseThrow(()->new UserNotFoundException("student not found"));

        //adminul si profesorii pot vedea mediile tuturor studentilor. studentii nu isi pot vedea mediile unii altora. tu ca student poti sa vezi doar media ta

        //daca nu esti admin, nici profesor si nici acelasi student care vrea sa isi vada media atunci nu poti vedea media
        if(!username.equals("admin") && professorRepository.findByUsername(username).isEmpty() && !student.getUsername().equals(username))
            throw new UserNotFoundException("you can't see this student's grades");

        Double average=0.0;

        //lista cu toate notele lui din baza de date
        List<Long> grades=gradeRepository.findScoreBystudentId(studentId);

        for(int i=0; i < grades.size(); i++) {
            Long element = grades.get(i);
            average=average+element;
        }

        average=average/grades.size();

        return average;
    }



}
