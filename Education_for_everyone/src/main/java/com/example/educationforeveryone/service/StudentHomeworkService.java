package com.example.educationforeveryone.service;

import com.example.educationforeveryone.exceptions.DoNotMatchException;
import com.example.educationforeveryone.exceptions.GradeException;
import com.example.educationforeveryone.exceptions.alreadyExistException.UserAlreadyExistException;
import com.example.educationforeveryone.exceptions.notFoundException.UserNotFoundException;
import com.example.educationforeveryone.models.Homework;
import com.example.educationforeveryone.models.Professor;
import com.example.educationforeveryone.models.Student;
import com.example.educationforeveryone.models.StudentHomework;
import com.example.educationforeveryone.repository.StudentGroupRepository;
import com.example.educationforeveryone.repository.StudentHomeworkRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class StudentHomeworkService {
    private final StudentService studentService;
    private final ProfessorService professorService;
    private final StudentHomeworkRepository studentHomeworkRepository;
    private final HomeworkService homeworkService;
    private final EmailService emailService;
    private final StudentGroupRepository studentGroupRepository;
    private final GroupService groupService;

    public StudentHomeworkService(StudentService studentService,
                                  ProfessorService professorService,
                                  StudentHomeworkRepository studentHomeworkRepository,
                                  HomeworkService homeworkService,
                                  EmailService emailService,
                                  StudentGroupRepository studentGroupRepository,
                                  GroupService groupService) {
        this.studentService = studentService;
        this.professorService = professorService;
        this.studentHomeworkRepository = studentHomeworkRepository;
        this.homeworkService = homeworkService;
        this.emailService = emailService;
        this.studentGroupRepository = studentGroupRepository;
        this.groupService = groupService;
    }

    public void registerStudentHomework(Long studentId, Long homeworkId, String username) {
        Student student = studentService.getStudentByIdOrThrowException(studentId);
        Homework homework = homeworkService.getHomeworkByIdOrThrowException(homeworkId);
        Professor professor = professorService.getProfessorByUsernameOrThrowException(username);
        validateProfessorInStudentGroups(studentId, professor);
        checkIfHomeworkIsAlreadyAssigned(studentId, homeworkId);
        StudentHomework savedStudentHomework = studentHomeworkRepository.save(buildStudentHomework(student, homework));
        log.info("Successfully saved studentHomework with id: {}", savedStudentHomework.getId());
        emailService.sendNewHomeworkEmail(student, homework);
    }

    public void assignScoreToStudent(Long studentId, Long homeworkId, Long score, String username) {
        Student student = studentService.getStudentByIdOrThrowException(studentId);
        Homework homework = homeworkService.getHomeworkByIdOrThrowException(homeworkId);
        StudentHomework studentHomework = getStudentHomeworkOrThrowException(studentId, homeworkId);
        Professor professor = professorService.getProfessorByUsernameOrThrowException(username);
        validateProfessorInStudentGroups(studentId, professor);
        validateAndSetScore(score, homework, studentHomework);
        studentHomeworkRepository.save(studentHomework);
        emailService.sendHomeworkScoreEmail(score, student, homework);
    }

    public void removeStudentHomework(Long studentId, Long homeworkId, String username) {
        studentService.checkStudentExistsOrThrowException(studentId);
        homeworkService.checkHomeworkExistsOrThrowException(homeworkId);
        StudentHomework studentHomework = getStudentHomeworkOrThrowException(studentId, homeworkId);
        Professor professor = professorService.getProfessorByUsernameOrThrowException(username);
        validateProfessorInStudentGroups(studentId, professor);
        studentHomeworkRepository.delete(studentHomework);
    }

    public List<String> getStudentGrades(String username, Long studentId) {
        Student student = studentService.getStudentByIdOrThrowException(studentId);
        checkPermissionToViewGradesOrThrowException(username, student);
        return studentHomeworkRepository.showScoreByStudentId(studentId);
    }

    public Double getAverageStudentGrades(Long studentId, String username) {
        Student student = studentService.getStudentByIdOrThrowException(studentId);
        checkPermissionToViewGradesOrThrowException(username, student);
        List<Long> grades = studentHomeworkRepository.findScoreByStudentId(studentId);
        return calculateAverageStudentGrades(grades);
    }

    private void validateAndSetScore(Long score, Homework homework, StudentHomework studentHomework) {
        validateScore(score, homework);
        studentHomework.setScore(score);
    }

    private void validateScore(Long score, Homework homework) {
        if (score > homework.getPoints())
            throw new GradeException("The score exceeds the points assigned to this homework!");
    }

    private void validateProfessorInStudentGroups(Long studentId, Professor professor) {
        List<Long> groupIds = studentGroupRepository.findGroupIdsByStudentId(studentId);
        boolean isPresent = false;
        for (Long groupId : groupIds) {
            if (groupService.isProfessorInGroup(professor.getId(), groupId)) {
                isPresent = true;
                break;
            }
        }
        if (!isPresent) {
            throw new UserNotFoundException("Professor not in this group. You cannot edit a student who is not part of your group!");
        }
    }

    private StudentHomework getStudentHomeworkOrThrowException(Long studentId, Long homeworkId) {
        StudentHomework studentHomework = studentHomeworkRepository.findByStudentIdAndHomeworkId(studentId, homeworkId)
                .orElseThrow(() -> new DoNotMatchException("Homework and student do not match!"));
        return studentHomework;
    }

    private void checkIfHomeworkIsAlreadyAssigned(Long studentId, Long homeworkId) {
        if (studentHomeworkRepository.findByStudentIdAndHomeworkId(studentId, homeworkId).isPresent()) {
            throw new UserAlreadyExistException("This homework has already been assigned to the student!");
        }
    }

    private Double calculateAverageStudentGrades(List<Long> grades) {
        if (grades.isEmpty()) {
            return 0.0;
        }
        Double total = 0.0;
        for (Long grade : grades) {
            total += grade;
        }
        return total / grades.size();
    }

    private void checkPermissionToViewGradesOrThrowException(String username, Student student) {
        if (!hasPermissionToViewGrades(username, student)) {
            throw new UserNotFoundException("You can't see this student's grades");
        }
    }

    private boolean hasPermissionToViewGrades(String username, Student student) {
        boolean isAdmin = username.equals("admin");
        boolean isProfessor = professorService.checkIfProfessorExists(username);
        boolean isSameStudent = student.getUsername().equals(username);
        return isAdmin || isProfessor || isSameStudent;
    }


    private StudentHomework buildStudentHomework(Student student, Homework homework) {
        return StudentHomework.builder()
                .student(Student.builder().id(student.getId()).build())
                .homework(Homework.builder().id(homework.getId()).build())
                .studentFirstName(student.getFirstName())
                .studentLastName(student.getLastName())
                .build();
    }

}
