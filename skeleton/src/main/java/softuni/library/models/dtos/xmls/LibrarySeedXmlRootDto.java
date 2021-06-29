package softuni.library.models.dtos.xmls;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "libraries")
@XmlAccessorType(XmlAccessType.FIELD)
public class LibrarySeedXmlRootDto {
    @XmlElement(name = "library")
    private List<LibrarySeedXmlDto> libraries;

    public LibrarySeedXmlRootDto() {
    }

    public List<LibrarySeedXmlDto> getLibraries() {
        return libraries;
    }

    public void setLibraries(List<LibrarySeedXmlDto> libraries) {
        this.libraries = libraries;
    }
}
