package br.com.eurdio.integrationtests.controller.withjson;

import br.com.eurdio.configs.TestConfigs;
import br.com.eurdio.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.eurdio.integrationtests.vo.AccountCredentialsVO;
import br.com.eurdio.integrationtests.vo.Book;
import br.com.eurdio.integrationtests.vo.TokenVO;
import br.com.eurdio.integrationtests.vo.wrappers.WrapperBook;
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

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static br.com.eurdio.configs.TestConfigs.CONTENT_TYPE_JSON;
import static br.com.eurdio.configs.TestConfigs.ORIGIN_ERUDIO;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookControllerJsonTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;

    private static ObjectMapper objectMapper;

    private static Book book;

    @BeforeAll
    public static void setUp(){
       objectMapper = new ObjectMapper();
       objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
//       DateFormat format = objectMapper.getDateFormat();
       objectMapper.setTimeZone(TimeZone.getDefault());
//       objectMapper.setLocale(Locale.forLanguageTag("BRL"));
       book = new Book();
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

        var content = given()
                        .spec(specification)
                        .contentType(TestConfigs.CONTENT_TYPE_JSON)
                        .header(TestConfigs.HEADER_PARAM_ORIGIN, ORIGIN_ERUDIO)
                        .body(book)
                        .when()
                            .post()
                        .then()
                        .statusCode(200)
                            .extract()
                                .body()
                                    .asString();

            Book createdBook = objectMapper.readValue(content, Book.class);
//            book = createdBook;

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

        var content = given()
                .spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .header(TestConfigs.HEADER_PARAM_ORIGIN, ORIGIN_ERUDIO)
                .pathParam("id", book.getId())
                .when()
                .get("{id}")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        Book persistedBook = objectMapper.readValue(content, Book.class);
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
//        book.setLastName("Martin");

        var content = given()
                .spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .header(TestConfigs.HEADER_PARAM_ORIGIN, ORIGIN_ERUDIO)
                .pathParam("id", book.getId())
                .body(book)
                .when()
                .put("{id}")
                .then()
                .statusCode(200)
                .extract()
                .asString();

        Book persistedBook = objectMapper.readValue(content, Book.class);
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
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
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
                .contentType(CONTENT_TYPE_JSON)
                .queryParams("page",0,"size",10,"direction","asc")
                .header(TestConfigs.HEADER_PARAM_ORIGIN, ORIGIN_ERUDIO)
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        assertNotNull(content);

        WrapperBook bookWrapper = objectMapper.readValue(content, WrapperBook.class);

        List<Book> allBook = bookWrapper.getEmbedded().getBooks();
        Book lastBook = allBook.get(allBook.size()-1);

        Assertions.assertEquals(new BigDecimal("54.00"), lastBook.getPrice());
        Assertions.assertEquals("Aguinaldo Aragon Fernandes e Vladimir Ferraz de Abreu", lastBook.getAuthor());
        Assertions.assertEquals("Implantando a governança de TI", lastBook.getTitle());
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


    private void mockBook() {
        book.setId(5);
        book.setAuthor("Graciliano Ramos");
        book.setPrice(new BigDecimal("70.12"));
        book.setLaunchDate(new Date("1938/05/04"));
        book.setTitle("Vidas Secas");
    }

}
