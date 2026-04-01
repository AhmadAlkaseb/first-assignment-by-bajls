package integrationtests;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.emptyOrNullString;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NameGenderIntegrationTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("Get name and gender")
    void getNameGender() {
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .when()
                .get("/name-gender")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("firstName", not(emptyOrNullString()))
                .body("lastName", not(emptyOrNullString()))
                .body("gender", anyOf(is("male"), is("female")));
    }
}
