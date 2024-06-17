package br.com.eurdio.unitTests.mapper.mocks;

import br.com.eurdio.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MockBook {

    public Page<Book>  mockBookList(){
        Function<Long, Book> mockABook = id -> mockBookEntity(id);
        List<Long> listOfIds = List.of(1L,2L,3L,4L,5L);
        List<Book> bookList = listOfIds.stream().map(mockABook).collect(Collectors.toList());
        Pageable pageable = Pageable.ofSize(bookList.size());
        List<Book> pagedList = bookList.stream()
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .collect(Collectors.toList());

        Page<Book> page = new PageImpl<>(pagedList, pageable, bookList.size());
        return page;
    }

    public Book mockBookEntity(Long id){
        Book book = new Book();
        book.setId(id);
        book.setAuthor("Ernest Hemingway");
        book.setTitle("For Whom The Bell Toes");
        book.setPrice(new BigDecimal(id*10));
        book.setLaunchDate(Date.valueOf(LocalDateTime.of(1940, 10, 21,12,0).toLocalDate()));
        return book;
    }
}
