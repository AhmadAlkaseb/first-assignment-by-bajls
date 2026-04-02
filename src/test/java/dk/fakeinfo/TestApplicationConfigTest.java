package dk.fakeinfo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dk.fakeinfo.model.PostalCode;
import dk.fakeinfo.repository.PostalCodeRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class TestApplicationConfigTest {

    private final TestApplicationConfig config = new TestApplicationConfig();

    @Test
    void postalCodeRepositoryReturnsExpectedPostalCode() {
        PostalCodeRepository repository = config.postalCodeRepository();

        Optional<PostalCode> result = repository.findRandomPostalCode();

        assertThat(result).isPresent();
        assertThat(result.orElseThrow().getPostalCode()).isEqualTo("2100");
        assertThat(result.orElseThrow().getTownName()).isEqualTo("Kobenhavn O");
    }

    @Test
    void postalCodeRepositorySupportsObjectMethodsAndRejectsOtherMethods() {
        PostalCodeRepository repository = config.postalCodeRepository();

        assertThat(repository.toString()).isEqualTo("TestPostalCodeRepository");
        assertThat(repository.equals(repository)).isTrue();
        assertThat(repository.hashCode()).isNotZero();
        assertThatThrownBy(repository::count)
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("count");
    }
}
