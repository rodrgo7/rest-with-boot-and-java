package com.oliveiradev.mapper;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.oliveiradev.data.vo.v1.BookVO;
import com.oliveiradev.data.vo.v1.PersonVO;
import com.oliveiradev.models.Book;
import com.oliveiradev.models.Person;

@Configuration
public class ModelMapperConfig {
    
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.createTypeMap(Person.class, PersonVO.class).addMapping(Person::getId, PersonVO::setKey);
        modelMapper.createTypeMap(PersonVO.class, Person.class).addMapping(PersonVO::getKey, Person::setId);

        modelMapper.createTypeMap(Book.class, BookVO.class).addMapping(Book::getId, BookVO::setKey);
        modelMapper.createTypeMap(BookVO.class, Book.class).addMapping(BookVO::getKey, Book::setId);
        return modelMapper;
    }

    public static <O, D> D parseObject(O origin, Class<D> destination, ModelMapper mapper) {
        return mapper.map(origin, destination);
    }

    public static <O, D> List<D> parseListObject(List<O> origin, Class<D> destination, ModelMapper mapper) {
        List<D> destinationObjects = new ArrayList<>();
        for (O o : origin) {
            destinationObjects.add(mapper.map(o, destination));
        }
        return destinationObjects;
    }
}