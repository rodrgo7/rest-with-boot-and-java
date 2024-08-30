package com.oliveiradev.services;

import java.util.logging.Logger;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import com.oliveiradev.controllers.PersonController;
import com.oliveiradev.data.vo.v1.PersonVO;
import com.oliveiradev.data.vo.v2.PersonVOV2;
import com.oliveiradev.exceptions.RequiredObjectIsNullException;
import com.oliveiradev.exceptions.ResourceNotFoundException;
import com.oliveiradev.models.Person;
import com.oliveiradev.repositories.PersonRepository;

import jakarta.transaction.Transactional;

@Service
public class PersonService {
    private Logger logger = Logger.getLogger(PersonService.class.getName());

    @Autowired
    PagedResourcesAssembler<PersonVO> assembler;
    
    @Autowired
    PersonRepository repository;

    @Autowired
    ModelMapper mapper;

    public PagedModel<EntityModel<PersonVO>> findAll(Pageable pageable) {
        logger.info("Finding all persons!");

        var personPage = repository.findAll(pageable);
        var personVosPage = personPage.map(p -> mapper.map(p, PersonVO.class));

        personVosPage.map(p -> p.add(linkTo(methodOn(PersonController.class)
            .findById(p.getKey())).withSelfRel()));
        
            Link link = linkTo(
                methodOn(PersonController.class).findAll(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    "asc")).withSelfRel();
            return assembler.toModel(personVosPage, link);
    }

    public PagedModel<EntityModel<PersonVO>> findPersonsByNames(String firstName, Pageable pageable) {
        logger.info("Finding all persons!");
        
        var personPage = repository.findPersonsByNames(firstName, pageable);
        var personVosPage = personPage.map(p -> mapper.map(p, PersonVO.class));

        personVosPage.map(
            p -> p.add(
                linkTo(methodOn(PersonController.class)
            .findById(p.getKey())).withSelfRel()));
        
            Link link = linkTo(
                methodOn(PersonController.class).findAll(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    "asc")).withSelfRel();
            return assembler.toModel(personVosPage, link);
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
        entity = repository.save(entity); 
        logger.info("Generated ID: " + entity.getId());
    
        var vo = mapper.map(entity, PersonVO.class);        
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

    @Transactional
    public PersonVO disabledPerson(Long id) {
        logger.info("Finding one person");
        
        repository.disablePerson(id);
        
        var entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        var vo = mapper.map(entity, PersonVO.class);
        vo.add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());
        return vo;
    }

    public void delete(Long id) {
        logger.info("Deleting one person!");

        var entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        repository.delete(entity);
    }

}