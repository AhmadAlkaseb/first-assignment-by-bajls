package integrationtests;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.emptyOrNullString;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AddressIntegrationTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("Get address")
    void getAddress() {
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .when()
                .get("/address")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("address.street", not(emptyOrNullString()))
                .body("address.number", not(emptyOrNullString()))
                .body("address.postal_code", is("2100"))
                .body("address.town_name", is("Kobenhavn O"));
    }
}
