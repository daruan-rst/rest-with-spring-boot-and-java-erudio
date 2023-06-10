package br.com.eurdio.services;

import br.com.eurdio.controller.BookController;
import br.com.eurdio.exceptions.ResourceNotFoundException;
import br.com.eurdio.model.Book;
import br.com.eurdio.repository.BookRepository;

import java.util.List;
import java.util.logging.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    private Logger logger = Logger.getLogger(BookService.class.getName());

    public List<Book> findAll(){
        List<Book> books = bookRepository.findAll();
        books.forEach(b -> {
            try {
                b.add(linkTo(methodOn(BookController.class).findById(b.getId())).withSelfRel());
            }catch (Exception e){
                throw new RuntimeException(e);
            }
        });
        return books;
    }

    public Book findById(Long id){
        logger.info(String.format("Searching book by id: %s", id));
        Book book  = bookRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No book found for this ID"));
        book.add(linkTo(methodOn(BookController.class).findById(book.getId())).withSelfRel());
        return book;
    }

    public Book createBook(Book book){
        logger.info("Creating book");
        book = bookRepository.save(book);
        book.add(linkTo(methodOn(BookController.class).findById(book.getId())).withSelfRel());
        return book;
    }

    public Book updateBook(Book oldBook, Long id){
        logger.info("Updating book");
        Book book = bookRepository.findById(id).orElseThrow();
        book.setTitle(oldBook.getTitle());
        book.setPrice(oldBook.getPrice());
        book.setLaunchDate(oldBook.getLaunchDate());
        book.setAuthor(oldBook.getAuthor());
        bookRepository.save(book);
        book.add(linkTo(methodOn(BookController.class).findById(id)).withSelfRel());
        return book;
    }

    public void deleteBookById(Long id){
        logger.info("Deleting book by id");
        Book book = bookRepository.findById(id).orElseThrow();
        book.add(linkTo(methodOn(BookController.class).findById(id)).withSelfRel());
        bookRepository.deleteById(id);
    }


}
