package integrationtests;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.emptyOrNullString;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PersonIntegrationTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("Get person")
    void getPerson() {
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .when()
                .get("/person")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("cpr", matchesPattern("\\d{10}"))
                .body("firstName", not(emptyOrNullString()))
                .body("lastName", not(emptyOrNullString()))
                .body("gender", anyOf(is("male"), is("female")))
                .body("birthDate", matchesPattern("\\d{4}-\\d{2}-\\d{2}"))
                .body("address.street", not(emptyOrNullString()))
                .body("address.postal_code", is("2100"))
                .body("address.town_name", is("Kobenhavn O"))
                .body("phoneNumber", matchesPattern("\\d{8}"));
    }
}
