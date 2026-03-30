package integrationtests;

import static org.hamcrest.Matchers.matchesPattern;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PhoneIntegrationTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("Get phone")
    void getPhone() {
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .when()
                .get("/phone")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("phoneNumber", matchesPattern("\\d{8}"));
    }
}
