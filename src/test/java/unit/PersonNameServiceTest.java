package unit;

import dk.fakeinfo.model.PersonName;
import dk.fakeinfo.model.PersonNamesFile;
import dk.fakeinfo.service.PersonNameService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;


/*
    * Unit tests for PersonNameService.
    * Tests the getRandomName method to ensure it returns valid names from the list.
    * Also tests the loadNames method to ensure it properly loads names and handles exceptions.
    * Mocks the ObjectMapper to control the behavior of loading names from the JSON file.
    * Uses reflection to access the private names field for testing purposes.
    *
 */
@ExtendWith(MockitoExtension.class)
class PersonNameServiceTest {

    @Mock
    private ObjectMapper objectMapper;

    private PersonNameService service;

    private static final List<PersonName> NAMES = List.of(
            new PersonName("Alice", "Hansen", "female"),
            new PersonName("Bob", "Nielsen", "male"),
            new PersonName("Carol", "Jensen", "female")
    );

    @BeforeEach
    void setUp() throws Exception {
        service = new PersonNameService(objectMapper);
        Field field = PersonNameService.class.getDeclaredField("names");
        field.setAccessible(true);
        field.set(service, NAMES);
    }

    @Test
    void getRandomName_returnsNameFromList() {
        PersonName result = service.getRandomName();

        assertThat(result).isIn(NAMES);
    }

    @Test
    void getRandomName_returnsNonNullName() {
        PersonName result = service.getRandomName();

        assertThat(result).isNotNull();
        assertThat(result.firstName()).isNotBlank();
        assertThat(result.lastName()).isNotBlank();
    }

    @Test
    void failsToLoadNames_throwsIOException() throws IOException {
        when(objectMapper.readValue(any(InputStream.class), eq(PersonNamesFile.class)))
                .thenThrow(new IOException("Failed to read names file"));

        PersonNameService newService = new PersonNameService(objectMapper);

        assertThatThrownBy(newService::getLoadNames)
                .isInstanceOf(IOException.class);
    }
}