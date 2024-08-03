/*
package com.oliveiradev.mapper;

import java.util.ArrayList;
import java.util.List;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.loader.api.BeanMappingBuilder;

import com.oliveiradev.data.vo.v1.PersonVO;
import com.oliveiradev.models.Person;

public class Dozer_Mapper {
    private static Mapper mapper;

    static {
        mapper = DozerBeanMapperBuilder.create()
            .withMappingBuilder(new BeanMappingBuilder() {
                @Override
                protected void configure() {
                    mapping(Person.class, PersonVO.class)
                        .fields("id", "key");
                    mapping(PersonVO.class, Person.class)
                        .fields("key", "id");
                }
            })
            .build();
    }

    public static <O, D> D parseObject(O origin, Class<D> destination) {
        return mapper.map(origin, destination);
    }

    public static <O, D> List<D> parseListObject(List<O> origin, Class<D> destination) {
        List<D> destinationObjects = new ArrayList<>();
        for (O o : origin) {
            destinationObjects.add(mapper.map(o, destination));
        }
        return destinationObjects;
    }
}
*/