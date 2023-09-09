package br.com.eurdio.integrationtests.controller.withjson;

import br.com.eurdio.configs.TestConfigs;
import br.com.eurdio.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.eurdio.integrationtests.vo.AccountCredentialsVO;
import br.com.eurdio.integrationtests.vo.PersonVO;
import br.com.eurdio.integrationtests.vo.TokenVO;
import br.com.eurdio.integrationtests.vo.wrappers.WrapperPersonVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import static br.com.eurdio.configs.TestConfigs.CONTENT_TYPE_JSON;
import static br.com.eurdio.configs.TestConfigs.ORIGIN_ERUDIO;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonControllerJsonTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;

    private static ObjectMapper objectMapper;

    private static PersonVO person;

    @BeforeAll
    public static void setUp(){
       objectMapper = new ObjectMapper();
       objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

       person = new PersonVO();
    }

    @Test
    @Order(0)
    public void authorization() {
        AccountCredentialsVO user = new AccountCredentialsVO("daruan", "admin123");

        var accessToken = given()
                .basePath("auth/signin")
                    .port(TestConfigs.SERVER_PORT)
                    .contentType(TestConfigs.CONTENT_TYPE_JSON)
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
                .setBasePath("api/person/v1")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();
    }


    @Test
    @Order(1)
    public void testCreate() throws JsonProcessingException {
        mockPerson();

        var content = given()
                        .spec(specification)
                        .contentType(TestConfigs.CONTENT_TYPE_JSON)
                        .header(TestConfigs.HEADER_PARAM_ORIGIN, ORIGIN_ERUDIO)
                        .body(person)
                        .when()
                            .post()
                        .then()
                        .statusCode(200)
                            .extract()
                                .body()
                                    .asString();

            PersonVO createdPerson = objectMapper.readValue(content, PersonVO.class);
            person = createdPerson;

            assertNotNull(createdPerson);
            assertTrue(createdPerson.getId() > 0);
            assertNotNull(createdPerson.getFirstName());
            assertNotNull(createdPerson.getLastName());
            assertNotNull(createdPerson.getAddress());
            assertNotNull(createdPerson.getGender());
             assertTrue(createdPerson.getEnabled());

            assertEquals("Richard" , createdPerson.getFirstName());
            assertEquals("Stallman" , createdPerson.getLastName());
            assertEquals("New York City, New York, US", createdPerson.getAddress());
            assertEquals("Male"  , createdPerson.getGender());


    }

    @Test
    @Order(2)
    public void testFindById() throws JsonProcessingException {

        var content = given()
                .spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .header(TestConfigs.HEADER_PARAM_ORIGIN, ORIGIN_ERUDIO)
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
        assertTrue(persistedPerson.getId() > 0);
        assertNotNull(persistedPerson.getFirstName());
        assertNotNull(persistedPerson.getLastName());
        assertNotNull(persistedPerson.getAddress());
        assertNotNull(persistedPerson.getGender());
        assertTrue(persistedPerson.getEnabled());

        assertEquals("Richard" , persistedPerson.getFirstName());
        assertEquals("Stallman" , persistedPerson.getLastName());
        assertEquals("New York City, New York, US", persistedPerson.getAddress());
        assertEquals("Male"  , persistedPerson.getGender());


    }

    @Test
    @Order(3)
    public void testDisable() throws JsonProcessingException {

        var content = given()
                .spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .header(TestConfigs.HEADER_PARAM_ORIGIN, ORIGIN_ERUDIO)
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
        assertTrue(persistedPerson.getId() > 0);
        assertNotNull(persistedPerson.getFirstName());
        assertNotNull(persistedPerson.getLastName());
        assertNotNull(persistedPerson.getAddress());
        assertNotNull(persistedPerson.getGender());
        assertFalse(persistedPerson.getEnabled());

        assertEquals("Richard" , persistedPerson.getFirstName());
        assertEquals("Stallman" , persistedPerson.getLastName());
        assertEquals("New York City, New York, US", persistedPerson.getAddress());
        assertEquals("Male"  , persistedPerson.getGender());


    }

    @Test
    @Order(4)
    public void testUpdate() throws JsonProcessingException {
        person.setFirstName("Robert");
        person.setLastName("Martin");

        var content = given()
                .spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .header(TestConfigs.HEADER_PARAM_ORIGIN, ORIGIN_ERUDIO)
                .body(person)
                .when()
                .put()
                .then()
                .statusCode(200)
                .extract()
                .asString();

        PersonVO persistedPerson = objectMapper.readValue(content, PersonVO.class);
        person = persistedPerson;

        assertNotNull(persistedPerson);
        assertTrue(persistedPerson.getId() > 0);
        assertNotNull(persistedPerson.getFirstName());
        assertNotNull(persistedPerson.getLastName());
        assertNotNull(persistedPerson.getAddress());
        assertNotNull(persistedPerson.getGender());
        assertFalse(persistedPerson.getEnabled());

        assertEquals("Robert" , persistedPerson.getFirstName());
        assertEquals("Martin" , persistedPerson.getLastName());
        assertEquals("New York City, New York, US", persistedPerson.getAddress());
        assertEquals("Male"  , persistedPerson.getGender());


    }

    @Test
    @Order(5)
    public void testDelete() {

        given()
                .spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .header(TestConfigs.HEADER_PARAM_ORIGIN, ORIGIN_ERUDIO)
                .pathParam("id", person.getId())
                .when()
                .delete("{id}")
                .then()
                .statusCode(204)
                .extract()
                .asString();


            var content = given()
                    .spec(specification)
                    .contentType(TestConfigs.CONTENT_TYPE_JSON)
                    .header(TestConfigs.HEADER_PARAM_ORIGIN, ORIGIN_ERUDIO)
                    .pathParam("id", person.getId())
                    .when()
                    .get("{id}")
                    .then()
                    .statusCode(404)
                    .extract()
                    .body()
                    .asString();

        Assertions.assertTrue(content.contains("No records found for this ID!"));

    }

    @Test
    @Order(6)
    public void testFindAll() throws JsonProcessingException {

        var content = given()
                .spec(specification)
                .contentType(CONTENT_TYPE_JSON)
                .queryParams("page",3,"size",10,"direction","asc")
                .header(TestConfigs.HEADER_PARAM_ORIGIN, ORIGIN_ERUDIO)
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        assertNotNull(content);

        WrapperPersonVO wrapper = objectMapper.readValue(content, WrapperPersonVO.class);

        var allPeople = wrapper.getEmbedded().getPersons();
        PersonVO lastPerson = allPeople.get(allPeople.size()-1);

        Assertions.assertEquals("Allyn", lastPerson.getFirstName());
        Assertions.assertEquals("Josh", lastPerson.getLastName());
        Assertions.assertEquals("119 Declaration Lane", lastPerson.getAddress());
        Assertions.assertEquals("Female", lastPerson.getGender());
        Assertions.assertFalse(lastPerson.getEnabled());

    }

    @Test
    @Order(7)
    public void testFindAllWithoutToken() throws JsonProcessingException {

        RequestSpecification specification = new RequestSpecBuilder()
                .addHeader(TestConfigs.HEADER_PARAM_AUTHORIZATION, "")
                .setBasePath("api/person/v1")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

         given()
                .spec(specification)
                .contentType(CONTENT_TYPE_JSON)
                .header(TestConfigs.HEADER_PARAM_ORIGIN, ORIGIN_ERUDIO)
                .when()
                .get()
                .then()
                .statusCode(403)
                .extract()
                .body()
                .asString();

    }

    @Test
    @Order(8)
    public void testFindByName() throws JsonProcessingException {

        var content = given()
                .spec(specification)
                .contentType(CONTENT_TYPE_JSON)
                .accept(CONTENT_TYPE_JSON)
                .pathParam("firstName","ayr")
                .queryParams("page",0,"size",6,"direction","asc")
                .header(TestConfigs.HEADER_PARAM_ORIGIN, ORIGIN_ERUDIO)
                .when()
                .get("findPersonByName/{firstName}")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        assertNotNull(content);

        WrapperPersonVO wrapper = objectMapper.readValue(content, WrapperPersonVO.class);

        var peopleList = wrapper.getEmbedded().getPersons();
        PersonVO thisPerson = peopleList.get(0);

        Assertions.assertEquals("Ayrton", thisPerson.getFirstName());
        Assertions.assertEquals("Senna", thisPerson.getLastName());
        Assertions.assertEquals("São Paulo", thisPerson.getAddress());
        Assertions.assertEquals("Male", thisPerson.getGender());
        Assertions.assertTrue(thisPerson.getEnabled());

    }

    @Test
    @Order(9)
    public void testHATEOAS() throws JsonProcessingException {

        var content = given()
                .spec(specification)
                .contentType(CONTENT_TYPE_JSON)
                .queryParams("page",3,"size",10,"direction","asc")
                .header(TestConfigs.HEADER_PARAM_ORIGIN, ORIGIN_ERUDIO)
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        assertNotNull(content);

        assertTrue(content.contains("\"_links\":{\"self\":{\"href\":\"http://localhost:8888/api/person/v1/842\"}}}"));
        assertTrue(content.contains("\"_links\":{\"self\":{\"href\":\"http://localhost:8888/api/person/v1/681\"}}}"));
        assertTrue(content.contains("\"_links\":{\"self\":{\"href\":\"http://localhost:8888/api/person/v1/418\"}}}"));

        assertTrue(content.contains("{\"first\":{\"href\":\"http://localhost:8888/api/person/v1?direction=asc&page=0&size=10&sort=firstName,asc\"}"));
        assertTrue(content.contains("\"prev\":{\"href\":\"http://localhost:8888/api/person/v1?direction=asc&page=2&size=10&sort=firstName,asc\"}"));
        assertTrue(content.contains("\"self\":{\"href\":\"http://localhost:8888/api/person/v1?page=3&size=10&direction=asc\"}"));
        assertTrue(content.contains("\"next\":{\"href\":\"http://localhost:8888/api/person/v1?direction=asc&page=4&size=10&sort=firstName,asc\"}"));
        assertTrue(content.contains("\"last\":{\"href\":\"http://localhost:8888/api/person/v1?direction=asc&page=101&size=10&sort=firstName,asc\"}}"));

        assertTrue(content.contains("\"page\":{\"size\":10,\"totalElements\":1011,\"totalPages\":102,\"number\":3}}"));

    }





    private void mockPerson() {
        person.setFirstName("Richard");
        person.setLastName("Stallman");
        person.setAddress("New York City, New York, US");
        person.setGender("Male");
        person.setEnabled(true);
    }

}
