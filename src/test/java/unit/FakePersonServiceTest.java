package unit;

import dk.fakeinfo.model.Address;
import dk.fakeinfo.model.FakePerson;
import dk.fakeinfo.model.NameGenderDobResponse;
import dk.fakeinfo.model.NameGenderResponse;
import dk.fakeinfo.model.PersonName;
import dk.fakeinfo.model.PostalCode;
import dk.fakeinfo.repository.PostalCodeRepository;
import dk.fakeinfo.service.FakePersonService;
import dk.fakeinfo.service.PersonNameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

/**
 * Black-box unit tests for FakePersonService.
 *
 * Strategy:
 *   - Dependencies (PersonNameService, PostalCodeRepository) are mocked so these tests
 *     run without a database or classpath JSON file.
 *   - Every test follows Arrange / Act / Assert.
 *   - Where randomness is involved, invariants are verified over many samples rather
 *     than asserting one specific value.
 *   - Only public method behaviour is tested; no private helpers are accessed.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("FakePersonService — black-box unit tests")
class FakePersonServiceTest {

    // -------------------------------------------------------------------------
    // Shared fixtures
    // -------------------------------------------------------------------------

    /** A female name record identical in structure to those from person-names.json. */
    private static final PersonName FEMALE_NAME = new PersonName("Anna", "Jensen", "female");

    /** A male name record identical in structure to those from person-names.json. */
    private static final PersonName MALE_NAME = new PersonName("Lars", "Nielsen", "male");

    /** A postal-code/town pair representing a valid database row. */
    private static final PostalCode POSTAL_CODE = new PostalCode("2000", "Frederiksberg");

    /**
     * All valid Danish mobile phone prefixes, copied from the production service.
     * Used as the authoritative source of truth for prefix validation in tests.
     */
    private static final Set<String> VALID_PREFIXES = Set.of(
            "2",   "30",  "31",  "40",  "41",  "42",  "50",  "51",  "52",  "53",
            "60",  "61",  "71",  "81",  "91",  "92",  "93",
            "342", "344", "345", "346", "347", "348", "349",
            "356", "357", "359", "362", "365", "366", "389", "398",
            "431", "441", "462", "466", "468", "472", "474", "476", "478",
            "485", "486", "488", "489", "493", "494", "495", "496", "498", "499",
            "542", "543", "545", "551", "552", "556",
            "571", "572", "573", "574", "577", "579",
            "584", "586", "587", "589", "597", "598",
            "627", "629", "641", "649", "658",
            "662", "663", "664", "665", "667",
            "692", "693", "694", "697",
            "771", "772", "782", "783", "785", "786", "788", "789",
            "826", "827", "829"
    );

    // -------------------------------------------------------------------------
    // Mocks
    // -------------------------------------------------------------------------

    @Mock
    private PersonNameService personNameService;

    @Mock
    private PostalCodeRepository postalCodeRepository;

    private FakePersonService service;

    @BeforeEach
    void createService() {
        service = new FakePersonService(personNameService, postalCodeRepository);
    }

    // -------------------------------------------------------------------------
    // Private test helpers — validation logic only, no production code duplication
    // -------------------------------------------------------------------------

    /**
     * Returns true when the six-character DDMMYY string encodes a real calendar date.
     * Tries both the 1900 and 2000 century to handle two-digit year ambiguity.
     */
    private static boolean isValidCprDate(String ddmmyy) {
        int day   = Integer.parseInt(ddmmyy.substring(0, 2));
        int month = Integer.parseInt(ddmmyy.substring(2, 4));
        int yy    = Integer.parseInt(ddmmyy.substring(4, 6));
        for (int century : new int[]{2000, 1900}) {
            try {
                LocalDate.of(century + yy, month, day);
                return true;
            } catch (Exception ignored) { }
        }
        return false;
    }

    /** Returns true when the phone number starts with a known valid Danish mobile prefix. */
    private static boolean hasValidPrefix(String phone) {
        return VALID_PREFIXES.stream().anyMatch(phone::startsWith);
    }

    /**
     * Asserts all Address fields against the documented business rules.
     * Extracted as a helper because the same rules apply to getAddress(),
     * getFakePerson(), and getFakePersons().
     */
    private static void assertAddressIsValid(Address address) {
        assertThat(address).isNotNull();

        // Street: exactly 40 characters and not blank
        assertThat(address.street())
                .isNotBlank()
                .hasSize(40);

        // House number: 1–999 digits, optional single uppercase letter suffix (including Ø, Æ, Å)
        assertThat(address.number())
                .isNotBlank()
                .matches("[1-9]\\d{0,2}[A-ZØÆÅ]?");

        // Floor: either the literal string "st" or an integer in [1, 99]
        Object floor = address.floor();
        if (floor instanceof String s) {
            assertThat(s).as("floor string must be 'st'").isEqualTo("st");
        } else if (floor instanceof Integer i) {
            assertThat(i).as("floor integer must be in [1, 99]").isBetween(1, 99);
        } else {
            fail("floor must be String or Integer, but was: " + floor.getClass().getSimpleName());
        }

        // Door: "th", "tv", "mf", integer 1-50, or a letter optionally followed by dash then digits
        Object door = address.door();
        if (door instanceof String s) {
            assertThat(s)
                    .as("door string must match 'th', 'tv', 'mf', or letter(+dash)+digits")
                    .matches("th|tv|mf|[a-zøæå]-?\\d+");
        } else if (door instanceof Integer i) {
            assertThat(i).as("door integer must be in [1, 50]").isBetween(1, 50);
        } else {
            fail("door must be String or Integer, but was: " + door.getClass().getSimpleName());
        }

        // Postal code: exactly 4 characters
        assertThat(address.postalCode())
                .isNotBlank()
                .hasSize(4);

        // Town name: non-blank
        assertThat(address.townName()).isNotBlank();
    }

    /** Asserts that a CPR string is exactly 10 digits and its date portion is valid. */
    private static void assertCprIsValid(String cpr) {
        assertThat(cpr)
                .as("CPR must be exactly 10 digits")
                .matches("\\d{10}");
        assertThat(isValidCprDate(cpr.substring(0, 6)))
                .as("CPR date portion '%s' must be a valid DDMMYY date", cpr.substring(0, 6))
                .isTrue();
    }

    // =========================================================================
    // 1. CPR GENERATION  —  getFakeCpr()
    // =========================================================================

    @Nested
    @DisplayName("1. getFakeCpr()")
    class GetFakeCprTests {

        @Test
        @DisplayName("returns exactly 10 characters")
        void cprIsExactlyTenCharacters() {
            // Arrange
            when(personNameService.getRandomName()).thenReturn(FEMALE_NAME);

            // Act
            String cpr = service.getFakeCpr();

            // Assert
            assertThat(cpr).hasSize(10);
        }

        @Test
        @DisplayName("contains only digit characters")
        void cprContainsOnlyDigits() {
            // Arrange
            when(personNameService.getRandomName()).thenReturn(MALE_NAME);

            // Act
            String cpr = service.getFakeCpr();

            // Assert
            assertThat(cpr).matches("\\d{10}");
        }

        @Test
        @DisplayName("first 6 digits represent a valid calendar date in DDMMYY format")
        void cprDatePortionIsAValidCalendarDate() {
            // Arrange
            when(personNameService.getRandomName()).thenReturn(FEMALE_NAME);

            // Act
            String cpr = service.getFakeCpr();

            // Assert
            String datePart = cpr.substring(0, 6);
            assertThat(isValidCprDate(datePart))
                    .as("CPR date portion '%s' should encode a real calendar date", datePart)
                    .isTrue();
        }

        @Test
        @DisplayName("last digit is even for a female person (female parity rule)")
        void lastDigitIsEvenForFemale() {
            // Arrange — always return a female name
            when(personNameService.getRandomName()).thenReturn(FEMALE_NAME);

            // Act & Assert — 100 iterations eliminate the chance of accidental success
            IntStream.range(0, 100).forEach(i -> {
                String cpr = service.getFakeCpr();
                int lastDigit = Character.getNumericValue(cpr.charAt(9));
                assertThat(lastDigit % 2)
                        .as("Female CPR (iteration %d) last digit should be even, was %d in '%s'",
                                i, lastDigit, cpr)
                        .isEqualTo(0);
            });
        }

        @Test
        @DisplayName("last digit is odd for a male person (male parity rule)")
        void lastDigitIsOddForMale() {
            // Arrange — always return a male name
            when(personNameService.getRandomName()).thenReturn(MALE_NAME);

            // Act & Assert — 100 iterations
            IntStream.range(0, 100).forEach(i -> {
                String cpr = service.getFakeCpr();
                int lastDigit = Character.getNumericValue(cpr.charAt(9));
                assertThat(lastDigit % 2)
                        .as("Male CPR (iteration %d) last digit should be odd, was %d in '%s'",
                                i, lastDigit, cpr)
                        .isEqualTo(1);
            });
        }

        @Test
        @DisplayName("never generates an impossible date across 500 samples")
        void neverGeneratesAnImpossibleDate() {
            // Arrange
            when(personNameService.getRandomName()).thenReturn(FEMALE_NAME);

            // Act & Assert — 500 samples to cover rare calendar dates (e.g. Feb 29)
            IntStream.range(0, 500).forEach(i -> {
                String cpr = service.getFakeCpr();
                assertThat(cpr).as("CPR #%d must be 10 digits", i).matches("\\d{10}");
                assertThat(isValidCprDate(cpr.substring(0, 6)))
                        .as("CPR #%d date portion '%s' must be valid", i, cpr.substring(0, 6))
                        .isTrue();
            });
        }
    }

    // =========================================================================
    // 2. NAME + GENDER GENERATION  —  getNameGender()
    // =========================================================================

    @Nested
    @DisplayName("2. getNameGender()")
    class GetNameGenderTests {

        @Test
        @DisplayName("returns a non-null response object")
        void resultIsNotNull() {
            // Arrange
            when(personNameService.getRandomName()).thenReturn(FEMALE_NAME);

            // Act
            NameGenderResponse result = service.getNameGender();

            // Assert
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("first name is not blank")
        void firstNameIsNotBlank() {
            // Arrange
            when(personNameService.getRandomName()).thenReturn(FEMALE_NAME);

            // Act
            NameGenderResponse result = service.getNameGender();

            // Assert
            assertThat(result.firstName()).isNotBlank();
        }

        @Test
        @DisplayName("last name is not blank")
        void lastNameIsNotBlank() {
            // Arrange
            when(personNameService.getRandomName()).thenReturn(FEMALE_NAME);

            // Act
            NameGenderResponse result = service.getNameGender();

            // Assert
            assertThat(result.lastName()).isNotBlank();
        }

        @Test
        @DisplayName("gender is one of the two allowed values: 'male' or 'female'")
        void genderIsOneOfTheAllowedValues() {
            // Arrange
            when(personNameService.getRandomName()).thenReturn(MALE_NAME);

            // Act
            NameGenderResponse result = service.getNameGender();

            // Assert
            assertThat(result.gender()).isIn("male", "female");
        }

        @Test
        @DisplayName("all three fields come directly from the dataset record (female)")
        void allFieldsMatchDatasetRecordForFemale() {
            // Arrange
            when(personNameService.getRandomName()).thenReturn(FEMALE_NAME);

            // Act
            NameGenderResponse result = service.getNameGender();

            // Assert — the service must pass through the dataset values unchanged
            assertThat(result.firstName()).isEqualTo("Anna");
            assertThat(result.lastName()).isEqualTo("Jensen");
            assertThat(result.gender()).isEqualTo("female");
        }

        @Test
        @DisplayName("all three fields come directly from the dataset record (male)")
        void allFieldsMatchDatasetRecordForMale() {
            // Arrange
            when(personNameService.getRandomName()).thenReturn(MALE_NAME);

            // Act
            NameGenderResponse result = service.getNameGender();

            // Assert
            assertThat(result.firstName()).isEqualTo("Lars");
            assertThat(result.lastName()).isEqualTo("Nielsen");
            assertThat(result.gender()).isEqualTo("male");
        }
    }

    // =========================================================================
    // 3. NAME + GENDER + DATE OF BIRTH  —  getNameGenderDob()
    // =========================================================================

    @Nested
    @DisplayName("3. getNameGenderDob()")
    class GetNameGenderDobTests {

        @Test
        @DisplayName("returns a non-null response with all four fields populated")
        void allFieldsArePopulated() {
            // Arrange
            when(personNameService.getRandomName()).thenReturn(FEMALE_NAME);

            // Act
            NameGenderDobResponse result = service.getNameGenderDob();

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.firstName()).isNotBlank();
            assertThat(result.lastName()).isNotBlank();
            assertThat(result.gender()).isIn("male", "female");
            assertThat(result.birthDate()).isNotBlank();
        }

        @Test
        @DisplayName("birthDate is a parseable ISO date string (yyyy-MM-dd)")
        void birthDateIsValidIsoString() {
            // Arrange
            when(personNameService.getRandomName()).thenReturn(FEMALE_NAME);

            // Act
            NameGenderDobResponse result = service.getNameGenderDob();

            // Assert — LocalDate.parse throws DateTimeParseException for invalid formats
            assertThat(LocalDate.parse(result.birthDate())).isNotNull();
        }

        @Test
        @DisplayName("birthDate falls within the supported range: 1900-01-01 to today")
        void birthDateIsWithinSupportedRange() {
            // Arrange
            when(personNameService.getRandomName()).thenReturn(FEMALE_NAME);

            // Act
            NameGenderDobResponse result = service.getNameGenderDob();
            LocalDate dob = LocalDate.parse(result.birthDate());

            // Assert
            assertThat(dob)
                    .isAfterOrEqualTo(LocalDate.of(1900, 1, 1))
                    .isBeforeOrEqualTo(LocalDate.now());
        }

        @Test
        @DisplayName("birthDate range rule holds across 100 consecutive samples")
        void birthDateRangeHoldsForManySamples() {
            // Arrange
            when(personNameService.getRandomName()).thenReturn(FEMALE_NAME);
            LocalDate min = LocalDate.of(1900, 1, 1);
            LocalDate max = LocalDate.now();

            // Act & Assert
            IntStream.range(0, 100).forEach(i -> {
                LocalDate dob = LocalDate.parse(service.getNameGenderDob().birthDate());
                assertThat(dob)
                        .as("DOB #%d should be between 1900-01-01 and today", i)
                        .isAfterOrEqualTo(min)
                        .isBeforeOrEqualTo(max);
            });
        }
    }

    // =========================================================================
    // 4. ADDRESS GENERATION  —  getAddress()
    // =========================================================================

    @Nested
    @DisplayName("4. getAddress()")
    class GetAddressTests {

        @BeforeEach
        void stubPostalCode() {
            when(postalCodeRepository.findRandomPostalCode()).thenReturn(Optional.of(POSTAL_CODE));
        }

        @Test
        @DisplayName("returns a non-null Address")
        void returnsNonNullAddress() {
            // Act
            Address address = service.getAddress();

            // Assert
            assertThat(address).isNotNull();
        }

        @Test
        @DisplayName("street is not blank and is exactly 40 characters long")
        void streetIsNotBlankAndExactly40Chars() {
            // Act
            Address address = service.getAddress();

            // Assert
            assertThat(address.street()).isNotBlank().hasSize(40);
        }

        @Test
        @DisplayName("house number is a value from 1 to 999, optionally suffixed by one uppercase letter")
        void houseNumberMatchesExpectedFormat() {
            // Act
            Address address = service.getAddress();

            // Assert
            assertThat(address.number())
                    .isNotBlank()
                    .matches("[1-9]\\d{0,2}[A-ZØÆÅ]?");
        }

        @Test
        @DisplayName("floor is the string 'st' or an integer in the range [1, 99]")
        void floorIsValidValue() {
            // Act
            Address address = service.getAddress();

            // Assert
            Object floor = address.floor();
            if (floor instanceof String s) {
                assertThat(s).as("only allowed floor string is 'st'").isEqualTo("st");
            } else if (floor instanceof Integer i) {
                assertThat(i).as("floor integer must be in [1, 99]").isBetween(1, 99);
            } else {
                fail("floor must be String or Integer, but was: " + floor.getClass().getSimpleName());
            }
        }

        @Test
        @DisplayName("door is 'th', 'tv', 'mf', an integer 1-50, or a letter(+dash)+number pattern")
        void doorIsValidValue() {
            // Act
            Address address = service.getAddress();

            // Assert
            Object door = address.door();
            if (door instanceof String s) {
                assertThat(s)
                        .as("door string must be 'th', 'tv', 'mf', or letter-pattern")
                        .matches("th|tv|mf|[a-zøæå]-?\\d+");
            } else if (door instanceof Integer i) {
                assertThat(i).as("door integer must be in [1, 50]").isBetween(1, 50);
            } else {
                fail("door must be String or Integer, but was: " + door.getClass().getSimpleName());
            }
        }

        @Test
        @DisplayName("postalCode matches the value returned by the database")
        void postalCodeMatchesDatabaseValue() {
            // Act
            Address address = service.getAddress();

            // Assert
            assertThat(address.postalCode()).isEqualTo("2000");
        }

        @Test
        @DisplayName("townName matches the value returned by the database")
        void townNameMatchesDatabaseValue() {
            // Act
            Address address = service.getAddress();

            // Assert
            assertThat(address.townName()).isEqualTo("Frederiksberg");
        }

        @Test
        @DisplayName("all address field rules hold across 100 consecutive samples")
        void allFieldRulesHoldAcross100Samples() {
            // Act & Assert — run many times to cover all random floor/door branches
            IntStream.range(0, 100).forEach(i -> assertAddressIsValid(service.getAddress()));
        }

        @Test
        @DisplayName("throws IllegalStateException when the database has no postal codes")
        void throwsWhenDatabaseHasNoPostalCodes() {
            // Arrange
            when(postalCodeRepository.findRandomPostalCode()).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> service.getAddress())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("postal");
        }
    }

    // =========================================================================
    // 5. PHONE NUMBER GENERATION  —  getPhoneNumber()
    // =========================================================================

    @Nested
    @DisplayName("5. getPhoneNumber()")
    class GetPhoneNumberTests {

        @Test
        @DisplayName("phone number is exactly 8 characters long")
        void phoneIsExactlyEightCharacters() {
            // Act
            String phone = service.getPhoneNumber();

            // Assert
            assertThat(phone).hasSize(8);
        }

        @Test
        @DisplayName("phone number contains only digit characters")
        void phoneContainsOnlyDigits() {
            // Act
            String phone = service.getPhoneNumber();

            // Assert
            assertThat(phone).matches("\\d{8}");
        }

        @Test
        @DisplayName("phone number starts with a valid Danish mobile prefix")
        void phoneStartsWithAValidDanishPrefix() {
            // Act
            String phone = service.getPhoneNumber();

            // Assert
            assertThat(hasValidPrefix(phone))
                    .as("Phone number '%s' must start with a valid Danish mobile prefix", phone)
                    .isTrue();
        }

        @Test
        @DisplayName("phone number never starts with '0' or '1' (no such valid prefix)")
        void phoneDoesNotStartWithZeroOrOne() {
            // Act
            String phone = service.getPhoneNumber();

            // Assert
            assertThat(phone)
                    .doesNotStartWith("0")
                    .doesNotStartWith("1");
        }

        @Test
        @DisplayName("all format and prefix rules hold across 200 samples")
        void allRulesHoldAcross200Samples() {
            // Act & Assert
            IntStream.range(0, 200).forEach(i -> {
                String phone = service.getPhoneNumber();
                assertThat(phone).as("phone #%d must be 8 digits", i).matches("\\d{8}");
                assertThat(hasValidPrefix(phone))
                        .as("phone #%d '%s' must have a valid prefix", i, phone)
                        .isTrue();
            });
        }
    }

    // =========================================================================
    // 6. FULL PERSON GENERATION  —  getFakePerson()
    // =========================================================================

    @Nested
    @DisplayName("6. getFakePerson()")
    class GetFakePersonTests {

        @BeforeEach
        void stubDependencies() {
            when(personNameService.getRandomName()).thenReturn(FEMALE_NAME);
            when(postalCodeRepository.findRandomPostalCode()).thenReturn(Optional.of(POSTAL_CODE));
        }

        @Test
        @DisplayName("returns a non-null FakePerson")
        void resultIsNotNull() {
            // Act
            FakePerson person = service.getFakePerson();

            // Assert
            assertThat(person).isNotNull();
        }

        @Test
        @DisplayName("CPR is a valid 10-digit string with a valid date portion")
        void cprIsValid() {
            // Act
            FakePerson person = service.getFakePerson();

            // Assert
            assertCprIsValid(person.cpr());
        }

        @Test
        @DisplayName("firstName and lastName are not blank")
        void nameFieldsAreNotBlank() {
            // Act
            FakePerson person = service.getFakePerson();

            // Assert
            assertThat(person.firstName()).isNotBlank();
            assertThat(person.lastName()).isNotBlank();
        }

        @Test
        @DisplayName("gender is 'male' or 'female'")
        void genderIsAValidValue() {
            // Act
            FakePerson person = service.getFakePerson();

            // Assert
            assertThat(person.gender()).isIn("male", "female");
        }

        @Test
        @DisplayName("birthDate is a valid ISO date in range [1900-01-01, today]")
        void birthDateIsValid() {
            // Act
            FakePerson person = service.getFakePerson();
            LocalDate dob = LocalDate.parse(person.birthDate());

            // Assert
            assertThat(dob)
                    .isAfterOrEqualTo(LocalDate.of(1900, 1, 1))
                    .isBeforeOrEqualTo(LocalDate.now());
        }

        @Test
        @DisplayName("address is present and satisfies all field rules")
        void addressIsValid() {
            // Act
            FakePerson person = service.getFakePerson();

            // Assert
            assertAddressIsValid(person.address());
        }

        @Test
        @DisplayName("phoneNumber is an 8-digit string starting with a valid prefix")
        void phoneNumberIsValid() {
            // Act
            FakePerson person = service.getFakePerson();

            // Assert
            assertThat(person.phoneNumber()).matches("\\d{8}");
            assertThat(hasValidPrefix(person.phoneNumber())).isTrue();
        }
    }

    // =========================================================================
    // 7. CROSS-FIELD CONSISTENCY  —  CPR ↔ DOB ↔ Gender
    // =========================================================================

    @Nested
    @DisplayName("7. Cross-field consistency")
    class CrossFieldConsistencyTests {

        @BeforeEach
        void stubPostalCode() {
            // lenient: some tests call getCprNameGender/getCprNameGenderDob which
            // do not touch the address repository, so the stub may not always be used.
            lenient().when(postalCodeRepository.findRandomPostalCode()).thenReturn(Optional.of(POSTAL_CODE));
        }

        @Test
        @DisplayName("CPR date portion encodes the same date as birthDate in getFakePerson() (female)")
        void cprDateMatchesBirthDateForFemale() {
            // Arrange
            when(personNameService.getRandomName()).thenReturn(FEMALE_NAME);

            // Act
            FakePerson person = service.getFakePerson();
            String cpr = person.cpr();
            LocalDate dob = LocalDate.parse(person.birthDate());

            // Assert — CPR layout is DDMMYY…, birthDate is YYYY-MM-DD
            assertThat(cpr.substring(0, 2))
                    .as("CPR day")
                    .isEqualTo(String.format("%02d", dob.getDayOfMonth()));
            assertThat(cpr.substring(2, 4))
                    .as("CPR month")
                    .isEqualTo(String.format("%02d", dob.getMonthValue()));
            assertThat(cpr.substring(4, 6))
                    .as("CPR two-digit year")
                    .isEqualTo(String.format("%02d", dob.getYear() % 100));
        }

        @Test
        @DisplayName("CPR date portion encodes the same date as birthDate in getFakePerson() (male)")
        void cprDateMatchesBirthDateForMale() {
            // Arrange
            when(personNameService.getRandomName()).thenReturn(MALE_NAME);

            // Act
            FakePerson person = service.getFakePerson();
            String cpr = person.cpr();
            LocalDate dob = LocalDate.parse(person.birthDate());

            // Assert
            assertThat(cpr.substring(0, 2)).isEqualTo(String.format("%02d", dob.getDayOfMonth()));
            assertThat(cpr.substring(2, 4)).isEqualTo(String.format("%02d", dob.getMonthValue()));
            assertThat(cpr.substring(4, 6)).isEqualTo(String.format("%02d", dob.getYear() % 100));
        }

        @Test
        @DisplayName("CPR last digit parity matches gender across 100 samples — female always even")
        void cprParityMatchesFemaleGenderAcrossManyPerson() {
            // Arrange
            when(personNameService.getRandomName()).thenReturn(FEMALE_NAME);

            // Act & Assert
            IntStream.range(0, 100).forEach(i -> {
                FakePerson person = service.getFakePerson();
                int lastDigit = Character.getNumericValue(person.cpr().charAt(9));
                assertThat(lastDigit % 2)
                        .as("Female person #%d: CPR last digit must be even, was %d", i, lastDigit)
                        .isEqualTo(0);
            });
        }

        @Test
        @DisplayName("CPR last digit parity matches gender across 100 samples — male always odd")
        void cprParityMatchesMaleGenderAcrossManySamples() {
            // Arrange
            when(personNameService.getRandomName()).thenReturn(MALE_NAME);

            // Act & Assert
            IntStream.range(0, 100).forEach(i -> {
                FakePerson person = service.getFakePerson();
                int lastDigit = Character.getNumericValue(person.cpr().charAt(9));
                assertThat(lastDigit % 2)
                        .as("Male person #%d: CPR last digit must be odd, was %d", i, lastDigit)
                        .isEqualTo(1);
            });
        }

        @Test
        @DisplayName("getCprNameGenderDob: CPR date encodes the same date as birthDate")
        void cprAndDobAreConsistentInCprNameGenderDob() {
            // Arrange
            when(personNameService.getRandomName()).thenReturn(FEMALE_NAME);

            // Act
            FakePerson person = service.getCprNameGenderDob();
            String cpr = person.cpr();
            LocalDate dob = LocalDate.parse(person.birthDate());

            // Assert
            assertThat(cpr.substring(0, 2)).isEqualTo(String.format("%02d", dob.getDayOfMonth()));
            assertThat(cpr.substring(2, 4)).isEqualTo(String.format("%02d", dob.getMonthValue()));
            assertThat(cpr.substring(4, 6)).isEqualTo(String.format("%02d", dob.getYear() % 100));
        }

        @Test
        @DisplayName("getCprNameGender: CPR last digit parity is consistent with gender (female)")
        void cprNameGenderHasConsistentParityForFemale() {
            // Arrange
            when(personNameService.getRandomName()).thenReturn(FEMALE_NAME);

            // Act & Assert — 50 iterations
            IntStream.range(0, 50).forEach(i -> {
                FakePerson person = service.getCprNameGender();
                int lastDigit = Character.getNumericValue(person.cpr().charAt(9));
                assertThat(lastDigit % 2)
                        .as("Female getCprNameGender #%d: last digit must be even", i)
                        .isEqualTo(0);
            });
        }
    }

    // =========================================================================
    // 8. BULK GENERATION  —  getFakePersons(n)
    // =========================================================================

    @Nested
    @DisplayName("8. getFakePersons(n) — bulk generation")
    class GetFakePersonsBulkTests {

        @BeforeEach
        void stubDependencies() {
            // lenient() prevents UnnecessaryStubbingException for invalid-input tests
            // that throw before the mocks are ever called.
            lenient().when(personNameService.getRandomName()).thenReturn(FEMALE_NAME);
            lenient().when(postalCodeRepository.findRandomPostalCode()).thenReturn(Optional.of(POSTAL_CODE));
        }

        // --- Valid inputs ---

        @Test
        @DisplayName("returns exactly 2 persons when n = 2 (lower boundary)")
        void returnsExactlyTwoPersonsAtLowerBoundary() {
            // Act
            List<FakePerson> persons = service.getFakePersons(2);

            // Assert
            assertThat(persons).hasSize(2);
        }

        @Test
        @DisplayName("returns exactly 100 persons when n = 100 (upper boundary)")
        void returnsExactlyOneHundredPersonsAtUpperBoundary() {
            // Act
            List<FakePerson> persons = service.getFakePersons(100);

            // Assert
            assertThat(persons).hasSize(100);
        }

        @Test
        @DisplayName("returns exactly the requested count for a mid-range value")
        void returnsCorrectCountForMidRangeValue() {
            // Act
            List<FakePerson> persons = service.getFakePersons(50);

            // Assert
            assertThat(persons).hasSize(50);
        }

        @Test
        @DisplayName("result list contains no null elements")
        void resultListContainsNoNullElements() {
            // Act
            List<FakePerson> persons = service.getFakePersons(10);

            // Assert
            assertThat(persons).doesNotContainNull();
        }

        @Test
        @DisplayName("every generated person contains a valid CPR")
        void everyPersonHasAValidCpr() {
            // Act
            List<FakePerson> persons = service.getFakePersons(20);

            // Assert
            persons.forEach(p -> assertCprIsValid(p.cpr()));
        }

        @Test
        @DisplayName("every generated person has a valid address")
        void everyPersonHasAValidAddress() {
            // Act
            List<FakePerson> persons = service.getFakePersons(10);

            // Assert
            persons.forEach(p -> assertAddressIsValid(p.address()));
        }

        @ParameterizedTest(name = "n = {0} should return exactly {0} persons")
        @ValueSource(ints = {2, 3, 10, 50, 99, 100})
        @DisplayName("accepts any valid n in [2, 100] and returns the correct count")
        void acceptsAllValidBoundaryAndMidValues(int n) {
            // Act
            List<FakePerson> persons = service.getFakePersons(n);

            // Assert
            assertThat(persons).hasSize(n);
        }

        // --- Invalid inputs ---

        @Test
        @DisplayName("throws IllegalArgumentException when n = 1 (one below minimum)")
        void throwsForOneBelowMinimum() {
            // Act & Assert
            assertThatThrownBy(() -> service.getFakePersons(1))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("throws IllegalArgumentException when n = 0")
        void throwsForZero() {
            // Act & Assert
            assertThatThrownBy(() -> service.getFakePersons(0))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("throws IllegalArgumentException when n is negative")
        void throwsForNegativeInput() {
            // Act & Assert
            assertThatThrownBy(() -> service.getFakePersons(-1))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("throws IllegalArgumentException when n = 101 (one above maximum)")
        void throwsForOneAboveMaximum() {
            // Act & Assert
            assertThatThrownBy(() -> service.getFakePersons(101))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @ParameterizedTest(name = "n = {0} should throw IllegalArgumentException")
        @ValueSource(ints = {-100, -1, 0, 1, 101, 200, 1000})
        @DisplayName("rejects all out-of-range inputs with IllegalArgumentException")
        void rejectsAllOutOfRangeInputs(int n) {
            // Act & Assert
            assertThatThrownBy(() -> service.getFakePersons(n))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("exception message for invalid n mentions the allowed range (2 to 100)")
        void exceptionMessageDescribesAllowedRange() {
            // Act & Assert
            assertThatThrownBy(() -> service.getFakePersons(1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Parameter n must be between 2 and 100.");
        }
    }
}
