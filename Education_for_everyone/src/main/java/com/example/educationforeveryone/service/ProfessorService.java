package com.example.educationforeveryone.service;

import com.example.educationforeveryone.SendEmailService;
import com.example.educationforeveryone.dtos.GetProfessorDto;
import com.example.educationforeveryone.dtos.RegisterProfessorDto;
import com.example.educationforeveryone.exceptions.alreadyExistException.UserAlreadyExistException;
import com.example.educationforeveryone.exceptions.notFoundException.UserNotFoundException;
import com.example.educationforeveryone.models.Professor;
import com.example.educationforeveryone.repository.ProfessorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ProfessorService {

    private final ProfessorRepository professorRepository;
    private final SendEmailService sendEmailService;
    private final KeycloakAdminService keycloakAdminService;

    public ProfessorService(ProfessorRepository professorRepository,
                            SendEmailService sendEmailService,
                            KeycloakAdminService keycloakAdminService) {
        this.professorRepository = professorRepository;
        this.sendEmailService = sendEmailService;
        this.keycloakAdminService = keycloakAdminService;
    }

    public void registerProfessor(RegisterProfessorDto registerProfessorDto) {
        if (professorRepository.findByUsername(registerProfessorDto.getUsername()).isPresent()) {
            throw new UserAlreadyExistException("Username Already Exist");
        }
        if (professorRepository.findByEmail(registerProfessorDto.getEmail()).isPresent()) {
            throw new UserAlreadyExistException("Email Already Exist");
        }
        Professor savedProfessor = professorRepository.save(buildProfessor(registerProfessorDto));
        log.info("Successfully saved professor with id: {}", savedProfessor.getId());
        sendEmailService.sendRegisterMail(registerProfessorDto.getFirstName(), registerProfessorDto.getLastName(), registerProfessorDto.getUsername(), registerProfessorDto.getEmail());
        keycloakAdminService.registerUser(registerProfessorDto.getUsername(), registerProfessorDto.getPassword(), "ROLE_PROFESSOR");
    }

    public void removeProfessor(Long professorId, String username) {
        Professor professor = findProfessorByIdOrThrowException(professorId);
        if (username.equals("admin") || username.equals(professor.getUsername())) { // If it is an admin or if it is the actual professor
            professorRepository.delete(professor);
            log.info("Successfully deleted professor with id: {}", professorId);
        } else {
            throw new UserNotFoundException("You cannot delete another teacher's account!");
        }
    }

    public GetProfessorDto getProfessorById(Long professorId) {
        Professor professor = findProfessorByIdOrThrowException(professorId);
        return buildGetProfessorDto(professor);
    }

    public void updateProfessor(Long professorId, RegisterProfessorDto newRegisterProfessorDto, String username) {
        Professor professor = findProfessorByIdOrThrowException(professorId);
        if (username.equals("admin") || username.equals(professor.getUsername())) {
            setFieldsIfNotNull(newRegisterProfessorDto, professor);
            professorRepository.save(professor);
            log.info("Successfully updated professor with id: {}", professorId);
        } else {
            throw new UserNotFoundException("You cannot edit another teacher's account!");
        }
    }

    public List<GetProfessorDto> getAllProfessors() {
        List<GetProfessorDto> professors = professorRepository.findAllProfessors();
        if (professors.isEmpty()) {
            throw new UserNotFoundException("There are no professors to display!");
        }
        return professors;
    }

    public List<GetProfessorDto> getAllProfessorsByName(String professorName) {
        List<GetProfessorDto> professors = professorRepository.findAllProfessorsByName(professorName);
        if (professors.isEmpty()) {
            throw new UserNotFoundException("Professors not found!");
        }
        return professors;
    }

    public void removeAllProfessors() {
        professorRepository.deleteAll();
        log.info("Successfully deleted all professors");
    }

    private Professor findProfessorByIdOrThrowException(Long professorId) {
        return professorRepository.findById(professorId).orElseThrow(() -> new UserNotFoundException("Professor not found"));
    }

    private void setFieldsIfNotNull(RegisterProfessorDto newRegisterProfessorDto, Professor professor) {
        if (newRegisterProfessorDto.getFirstName() != null) {
            professor.setFirstName(newRegisterProfessorDto.getFirstName());
        }
        if (newRegisterProfessorDto.getLastName() != null) {
            professor.setLastName(newRegisterProfessorDto.getLastName());
        }
        if (newRegisterProfessorDto.getEmail() != null) {
            professor.setEmail(newRegisterProfessorDto.getEmail());
        }
        if (newRegisterProfessorDto.getPassword() != null) {
            professor.setPassword(newRegisterProfessorDto.getPassword());
        }
        if (newRegisterProfessorDto.getUsername() != null) {
            professor.setUsername(newRegisterProfessorDto.getUsername());
        }
        if (newRegisterProfessorDto.getSubject() != null) {
            professor.setSubject(newRegisterProfessorDto.getSubject());
        }
    }

    private Professor buildProfessor(RegisterProfessorDto registerProfessorDto) {
        return Professor.builder()
                .firstName(registerProfessorDto.getFirstName())
                .lastName(registerProfessorDto.getLastName())
                .email(registerProfessorDto.getEmail())
                .password(registerProfessorDto.getPassword())
                .username(registerProfessorDto.getUsername())
                .subject(registerProfessorDto.getSubject()).build();
    }

    private GetProfessorDto buildGetProfessorDto(Professor professor) {
        return GetProfessorDto.builder()
                .firstName(professor.getFirstName())
                .lastName(professor.getLastName())
                .email(professor.getEmail()).build();
    }
}
