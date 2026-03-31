package unit.address;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import dk.fakeinfo.model.Address;

import dk.fakeinfo.repository.PostalCodeRepository;
import dk.fakeinfo.service.FakePersonService;
import dk.fakeinfo.service.PersonNameService;

import static org.assertj.core.api.Assertions.assertThat;

class AddressTest {

     @Mock
    private PersonNameService personNameService;

    @Mock
    private PostalCodeRepository postalCodeRepository;

    private FakePersonService service;

    @BeforeEach
    void createService() {
        service = new FakePersonService(personNameService, postalCodeRepository);
    }



    @Nested
    class FloorValidationTests {

        @ParameterizedTest(name = "\"{0}\" should return {1}")
        @DisplayName("Floor value should be validated correctly")
        @CsvSource({
                "st, true",
                "1, true",
                "2, true",
                "99, true",
                "0, false",
                "100, false",
                "3, true",
                "mf, false",
                "tv, false",
                "abc, false"
        })
        void testFloorValidation(String input, boolean expectedValue) {
            // Arrange

            // Act
            boolean output = isValidFloor(input);

            // Assert
            assertThat(output).isEqualTo(expectedValue);
        }
    }

    @Nested
    class DoorValidationTests {

        @ParameterizedTest(name = "\"{0}\" should return {1}")
        @DisplayName("Door value should be validated correctly")
        @CsvSource({
                "th, true",
                "tv, true",
                "mf, true",
                "1, true",
                "2, true",
                "10, true",
                "50, true",
                "0, false",
                "51, false",
                "a4, true",
                "b999, true",
                "c-4, true",
                "d-999, true",
                "a0, false",
                "a-0, false",
                "a1000, false",
                "a-1000, false",
                "st, false",
                "xx, false"
        })
        void testDoorValidation(String input, boolean expectedValue) {
            // Arrange

            // Act
            boolean output = isValidDoor(input);

            // Assert
            assertThat(output).isEqualTo(expectedValue);
        }
    }

    @Nested
    class HouseNumberValidationTests {

        @ParameterizedTest(name = "\"{0}\" should return {1}")
        @DisplayName("House number should be validated correctly")
        @CsvSource({
                "1, true",
                "2, true",
                "10, true",
                "999, true",
                "1A, true",
                "25B, true",
                "999Z, true",
                "0, false",
                "1000, false",
                "01, false",
                "1a, false",
                "A1, false",
                "'', false"
        })
        void testHouseNumberValidation(String input, boolean expectedValue) {
            // Arrange

            // Act
            boolean output = isValidHouseNumber(input);

            // Assert
            assertThat(output).isEqualTo(expectedValue);
        }
    }

    @Nested
    class PostalCodeValidationTests {

        @ParameterizedTest(name = "\"{0}\" should return {1}")
        @DisplayName("Postal code format should be validated correctly")
        @CsvSource({
                "1000, true",
                "2100, true",
                "9999, true",
                "123, false",
                "abcde, false",
                "12A4, false",
                "'', false"
        })
        void testPostalCodeValidation(String input, boolean expectedValue) {
            // Arrange

            // Act
            boolean output = isValidPostalCode(input);

            // Assert
            assertThat(output).isEqualTo(expectedValue);
        }
    }

    private boolean isValidFloor(String value) {
        if ("st".equals(value)) {
            return true;
        }

        if (!value.matches("^\\d+$")) {
            return false;
        }

        int number = Integer.parseInt(value);
        return number >= 1 && number <= 99;
    }

    private boolean isValidDoor(String value) {
        if (value.equals("th") || value.equals("tv") || value.equals("mf")) {
            return true;
        }

        if (value.matches("^([1-9]|[1-4][0-9]|50)$")) {
            return true;
        }

        if (value.matches("^[a-z]([1-9][0-9]{0,2})$")) {
            int numericPart = Integer.parseInt(value.substring(1));
            return numericPart >= 1 && numericPart <= 999;
        }

        if (value.matches("^[a-z]-([1-9][0-9]{0,2})$")) {
            int numericPart = Integer.parseInt(value.substring(2));
            return numericPart >= 1 && numericPart <= 999;
        }

        return false;
    }

    private boolean isValidHouseNumber(String value) {
        return value != null && value.matches("^[1-9][0-9]{0,2}[A-Z]?$");
    }

    private boolean isValidPostalCode(String value) {
        return value != null && value.matches("^\\d{4}$");
    }
}