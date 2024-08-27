package com.oliveiradev.services;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.stereotype.Service;

import com.oliveiradev.controllers.BookController;
import com.oliveiradev.data.vo.v1.BookVO;
import com.oliveiradev.exceptions.RequiredObjectIsNullException;
import com.oliveiradev.exceptions.ResourceNotFoundException;
import com.oliveiradev.models.Book;
import com.oliveiradev.repositories.BookRepository;

@Service
public class BookService {
	
	private Logger logger = Logger.getLogger(BookService.class.getName());
	
	@Autowired
	BookRepository repository;

	@Autowired
    ModelMapper mapper;

	public List<BookVO> findAll() {
        logger.info("Finding all books!");

        List<Book> books = repository.findAll();
        List<BookVO> bookVOs = books.stream()
            .map(book -> {
                BookVO vo = mapper.map(book, BookVO.class);
                vo.add(linkTo(methodOn(BookController.class).findById(vo.getKey())).withSelfRel());
                return vo;
            })
            .collect(Collectors.toList());
        return bookVOs;
    }

	public BookVO findById(Long id) {		
		logger.info("Finding one book!");
		
		var entity = repository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
		var vo = mapper.map(entity, BookVO.class);
		vo.add(linkTo(methodOn(BookController.class).findById(id)).withSelfRel());
		return vo;
	}

	public BookVO create(BookVO book) {
		if (book == null) throw new RequiredObjectIsNullException();
		
		logger.info("Creating one book!");
		var entity = mapper.map(book, Book.class);
		var savedEntity = repository.save(entity);
		var vo = mapper.map(savedEntity, BookVO.class);
		vo.add(linkTo(methodOn(BookController.class).findById(vo.getKey())).withSelfRel());
		return vo;
	}
	
	public BookVO update(BookVO book) {
		if (book == null) throw new RequiredObjectIsNullException();
		
		logger.info("Updating one book!");		
		var entity = repository.findById(book.getKey())
			.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

		entity.setAuthor(book.getAuthor());
		entity.setLaunchDate(book.getLaunchDate());
		entity.setPrice(book.getPrice());
		entity.setTitle(book.getTitle());
		
		var vo = mapper.map(repository.save(entity), BookVO.class);     
		vo.add(linkTo(methodOn(BookController.class).findById(vo.getKey())).withSelfRel());
		return vo;
	}
	
	public void delete(Long id) {		
		logger.info("Deleting one book!");
		
		var entity = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
		repository.delete(entity);
	}
}