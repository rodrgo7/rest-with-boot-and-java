package com.oliveiradev.rest_java.services;

import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.oliveiradev.rest_java.models.Person;

@Service
public class PersonService {
    private final AtomicLong counter = new AtomicLong();
    private Logger logger = Logger.getLogger(PersonService.class.getName());

    public Person findById(String id) {

        logger.info("Finding one person");
        
        Person person = new Person();
        person.setId(counter.incrementAndGet());
        person.setFirstName("Rodrigo");
        person.setLastName("Oliveira");
        person.setAddress("Barueri - São Paulo");
        person.setGender("Masculino");

        return person();
    }

    private Person person() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'person'");
    }
}
