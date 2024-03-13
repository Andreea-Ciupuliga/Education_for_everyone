package com.example.educationforeveryone.service;

import com.example.educationforeveryone.exceptions.notFoundException.HomeworkNotFoundException;
import com.example.educationforeveryone.models.Homework;
import com.example.educationforeveryone.repository.HomeworkRepository;
import org.springframework.stereotype.Service;

@Service
public class HomeworkService {

    private final HomeworkRepository homeworkRepository;

    public HomeworkService(HomeworkRepository homeworkRepository) {
        this.homeworkRepository = homeworkRepository;
    }

    public void registerHomework(Homework homework) {
        Homework newHomework = Homework.builder()
                .points(homework.getPoints())
                .deadline(homework.getDeadline())
                .task(homework.getTask())
                .build();

        homeworkRepository.save(newHomework);
    }

    public void removeHomework(Long homeworkId) {
        Homework homework = homeworkRepository.findById(homeworkId).orElseThrow(() -> new HomeworkNotFoundException("Homework not found"));
        homeworkRepository.delete(homework);
    }

    public Homework getHomework(Long homeworkId) {
        Homework homework = homeworkRepository.findById(homeworkId).orElseThrow(() -> new HomeworkNotFoundException("Homework not found"));
        return homework;
    }

    public void putHomework(Long homeworkId, Homework newHomework) {
        Homework homework = homeworkRepository.findById(homeworkId).orElseThrow(() -> new HomeworkNotFoundException("Homework not found"));

        if (newHomework.getDeadline() != null)
            homework.setDeadline(newHomework.getDeadline());

        if (newHomework.getTask() != null)
            homework.setTask(newHomework.getTask());

        if (newHomework.getPoints() != null)
            homework.setPoints(newHomework.getPoints());

        homeworkRepository.save(homework);

    }
}
