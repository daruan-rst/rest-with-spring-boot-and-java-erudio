package br.com.eurdio.unitTests.mapper.mocks;

import br.com.eurdio.model.Book;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MockBook {

    public List<Book> mockBookList(){
        Function<Long, Book> mockABook = id -> mockBookEntity(id);
        List<Long> listOfIds = List.of(1L,2L,3L,4L,5L);
        List<Book> bookList = listOfIds.stream().map(mockABook).collect(Collectors.toList());
        return bookList;
    }

    public Book mockBookEntity(Long id){
        Book book = new Book();
        book.setId(id);
        book.setAuthor("Ernest Hemingway");
        book.setTitle("For Whom The Bell Toes");
        book.setPrice(new BigDecimal(id*10));
        book.setLaunchDate(LocalDateTime.of(1940, 10, 21,12,0));
        return book;
    }
}
