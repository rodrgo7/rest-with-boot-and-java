package com.oliveiradev.rest_java.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.oliveiradev.rest_java.models.Person;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long>{

}