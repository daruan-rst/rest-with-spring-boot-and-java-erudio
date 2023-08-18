package br.com.eurdio.integrationtests.controller.withYaml;

import br.com.eurdio.configs.TestConfigs;
import br.com.eurdio.integrationtests.controller.withYaml.mapper.YMLMapper;
import br.com.eurdio.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.eurdio.integrationtests.vo.AccountCredentialsVO;
import br.com.eurdio.integrationtests.vo.PersonVO;
import br.com.eurdio.integrationtests.vo.TokenVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import static br.com.eurdio.configs.TestConfigs.CONTENT_TYPE_YML;
import static br.com.eurdio.configs.TestConfigs.ORIGIN_ERUDIO;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonControllerYmlTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;

    private static YMLMapper objectMapper;

    private static PersonVO person;

    @BeforeAll
    public static void setUp(){
       objectMapper = new YMLMapper();

       person = new PersonVO();
    }

    @Test
    @Order(0)
    public void authorization() {
        AccountCredentialsVO user = new AccountCredentialsVO("daruan", "admin123");

        var accessToken = given()
                .config(
                        RestAssuredConfig
                                .config()
                                .encoderConfig(EncoderConfig.encoderConfig()
                                        .encodeContentTypeAs(
                                                TestConfigs.CONTENT_TYPE_YML,
                                                ContentType.TEXT)))
                .accept(TestConfigs.CONTENT_TYPE_YML)
                .basePath("auth/signin")
                    .port(TestConfigs.SERVER_PORT)
                    .contentType(CONTENT_TYPE_YML)
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

        PersonVO createdPerson = given()
                .spec(specification)
                .config(
                        RestAssuredConfig
                                .config()
                                .encoderConfig(EncoderConfig.encoderConfig()
                                        .encodeContentTypeAs(
                                                TestConfigs.CONTENT_TYPE_YML,
                                                ContentType.TEXT)))
                .contentType(CONTENT_TYPE_YML)
                .accept(CONTENT_TYPE_YML)
                        .header(TestConfigs.HEADER_PARAM_ORIGIN, ORIGIN_ERUDIO)
                        .body(person, objectMapper)
                        .when()
                            .post()
                        .then()
                        .statusCode(200)
                            .extract()
                                .body()
                                    .as(PersonVO.class, objectMapper);

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

        PersonVO persistedPerson = given()
                .spec(specification)
                .config(
                        RestAssuredConfig
                                .config()
                                .encoderConfig(EncoderConfig.encoderConfig()
                                        .encodeContentTypeAs(
                                                TestConfigs.CONTENT_TYPE_YML,
                                                ContentType.TEXT)))
                .contentType(CONTENT_TYPE_YML)
                .accept(CONTENT_TYPE_YML)
                .header(TestConfigs.HEADER_PARAM_ORIGIN, ORIGIN_ERUDIO)
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

        PersonVO persistedPerson = given()
                .spec(specification)
                .config(
                        RestAssuredConfig
                                .config()
                                .encoderConfig(EncoderConfig.encoderConfig()
                                        .encodeContentTypeAs(
                                                TestConfigs.CONTENT_TYPE_YML,
                                                ContentType.TEXT)))
                .contentType(CONTENT_TYPE_YML)
                .accept(CONTENT_TYPE_YML)
                .header(TestConfigs.HEADER_PARAM_ORIGIN, ORIGIN_ERUDIO)
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

        PersonVO persistedPerson = given()
                .spec(specification)
                .config(
                        RestAssuredConfig
                                .config()
                                .encoderConfig(EncoderConfig.encoderConfig()
                                        .encodeContentTypeAs(
                                                TestConfigs.CONTENT_TYPE_YML,
                                                ContentType.TEXT)))
                .contentType(CONTENT_TYPE_YML)
                .accept(CONTENT_TYPE_YML)
                .header(TestConfigs.HEADER_PARAM_ORIGIN, ORIGIN_ERUDIO)
                .body(person, objectMapper )
                .when()
                .put()
                .then()
                .statusCode(200)
                .extract()
                .as(PersonVO.class, objectMapper);

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
                .config(
                        RestAssuredConfig
                                .config()
                                .encoderConfig(EncoderConfig.encoderConfig()
                                        .encodeContentTypeAs(
                                                TestConfigs.CONTENT_TYPE_YML,
                                                ContentType.TEXT)))
                .contentType(CONTENT_TYPE_YML)
                .accept(CONTENT_TYPE_YML)
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
                    .contentType(CONTENT_TYPE_YML)
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

        PersonVO[] allPeople = given()
                .spec(specification)
                .config(
                        RestAssuredConfig
                                .config()
                                .encoderConfig(EncoderConfig.encoderConfig()
                                        .encodeContentTypeAs(
                                                TestConfigs.CONTENT_TYPE_YML,
                                                ContentType.TEXT)))
                .contentType(CONTENT_TYPE_YML)
                .accept(CONTENT_TYPE_YML)
                .header(TestConfigs.HEADER_PARAM_ORIGIN, ORIGIN_ERUDIO)
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(PersonVO[].class, objectMapper);

        assertNotNull(allPeople);

        PersonVO lastPerson = allPeople[allPeople.length-1];

        Assertions.assertEquals("Carrie", lastPerson.getFirstName());
        Assertions.assertEquals("Fisher", lastPerson.getLastName());
        Assertions.assertEquals("Los Angeles, California", lastPerson.getAddress());
        Assertions.assertEquals("Female", lastPerson.getGender());

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
                .contentType(CONTENT_TYPE_YML)
                 .accept(CONTENT_TYPE_YML)
                .header(TestConfigs.HEADER_PARAM_ORIGIN, ORIGIN_ERUDIO)
                .when()
                .get()
                .then()
                .statusCode(403)
                .extract()
                .body()
                .asString();



    }


    private void mockPerson() {
        person.setFirstName("Richard");
        person.setLastName("Stallman");
        person.setAddress("New York City, New York, US");
        person.setGender("Male");
    }

}
