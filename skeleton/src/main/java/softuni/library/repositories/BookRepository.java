package softuni.library.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import softuni.library.models.entities.Book;

import java.time.LocalDate;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Book findByNameAndEditionAndWritten(String name, int edition, LocalDate written);
    //Book findByNameAndEdition(String name, int edition);
}
