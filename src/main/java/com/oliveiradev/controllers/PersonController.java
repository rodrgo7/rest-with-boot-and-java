package com.oliveiradev.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.oliveiradev.data.vo.v1.PersonVO;
import com.oliveiradev.data.vo.v2.PersonVOV2;
import com.oliveiradev.services.PersonService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import util.MediaType;

@RestController
@RequestMapping("/api/person/v1")
@Tag(name = "Person", description = "Endpoints for Managing People")
public class PersonController {    
    @Autowired
    private PersonService service;

    @GetMapping(produces= { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML })
    @Operation(summary = "Finds all Persons", description = "Finds all Persons", tags = {"Persons"})
    public List<PersonVO> findAll() {
        return service.findAll();
    }

    @GetMapping(value="/{id}", produces={ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML })
    @Operation(summary = "Find a Person", description = "Find a Person", tags = {"Person"})
    public PersonVO findById(@PathVariable(value="id") Long id) {
        return service.findById(id);
    }

    @PostMapping(
        consumes={ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML }, 
        produces={ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML })
    @Operation(summary = "Adds a new Person",
			description = "Adds a new Person by passing in a JSON, XML or YML representation of the person!", tags = {"Person"})
    public PersonVO create(@RequestBody PersonVO person) {
        return service.create(person);
    }

    @PostMapping(value="/v2", 
        consumes={ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML }, 
        produces={ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML })
    @Operation(summary = "Adds a new Person v2",
			description = "Adds a new Person with birthday and passing in a JSON, XML or YML representation of the person!", tags = {"Person v2"})
    public PersonVOV2 create(@RequestBody PersonVOV2 person) {
        return service.createV2(person);
    }

    @PutMapping(
        consumes={ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML }, 
        produces={ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML })
    @Operation(summary = "Updates a Person",
			description = "Updates a Person by passing in a JSON, XML or YML representation of the person!",
			tags = {"Person"})
    public PersonVO update(@RequestBody PersonVO person) {
        return service.update(person);
    }

    @DeleteMapping(value="/{id}")
    @Operation(summary = "Deletes a Person",
			description = "Deletes a Person by passing in a JSON, XML or YML representation of the person!",
			tags = {"Person"})
    public ResponseEntity<?> delete(@PathVariable(value="id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
