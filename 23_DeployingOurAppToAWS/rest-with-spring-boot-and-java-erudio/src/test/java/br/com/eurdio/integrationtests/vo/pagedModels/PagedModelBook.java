package br.com.eurdio.integrationtests.vo.pagedModels;

import br.com.eurdio.integrationtests.vo.Book;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.Objects;

@XmlRootElement
public class PagedModelBook {

    @XmlElement(name = "content")
    private List<Book> content;

    public PagedModelBook(List<Book> content) {
        this.content = content;
    }

    public PagedModelBook() {
    }

    public List<Book> getContent() {
        return content;
    }

    public void setContent(List<Book> content) {
        this.content = content;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PagedModelBook that = (PagedModelBook) o;

        return Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return content != null ? content.hashCode() : 0;
    }

}
