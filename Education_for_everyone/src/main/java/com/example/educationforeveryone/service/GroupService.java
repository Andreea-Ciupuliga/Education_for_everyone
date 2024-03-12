package com.example.educationforeveryone.service;

import com.example.educationforeveryone.dtos.GetGroupDto;
import com.example.educationforeveryone.dtos.RegisterGroupDto;
import com.example.educationforeveryone.exceptions.alreadyExistException.GroupAlreadyExistException;
import com.example.educationforeveryone.exceptions.notFoundException.GroupNotFoundException;
import com.example.educationforeveryone.exceptions.notFoundException.UserNotFoundException;
import com.example.educationforeveryone.models.Group;
import com.example.educationforeveryone.models.Professor;
import com.example.educationforeveryone.repository.GroupRepository;
import com.example.educationforeveryone.repository.ProfessorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final ProfessorRepository professorRepository;

    public GroupService(GroupRepository groupRepository, ProfessorRepository professorRepository) {
        this.groupRepository = groupRepository;
        this.professorRepository = professorRepository;
    }

    public void registerGroup(RegisterGroupDto registerGroupDto, String username) {

        //verificam daca exista deja un grup cu numele asta
        if (groupRepository.findByGroupName(registerGroupDto.getGroupName()).isPresent()) {
            throw new GroupAlreadyExistException("Group Already Exist");
        }

        //Cautam profesorul care vrea sa faca grupul in baza de date dupa username-ul din token ca sa vedem ca exista profesorul respectiv
        Professor professor = professorRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("professor not found"));

        Group group = Group.builder()
                .professor(Professor.builder().id(professor.getId()).build()) //id ul profesorului o sa il dam automat de la profesorul gasit dupa username
                .groupName(registerGroupDto.getGroupName())
                .subject(registerGroupDto.getSubject())
                .yearOfStudy(registerGroupDto.getYearOfStudy())
                .availableSeats(registerGroupDto.getTotalSeats()) // la inceput locurile libere ar trebui sa fie acelasi numar ca locurile totale doarece daca abia s-a creat grupul nu a ocupat nimeni nimic
                .totalSeats(registerGroupDto.getTotalSeats()).build();

        groupRepository.save(group);

    }

    public void removeGroup(Long groupId, String username) {
        //verificam daca grupul exista
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException("group not found"));

        //daca e admin poate sa stearga orice grup
        if (username.equals("admin"))
            groupRepository.delete(group);


        else {
            //daca nu e admin verificam daca este profesor. Il cautam in baza de date dupa username-ul din token
            Professor professor = professorRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("professor not found"));

            //daca e profesor la grupul respectiv il poate sterge
            if (groupRepository.findByProfessorIdAndGroupId(professor.getId(), groupId).isPresent()) {
                groupRepository.delete(group);
            }

            //daca nu e profesor la grupul respectiv nu il poate sterge si aruncam exceptie
            else if (groupRepository.findByProfessorIdAndGroupId(professor.getId(), groupId).isEmpty()) {
                throw new UserNotFoundException("Professor not in this group. You cannot delete a group you are not part of");
            }
        }
    }

    public GetGroupDto getGroup(Long groupId) {
        //verificam daca exista grupul cu id-ul respectiv
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException("group not found"));

        GetGroupDto getGrouptDto = GetGroupDto.builder()
                .groupName(group.getGroupName())
                .subject(group.getSubject())
                .yearOfStudy(group.getYearOfStudy())
                .availableSeats(group.getAvailableSeats()).build();

        return getGrouptDto;
    }

    public void putGroup(Long groupId, RegisterGroupDto newRegisterGroupDto, String username) {
        //vedem daca exista grupul cu id-ul respectiv
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException("group not found"));

        //vedem daca exista in baza de date profesorul cu username-ul extras din token
        Professor professor = professorRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("professor not found"));

        //daca profeosrul nu preda la grupul respectiv aruncam exceptie
        if (groupRepository.findByProfessorIdAndGroupId(professor.getId(), groupId).isEmpty()) {
            throw new UserNotFoundException("Professor not in this group. You cannot edit a group you are not part of");
        }

        if (newRegisterGroupDto.getGroupName() != null)
            group.setGroupName(newRegisterGroupDto.getGroupName());

        if (newRegisterGroupDto.getSubject() != null)
            group.setSubject(newRegisterGroupDto.getSubject());

        if (newRegisterGroupDto.getYearOfStudy() != null)
            group.setYearOfStudy(newRegisterGroupDto.getYearOfStudy());

        if (newRegisterGroupDto.getAvailableSeats() != null)
            group.setAvailableSeats(newRegisterGroupDto.getAvailableSeats());

        if (newRegisterGroupDto.getTotalSeats() != null)
            group.setTotalSeats(newRegisterGroupDto.getTotalSeats());

        groupRepository.save(group);
    }

    public List<GetGroupDto> getAllGroups() {

        if (groupRepository.findAllGroups().isEmpty())
            throw new GroupNotFoundException("there are no groups to display");

        //afisam toate grupurile
        return groupRepository.findAllGroups();
    }

    public List<GetGroupDto> getAllGroupsBySubject(String subject) {

        //cautam toate grupurile la care se preda materia respectiva
        if (groupRepository.findAllBySubjectContains(subject).isEmpty()) {
            throw new GroupNotFoundException("Subject Not Found");
        }

        return groupRepository.findAllBySubjectContains(subject);
    }

    public List<GetGroupDto> getAllGroupsByProfessorName(String professorName) {

        //cautam toate grupurile la care se preda profeosrul cu numele respectiv
        if (groupRepository.findAllByProfessorNameContains(professorName).isEmpty()) {
            throw new UserNotFoundException("Professor Not Found");
        }

        return groupRepository.findAllByProfessorNameContains(professorName);
    }

}
