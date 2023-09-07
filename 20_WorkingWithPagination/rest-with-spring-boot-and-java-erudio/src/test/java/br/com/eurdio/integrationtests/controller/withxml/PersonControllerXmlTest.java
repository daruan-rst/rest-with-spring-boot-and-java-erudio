package br.com.eurdio.integrationtests.controller.withxml;

import br.com.eurdio.configs.TestConfigs;
import br.com.eurdio.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.eurdio.integrationtests.vo.AccountCredentialsVO;
import br.com.eurdio.integrationtests.vo.PersonVO;
import br.com.eurdio.integrationtests.vo.TokenVO;
import br.com.eurdio.integrationtests.vo.pagedModels.PagedModelPerson;
import br.com.eurdio.integrationtests.vo.wrappers.WrapperPersonVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static br.com.eurdio.configs.TestConfigs.*;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonControllerXmlTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;

    private static XmlMapper objectMapper;

    private static PersonVO person;

    @BeforeAll
    public static void setUp(){
       objectMapper = new XmlMapper();
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
                    .contentType(CONTENT_TYPE_XML)
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
                        .contentType(CONTENT_TYPE_XML)
                        .accept(CONTENT_TYPE_XML)
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
                .contentType(CONTENT_TYPE_XML)
                .accept(CONTENT_TYPE_XML)
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
                .contentType(CONTENT_TYPE_XML)
                .accept(CONTENT_TYPE_XML)
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
                .contentType(CONTENT_TYPE_XML)
                .accept(CONTENT_TYPE_XML)
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
                .contentType(CONTENT_TYPE_XML)
                .accept(CONTENT_TYPE_XML)
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
                    .contentType(CONTENT_TYPE_XML)
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
                .contentType(CONTENT_TYPE_XML)
                .queryParams("page",3,"size",10,"direction","asc")
                .accept(CONTENT_TYPE_XML)
                .header(TestConfigs.HEADER_PARAM_ORIGIN, ORIGIN_ERUDIO)
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        assertNotNull(content);

        PagedModelPerson wrapper = objectMapper.readValue(content, PagedModelPerson.class);

        var allPeople = wrapper.getContent();

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
                .contentType(CONTENT_TYPE_XML)
                 .accept(CONTENT_TYPE_XML)
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
                .contentType(CONTENT_TYPE_XML)
                .accept(CONTENT_TYPE_XML)
                .pathParam("firstName", "ayr")
                .queryParams("page",0,"size",6,"direction","desc")
                .header(TestConfigs.HEADER_PARAM_ORIGIN, ORIGIN_ERUDIO)
                .when()
                .get("findPersonByName/{firstName}")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        assertNotNull(content);

        PagedModelPerson wrapper = objectMapper.readValue(content, PagedModelPerson.class);

        var peopleList = wrapper.getContent();

        PersonVO thisPerson = peopleList.get(0);

        Assertions.assertEquals("Ayrton", thisPerson.getFirstName());
        Assertions.assertEquals("Senna", thisPerson.getLastName());
        Assertions.assertEquals("São Paulo", thisPerson.getAddress());
        Assertions.assertEquals("Male", thisPerson.getGender());
        Assertions.assertTrue(thisPerson.getEnabled());

    }





    private void mockPerson() {
        person.setFirstName("Richard");
        person.setLastName("Stallman");
        person.setAddress("New York City, New York, US");
        person.setGender("Male");
        person.setEnabled(true);
    }

}
