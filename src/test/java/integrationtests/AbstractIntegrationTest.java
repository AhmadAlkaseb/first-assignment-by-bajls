package integrationtests;

import static org.mockito.Mockito.when;

import dk.fakeinfo.FakeInfoApplication;
import dk.fakeinfo.model.PostalCode;
import dk.fakeinfo.repository.PostalCodeRepository;
import io.restassured.RestAssured;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
        classes = FakeInfoApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                // Kør integrationstests uden database/JPA, fordi repoet bliver mock'et.
                "spring.autoconfigure.exclude="
                        + "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,"
                        + "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,"
                        + "org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration"
        }
)
@ActiveProfiles("test")
abstract class AbstractIntegrationTest {

    @LocalServerPort
    private int port;

    @MockBean
    protected PostalCodeRepository postalCodeRepository;

    @BeforeEach
    void setUpBase() {
        // Peg RestAssured mod den tilfældige port Spring starter testen på.
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;

        // Giv alle integrationstests en stabil standard-postkode.
        when(postalCodeRepository.findRandomPostalCode())
                .thenReturn(Optional.of(new PostalCode("2100", "Kobenhavn O")));
    }
}
