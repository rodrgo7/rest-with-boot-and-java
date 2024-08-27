package com.oliveiradev.unittests.mockito.services;

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
import com.oliveiradev.data.vo.v1.BookVO;
import com.oliveiradev.models.Book;
import com.oliveiradev.repositories.BookRepository;
import com.oliveiradev.services.BookService;
import com.oliveiradev.unittests.mapper.mocks.MockBook;

@TestInstance(Lifecycle.PER_CLASS)
class BookServicesTest {
	MockBook input;
	
	@InjectMocks
	private BookService service;
	
	@Mock
	BookRepository repository;

	@Mock
    private ModelMapper mapper;
	
	@BeforeEach
	void setUpMocks() throws Exception {
		input = new MockBook();
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testFindById() {
		Book entity = input.mockEntity(1); 
		entity.setId(1L);
		
		when(repository.findById(1L)).thenReturn(Optional.of(entity));
		when(mapper.map(entity, BookVO.class)).thenReturn(input.mockVO(1));
		
	var result = service.findById(1L);
		assertNotNull(result);
		assertNotNull(result.getKey());
		assertNotNull(result.getLinks());		
		assertTrue(result.toString().contains("links: [</api/book/v1/1>;rel=\"self\"]"));
		assertEquals("Some Author1", result.getAuthor());
		assertEquals("Some Title1", result.getTitle());
		assertEquals(25D, result.getPrice());
		assertNotNull(result.getLaunchDate());
	}
	
	@Test
	void testCreate() {
    	Book entity = input.mockEntity(1);
    	entity.setId(1L);
    
    	Book persisted = entity;
    	persisted.setId(1L);
    
	    BookVO vo = input.mockVO(1);
	    vo.setKey(1L);
    
	    when(mapper.map(vo, Book.class)).thenReturn(entity);
	    when(repository.save(any(Book.class))).thenReturn(persisted);
	    when(mapper.map(persisted, BookVO.class)).thenReturn(vo);
    
    var result = service.create(vo);    
    	assertNotNull(result);
    	assertNotNull(result.getKey());
    	assertNotNull(result.getLinks());        
    	assertTrue(result.toString().contains("links: [</api/book/v1/1>;rel=\"self\"]"));
    	assertEquals("Some Author1", result.getAuthor());
    	assertEquals("Some Title1", result.getTitle());
    	assertEquals(25D, result.getPrice());
    	assertNotNull(result.getLaunchDate());
	}

	@Test
	void testCreateWithNullBook() {
		Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
			service.create(null);
		});
		
		String expectedMessage = "It is not allowed to persist a null object!";
		String actualMessage = exception.getMessage();
		
		assertTrue(actualMessage.contains(expectedMessage));
	}

	@Test
	void testUpdate() {
		Book entity = input.mockEntity(1); 		
		Book persisted = entity;
		persisted.setId(1L);
		
		BookVO vo = input.mockVO(1);
		vo.setKey(1L);		

		when(repository.findById(1L)).thenReturn(Optional.of(entity));
		when(repository.save(entity)).thenReturn(persisted);		
    	when(mapper.map(vo, Book.class)).thenReturn(persisted);
    	when(mapper.map(persisted, BookVO.class)).thenReturn(vo);
		
		var result = service.update(vo);		
		assertNotNull(result);
		assertNotNull(result.getKey());
		assertNotNull(result.getLinks());
		
		assertTrue(result.toString().contains("links: [</api/book/v1/1>;rel=\"self\"]"));
		assertEquals("Some Author1", result.getAuthor());
		assertEquals("Some Title1", result.getTitle());
		assertEquals(25D, result.getPrice());
		assertNotNull(result.getLaunchDate());
	}
	
	@Test
	void testUpdateWithNullBook() {
		Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
			service.update(null);
		});
		
		String expectedMessage = "It is not allowed to persist a null object!";
		String actualMessage = exception.getMessage();
		
		assertTrue(actualMessage.contains(expectedMessage));
	}
	
	@Test
	void testDelete() {
		Book entity = input.mockEntity(1); 
		entity.setId(1L);
		
		when(repository.findById(1L)).thenReturn(Optional.of(entity));
		
		service.delete(1L);
	}
	
	@Test
	void testFindAll() {
		List<Book> list = input.mockEntityList(); 

		when(repository.findAll()).thenReturn(list);
		
		for (int i = 0; i < list.size(); i++) {
        when(mapper.map(list.get(i), BookVO.class)).thenReturn(input.mockVO(i));
    }
		
		var people = service.findAll();		
		assertNotNull(people);
		assertEquals(14, people.size());
		
		var bookOne = people.get(1);		
		assertNotNull(bookOne);
		assertNotNull(bookOne.getKey());
		assertNotNull(bookOne.getLinks());
		
		assertTrue(bookOne.toString().contains("links: [</api/book/v1/1>;rel=\"self\"]"));
		assertEquals("Some Author1", bookOne.getAuthor());
		assertEquals("Some Title1", bookOne.getTitle());
		assertEquals(25D, bookOne.getPrice());
		assertNotNull(bookOne.getLaunchDate());
		
		var bookFour = people.get(4);
		
		assertNotNull(bookFour);
		assertNotNull(bookFour.getKey());
		assertNotNull(bookFour.getLinks());
		
		assertTrue(bookFour.toString().contains("links: [</api/book/v1/4>;rel=\"self\"]"));
		assertEquals("Some Author4", bookFour.getAuthor());
		assertEquals("Some Title4", bookFour.getTitle());
		assertEquals(25D, bookFour.getPrice());
		assertNotNull(bookFour.getLaunchDate());
		
		var bookSeven = people.get(7);		
		assertNotNull(bookSeven);
		assertNotNull(bookSeven.getKey());
		assertNotNull(bookSeven.getLinks());
		
		assertTrue(bookSeven.toString().contains("links: [</api/book/v1/7>;rel=\"self\"]"));
		assertEquals("Some Author7", bookSeven.getAuthor());
		assertEquals("Some Title7", bookSeven.getTitle());
		assertEquals(25D, bookSeven.getPrice());
		assertNotNull(bookSeven.getLaunchDate());
	}
}