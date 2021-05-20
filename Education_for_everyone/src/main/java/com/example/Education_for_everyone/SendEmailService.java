package com.example.Education_for_everyone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
public class SendEmailService {

    private JavaMailSender javaMailSender;

    @Autowired
    public SendEmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendEmail(String to, String body, String topic)
    {
        System.out.println("sending email");

        SimpleMailMessage simpleMailMessage=new SimpleMailMessage();

        simpleMailMessage.setFrom("tutoriale.modelare.3d.tw@gmail.com");
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(topic);
        simpleMailMessage.setText(body);

        javaMailSender.send(simpleMailMessage);
        System.out.println("email send");
    }
}
