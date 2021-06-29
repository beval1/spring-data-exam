package softuni.library.services.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.library.models.dtos.jsons.AuthorSeedJsonDto;
import softuni.library.models.entities.Author;
import softuni.library.repositories.AuthorRepository;
import softuni.library.services.AuthorService;
import softuni.library.util.ValidationUtilImpl;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static softuni.library.constants.Constants.AUTHORS_JSON;

@Service
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;
    private final Gson gson;
    private final ValidationUtilImpl validationUtil;
    private final ModelMapper modelMapper;

    public AuthorServiceImpl(AuthorRepository authorRepository, Gson gson, ValidationUtilImpl validationUtil, ModelMapper modelMapper) {
        this.authorRepository = authorRepository;
        this.gson = gson;
        this.validationUtil = validationUtil;
        this.modelMapper = modelMapper;
    }


    @Override
    public boolean areImported() {
        return this.authorRepository.count() > 0;
    }

    @Override
    public String readAuthorsFileContent() throws IOException {
        return Files.readString(Path.of(AUTHORS_JSON));
    }

    @Override
    public String importAuthors() throws FileNotFoundException {
        StringBuilder resultInfo = new StringBuilder();

        AuthorSeedJsonDto[] dtos = gson.fromJson(new FileReader(AUTHORS_JSON), AuthorSeedJsonDto[].class);

        Arrays.stream(dtos).forEach(a -> {
            if (this.validationUtil.isValid(a)) {
                //Check if author exists in DB, the combination of first and last name is unique
                if (this.authorRepository.findByFirstNameAndLastName(a.getFirstName(), a.getLastName()) == null){
                    Author author = this.modelMapper.map(a, Author.class);
                    this.authorRepository.saveAndFlush(author);
                    resultInfo.append(String.format("Successfully imported Author: %s %s - %s",
                            a.getFirstName(), a.getLastName(), a.getBirthTown()));
                } else {
                    resultInfo.append("Invalid Author");
                }
            } else {
                resultInfo.append("Invalid Author");
            }
            resultInfo.append(System.lineSeparator());
        });

        return resultInfo.toString();
    }
}
