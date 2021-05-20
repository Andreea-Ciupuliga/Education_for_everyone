package com.example.Education_for_everyone.service;

import com.example.Education_for_everyone.SendEmailService;
import com.example.Education_for_everyone.dtos.GetGroupDto;
import com.example.Education_for_everyone.dtos.GetProfessorDto;
import com.example.Education_for_everyone.dtos.RegisterProfessorDto;
import com.example.Education_for_everyone.exceptions.GroupNotFoundException;
import com.example.Education_for_everyone.exceptions.UserAlreadyExistException;
import com.example.Education_for_everyone.exceptions.UserNotFoundException;
import com.example.Education_for_everyone.models.Professor;
import com.example.Education_for_everyone.repository.ProfessorRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
            throw new UserAlreadyExistException("Username Already Exist");
        }

        if(professorRepository.findByEmail(registerProfessorDto.getEmail()).isPresent())
        {
            throw new UserAlreadyExistException("Email Already Exist");
        }

        Professor professor= Professor.builder()
                .firstName(registerProfessorDto.getFirstName())
                .lastName(registerProfessorDto.getLastName())
                .email(registerProfessorDto.getEmail())
                .password(registerProfessorDto.getPassword())
                .username(registerProfessorDto.getUsername())
                .subject(registerProfessorDto.getSubject()).build();

        professorRepository.save(professor);

        //trimitem mail ca s-a inregistrat
        String body="Hello "+registerProfessorDto.getFirstName()+" "+registerProfessorDto.getLastName()+" you registered with your username: "+registerProfessorDto.getUsername();
        sendEmailService.sendEmail(registerProfessorDto.getEmail(),body,"Inregistrare");

        //specificam direct ROLE_PROFESSOR ptc nu vrem ca cine inregistreaza din postman sa poata decide ce rol sa aiba
        keycloakAdminService.registerUser(registerProfessorDto.getUsername(),registerProfessorDto.getPassword(), "ROLE_PROFESSOR");

    }

    @SneakyThrows
    public void removeProfessor(Long professorId,String username)
    {
        Professor professor = professorRepository.findById(professorId).orElseThrow(()->new UserNotFoundException("professor not found"));

        //daca este admin sau este chiar profesorul
        if(username.equals("admin") || username.equals(professor.getUsername()))
            professorRepository.delete(professor);


        else if(!(username.equals(professor.getUsername())))
            throw new UserNotFoundException("you cannot delete another teacher's account");

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
    public void putProfessor(Long professorId, RegisterProfessorDto newRegisterProfessorDto,String username)
    {
        Professor professor = professorRepository.findById(professorId).orElseThrow(()->new UserNotFoundException("professor not found"));

        if(username.equals("admin") || username.equals(professor.getUsername()))
        {
            if (newRegisterProfessorDto.getFirstName() != null)
                professor.setFirstName(newRegisterProfessorDto.getFirstName());

            if (newRegisterProfessorDto.getLastName() != null)
                professor.setLastName(newRegisterProfessorDto.getLastName());

            if (newRegisterProfessorDto.getEmail() != null)
                professor.setEmail(newRegisterProfessorDto.getEmail());

            if (newRegisterProfessorDto.getPassword() != null)
                professor.setPassword(newRegisterProfessorDto.getPassword());

            if (newRegisterProfessorDto.getUsername() != null)
                professor.setUsername(newRegisterProfessorDto.getUsername());

            if (newRegisterProfessorDto.getSubject() != null)
                professor.setSubject(newRegisterProfessorDto.getSubject());

            professorRepository.save(professor);
        }

        else if(!(username.equals(professor.getUsername())))
            throw new UserNotFoundException("you cannot edit another teacher's account");

    }


    @SneakyThrows
    public List<GetProfessorDto> getAllProfessors() {

        if(professorRepository.findAllProfessors().isEmpty())
            throw new UserNotFoundException("there are no professors to display");

        //afisam toti profesorii
        return professorRepository.findAllProfessors();
    }

    @SneakyThrows
    public List<GetProfessorDto> getAllProfessorsByName(String professorName) {

        //cautam toti profesorii cu numele respectiv
        if(professorRepository.findAllProfessorsByName(professorName).isEmpty())
        {
            throw new UserNotFoundException("Professors Not Found");
        }

        return professorRepository.findAllProfessorsByName(professorName);
    }

    @SneakyThrows
    public void removeAllProfessors() {
        professorRepository.deleteAll();
    }
}
