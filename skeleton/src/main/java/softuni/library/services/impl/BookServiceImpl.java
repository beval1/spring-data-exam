package softuni.library.services.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.library.models.dtos.jsons.BookSeedJsonDto;
import softuni.library.models.entities.Author;
import softuni.library.models.entities.Book;
import softuni.library.repositories.AuthorRepository;
import softuni.library.repositories.BookRepository;
import softuni.library.services.BookService;
import softuni.library.util.ValidationUtilImpl;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Arrays;

import static softuni.library.constants.Constants.BOOKS_JSON;

@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final Gson gson;
    private final ValidationUtilImpl validationUtil;
    private final ModelMapper modelMapper;
    private final AuthorRepository authorRepository;

    public BookServiceImpl(BookRepository bookRepository, Gson gson, ValidationUtilImpl validationUtil, ModelMapper modelMapper, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.gson = gson;
        this.validationUtil = validationUtil;
        this.modelMapper = modelMapper;
        this.authorRepository = authorRepository;
    }


    @Override
    public boolean areImported() {
        return this.bookRepository.count() > 0;
    }

    @Override
    public String readBooksFileContent() throws IOException {
        return Files.readString(Path.of(BOOKS_JSON));
    }

    @Override
    public String importBooks() throws FileNotFoundException {
        StringBuilder resultInfo = new StringBuilder();

        BookSeedJsonDto[] dtos = gson.fromJson(new FileReader(BOOKS_JSON), BookSeedJsonDto[].class);

        Arrays.stream(dtos).forEach(b -> {
            if (this.validationUtil.isValid(b)) {
                LocalDate written = LocalDate.parse(b.getWritten());
                //this.bookRepository.findByNameAndEditionAndWritten(b.getName(), b.getEdition(), written)
                //this.bookRepository.findByNameAndEdition(b.getName(), b.getEdition())
                //Check if book already exists in DB.
                if (this.bookRepository.findByNameAndEditionAndWritten(b.getName(), b.getEdition(), written) == null) {
                    Book book = this.modelMapper.map(b, Book.class);
                    Author author = this.authorRepository.findById((long) b.getAuthor()).orElse(null);
                    book.setAuthor(author);
                    book.setWritten(written);
                    this.bookRepository.saveAndFlush(book);
                    resultInfo.append(String.format("Successfully imported Book: %s written in %s",
                            b.getName(), b.getWritten()));
                } else {
                    resultInfo.append("Invalid Book");
                }
            } else {
                resultInfo.append("Invalid Book");
            }
            resultInfo.append(System.lineSeparator());
        });

        return resultInfo.toString();
    }
}