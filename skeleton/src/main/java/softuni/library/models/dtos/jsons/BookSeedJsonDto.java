package softuni.library.models.dtos.jsons;

import com.google.gson.annotations.Expose;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class BookSeedJsonDto {
    @Expose
    private String name;
    @Expose
    private int edition;
    @Expose
    private String written;
    @Expose
    private String description;
    @Expose
    private int author;

    public BookSeedJsonDto() {
    }

    @NotNull
    @Length(min = 5)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Min(1)
    @Max(5)
    public int getEdition() {
        return edition;
    }

    public void setEdition(int edition) {
        this.edition = edition;
    }

    @NotNull
    public String getWritten() {
        return written;
    }

    public void setWritten(String written) {
        this.written = written;
    }



    @Length(min = 100)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAuthor() {
        return author;
    }

    public void setAuthor(int author) {
        this.author = author;
    }
}
