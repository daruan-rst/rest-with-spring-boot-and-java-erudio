package br.com.eurdio.services;

import br.com.eurdio.model.Book;
import br.com.eurdio.repository.BookRepository;
import br.com.eurdio.unitTests.mapper.mocks.MockBook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    private MockBook util;

    @InjectMocks
    private BookService service;

    @Mock
    private BookRepository repository;

    @BeforeEach
    void setUp(){
     util = new MockBook();
    MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll() {
        Mockito.when(repository.findAll()).thenReturn(util.mockBookList());
        List<Book> listOfBooks = service.findAll();
        Assertions.assertEquals(5, listOfBooks.size());
        Book firstBook = listOfBooks.get(0);
        Assertions.assertEquals(1,firstBook.getId());
        Assertions.assertEquals("For Whom The Bell Toes",firstBook.getTitle());
        Assertions.assertEquals("Ernest Hemingway",firstBook.getAuthor());
        Assertions.assertEquals(BigDecimal.valueOf(10L),firstBook.getPrice());
        Assertions.assertEquals(Date.valueOf(LocalDateTime.of(1940, 10, 21,12,0).toLocalDate()),firstBook.getLaunchDate());
    }

    @Test
    void testFindById() {
        Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.of(util.mockBookEntity(1L)));
        Book book = service.findById(1L);
        Assertions.assertEquals(1,book.getId());
        Assertions.assertEquals("For Whom The Bell Toes",book.getTitle());
        Assertions.assertEquals("Ernest Hemingway",book.getAuthor());
        Assertions.assertEquals(BigDecimal.valueOf(10L),book.getPrice());
        Assertions.assertEquals(Date.valueOf(LocalDateTime.of(1940, 10, 21,12,0).toLocalDate()),book.getLaunchDate());
    }

    @Test
    void testCreateBook() {
        Book toBePersistedBook = util.mockBookEntity(1L);
        Mockito.when(repository.save(toBePersistedBook)).thenReturn(util.mockBookEntity(1L));
        Book book = service.createBook(toBePersistedBook);
        Assertions.assertEquals(1,book.getId());
        Assertions.assertEquals("For Whom The Bell Toes",book.getTitle());
        Assertions.assertEquals("Ernest Hemingway",book.getAuthor());
        Assertions.assertEquals(BigDecimal.valueOf(10L),book.getPrice());
        Assertions.assertEquals(Date.valueOf(LocalDateTime.of(1940, 10, 21,12,0).toLocalDate()),book.getLaunchDate());
    }

    @Test
    void testUpdateBook() {
        Book toBeUpdatedBook = util.mockBookEntity(1L);
        Book secondMock = util.mockBookEntity(3L);
        secondMock.setAuthor("Wolfgang von Goethe");
        secondMock.setTitle("Faust: eine Tragödie");
        secondMock.setLaunchDate(Date.valueOf(LocalDateTime.of(1806, 10, 21,12,0).toLocalDate()));
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(toBeUpdatedBook));
        Mockito.when(repository.save(any())).thenReturn(secondMock);
        Book book = service.updateBook(toBeUpdatedBook, 1L);
        Assertions.assertEquals(3,book.getId());
        Assertions.assertEquals("Faust: eine Tragödie",book.getTitle());
        Assertions.assertEquals("Wolfgang von Goethe",book.getAuthor());
        Assertions.assertEquals(BigDecimal.valueOf(30L),book.getPrice());
        Assertions.assertEquals(Date.valueOf(LocalDateTime.of(1806, 10, 21,12,0).toLocalDate()),book.getLaunchDate());
    }

    @Test
    void testDeleteBookById() {
        Book book = util.mockBookEntity(1L);

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(book));

        service.deleteBookById(1L);
    }
    
}