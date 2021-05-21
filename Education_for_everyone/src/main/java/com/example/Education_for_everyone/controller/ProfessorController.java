package com.example.Education_for_everyone.controller;
import com.example.Education_for_everyone.dtos.GetProfessorDto;
import com.example.Education_for_everyone.dtos.RegisterProfessorDto;
import com.example.Education_for_everyone.service.ProfessorService;
import com.example.Education_for_everyone.utils.Helper;
import com.example.Education_for_everyone.utils.SuccessDto;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import java.util.List;

@RestController
@RequestMapping("/professor")
public class ProfessorController {
    private ProfessorService professorService;

    @Autowired
    public ProfessorController(ProfessorService professorService) {
        this.professorService = professorService;
    }

    @PostMapping("/register")
    @SneakyThrows
    public ResponseEntity<SuccessDto> registerProfessor(@RequestBody RegisterProfessorDto registerProfessorDto)
    {
        professorService.registerProfessor(registerProfessorDto);
        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR')")
    @DeleteMapping()
    @SneakyThrows
    public ResponseEntity<SuccessDto>removeProfessor(@RequestParam Long professorId,Authentication authentication)
    {
        professorService.removeProfessor(professorId, Helper.getKeycloakUser(authentication));
        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/all")
    @SneakyThrows
    public ResponseEntity<SuccessDto>removeAllProfessors()
    {
        professorService.removeAllProfessors();
        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping()
    @SneakyThrows
    public ResponseEntity<GetProfessorDto>getProfessor(@RequestParam Long professorId)
    {
        return new ResponseEntity<>(professorService.getProfessor(professorId), HttpStatus.OK);
    }

    //afisam toti profesorii
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR','STUDENT')")
    @GetMapping("/showProfessors")
    public ResponseEntity<List<GetProfessorDto>> getAllAllProfessors() {

        return new ResponseEntity<>(professorService.getAllProfessors(), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR','STUDENT')")
    @GetMapping("/showProfessorsByName")
    public ResponseEntity<List<GetProfessorDto>> getAllProfessorsByName(@RequestParam String professorName) {

        return new ResponseEntity<>(professorService.getAllProfessorsByName(professorName), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR')")
    @PutMapping()
    @SneakyThrows
    public ResponseEntity<SuccessDto>putProfessor(@RequestParam Long professorId,@RequestBody RegisterProfessorDto registerProfessorDto,Authentication authentication)
    {
        professorService.putProfessor(professorId,registerProfessorDto,Helper.getKeycloakUser(authentication));
        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }

}
