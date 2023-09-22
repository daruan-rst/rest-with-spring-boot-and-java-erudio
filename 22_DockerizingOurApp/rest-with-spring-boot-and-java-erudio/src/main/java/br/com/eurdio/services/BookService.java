package br.com.eurdio.services;

import br.com.eurdio.controller.BookController;
import br.com.eurdio.exceptions.ResourceNotFoundException;
import br.com.eurdio.model.Book;
import br.com.eurdio.repository.BookRepository;

import java.util.List;
import java.util.logging.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    PagedResourcesAssembler<Book> assembler;

    private Logger logger = Logger.getLogger(BookService.class.getName());

    public PagedModel<EntityModel<Book>> findAll(Pageable pageable){

        var books = bookRepository.findAll(pageable);

        books.forEach(b -> {
            b.add(linkTo(methodOn(BookController.class).findById(b.getId())).withSelfRel());
        });

        Link link = linkTo(methodOn(BookController.class)
                        .findAll(pageable.getPageNumber(),
                                pageable.getPageSize(),
                                pageable.getSort().toString()))
                                .withSelfRel();

        return assembler.toModel(books, link);
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
        book = bookRepository.save(book);
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
