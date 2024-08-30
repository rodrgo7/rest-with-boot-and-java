package com.oliveiradev.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oliveiradev.models.Book;

public interface BookRepository extends JpaRepository<Book, Long> { }