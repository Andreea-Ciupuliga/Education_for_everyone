package com.example.Education_for_everyone.service;

import com.example.Education_for_everyone.dtos.GetBookDto;
import com.example.Education_for_everyone.dtos.RegisterBookDto;
import com.example.Education_for_everyone.exceptions.BookNotFoundException;
import com.example.Education_for_everyone.exceptions.HomeworkNotFoundException;
import com.example.Education_for_everyone.models.Book;
import com.example.Education_for_everyone.models.Homework;
import com.example.Education_for_everyone.repository.HomeworkRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HomeworkService {

    private HomeworkRepository homeworkRepository;

    @Autowired
    public HomeworkService(HomeworkRepository homeworkRepository) {
        this.homeworkRepository = homeworkRepository;
    }

    @SneakyThrows
    public void registerHomework(Homework homework)
    {
        Homework newHomework=Homework.builder()
                .points(homework.getPoints())
                .deadline(homework.getDeadline())
                .task(homework.getTask())
                .build();

        homeworkRepository.save(newHomework);
    }


    @SneakyThrows
    public void removeHomework(Long homeworkId)
    {
       Homework homework = homeworkRepository.findById(homeworkId).orElseThrow(()->new HomeworkNotFoundException("Homework not found"));
        homeworkRepository.delete(homework);
    }


    @SneakyThrows
    public Homework getHomework(Long homeworkId)
    {

        Homework homework = homeworkRepository.findById(homeworkId).orElseThrow(()->new HomeworkNotFoundException("Homework not found"));

        return homework;
    }


    @SneakyThrows
    public void putHomework(Long homeworkId, Homework newHomework)
    {
        Homework homework = homeworkRepository.findById(homeworkId).orElseThrow(()->new HomeworkNotFoundException("Homework not found"));


        if(newHomework.getDeadline()!=null)
            homework.setDeadline(newHomework.getDeadline());

        if(newHomework.getGrade()!=null)
            homework.setGrade(newHomework.getGrade());

        if(newHomework.getPoints()!=null)
            homework.setPoints(newHomework.getPoints());

        homeworkRepository.save( homework);


    }
}
