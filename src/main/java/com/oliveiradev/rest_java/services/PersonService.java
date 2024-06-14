package com.oliveiradev.rest_java.services;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oliveiradev.rest_java.data.vo.v1.PersonVO;
import com.oliveiradev.rest_java.exceptions.ResourceNotFoundException;
import com.oliveiradev.rest_java.mapper.DozerMapper;
import com.oliveiradev.rest_java.models.Person;
import com.oliveiradev.rest_java.repositories.PersonRepository;

@Service
public class PersonService {
    private Logger logger = Logger.getLogger(PersonService.class.getName());

    @Autowired
    PersonRepository repository;
    public List<PersonVO> findAll() {
        logger.info("Finding all person");
        return DozerMapper.parseListObject(repository.findAll(), PersonVO.class);
    }

    public PersonVO findById(Long id) {
        logger.info("Finding one person");
        var entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("No records found for this"));
        return DozerMapper.parseObject(entity, PersonVO.class);
    }

    public PersonVO create (PersonVO person) {
        logger.info("Creating one person!");

        var entity = DozerMapper.parseObject(person, Person.class);
        var vo = DozerMapper.parseObject(repository.save(entity), PersonVO.class);        
        return vo;
    }

    public PersonVO update (PersonVO person) {
        logger.info("Update one person!");
        var entity = repository.findById(person.getId())
            .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));

        entity.setFirstName(person.getFirstName());
        entity.setLastName(person.getLastName());
        entity.setAddress(person.getAddress());
        entity.setGender(person.getGender());

        var vo = DozerMapper.parseObject(repository.save(entity), PersonVO.class);        
        return vo;
    }

    public void delete (Long id) {
        logger.info("Deleting one person!");

        var entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
        repository.delete(entity);
    }    
}