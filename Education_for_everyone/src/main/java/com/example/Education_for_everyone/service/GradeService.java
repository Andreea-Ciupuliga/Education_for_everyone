package com.example.Education_for_everyone.service;

import com.example.Education_for_everyone.SendEmailService;
import com.example.Education_for_everyone.exceptions.*;
import com.example.Education_for_everyone.models.Grade;
import com.example.Education_for_everyone.models.Homework;
import com.example.Education_for_everyone.models.Student;
import com.example.Education_for_everyone.repository.GradeRepository;
import com.example.Education_for_everyone.repository.HomeworkRepository;
import com.example.Education_for_everyone.repository.StudentRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GradeService {
    private StudentRepository studentRepository;
    private GradeRepository gradeRepository;
    private HomeworkRepository homeworkRepository;
    private SendEmailService sendEmailService;

    @Autowired
    public GradeService(StudentRepository studentRepository, GradeRepository gradeRepository, HomeworkRepository homeworkRepository,SendEmailService sendEmailService) {
        this.studentRepository = studentRepository;
        this.gradeRepository = gradeRepository;
        this.homeworkRepository = homeworkRepository;
        this.sendEmailService = sendEmailService;
    }

    @SneakyThrows
    public void assignHomeworkToStudent(Long studentId,Long homeworkId)
    {
        Student student = studentRepository.findById(studentId).orElseThrow(()->new UserNotFoundException("student not found"));
        Homework homework=homeworkRepository.findById(homeworkId).orElseThrow(()->new HomeworkNotFoundException("homework not found"));

        if(gradeRepository.findBystudentIdAndhomeworkId(studentId,homeworkId).isPresent())
            throw new UserAlreadyExistException("this homework has already been assigned to the student");

        Grade grade=Grade.builder()
                .student(Student.builder().id(student.getId()).build())
                .homework(Homework.builder().id(homework.getId()).build())
                .studentFirstName(student.getFirstName())
                .studentLastName(student.getLastName())
                .build();

        gradeRepository.save(grade);

        String body="Hello "+student.getFirstName()+" "+student.getLastName()+" you have a new homework: "+homework.getTask();
        sendEmailService.sendEmail(student.getEmail(),body,"New homework");

    }

    @SneakyThrows
    public void assignScoreToStudent(Long studentId,Long homeworkId,Long score)
    {

        if(studentRepository.findById(studentId).isEmpty())
            throw new UserNotFoundException("student not found");

        Homework homework=homeworkRepository.findById(homeworkId).orElseThrow(()->new HomeworkNotFoundException("homework not found"));


        Grade grade=gradeRepository.findBystudentIdAndhomeworkId(studentId,homeworkId ).orElseThrow(()->new HomeworkAndStudentDoNotMatchException("Homework And Student Do Not Match"));

        if(score>homework.getPoints())
            throw new ScoreAssignedIncorrectlyException("the score exceeds the points assigned to this homework");

        grade.setScore(score);

        gradeRepository.save(grade);
    }

    @SneakyThrows
    public void removeHomeworkFromStudent(Long studentId,Long homeworkId)
    {
        if(studentRepository.findById(studentId).isEmpty())
            throw new UserNotFoundException("student not found");

        if(homeworkRepository.findById(homeworkId).isEmpty())
            throw new HomeworkNotFoundException("homework not found");

        Grade grade=gradeRepository.findBystudentIdAndhomeworkId(studentId,homeworkId ).orElseThrow(()->new HomeworkAndStudentDoNotMatchException("Homework And Student Do Not Match"));

        gradeRepository.delete(grade);
    }

}
