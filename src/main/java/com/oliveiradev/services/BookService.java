package com.oliveiradev.services;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.logging.Logger;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
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
	PagedResourcesAssembler<BookVO> assembler;
	
	@Autowired
	BookRepository repository;

	@Autowired
    ModelMapper mapper;

	public PagedModel<EntityModel<BookVO>> findAll(Pageable pageable) {
		logger.info("Finding all books!");
	
		var bookPage = repository.findAll(pageable);
		var bookVosPage = bookPage.map(b -> mapper.map(b, BookVO.class));
	
		bookVosPage.forEach(b -> b.add(linkTo(methodOn(BookController.class)
			.findById(b.getKey())).withSelfRel()));
		
		Link link = linkTo(
			methodOn(BookController.class).findAll(
				pageable.getPageNumber(),
				pageable.getPageSize(),
				"asc")).withSelfRel();
		return assembler.toModel(bookVosPage, link);
	}
	
	

	public BookVO findById(Long id) {		
		logger.info("Finding one book!");
		
		var entity = repository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
		var vo = mapper.map(entity, BookVO.class);
		vo.add(linkTo(methodOn(BookController.class).findById(id)).withSelfRel());
		return vo;
	}

	public BookVO create (BookVO book) {
		if (book == null) throw new RequiredObjectIsNullException();
		logger.info("Creating one person");

		var entity = mapper.map(book, Book.class);
		entity = repository.save(entity);

		logger.info("Generated ID: " + entity.getId());

		var vo = mapper.map(entity, BookVO.class);

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