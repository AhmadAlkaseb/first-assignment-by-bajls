package dk.fakeinfo;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import dk.fakeinfo.model.PostalCode;
import dk.fakeinfo.repository.PostalCodeRepository;
import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
class TestApplicationConfig {

    @Bean
    @Primary
    PostalCodeRepository postalCodeRepository() {
        PostalCodeRepository repository = mock(PostalCodeRepository.class);
        when(repository.findRandomPostalCode())
                .thenReturn(Optional.of(new PostalCode("2100", "Kobenhavn O")));
        return repository;
    }
}
