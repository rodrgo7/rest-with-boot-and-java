package com.oliveiradev.tests.integrationstests.controller.withjson;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.List;

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
import com.oliveiradev.tests.integrations.vo.BookVO;
import com.oliveiradev.tests.integrations.vo.wrappers.WrapperBookVO;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class BookControllerJsonTest extends AbstractIntegrationTest {	
	private static RequestSpecification specification;
	private static ObjectMapper objectMapper;

	private static BookVO book;
	
	@BeforeAll
	public static void setup() {
		objectMapper = new ObjectMapper();
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		
		book = new BookVO();
	}
	
	@Test
	@Order(1)
	public void authorization() {
		AccountCredentialsVO user = new AccountCredentialsVO();
		user.setUsername("rodrigo");
        user.setPassword("admin123");
		
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
				.setBasePath("/api/book/v1")
				.setPort(TestConfigs.SERVER_PORT)
					.addFilter(new RequestLoggingFilter(LogDetail.ALL))
					.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();
	}
	
	@Test
	@Order(2)
	public void testCreate() throws JsonMappingException, JsonProcessingException {
		mockBook();
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_JSON)
					.body(book)
					.when()
					.post()
				.then()
					.statusCode(200)
						.extract()
						.body()
							.asString();
		
		BookVO books = objectMapper.readValue(content, BookVO.class);
		book = books;

		assertNotNull(book.getId());
		assertNotNull(book.getTitle());
		assertNotNull(book.getAuthor());
		assertNotNull(book.getPrice());
		
		assertTrue(book.getId() > 0);
		
		assertEquals("Working effectively with legacy code", book.getTitle());
		assertEquals("Viktor Mayer-Schonberger e Kenneth Kukier", book.getAuthor());
		assertEquals(49.0, book.getPrice());
	}

	
	@Test
    @Order(3)
    public void testUpdate() throws JsonMappingException, JsonProcessingException {        
        book.setTitle("Working effectively with legacy code - Updated");

        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_JSON)
                    .body(book)
                    .when()
                    .put()
                .then()
                    .statusCode(200)
                        .extract()
                        .body()
                            .asString();
        
        BookVO bookUpdated = objectMapper.readValue(content, BookVO.class);
        
        assertNotNull(bookUpdated.getId());
        assertNotNull(bookUpdated.getTitle());
        assertNotNull(bookUpdated.getAuthor());
        assertNotNull(bookUpdated.getPrice());

        assertEquals(bookUpdated.getId(), book.getId());
        assertEquals("Working effectively with legacy code - Updated", bookUpdated.getTitle());
        assertEquals("Viktor Mayer-Schonberger e Kenneth Kukier", bookUpdated.getAuthor());
        assertEquals(49.0, bookUpdated.getPrice());
    }

	@Test
	@Order(4)
	public void testFindById() throws JsonMappingException, JsonProcessingException {
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_JSON)
					.pathParam("id", book.getId())
					.when()
					.get("{id}")
				.then()
					.statusCode(200)
						.extract()
						.body()
							.asString();
		
		BookVO foundBook = objectMapper.readValue(content, BookVO.class);

		assertNotNull(foundBook.getId());		
		assertNotNull(foundBook.getTitle());
		assertNotNull(foundBook.getAuthor());		
		assertNotNull(foundBook.getPrice());
		
		assertEquals(foundBook.getId(), book.getId());
		assertEquals("Working effectively with legacy code - Updated", book.getTitle());
		assertEquals("Viktor Mayer-Schonberger e Kenneth Kukier", book.getAuthor());
		assertEquals(49., book.getPrice());
	}

	
	@Test
    @Order(5)
    public void testDelete() {
        given().spec(specification)
            .contentType(TestConfigs.CONTENT_JSON)
                .pathParam("id", book.getId())
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
                    //.queryParams("page", 0 , "limit", 12, "direction", "asc")
                    .when()
                    .get()
                .then()
                    .statusCode(200)
                .extract()
                    .body()
                .asString();
        
			WrapperBookVO wrapper = objectMapper.readValue(content, WrapperBookVO.class);
			List<BookVO> books = wrapper.getEmbedded().getBooks();
		
        BookVO foundBookOne = books.get(0);
        
        assertNotNull(foundBookOne.getId());
        assertNotNull(foundBookOne.getTitle());
        assertNotNull(foundBookOne.getAuthor());
        assertNotNull(foundBookOne.getPrice());

        assertTrue(foundBookOne.getId() > 0);

        assertEquals("Big Data: como extrair volume, variedade, velocidade e valor da avalanche de informação cotidiana", foundBookOne.getTitle());
        assertEquals("Viktor Mayer-Schonberger e Kenneth Kukier", foundBookOne.getAuthor());
        assertEquals(54.0, foundBookOne.getPrice());

	}

	@Test
	@Order(7)
	public void testHATEOAS() throws JsonMappingException, JsonProcessingException {		
		var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_JSON)
            	.queryParams("page", 0 , "size", 12, "direction", "asc")
                    .when()
                    .get()
                .then()
                    .statusCode(200)
                .extract()
                    .body()
                .asString();
		
		assertTrue(content.contains("\"_links\":{\"self\":{\"href\":\"http://localhost:8888/api/book/v1/3\"}}}"));
		assertTrue(content.contains("\"_links\":{\"self\":{\"href\":\"http://localhost:8888/api/book/v1/5\"}}}"));
		assertTrue(content.contains("\"_links\":{\"self\":{\"href\":\"http://localhost:8888/api/book/v1/7\"}}}"));
		assertTrue(content.contains("{\"first\":{\"href\":\"http://localhost:8888/api/book/v1?direction=asc&page=0&size=12&sort=title,asc\"}"));
		assertTrue(content.contains("\"self\":{\"href\":\"http://localhost:8888/api/book/v1?page=0&size=12&direction=asc\"}"));
		assertTrue(content.contains("\"next\":{\"href\":\"http://localhost:8888/api/book/v1?direction=asc&page=1&size=12&sort=title,asc\"}"));
		assertTrue(content.contains("\"last\":{\"href\":\"http://localhost:8888/api/book/v1?direction=asc&page=1&size=12&sort=title,asc\"}}"));
		assertTrue(content.contains("\"page\":{\"size\":12,\"totalElements\":15,\"totalPages\":2,\"number\":0}}"));
	}
	
	private void mockBook() {
        book.setTitle("Working effectively with legacy code");
        book.setAuthor("Viktor Mayer-Schonberger e Kenneth Kukier");
        book.setPrice(Double.valueOf(49.0));
        book.setLaunchDate(new Date());
    }  
}