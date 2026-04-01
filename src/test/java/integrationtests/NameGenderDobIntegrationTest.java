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

class NameGenderDobIntegrationTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("Get name, gender and date of birth")
    void getNameGenderDob() {
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .when()
                .get("/name-gender-dob")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("firstName", not(emptyOrNullString()))
                .body("lastName", not(emptyOrNullString()))
                .body("gender", anyOf(is("male"), is("female")))
                .body("birthDate", matchesPattern("\\d{4}-\\d{2}-\\d{2}"));
    }
}
