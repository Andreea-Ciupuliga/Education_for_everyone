package com.example.educationforeveryone.service;

import com.example.educationforeveryone.models.Homework;
import com.example.educationforeveryone.models.Student;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendRegisterMail(String firstName, String lastName, String username, String email) {
        String body = "Hello " + firstName + " " + lastName + " you registered with your username: " + username;
        sendEmail(email, body, "Register");
    }

    public void sendHomeworkScoreEmail(Long score, Student student, Homework homework) {
        String body = "Hello " + student.getFirstName() + " " + student.getLastName() + " for homework: " + homework.getTask() + " you got the score: " + score;
        sendEmail(student.getEmail(), body, "Homework score");
    }

    public void sendNewHomeworkEmail(Student student, Homework homework) {
        String body = "Hello " + student.getFirstName() + " " + student.getLastName() + " you have a new homework: " + homework.getTask();
        sendEmail(student.getEmail(), body, "New homework");
    }

    public void sendEmail(String to, String body, String topic) {
        log.info("Sending email ...");
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("tutoriale.modelare.3d.tw@gmail.com");
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(topic);
        simpleMailMessage.setText(body);
        javaMailSender.send(simpleMailMessage);
        log.info("Successfully send mail");
    }
}
