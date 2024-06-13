package com.oliveiradev.rest_java.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oliveiradev.rest_java.models.Person;

public interface PersonRepository extends JpaRepository<Person, Long> {}