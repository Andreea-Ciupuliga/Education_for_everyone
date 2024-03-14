package com.example.educationforeveryone.controller;

import com.example.educationforeveryone.dtos.GetProfessorDto;
import com.example.educationforeveryone.dtos.RegisterProfessorDto;
import com.example.educationforeveryone.service.ProfessorService;
import com.example.educationforeveryone.utils.Helper;
import com.example.educationforeveryone.utils.SuccessDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/professors")
@Tag(name = "/professors", description = "professor controller")
public class ProfessorController {
    private final ProfessorService professorService;

    public ProfessorController(ProfessorService professorService) {
        this.professorService = professorService;
    }

    @Operation(summary = "Register a professor", description = "Register a professor")
    @PostMapping("/register")
    public ResponseEntity<SuccessDto> registerProfessor(@RequestBody RegisterProfessorDto registerProfessorDto) {
        professorService.registerProfessor(registerProfessorDto);
        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }

    @Operation(summary = "Remove a professor", description = "Remove a professor")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR')")
    @DeleteMapping("/{professorId}")
    public ResponseEntity<SuccessDto> removeProfessor(@PathVariable Long professorId, Authentication authentication) {
        professorService.removeProfessor(professorId, Helper.getKeycloakUser(authentication));
        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }

    @Operation(summary = "Remove all professors", description = "Remove all professors")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/all")
    public ResponseEntity<SuccessDto> removeAllProfessors() {
        professorService.removeAllProfessors();
        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }

    @Operation(summary = "Get professor by id", description = "Get professor by its id")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{professorId}")
    public ResponseEntity<GetProfessorDto> getProfessorById(@PathVariable Long professorId) {
        return new ResponseEntity<>(professorService.getProfessorById(professorId), HttpStatus.OK);
    }

    @Operation(summary = "Get all professors", description = "Get all professors")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR','STUDENT')")
    @GetMapping
    public ResponseEntity<List<GetProfessorDto>> getAllAllProfessors() {
        return new ResponseEntity<>(professorService.getAllProfessors(), HttpStatus.OK);
    }

    @Operation(summary = "Get professors by name", description = "Get professors by name")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR','STUDENT')")
    @GetMapping("/{professorName}")
    public ResponseEntity<List<GetProfessorDto>> getAllProfessorsByName(@PathVariable String professorName) {
        return new ResponseEntity<>(professorService.getAllProfessorsByName(professorName), HttpStatus.OK);
    }

    @Operation(summary = "Update a professor", description = "Update a professor")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR')")
    @PutMapping("/{professorId}")
    public ResponseEntity<SuccessDto> updateProfessor(@PathVariable Long professorId,
                                                      @RequestBody RegisterProfessorDto registerProfessorDto,
                                                      Authentication authentication) {
        professorService.updateProfessor(professorId, registerProfessorDto, Helper.getKeycloakUser(authentication));
        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }
}
