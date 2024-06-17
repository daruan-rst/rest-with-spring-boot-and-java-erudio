package br.com.eurdio.integrationtests.vo.wrappers;

import br.com.eurdio.integrationtests.vo.Book;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@XmlRootElement
public class BookEmbedded implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("bookList")
    private List<Book> books;

    public BookEmbedded(){
    }

    public List<Book> getBooks() {
        return books;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BookEmbedded that = (BookEmbedded) o;

        return Objects.equals(books, that.books);
    }

    @Override
    public int hashCode() {
        return books != null ? books.hashCode() : 0;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }
}
