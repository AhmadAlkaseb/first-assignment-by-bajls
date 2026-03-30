package integrationtests;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.emptyOrNullString;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PersonBulkIntegrationTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("Get multiple persons")
    void getAll() {
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .queryParam("n", 3)
                .when()
                .get("/person")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", hasSize(3))
                .body("cpr", everyItem(matchesPattern("\\d{10}")))
                .body("firstName", everyItem(not(emptyOrNullString())))
                .body("lastName", everyItem(not(emptyOrNullString())))
                .body("gender", everyItem(anyOf(is("male"), is("female"))))
                .body("birthDate", everyItem(matchesPattern("\\d{4}-\\d{2}-\\d{2}")))
                .body("address.postal_code", everyItem(is("2100")))
                .body("address.town_name", everyItem(is("Kobenhavn O")))
                .body("phoneNumber", everyItem(matchesPattern("\\d{8}")));
    }
}
