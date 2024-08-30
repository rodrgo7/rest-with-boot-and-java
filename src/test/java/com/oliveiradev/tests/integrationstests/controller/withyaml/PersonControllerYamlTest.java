package com.oliveiradev.tests.integrationstests.controller.withyaml;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import com.oliveiradev.tests.configs.TestConfigs;
import com.oliveiradev.data.vo.v1.security.TokenVO;
import com.oliveiradev.tests.integrationstests.controller.withyaml.mapper.YAMLMapper;
import com.oliveiradev.tests.integrations.testcontainers.AbstractIntegrationTest;
import com.oliveiradev.tests.integrations.vo.AccountCredentialsVO;
import com.oliveiradev.tests.integrations.vo.PersonVO;
import com.oliveiradev.tests.integrations.vo.pagedmodels.PagedModelPerson;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class PersonControllerYamlTest extends AbstractIntegrationTest {	
	private static RequestSpecification specification;
	private static YAMLMapper objectMapper;

	private static PersonVO person;
	
	@BeforeAll
	public static void setup() {
		objectMapper = new YAMLMapper();
		person = new PersonVO();
	}
	
	@Test
	@Order(0)
	public void authorization() throws JsonMappingException, JsonProcessingException {		
		AccountCredentialsVO user = new AccountCredentialsVO("rodrigo", "admin123");
		
		var accessToken = given()
				.config(
						RestAssuredConfig
							.config()
							.encoderConfig(EncoderConfig.encoderConfig()
								.encodeContentTypeAs(
									TestConfigs.CONTENT_YAML,
									ContentType.TEXT)))
				.basePath("/auth/signin")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_YAML)
					.accept(TestConfigs.CONTENT_YAML)
				.body(user, objectMapper)
					.when()
				.post()
					.then()
						.statusCode(200)
							.extract()
							.body()
								.as(TokenVO.class, objectMapper)
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
		
		var persistedPerson = given().spec(specification)
				.config(
						RestAssuredConfig
							.config()
							.encoderConfig(EncoderConfig.encoderConfig()
								.encodeContentTypeAs(
									TestConfigs.CONTENT_YAML,
									ContentType.TEXT)))
				.contentType(TestConfigs.CONTENT_YAML)
				.accept(TestConfigs.CONTENT_YAML)
					.body(person, objectMapper)
					.when()
					.post()
				.then()
					.statusCode(200)
						.extract()
						.body()
							.as(PersonVO.class, objectMapper);
									
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
		
		var persistedPerson = given().spec(specification)
				.config(
						RestAssuredConfig
							.config()
							.encoderConfig(EncoderConfig.encoderConfig()
								.encodeContentTypeAs(
									TestConfigs.CONTENT_YAML,
									ContentType.TEXT)))
				.contentType(TestConfigs.CONTENT_YAML)
				.accept(TestConfigs.CONTENT_YAML)
					.body(person, objectMapper)
					.when()
					.post()
				.then()
					.statusCode(200)
						.extract()
						.body()
						.as(PersonVO.class, objectMapper);
		
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
		var persistedPerson = given().spec(specification)
				.config(
					RestAssuredConfig
						.config()
						.encoderConfig(EncoderConfig.encoderConfig()
							.encodeContentTypeAs(
								TestConfigs.CONTENT_YAML,
								ContentType.TEXT)))
				.contentType(TestConfigs.CONTENT_YAML)
				.accept(TestConfigs.CONTENT_YAML)
					.pathParam("id", person.getId())
					.when()
					.patch("{id}")
				.then()
					.statusCode(200)
						.extract()
							.body()
								.as(PersonVO.class, objectMapper);
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
			
		var persistedPerson = given().spec(specification)
				.config(
						RestAssuredConfig
							.config()
							.encoderConfig(EncoderConfig.encoderConfig()
								.encodeContentTypeAs(
									TestConfigs.CONTENT_YAML,
									ContentType.TEXT)))
				.contentType(TestConfigs.CONTENT_YAML)
				.accept(TestConfigs.CONTENT_YAML)
					.pathParam("id", person.getId())
					.when()
					.get("{id}")
				.then()
					.statusCode(200)
						.extract()
						.body()
						.as(PersonVO.class, objectMapper);
		
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
		given().spec(specification)
			.config(
				RestAssuredConfig
					.config()
					.encoderConfig(EncoderConfig.encoderConfig()
						.encodeContentTypeAs(
							TestConfigs.CONTENT_YAML,
							ContentType.TEXT)))
			.contentType(TestConfigs.CONTENT_YAML)
			.accept(TestConfigs.CONTENT_YAML)
				.pathParam("id", person.getId())
				.when()
				.delete("{id}")
			.then()
				.statusCode(204);
	}
	
	@Test
	@Order(6)
	public void testFindAll() throws JsonMappingException, JsonProcessingException {		
		var wrapper = given().spec(specification)
				.config(
						RestAssuredConfig
							.config()
							.encoderConfig(EncoderConfig.encoderConfig()
								.encodeContentTypeAs(
									TestConfigs.CONTENT_YAML, ContentType.TEXT)))
				.contentType(TestConfigs.CONTENT_YAML)
				.queryParam("page", 3, "size", 10, "direction", "asc")
				.accept(TestConfigs.CONTENT_YAML)
					.when()
					.get()
				.then()
					.statusCode(200)
						.extract()
						.body()
						.as(PagedModelPerson.class, objectMapper);

		var people = wrapper.getContent();
		
		PersonVO foundPersonOne = people.get(0);
		
		assertNotNull(foundPersonOne.getId());
		assertNotNull(foundPersonOne.getFirstName());
		assertNotNull(foundPersonOne.getLastName());
		assertNotNull(foundPersonOne.getAddress());
		assertNotNull(foundPersonOne.getGender());

		assertTrue(foundPersonOne.getEnabled());
		
		assertEquals(275, foundPersonOne.getId());	
		assertEquals("Alyse", foundPersonOne.getFirstName());
		assertEquals("91 Bunker Hill Drive", foundPersonOne.getAddress());
		assertEquals("Female", foundPersonOne.getGender());
	}

	@Test
	@Order(7)
	public void testFindByName() throws JsonMappingException, JsonProcessingException {		
		var wrapper = given().spec(specification)
				.config(
						RestAssuredConfig
							.config()
							.encoderConfig(EncoderConfig.encoderConfig()
								.encodeContentTypeAs(
									TestConfigs.CONTENT_YAML, ContentType.TEXT)))
				.contentType(TestConfigs.CONTENT_YAML)
				.accept(TestConfigs.CONTENT_YAML)
				.pathParam("firstName", "Alyse")
				.queryParam("page", 0, "size", 6, "direction", "asc")
						.when()
						.get("findPersonsByName/{firstName}")
					.then()
						.statusCode(200)
							.extract()
							.body()
							.as(PagedModelPerson.class, objectMapper);
		
		var people = wrapper.getContent();
		
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
			.config(
				RestAssuredConfig
					.config()
					.encoderConfig(EncoderConfig.encoderConfig()
						.encodeContentTypeAs(
							TestConfigs.CONTENT_YAML,
							ContentType.TEXT)))
			.contentType(TestConfigs.CONTENT_YAML)
			.accept(TestConfigs.CONTENT_YAML)
				.when()
				.get()
			.then()
				.statusCode(403);
	}

	@Test
	@Order(9)
	public void testHATEOAS() throws JsonMappingException, JsonProcessingException {		
		var unthreatedContent = given().spec(specification).config(
                        RestAssuredConfig.config()
                            .encoderConfig(EncoderConfig.encoderConfig()
                                    .encodeContentTypeAs(TestConfigs.CONTENT_YAML, ContentType.TEXT)))
                .contentType(TestConfigs.CONTENT_YAML)
				.accept(TestConfigs.CONTENT_YAML)
            	.queryParams("page", 3 , "size", 10, "direction", "asc")
                    .when()
                    .get()
                .then()
                    .statusCode(200)
                .extract()
                    .body()
						.asString();
		
		var content = unthreatedContent.replace("\n", "").replace("\r", "");		
		/*assertTrue(content.contains("rel: \"self\"	href: \"http://localhost:8888/api/person/v1/275\"")); //TESTAR
		assertTrue(content.contains("rel: \"self\"	href: \"http://localhost:8888/api/person/v1/58\"")); //TESTAR
		assertTrue(content.contains("rel: \"self\"	href: \"http://localhost:8888/api/person/v1/482\""));*/ //TESTAR
		
		assertTrue(content.contains("rel: \"first\"  href: \"http://localhost:8888/api/person/v1?direction=asc&page=0&size=10&sort=firstName,asc\""));
		//assertTrue(content.contains("rel: \"prev\"  href: \"http://localhost:8888/api/person/v1?direction=asc&page=2&size=12&sort=firstName,asc\"")); //TESTAR
		assertTrue(content.contains("rel: \"self\"  href: \"http://localhost:8888/api/person/v1?page=3&size=10&direction=asc\""));
		assertTrue(content.contains("rel: \"next\"  href: \"http://localhost:8888/api/person/v1?direction=asc&page=4&size=10&sort=firstName,asc\""));
		assertTrue(content.contains("rel: \"last\"  href: \"http://localhost:8888/api/person/v1?direction=asc&page=100&size=10&sort=firstName,asc\""));
		
		//assertTrue(content.contains("page:  size: 12  totalElements: 1005  totalPages: 84  number: 3")); //TESTAR
	}
	
	private void mockPerson() {
		person.setFirstName("Rodrigo");
		person.setLastName("Oliveira");
		person.setAddress("Barueri - SP");
		person.setGender("Male");
		person.setEnabled(true);
	}
}