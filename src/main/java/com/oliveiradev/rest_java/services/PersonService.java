package com.oliveiradev.rest_java.services;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.oliveiradev.rest_java.controllers.PersonController;
import com.oliveiradev.rest_java.data.vo.v1.PersonVO;
import com.oliveiradev.rest_java.data.vo.v2.PersonVOV2;
import com.oliveiradev.rest_java.exceptions.RequiredObjectIsNullException;
import com.oliveiradev.rest_java.exceptions.ResourceNotFoundException;
import com.oliveiradev.rest_java.mapper.DozerMapper;
import com.oliveiradev.rest_java.mapper.custom.PersonMapper;
import com.oliveiradev.rest_java.models.Person;
import com.oliveiradev.rest_java.repositories.PersonRepository;

@Service
public class PersonService {
    private Logger logger = Logger.getLogger(PersonService.class.getName());

    @Autowired
    PersonRepository repository;

    @Autowired
    PersonMapper mapper;
    public List<PersonVO> findAll() {
        logger.info("Finding all person");

        var persons = DozerMapper.parseListObject(repository.findAll(), PersonVO.class);
        persons
            .stream()
            .forEach(p -> p.add(linkTo(methodOn(PersonController.class).findById(p.getKey())).withSelfRel()));
        return persons;
    }

    public PersonVO findById(Long id) {
        logger.info("Finding one person");
        
        var entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("No records found for this"));
        var vo = DozerMapper.parseObject(entity, PersonVO.class);
        vo.add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());
        return vo;
    }

    public PersonVO create (PersonVO person) {
        if (person == null) throw new RequiredObjectIsNullException();
        logger.info("Creating one person!");

        var entity = DozerMapper.parseObject(person, Person.class);
        var vo = DozerMapper.parseObject(repository.save(entity), PersonVO.class);        
        vo.add(linkTo(methodOn(PersonController.class).findById(vo.getKey())).withSelfRel());
        return vo;
    }    

     public PersonVOV2 createV2(PersonVOV2 person) {
		if (person == null) throw new RequiredObjectIsNullException();
        logger.info("Creating one person with V2!");
		var entity = mapper.convertVoTOEntity(person);
		var vo =  mapper.convertEntityToVo(repository.save(entity));
		return vo;
	}

    public PersonVO update (PersonVO person) {
        if (person == null) throw new RequiredObjectIsNullException();
        logger.info("Update one person!");

        var entity = repository.findById(person.getKey())
            .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));

        entity.setFirstName(person.getFirstName());
        entity.setLastName(person.getLastName());
        entity.setAddress(person.getAddress());
        entity.setGender(person.getGender());
        
        var vo = DozerMapper.parseObject(repository.save(entity), PersonVO.class);        
        vo.add(linkTo(methodOn(PersonController.class).findById(vo.getKey())).withSelfRel());
        return vo;
    }

    public void delete (Long id) {
        logger.info("Deleting one person!");

        var entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
        repository.delete(entity);
    }    
}