package integrationtests;

import static org.hamcrest.Matchers.matchesPattern;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CprIntegrationTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("Get cpr")
    void getCpr() {
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .when()
                .get("/cpr")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("cpr", matchesPattern("\\d{10}"));
    }
}
