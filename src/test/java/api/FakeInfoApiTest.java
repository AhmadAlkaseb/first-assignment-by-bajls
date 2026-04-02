package api;

import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FakeInfoApiTest {

    private Playwright playwright;
    private APIRequestContext request;

    @BeforeAll
    void beforeAll() {
        playwright = Playwright.create();
        request = playwright.request().newContext(
                new APIRequest.NewContextOptions()
                        .setBaseURL("http://localhost:8080")
        );
    }

    @AfterAll
    void afterAll() {
        if (request != null) {
            request.dispose();
            request = null;
        }

        if (playwright != null) {
            playwright.close();
            playwright = null;
        }
    }

    @Test
    void shouldGetCpr() {
        APIResponse response = request.get("/cpr");

        assertTrue(response.ok());
        assertEquals(200, response.status());
        assertTrue(response.text().contains("cpr"));
    }

    @Test
    void shouldGetNameGender() {
        APIResponse response = request.get("/name-gender");

        assertTrue(response.ok());
        assertEquals(200, response.status());
    }

    @Test
    void shouldGetAddress() {
        APIResponse response = request.get("/address");

        assertTrue(response.ok());
        assertEquals(200, response.status());
    }

    @Test
    void shouldGetSinglePerson() {
        APIResponse response = request.get("/person");

        assertTrue(response.ok());
        assertEquals(200, response.status());
    }

    @Test
    void shouldGetMultiplePersonsWhenNIsProvided() {
        APIResponse response = request.get("/person?n=3");

        assertTrue(response.ok());
        assertEquals(200, response.status());
    }

    @Test
    void shouldGetNameGenderDob() {
        APIResponse response = request.get("/name-gender-dob");

        assertTrue(response.ok());
        assertEquals(200, response.status());
    }

    @Test
    void shouldGetCprNameGender() {
        APIResponse response = request.get("/cpr-name-gender");

        assertTrue(response.ok());
        assertEquals(200, response.status());
    }

    @Test
    void shouldGetCprNameGenderDob() {
        APIResponse response = request.get("/cpr-name-gender-dob");

        assertTrue(response.ok());
        assertEquals(200, response.status());
    }

    @Test
    void shouldGetPhone() {
        APIResponse response = request.get("/phone");

        assertTrue(response.ok());
        assertEquals(200, response.status());
    }
}