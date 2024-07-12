package com.oliveiradev.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oliveiradev.models.Person;

public interface PersonRepository extends JpaRepository<Person, Long> {}