package com.example.educationforeveryone.service;

import com.example.educationforeveryone.exceptions.notFoundException.HomeworkNotFoundException;
import com.example.educationforeveryone.models.Homework;
import com.example.educationforeveryone.repository.HomeworkRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class HomeworkService {

    private final HomeworkRepository homeworkRepository;

    public HomeworkService(HomeworkRepository homeworkRepository) {
        this.homeworkRepository = homeworkRepository;
    }

    public void registerHomework(Homework homework) {
        Homework saveHomework = homeworkRepository.save(buildHomework(homework));
        log.info("Successfully saved homework with id: {}", saveHomework.getId());
    }

    public void removeHomework(Long homeworkId) {
        Homework homework = getHomeworkById(homeworkId);
        homeworkRepository.delete(homework);
        log.info("Successfully deleted homework with id: {}", homeworkId);
    }

    public void updateHomework(Long homeworkId, Homework newHomework) {
        Homework homework = getHomeworkById(homeworkId);
        setFieldsIfNotNull(newHomework, homework);
        Homework savedHomework = homeworkRepository.save(homework);
        log.info("Successfully updated homework with id: {}", savedHomework.getId());
    }

    public Homework getHomeworkById(Long homeworkId) {
        return homeworkRepository.findById(homeworkId).orElseThrow(() -> new HomeworkNotFoundException("Homework not found"));
    }

    private void setFieldsIfNotNull(Homework newHomework, Homework homework) {
        if (newHomework.getDeadline() != null)
            homework.setDeadline(newHomework.getDeadline());

        if (newHomework.getTask() != null)
            homework.setTask(newHomework.getTask());

        if (newHomework.getPoints() != null)
            homework.setPoints(newHomework.getPoints());
    }

    private Homework buildHomework(Homework homework) {
        return Homework.builder()
                .points(homework.getPoints())
                .deadline(homework.getDeadline())
                .task(homework.getTask())
                .build();
    }
}
