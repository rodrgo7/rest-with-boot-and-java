package com.oliveiradev.tests.integrationstests.controller.withjson;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.oliveiradev.tests.configs.TestConfigs;
import com.oliveiradev.data.vo.v1.security.TokenVO;
import com.oliveiradev.tests.integrations.testcontainers.AbstractIntegrationTest;
import com.oliveiradev.tests.integrations.vo.AccountCredentialsVO;
import com.oliveiradev.tests.integrations.vo.PersonVO;
import com.oliveiradev.tests.integrations.vo.wrappers.WrapperPersonVO;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class PersonControllerJsonTest extends AbstractIntegrationTest {	
	private static RequestSpecification specification;
	private static ObjectMapper objectMapper;

	private static PersonVO person;
	
	@BeforeAll
	public static void setup() {
		objectMapper = new ObjectMapper();
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		
		person = new PersonVO();
	}
	
	@Test
	@Order(0)
	public void authorization() throws JsonMappingException, JsonProcessingException {
		AccountCredentialsVO user = new AccountCredentialsVO("rodrigo", "admin123");
		
		var accessToken = given()
				.basePath("/auth/signin")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_JSON)
				.body(user)
					.when()
				.post()
					.then()
						.statusCode(200)
							.extract()
							.body()
								.as(TokenVO.class)
							.getAccessToken();
		
		specification = new RequestSpecBuilder()
				.addHeader(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + accessToken)
				.setBasePath("/api/person/v1")
				.setPort(TestConfigs.SERVER_PORT)
					.addFilter(new RequestLoggingFilter(LogDetail.ALL))
					.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();
	}
	
	@Test
	@Order(1)
	public void testCreate() throws JsonMappingException, JsonProcessingException {
		mockPerson();
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_JSON)
					.body(person)
					.when()
					.post()
				.then()
					.statusCode(200)
						.extract()
						.body()
							.asString();
		
		PersonVO persistedPerson = objectMapper.readValue(content, PersonVO.class);
		person = persistedPerson;		
		assertNotNull(persistedPerson);		
		assertNotNull(persistedPerson.getId());
		assertNotNull(persistedPerson.getFirstName());
		assertNotNull(persistedPerson.getLastName());
		assertNotNull(persistedPerson.getAddress());
		assertNotNull(persistedPerson.getGender());

		assertTrue(persistedPerson.getEnabled());
		assertTrue(persistedPerson.getId() > 0);
		
		assertEquals("Rodrigo", persistedPerson.getFirstName());
		assertEquals("Oliveira", persistedPerson.getLastName());
		assertEquals("Barueri - SP", persistedPerson.getAddress());
		assertEquals("Male", persistedPerson.getGender());
	}

	@Test
	@Order(2)
	public void testUpdate() throws JsonMappingException, JsonProcessingException {
		person.setLastName("Barros Oliveira");
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_JSON)
					.body(person)
					.when()
					.post()
				.then()
					.statusCode(200)
						.extract()
						.body()
							.asString();
		
		PersonVO persistedPerson = objectMapper.readValue(content, PersonVO.class);
		person = persistedPerson;		
		assertNotNull(persistedPerson);		
		assertNotNull(persistedPerson.getId());
		assertNotNull(persistedPerson.getFirstName());
		assertNotNull(persistedPerson.getLastName());
		assertNotNull(persistedPerson.getAddress());
		assertNotNull(persistedPerson.getGender());

		assertTrue(persistedPerson.getEnabled());
		
		assertEquals(person.getId(), persistedPerson.getId());
		assertEquals("Rodrigo", persistedPerson.getFirstName());
		assertEquals("Barros Oliveira", persistedPerson.getLastName());
		assertEquals("Barueri - SP", persistedPerson.getAddress());
		assertEquals("Male", persistedPerson.getGender());
	}

	@Test
	@Order(3)
	public void testDisablePersonById() throws JsonMappingException, JsonProcessingException {
		var content = given().spec(specification)
			.contentType(TestConfigs.CONTENT_JSON)
				.pathParam("id", person.getId())
				.when()
				.patch("{id}")
			.then()
				.statusCode(200)
					.extract()
					.body()
				.asString();
		
		PersonVO persistedPerson = objectMapper.readValue(content, PersonVO.class);
		person = persistedPerson;		
		assertNotNull(persistedPerson);		
		assertNotNull(persistedPerson.getId());
		assertNotNull(persistedPerson.getFirstName());
		assertNotNull(persistedPerson.getLastName());
		assertNotNull(persistedPerson.getAddress());
		assertNotNull(persistedPerson.getGender());

		assertFalse(persistedPerson.getEnabled());

		assertEquals(person.getId(), persistedPerson.getId());		
		assertEquals("Rodrigo", persistedPerson.getFirstName());
		assertEquals("Barros Oliveira", persistedPerson.getLastName());
		assertEquals("Barueri - SP", persistedPerson.getAddress());
		assertEquals("Male", persistedPerson.getGender());
	}

	@Test
	@Order(4)
	public void testFindById() throws JsonMappingException, JsonProcessingException {
		mockPerson();
			
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_JSON)
					.pathParam("id", person.getId())
					.when()
					.get("{id}")
				.then()
					.statusCode(200)
						.extract()
						.body()
							.asString();
		
		PersonVO persistedPerson = objectMapper.readValue(content, PersonVO.class);
		person = persistedPerson;		
		assertNotNull(persistedPerson);		
		assertNotNull(persistedPerson.getId());
		assertNotNull(persistedPerson.getFirstName());
		assertNotNull(persistedPerson.getLastName());
		assertNotNull(persistedPerson.getAddress());
		assertNotNull(persistedPerson.getGender());
		
		assertEquals(person.getId(), persistedPerson.getId());
		assertEquals("Rodrigo", persistedPerson.getFirstName());
		assertEquals("Barros Oliveira", persistedPerson.getLastName());
		assertEquals("Barueri - SP", persistedPerson.getAddress());
		assertEquals("Male", persistedPerson.getGender());
	}
	
	@Test
	@Order(5)
	public void testDelete() throws JsonMappingException, JsonProcessingException {
    	assertNotNull(specification, "Specification should not be null");
    
	    given().spec(specification)
        	.contentType(TestConfigs.CONTENT_JSON)
        	.pathParam("id", person.getId())
        	.when()
        	.delete("{id}")
        	.then()
        	.statusCode(204);
}
	
	@Test
	@Order(6)
	public void testFindAll() throws JsonMappingException, JsonProcessingException {	
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_JSON)
				.queryParam("page", 3, "size", 10, "direction", "asc")
					.when()
					.get()
				.then()
					.statusCode(200)
						.extract()
						.body()
							.asString();
	
	WrapperPersonVO wrapper = objectMapper.readValue(content, WrapperPersonVO.class);
	var people = wrapper.getEmbedded().getPersons();
	
	PersonVO foundPersonOne = people.get(0);	
	assertNotNull(foundPersonOne.getId());
	assertNotNull(foundPersonOne.getFirstName());
	assertNotNull(foundPersonOne.getLastName());
	assertNotNull(foundPersonOne.getAddress());
	assertNotNull(foundPersonOne.getGender());
	
	assertEquals(275, foundPersonOne.getId());	
	assertEquals("Alyse", foundPersonOne.getFirstName());
	assertEquals("91 Bunker Hill Drive", foundPersonOne.getAddress());
	assertEquals("Female", foundPersonOne.getGender());
}

	@Test
	@Order(7)
	public void testFindByName() throws JsonMappingException, JsonProcessingException {	
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_JSON)
				.accept(TestConfigs.CONTENT_JSON)
				.queryParam("page", 0, "size", 6, "direction", "asc")
				.pathParam("firstName", "Alyse")
					.when()
					.get("findPersonsByName/{firstName}")
				.then()
					.statusCode(200)
						.extract()
						.body()
							.asString();
	
	WrapperPersonVO wrapper = objectMapper.readValue(content, WrapperPersonVO.class);
	var people = wrapper.getEmbedded().getPersons();
	
	PersonVO foundPersonOne = people.get(0);	
		assertNotNull(foundPersonOne.getId());
		assertNotNull(foundPersonOne.getFirstName());
		assertNotNull(foundPersonOne.getLastName());
		assertNotNull(foundPersonOne.getAddress());
		assertNotNull(foundPersonOne.getGender());
	
		assertEquals(74, foundPersonOne.getId());	
		assertEquals("Ab", foundPersonOne.getFirstName());
		assertEquals("139 Eagan Hill", foundPersonOne.getAddress());
		assertEquals("Male", foundPersonOne.getGender());
	}

	@Test
	@Order(8)
	public void testFindAllWithoutToken() throws JsonMappingException, JsonProcessingException {
		RequestSpecification specificationWithoutToken = new RequestSpecBuilder()
			.setBasePath("/api/person/v1")
			.setPort(TestConfigs.SERVER_PORT)
				.addFilter(new RequestLoggingFilter(LogDetail.ALL))
				.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
			.build();
		
		given().spec(specificationWithoutToken)
			.contentType(TestConfigs.CONTENT_JSON)
				.when()
				.get()
			.then()
				.statusCode(403);
	}

	@Test
	@Order(9)
	public void testHATEOAS() throws JsonMappingException, JsonProcessingException {
		var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_JSON)
				.accept(TestConfigs.CONTENT_JSON)
            	.queryParams("page", 3 , "size", 10, "direction", "asc")
                    .when()
                    .get()
                .then()
                    .statusCode(200)
                .extract()
                    .body()
                .asString();
				
        assertTrue(content.contains("_links\":{\"self\":{\"href\":\"http://localhost:8888/api/person/v1/275"));
		assertTrue(content.contains("_links\":{\"self\":{\"href\":\"http://localhost:8888/api/person/v1/942"));
		assertTrue(content.contains("_links\":{\"self\":{\"href\":\"http://localhost:8888/api/person/v1/102"));

		assertTrue(content.contains("first\":{\"href\":\"http://localhost:8888/api/person/v1?direction=asc&page=0&size=10&sort=firstName,asc"));
		assertTrue(content.contains("prev\":{\"href\":\"http://localhost:8888/api/person/v1?direction=asc&page=2&size=10&sort=firstName,asc\"}"));
		assertTrue(content.contains("self\":{\"href\":\"http://localhost:8888/api/person/v1?page=3&size=10&direction=asc\"}"));

		assertTrue(content.contains("next\":{\"href\":\"http://localhost:8888/api/person/v1?direction=asc&page=4&size=10&sort=firstName,asc"));
		assertTrue(content.contains("last\":{\"href\":\"http://localhost:8888/api/person/v1?direction=asc&page=100&size=10&sort=firstName,asc"));

		//assertTrue(content.contains("page\":{\"size\":10,\"totalElements\":1005,\"totalPages\":101,\"number\":3"));
	}
	
	private void mockPerson() {
		person.setFirstName("Rodrigo");
		person.setLastName("Oliveira");
		person.setAddress("Barueri - SP");
		person.setGender("Male");
		person.setEnabled(true);
	}
}