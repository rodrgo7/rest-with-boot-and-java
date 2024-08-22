package com.oliveiradev.tests.unittests.mockito.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import com.oliveiradev.exceptions.RequiredObjectIsNullException;
import com.oliveiradev.data.vo.v1.PersonVO;
import com.oliveiradev.models.Person;
import com.oliveiradev.repositories.PersonRepository;
import com.oliveiradev.services.PersonService;
import com.oliveiradev.tests.unittests.mapper.mocks.MockPerson;

@TestInstance(Lifecycle.PER_CLASS)
class PersonServicesTest {
    MockPerson input;

    @InjectMocks
    private PersonService service;

    @Mock
    PersonRepository repository;

    @Mock
    private ModelMapper mapper;
    
    @BeforeEach
    void setUpMocks() throws Exception {
        input = new MockPerson();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindById() {
    Person person = input.mockEntity(1);
    person.setId(1L);

    when(repository.findById(1L)).thenReturn(Optional.of(person));
    when(mapper.map(person, PersonVO.class)).thenReturn(input.mockVO(1));

    var result = service.findById(1L);
    assertNotNull(result);
    assertNotNull(result.getKey());
    assertNotNull(result.getLinks());
    assertTrue(result.toString().contains("links: [</api/person/v1/1>;rel=\"self\"]"));
    assertEquals("Addres Test1", result.getAddress());
    assertEquals("First Name Test1", result.getFirstName());
    assertEquals("Last Name Test1", result.getLastName());
    assertEquals("Female", result.getGender());
}

   /*/ @Test
    void testFindAll() {
    List<Person> list = input.mockEntityList();
    
    when(repository.findAll()).thenReturn(list);

    // Mockar o comportamento do mapper para todos os objetos na lista
    for (int i = 0; i < list.size(); i++) {
        when(mapper.map(list.get(i), PersonVO.class)).thenReturn(input.mockVO(i));
    }

    var people = service.findAll();
    assertNotNull(people);
    assertEquals(14, people.size());

    var personOne = people.get(1);
    assertNotNull(personOne);
    assertNotNull(personOne.getKey());
    assertNotNull(personOne.getLinks());

    assertTrue(personOne.toString().contains("links: [</api/person/v1/1>;rel=\"self\"]"));
    assertEquals("Addres Test1", personOne.getAddress());
    assertEquals("First Name Test1", personOne.getFirstName());
    assertEquals("Last Name Test1", personOne.getLastName());
    assertEquals("Female", personOne.getGender());

    var personFour = people.get(4);
    assertNotNull(personFour);
    assertNotNull(personFour.getKey());
    assertNotNull(personFour.getLinks());

    assertTrue(personFour.toString().contains("links: [</api/person/v1/4>;rel=\"self\"]"));
    assertEquals("Addres Test4", personFour.getAddress());
    assertEquals("First Name Test4", personFour.getFirstName());
    assertEquals("Last Name Test4", personFour.getLastName());
    assertEquals("Male", personFour.getGender());

    var personSeven  = people.get(7);
    assertNotNull(personSeven);
    assertNotNull(personSeven.getKey());
    assertNotNull(personSeven.getLinks());

    assertTrue(personSeven.toString().contains("links: [</api/person/v1/7>;rel=\"self\"]"));
    assertEquals("Addres Test7", personSeven.getAddress());
    assertEquals("First Name Test7", personSeven.getFirstName());
    assertEquals("Last Name Test7", personSeven.getLastName());
    assertEquals("Female", personSeven.getGender()); 
}
*/
    @Test
    void testCreate() {
        Person persisted = input.mockEntity(1);
        persisted.setId(1L);

        PersonVO vo = input.mockVO(1);
        vo.setKey(1L);

        when(repository.save(any(Person.class))).thenReturn(persisted);
        when(mapper.map(vo, Person.class)).thenReturn(persisted);
        when(mapper.map(persisted, PersonVO.class)).thenReturn(vo);

        var result = service.create(vo);
        assertNotNull(result);
        assertNotNull(result.getKey());
        assertNotNull(result.getLinks());
        assertTrue(result.toString().contains("links: [</api/person/v1/1>;rel=\"self\"]"));
        assertEquals("Addres Test1", result.getAddress());
        assertEquals("First Name Test1", result.getFirstName());
        assertEquals("Last Name Test1", result.getLastName());
        assertEquals("Female", result.getGender());
    }

    @Test
    void testCreateWithNullPerson() {
        Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
            service.create(null);
        });
        String expectedMessage = "It is not allowed to persist a null object!";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testUpdate() {
        Person entity = input.mockEntity(1);
        Person persisted = entity;
        persisted.setId(1L);

        PersonVO vo = input.mockVO(1);
        vo.setKey(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(persisted);
        when(mapper.map(vo, Person.class)).thenReturn(persisted);
        when(mapper.map(persisted, PersonVO.class)).thenReturn(vo);

        var result = service.update(vo);
        assertNotNull(result);
        assertNotNull(result.getKey());
        assertNotNull(result.getLinks());

        assertTrue(result.toString().contains("links: [</api/person/v1/1>;rel=\"self\"]"));
        assertEquals("Addres Test1", result.getAddress());
        assertEquals("First Name Test1", result.getFirstName());
        assertEquals("Last Name Test1", result.getLastName());
        assertEquals("Female", result.getGender());
    }

    @Test
    void testUpdateWithNullPerson() {
        Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
            service.update(null);
        });
        String expectedMessage = "It is not allowed to persist a null object!";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testDelete() {
        Person entity = input.mockEntity(1);
        entity.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        service.delete(1L);
    }
}
