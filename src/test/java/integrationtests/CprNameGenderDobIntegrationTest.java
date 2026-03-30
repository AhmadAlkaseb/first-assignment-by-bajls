package integrationtests;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.nullValue;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CprNameGenderDobIntegrationTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("Get cpr, name, gender and date of birth")
    void getCprNameGenderDob() {
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .when()
                .get("/cpr-name-gender-dob")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("cpr", matchesPattern("\\d{10}"))
                .body("firstName", not(emptyOrNullString()))
                .body("lastName", not(emptyOrNullString()))
                .body("gender", anyOf(is("male"), is("female")))
                .body("birthDate", matchesPattern("\\d{4}-\\d{2}-\\d{2}"))
                .body("address", nullValue())
                .body("phoneNumber", nullValue());
    }
}
