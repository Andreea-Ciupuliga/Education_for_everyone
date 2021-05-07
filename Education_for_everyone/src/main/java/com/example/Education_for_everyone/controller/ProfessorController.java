package com.example.Education_for_everyone.controller;


import com.example.Education_for_everyone.dtos.GetProfessorDto;
import com.example.Education_for_everyone.dtos.RegisterProfessorDto;
import com.example.Education_for_everyone.service.ProfessorService;
import com.example.Education_for_everyone.utils.SuccessDto;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @DeleteMapping()
    @SneakyThrows
    public ResponseEntity<SuccessDto>removeProfessor(@RequestParam Long professorId)
    {
        professorService.removeProfessor(professorId);

        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }

//    @DeleteMapping("/all")
//    @SneakyThrows
//    public ResponseEntity<SuccessDto>removeAllProfessors()
//    {
//        professorService.removeAllProfessors();
//
//        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
//    }

    @GetMapping()
    @SneakyThrows
    public ResponseEntity<GetProfessorDto>getProfessor(@RequestParam Long professorId)
    {

        return new ResponseEntity<>(professorService.getProfessor(professorId), HttpStatus.OK);
    }


    @PutMapping()
    @SneakyThrows
    public ResponseEntity<SuccessDto>putProfessor(@RequestParam Long professorId,@RequestBody RegisterProfessorDto registerProfessorDto)
    {
        professorService.putProfessor(professorId,registerProfessorDto);

        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }

}
