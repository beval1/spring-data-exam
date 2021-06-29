package softuni.library.services.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.library.models.dtos.xmls.CharacterSeedXmlRootDto;
import softuni.library.models.entities.Book;
import softuni.library.models.entities.Character;
import softuni.library.repositories.BookRepository;
import softuni.library.repositories.CharacterRepository;
import softuni.library.services.CharacterService;
import softuni.library.util.ValidationUtilImpl;
import softuni.library.util.XmlParser;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static softuni.library.constants.Constants.CHARACTERS_XML;

@Service
public class CharacterServiceImpl implements CharacterService {
    private final CharacterRepository characterRepository;
    private final XmlParser xmlParser;
    private final ValidationUtilImpl validationUtil;
    private final ModelMapper modelMapper;
    private final BookRepository bookRepository;

    public CharacterServiceImpl(CharacterRepository characterRepository, XmlParser xmlParser, ValidationUtilImpl validationUtil, ModelMapper modelMapper, BookRepository bookRepository) {
        this.characterRepository = characterRepository;
        this.xmlParser = xmlParser;
        this.validationUtil = validationUtil;
        this.modelMapper = modelMapper;
        this.bookRepository = bookRepository;
    }

    @Override
    public boolean areImported() {
        return this.characterRepository.count() > 0;
    }

    @Override
    public String readCharactersFileContent() throws IOException {
        return Files.readString(Path.of(CHARACTERS_XML));
    }

    @Override
    public String importCharacters() throws JAXBException {
        StringBuilder resultInfo = new StringBuilder();

        CharacterSeedXmlRootDto dtos = this.xmlParser.parseXml(CharacterSeedXmlRootDto.class, CHARACTERS_XML);

        dtos.getCharacters().forEach(c -> {
            if (this.validationUtil.isValid(c)) {
                LocalDate birthday = LocalDate.parse(c.getBirthday());
                //Check if character already exists in DB
                if (this.characterRepository.findByFirstNameAndLastNameAndAgeAndBirthdayAndRole(c.getFirstName(), c.getLastName(),
                        c.getAge(), birthday, c.getRole()) == null) {
                    Character character = this.modelMapper.map(c, Character.class);
                    character.setBirthday(birthday);
                    Book book = this.bookRepository.findById((long) c.getBook().getId()).orElse(null);
                    character.setBook(book);
                    this.characterRepository.saveAndFlush(character);
                    resultInfo.append(String.format("Successfully imported Character: %s %s %s - age: %d",
                            c.getFirstName(), c.getMiddleName(), c.getLastName(), c.getAge()
                    ));
                } else {
                    resultInfo.append("Invalid Character");
                }
            } else {
                resultInfo.append("Invalid Character");
            }
            resultInfo.append(System.lineSeparator());
        });

        return resultInfo.toString();
    }

    @Override
    public String findCharactersInBookOrderedByLastNameDescendingThenByAge() {
        StringBuilder resultInfo = new StringBuilder();
        List<Character> characters = this.characterRepository.findCharacterByAgeOrderByBookAndLastNameAndAge();
        characters.forEach(c -> {
            resultInfo.append(String.format("Character name %s %s %s, age %d, in book %s",
                    c.getFirstName(), c.getMiddleName(), c.getLastName(), c.getAge(), c.getBook().getName()));
            resultInfo.append(System.lineSeparator());
        });
        return resultInfo.toString();
    }
}
