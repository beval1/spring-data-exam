package softuni.library.models.dtos.xmls;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "characters")
@XmlAccessorType(XmlAccessType.FIELD)
public class CharacterSeedXmlRootDto {
    @XmlElement(name = "character")
    private List<CharacterSeedXmlDto> characters;

    public CharacterSeedXmlRootDto() {
    }

    public List<CharacterSeedXmlDto> getCharacters() {
        return characters;
    }

    public void setCharacters(List<CharacterSeedXmlDto> characters) {
        this.characters = characters;
    }
}
