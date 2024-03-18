package com.example.educationforeveryone.service;

import com.example.educationforeveryone.SendEmailService;
import com.example.educationforeveryone.dtos.GetStudentDto;
import com.example.educationforeveryone.dtos.RegisterStudentDto;
import com.example.educationforeveryone.exceptions.alreadyExistException.UserAlreadyExistException;
import com.example.educationforeveryone.exceptions.notFoundException.UserNotFoundException;
import com.example.educationforeveryone.models.Book;
import com.example.educationforeveryone.models.Group;
import com.example.educationforeveryone.models.Student;
import com.example.educationforeveryone.repository.BookRepository;
import com.example.educationforeveryone.repository.GroupRepository;
import com.example.educationforeveryone.repository.StudentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class StudentService {

    private final StudentRepository studentRepository;
    private final SendEmailService sendEmailService;
    private final KeycloakAdminService keycloakAdminService;
    private final GroupRepository groupRepository;
    private final BookRepository bookRepository;

    public StudentService(StudentRepository studentRepository,
                          SendEmailService sendEmailService,
                          KeycloakAdminService keycloakAdminService,
                          GroupRepository groupRepository,
                          BookRepository bookRepository) {
        this.studentRepository = studentRepository;
        this.sendEmailService = sendEmailService;
        this.keycloakAdminService = keycloakAdminService;
        this.groupRepository = groupRepository;
        this.bookRepository = bookRepository;
    }

    public void registerStudent(RegisterStudentDto registerStudentDto) {
        if (studentRepository.findByUsername(registerStudentDto.getUsername()).isPresent()) {
            throw new UserAlreadyExistException("Username already exist!");
        }
        if (studentRepository.findByEmail(registerStudentDto.getEmail()).isPresent()) {
            throw new UserAlreadyExistException("Email already exist!");
        }
        Student savedStudent = studentRepository.save(buildStudent(registerStudentDto));
        log.info("Successfully created student with id: {}", savedStudent.getId());
        sendEmailService.sendRegisterMail(registerStudentDto.getFirstName(), registerStudentDto.getLastName(), registerStudentDto.getUsername(), registerStudentDto.getEmail());
        keycloakAdminService.registerUser(registerStudentDto.getUsername(), registerStudentDto.getPassword(), "ROLE_STUDENT");
    }

    public void removeStudent(Long studentId, String username) {
        Student student = findStudentByIdOrThrowException(studentId);
        validateUser(student, username);
        removeStudentFromGroups(studentId);
        increaseAvailableCopiesOfBooks(studentId);
        studentRepository.delete(student);
        log.info("Successfully deleted student with id: {}", studentId);
    }

    public GetStudentDto getStudentById(Long studentId) {
        Student student = findStudentByIdOrThrowException(studentId);
        return buildStudentDto(student);
    }

    public void updateStudent(Long studentId, RegisterStudentDto newRegisterStudentDto, String username) {
        Student student = findStudentByIdOrThrowException(studentId);
        validateUser(student, username);
        setFieldsIfNotNull(newRegisterStudentDto, student);
        studentRepository.save(student);
        log.info("Successfully updated student with id: {}", studentId);
    }

    public List<GetStudentDto> getAllStudents() {
        List<GetStudentDto> allStudents = studentRepository.findAllStudents();
        if (allStudents.isEmpty()) {
            throw new UserNotFoundException("There are no students to display!");
        }
        return allStudents;
    }

    public List<GetStudentDto> getAllStudentsByName(String studentName) {
        List<GetStudentDto> students = studentRepository.findAllStudentsByName(studentName);
        if (students.isEmpty()) {
            throw new UserNotFoundException("Students not found!");
        }
        return students;
    }

    public void removeAllStudents() {
        studentRepository.deleteAll();
    }

    private void validateUser(Student student, String username) {
        if (!(username.equals("admin") || username.equals(student.getUsername()))) {
            throw new UserNotFoundException("You cannot delete another student's account!");
        }
    }

    private void increaseAvailableCopiesOfBooks(Long studentId) {
        List<Book> books = bookRepository.findBooksByStudentId(studentId); // We see the list of books borrowed by the student.
        books.forEach(book -> {
            book.setAvailableCopies(book.getAvailableCopies() + 1); // For each book individually, we increase the number of available copies because we will remove the student who owns them.
            bookRepository.save(book);
        });
    }

    private void removeStudentFromGroups(Long studentId) {
        List<Group> groups = groupRepository.findGroupsByStudentId(studentId); // We check the list of groups to which the student belongs because if we delete the student, we want to remove them from the groups they belong to.
        groups.forEach(group -> {
            group.setAvailableSeats(group.getAvailableSeats() + 1); // For each group individually, we increase the number of available seats because we will remove the student who was part of them.
            groupRepository.save(group);
        });
    }

    private void setFieldsIfNotNull(RegisterStudentDto newRegisterStudentDto, Student student) {
        if (newRegisterStudentDto.getFirstName() != null) {
            student.setFirstName(newRegisterStudentDto.getFirstName());
        }
        if (newRegisterStudentDto.getLastName() != null) {
            student.setLastName(newRegisterStudentDto.getLastName());
        }
        if (newRegisterStudentDto.getEmail() != null) {
            student.setEmail(newRegisterStudentDto.getEmail());
        }
        if (newRegisterStudentDto.getPassword() != null) {
            student.setPassword(newRegisterStudentDto.getPassword());
        }
        if (newRegisterStudentDto.getUsername() != null) {
            student.setUsername(newRegisterStudentDto.getUsername());
        }
    }

    private Student findStudentByIdOrThrowException(Long studentId) {
        return studentRepository.findById(studentId).orElseThrow(() -> new UserNotFoundException("Student not found!"));
    }

    private Student buildStudent(RegisterStudentDto registerStudentDto) {
        return Student.builder()
                .firstName(registerStudentDto.getFirstName())
                .lastName(registerStudentDto.getLastName())
                .email(registerStudentDto.getEmail())
                .password(registerStudentDto.getPassword())
                .username(registerStudentDto.getUsername()).build();
    }

    private GetStudentDto buildStudentDto(Student student) {
        return GetStudentDto.builder()
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .email(student.getEmail()).build();
    }
}
