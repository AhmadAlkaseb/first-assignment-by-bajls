package dk.fakeinfo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.fakeinfo.model.PersonName;
import dk.fakeinfo.model.PersonNamesFile;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
public class PersonNameService {

    private final ObjectMapper objectMapper;
    private List<PersonName> names;

    public PersonNameService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    void loadNames() throws IOException {
        ClassPathResource resource = new ClassPathResource("data/person-names.json");
        try (InputStream inputStream = resource.getInputStream()) {
            PersonNamesFile data = objectMapper.readValue(inputStream, PersonNamesFile.class);
            this.names = data.persons();
        }
    }

    public void getLoadNames() throws IOException {
        loadNames();
    }

    public PersonName getRandomName() {
        int index = ThreadLocalRandom.current().nextInt(names.size());
        return names.get(index);
    }
}
