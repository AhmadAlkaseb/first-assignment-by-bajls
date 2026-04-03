package dk.fakeinfo.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.fakeinfo.model.PersonName;
import org.junit.jupiter.api.Test;

class PersonNameServiceTest {

    @Test
    void loadNamesLoadsDatasetAndGetRandomNameReturnsEntryFromIt() throws Exception {
        PersonNameService service = new PersonNameService(new ObjectMapper());

        service.loadNames();
        PersonName randomName = service.getRandomName();

        assertThat(randomName).isNotNull();
        assertThat(randomName.firstName()).isNotBlank();
        assertThat(randomName.lastName()).isNotBlank();
        assertThat(randomName.gender()).isIn("male", "female");
    }
}
