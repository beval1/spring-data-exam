package softuni.library.services.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.library.models.dtos.xmls.LibrarySeedXmlRootDto;
import softuni.library.models.entities.Book;
import softuni.library.models.entities.Library;
import softuni.library.repositories.BookRepository;
import softuni.library.repositories.LibraryRepository;
import softuni.library.services.LibraryService;
import softuni.library.util.ValidationUtilImpl;
import softuni.library.util.XmlParser;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static softuni.library.constants.Constants.LIBRARIES_XML;

@Service
public class LibraryServiceImpl implements LibraryService {
    private final LibraryRepository libraryRepository;
    private final XmlParser xmlParser;
    private final ValidationUtilImpl validationUtil;
    private final ModelMapper modelMapper;
    private final BookRepository bookRepository;

    public LibraryServiceImpl(LibraryRepository libraryRepository, XmlParser xmlParser, ValidationUtilImpl validationUtil, ModelMapper modelMapper, BookRepository bookRepository) {
        this.libraryRepository = libraryRepository;
        this.xmlParser = xmlParser;
        this.validationUtil = validationUtil;
        this.modelMapper = modelMapper;
        this.bookRepository = bookRepository;
    }


    @Override
    public boolean areImported() {
        return this.libraryRepository.count() > 0;
    }

    @Override
    public String readLibrariesFileContent() throws IOException {
        return Files.readString(Path.of(LIBRARIES_XML));
    }

    @Override
    public String importLibraries() throws JAXBException {
        StringBuilder resultInfo = new StringBuilder();

        LibrarySeedXmlRootDto dtos = this.xmlParser.parseXml(LibrarySeedXmlRootDto.class, LIBRARIES_XML);

        dtos.getLibraries().forEach(l -> {
            if (this.validationUtil.isValid(l)) {
                //Check if library already exists in DB, name is unique
                if (this.libraryRepository.findByName(l.getName()) == null) {
                    Library library = this.modelMapper.map(l, Library.class);
                    Book book = this.bookRepository.findById((long) l.getBook().getId()).orElse(null);
                    library.getBooks().add(book);
                    this.libraryRepository.saveAndFlush(library);
                    resultInfo.append(String.format("Successfully imported Library: %s - %s",
                            l.getName(), l.getLocation()
                    ));
                } else {
                    resultInfo.append("Invalid Library");
                }
            } else {
                resultInfo.append("Invalid Library");
            }
            resultInfo.append(System.lineSeparator());
        });

        return resultInfo.toString();
    }
}
