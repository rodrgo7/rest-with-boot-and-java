package com.oliveiradev.services;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oliveiradev.controllers.PersonController;
import com.oliveiradev.data.vo.v1.PersonVO;
import com.oliveiradev.data.vo.v2.PersonVOV2;
import com.oliveiradev.exceptions.RequiredObjectIsNullException;
import com.oliveiradev.exceptions.ResourceNotFoundException;
import com.oliveiradev.models.Person;
import com.oliveiradev.repositories.PersonRepository;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class PersonService {
    private Logger logger = Logger.getLogger(PersonService.class.getName());

    @Autowired
    PersonRepository repository;

    @Autowired
    ModelMapper mapper;


    public List<PersonVO> findAll() {
        logger.info("Finding all persons!");

        List<Person> persons = repository.findAll();
        List<PersonVO> personVOs = persons.stream()
            .map(person -> {
                PersonVO vo = mapper.map(person, PersonVO.class);
                vo.add(linkTo(methodOn(PersonController.class).findById(vo.getKey())).withSelfRel());
                return vo;
            })
            .collect(Collectors.toList());
        return personVOs;
    }

    public PersonVO findById(Long id) {
        logger.info("Finding one person");
        
        var entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        var vo = mapper.map(entity, PersonVO.class);
        vo.add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());
        return vo;
    }

    public PersonVO create(PersonVO person) {
        if (person == null) throw new RequiredObjectIsNullException();
        logger.info("Creating one person!");

        var entity = mapper.map(person, Person.class);
        var vo = mapper.map(repository.save(entity), PersonVO.class);        
        vo.add(linkTo(methodOn(PersonController.class).findById(vo.getKey())).withSelfRel());
        return vo;
    }

    public PersonVOV2 createV2(PersonVOV2 person) {
        if (person == null) throw new RequiredObjectIsNullException();
        logger.info("Creating one person with V2!");
        var entity = mapper.map(person, Person.class);
        var vo = mapper.map(repository.save(entity), PersonVOV2.class);
        return vo;
    }

    public PersonVO update(PersonVO person) {
        if (person == null) throw new RequiredObjectIsNullException();
        logger.info("Updating one person!");

        var entity = repository.findById(person.getKey())
            .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        entity.setFirstName(person.getFirstName());
        entity.setLastName(person.getLastName());
        entity.setAddress(person.getAddress());
        entity.setGender(person.getGender());

        var vo = mapper.map(repository.save(entity), PersonVO.class);        
        vo.add(linkTo(methodOn(PersonController.class).findById(vo.getKey())).withSelfRel());
        return vo;
    }

    public void delete(Long id) {
        logger.info("Deleting one person!");

        var entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        repository.delete(entity);
    }
}
