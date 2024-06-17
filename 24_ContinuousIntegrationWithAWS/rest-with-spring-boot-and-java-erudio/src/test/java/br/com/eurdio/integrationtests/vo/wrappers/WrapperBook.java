package br.com.eurdio.integrationtests.vo.wrappers;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

@XmlRootElement
public class WrapperBook {

    @JsonProperty("_embedded")
    private BookEmbedded embedded;

    public WrapperBook() {
    }

    public BookEmbedded getEmbedded() {
        return embedded;
    }

    public void setEmbedded(BookEmbedded embedded) {
        this.embedded = embedded;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WrapperBook that = (WrapperBook) o;

        return Objects.equals(embedded, that.embedded);
    }

    @Override
    public int hashCode() {
        return embedded != null ? embedded.hashCode() : 0;
    }
}
