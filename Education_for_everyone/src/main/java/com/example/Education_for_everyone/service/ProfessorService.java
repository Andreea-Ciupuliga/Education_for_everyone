package com.example.Education_for_everyone.service;

import com.example.Education_for_everyone.SendEmailService;
import com.example.Education_for_everyone.dtos.GetProfessorDto;
import com.example.Education_for_everyone.dtos.RegisterProfessorDto;
import com.example.Education_for_everyone.exceptions.UserAlreadyExistException;
import com.example.Education_for_everyone.exceptions.UserNotFoundException;
import com.example.Education_for_everyone.models.Professor;
import com.example.Education_for_everyone.repository.ProfessorRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProfessorService {

    private ProfessorRepository professorRepository;
    private SendEmailService sendEmailService;
    private final KeycloakAdminService keycloakAdminService; //ca sa pot face salvarea de useri si in keycloak

    @Autowired
    public ProfessorService(ProfessorRepository professorRepository, SendEmailService sendEmailService, KeycloakAdminService keycloakAdminService) {
        this.professorRepository = professorRepository;
        this.sendEmailService = sendEmailService;
        this.keycloakAdminService = keycloakAdminService;
    }




    @SneakyThrows
    public void registerProfessor(RegisterProfessorDto registerProfessorDto)
    {

        if(professorRepository.findByUsername(registerProfessorDto.getUsername()).isPresent())
        {

            throw new UserAlreadyExistException("Professor Already Exist");

        }

        String body="Hello "+registerProfessorDto.getFirstName()+" "+registerProfessorDto.getLastName()+" you registered with your username: "+registerProfessorDto.getUsername();
        sendEmailService.sendEmail(registerProfessorDto.getEmail(),body,"Inregistrare");

        Professor professor= Professor.builder()
                .firstName(registerProfessorDto.getFirstName())
                .lastName(registerProfessorDto.getLastName())
                .email(registerProfessorDto.getEmail())
                .password(registerProfessorDto.getPassword())
                .username(registerProfessorDto.getUsername())
                .subject(registerProfessorDto.getSubject()).build();

        professorRepository.save(professor);

        keycloakAdminService.registerUser(registerProfessorDto.getUsername(),registerProfessorDto.getPassword(), "ROLE_PROFESSOR");
        //specificam direct ROLE_STUDENT ptc nu vrem ca cine inregistreaza din postman sa poata decide ce rol sa aiba

    }

    @SneakyThrows
    public void removeProfessor(Long professorId)
    {
        Professor professor = professorRepository.findById(professorId).orElseThrow(()->new UserNotFoundException("professor not found"));
        professorRepository.delete(professor);
    }

    @SneakyThrows
    public GetProfessorDto getProfessor(Long professorId)
    {

        Professor professor = professorRepository.findById(professorId).orElseThrow(()->new UserNotFoundException("professor not found"));

        GetProfessorDto getPtofessorDto= GetProfessorDto.builder()
                .firstName(professor.getFirstName())
                .lastName(professor.getLastName())
                .email(professor.getEmail()).build();

        return getPtofessorDto;
    }

    @SneakyThrows
    public void putProfessor(Long professorId, RegisterProfessorDto newRegisterProfessorDto)
    {
        Professor professor = professorRepository.findById(professorId).orElseThrow(()->new UserNotFoundException("professor not found"));

        if(newRegisterProfessorDto.getFirstName()!=null)
            professor.setFirstName(newRegisterProfessorDto.getFirstName());

        if(newRegisterProfessorDto.getLastName()!=null)
            professor.setLastName(newRegisterProfessorDto.getLastName());

        if(newRegisterProfessorDto.getEmail()!=null)
            professor.setEmail(newRegisterProfessorDto.getEmail());

        if(newRegisterProfessorDto.getPassword()!=null)
            professor.setPassword(newRegisterProfessorDto.getPassword());

        if(newRegisterProfessorDto.getUsername()!=null)
            professor.setUsername(newRegisterProfessorDto.getUsername());

        if(newRegisterProfessorDto.getSubject()!=null)
            professor.setSubject(newRegisterProfessorDto.getSubject());

        professorRepository.save(professor);

    }

//    @SneakyThrows
//    public Professor getUser(String username) {
//        Professor professor = professorRepository.findByUsername(username).orElseThrow(()->new UserNotFoundException("professor not found"));
//        //user.setPassword(""); //ca sa nu ne afiseze si parola cand returnam toate datele in postman
//        return professor;
//    }

}
