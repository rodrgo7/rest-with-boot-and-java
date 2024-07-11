package com.oliveiradev.rest_java.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oliveiradev.rest_java.models.Book;

public interface BookRepository extends JpaRepository<Book, Long> {}