package br.com.eurdio.integrationtests.controller.withYaml;

import br.com.eurdio.configs.TestConfigs;
import br.com.eurdio.integrationtests.controller.withYaml.mapper.YMLMapper;
import br.com.eurdio.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.eurdio.integrationtests.vo.AccountCredentialsVO;
import br.com.eurdio.integrationtests.vo.Book;
import br.com.eurdio.integrationtests.vo.TokenVO;
import br.com.eurdio.integrationtests.vo.pagedModels.PagedModelBook;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static br.com.eurdio.configs.TestConfigs.*;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookControllerYmlTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;

    private static YMLMapper objectMapper;

    private static Book book;

    @BeforeAll
    public static void setUp(){
       objectMapper = new YMLMapper();
       book = new Book();
    }

    @Test
    @Order(0)
    public void authorization() {
        AccountCredentialsVO user = new AccountCredentialsVO("daruan", "admin123");

        var accessToken = given()
                    .config(RestAssuredConfig
                            .config().encoderConfig(EncoderConfig
                                     .encoderConfig()
                                     .encodeContentTypeAs(CONTENT_TYPE_YML,
                                                        ContentType.TEXT))
                            )
                .basePath("auth/signin")
                    .port(TestConfigs.SERVER_PORT)
                    .contentType(TestConfigs.CONTENT_TYPE_XML)
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
                .setBasePath("api/book/v1")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();
    }


    @Test
    @Order(1)
    public void testCreate() throws JsonProcessingException {
        mockBook();

        Book createdBook = given()
                .config(RestAssuredConfig
                        .config().encoderConfig(EncoderConfig
                                .encoderConfig()
                                .encodeContentTypeAs(CONTENT_TYPE_YML,
                                        ContentType.TEXT)))
                        .spec(specification)
                        .contentType(CONTENT_TYPE_YML)
                        .header(TestConfigs.HEADER_PARAM_ORIGIN, ORIGIN_ERUDIO)
                        .body(book, objectMapper)
                        .when()
                            .post()
                        .then()
                        .statusCode(200)
                            .extract()
                                .body()
                                    .as(Book.class, objectMapper);


            assertNotNull(createdBook);
            assertTrue(createdBook.getId() > 0);
            assertNotNull(createdBook.getPrice());
            assertNotNull(createdBook.getAuthor());
            assertNotNull(createdBook.getTitle());
            assertNotNull(createdBook.getLaunchDate());

            assertEquals(new BigDecimal("70.12"), createdBook.getPrice());
            assertEquals("Graciliano Ramos", createdBook.getAuthor());
            assertEquals("Vidas Secas", createdBook.getTitle());
            assertEquals(new Date("1938/05/4"), createdBook.getLaunchDate());


    }

    @Test
    @Order(2)
    public void testFindById() throws JsonProcessingException {

        Book persistedBook = given()
                .spec(specification)
                .contentType(CONTENT_TYPE_YML)
                .header(TestConfigs.HEADER_PARAM_ORIGIN, ORIGIN_ERUDIO)
                .pathParam("id", book.getId())
                .when()
                .get("{id}")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(Book.class, objectMapper);

        book = persistedBook;

        assertNotNull(persistedBook);
        assertTrue(persistedBook.getId() > 0);
        assertNotNull(persistedBook.getPrice());
        assertNotNull(persistedBook.getAuthor());
        assertNotNull(persistedBook.getTitle());
        assertNotNull(persistedBook.getLaunchDate());

        assertEquals(new BigDecimal("70.12"), persistedBook.getPrice());
        assertEquals("Graciliano Ramos", persistedBook.getAuthor());
        assertEquals("Vidas Secas", persistedBook.getTitle());
        assertEquals(new Date("1938/05/4"), persistedBook.getLaunchDate());


    }

    @Test
    @Order(3)
    public void testUpdate() throws JsonProcessingException {
        book.setAuthor("João Roberto");

        Book persistedBook = given()
                .config(
                        RestAssuredConfig.config()
                                .encoderConfig(EncoderConfig.encoderConfig()
                                        .encodeContentTypeAs(CONTENT_TYPE_YML,
                                                ContentType.TEXT)))
                .spec(specification)
                .contentType(CONTENT_TYPE_YML)
                .header(TestConfigs.HEADER_PARAM_ORIGIN, ORIGIN_ERUDIO)
                .pathParam("id", book.getId())
                .body(book, objectMapper)
                .when()
                .put("{id}")
                .then()
                .statusCode(200)
                .extract()
                .as(Book.class, objectMapper);

        book = persistedBook;

        assertNotNull(persistedBook);
        assertTrue(persistedBook.getId() > 0);
        assertNotNull(persistedBook.getPrice());
        assertNotNull(persistedBook.getAuthor());
        assertNotNull(persistedBook.getTitle());
        assertNotNull(persistedBook.getLaunchDate());

        assertEquals(new BigDecimal("70.12"), persistedBook.getPrice());
        assertEquals("João Roberto", persistedBook.getAuthor());
        assertEquals("Vidas Secas", persistedBook.getTitle());
        assertEquals(new Date("1938/05/4"), persistedBook.getLaunchDate());


    }

    @Test
    @Order(4)
    public void testDelete() {

        given()
                .spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_XML)
                .header(TestConfigs.HEADER_PARAM_ORIGIN, ORIGIN_ERUDIO)
                .pathParam("id", book.getId())
                .when()
                .delete("{id}")
                .then()
                .statusCode(204)
                .extract()
                .asString();


    }

    @Test
    @Order(5)
    public void testFindAll() throws JsonProcessingException {

        var content = given()
                .spec(specification)
                .contentType(CONTENT_TYPE_YML)
                .accept(CONTENT_TYPE_YML)
                .header(TestConfigs.HEADER_PARAM_ORIGIN, ORIGIN_ERUDIO)
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(PagedModelBook.class, objectMapper);

        assertNotNull(content);

        List<Book> allBooks = content.getContent();

        Book lastBook = allBooks.get(allBooks.size() -1);

        Assertions.assertEquals(new BigDecimal("95.00"), lastBook.getPrice());
        Assertions.assertEquals("Richard Hunter e George Westerman", lastBook.getAuthor());
        Assertions.assertEquals("O verdadeiro valor de TI", lastBook.getTitle());
        Assertions.assertEquals(new Date("2017/11/07"), lastBook.getLaunchDate());

    }

    @Test
    @Order(6)
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
    @Order(7)
    public void testHATEOAS() throws JsonProcessingException {

        var unthreatedContent = given()
                .spec(specification)
                .contentType(CONTENT_TYPE_YML)
                .accept(CONTENT_TYPE_YML)
                .header(TestConfigs.HEADER_PARAM_ORIGIN, ORIGIN_ERUDIO)
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        assertNotNull(unthreatedContent);

        var content = unthreatedContent.replace("\n", "").replace("\r", "");

        assertTrue(content.contains("  - rel: \"self\"    href: \"http://localhost:8888/api/book/v1/7\""));
        assertTrue(content.contains("  - rel: \"self\"    href: \"http://localhost:8888/api/book/v1/8\""));
        assertTrue(content.contains("  - rel: \"self\"    href: \"http://localhost:8888/api/book/v1/4\""));

        assertTrue(content.contains("rel: \"first\"  href: \"http://localhost:8888/api/book/v1?direct=author%3A%20ASC&page=0&size=10&sort=author,asc\""));
        assertTrue(content.contains("rel: \"self\"  href: \"http://localhost:8888/api/book/v1?page=0&size=10&direct=author%3A%20ASC\""));
        assertTrue(content.contains("rel: \"next\"  href: \"http://localhost:8888/api/book/v1?direct=author%3A%20ASC&page=1&size=10&sort=author,asc\""));
        assertTrue(content.contains("rel: \"last\"  href: \"http://localhost:8888/api/book/v1?direct=author%3A%20ASC&page=1&size=10&sort=author,asc\""));

        assertTrue(content.contains("page:  size: 10  totalElements: 14  totalPages: 2  number: 0"));


    }


    private void mockBook() {
        book.setId(5);
        book.setAuthor("Graciliano Ramos");
        book.setPrice(new BigDecimal("70.12"));
        book.setLaunchDate(new Date("1938/05/04"));
        book.setTitle("Vidas Secas");
    }

}
