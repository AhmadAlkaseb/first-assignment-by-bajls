package dk.fakeinfo.service;

import dk.fakeinfo.model.Address;
import dk.fakeinfo.model.FakePerson;
import dk.fakeinfo.model.NameGenderDobResponse;
import dk.fakeinfo.model.NameGenderResponse;
import dk.fakeinfo.model.PostalCode;
import dk.fakeinfo.model.PersonName;
import dk.fakeinfo.repository.PostalCodeRepository;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import org.springframework.stereotype.Service;

@Service
public class FakePersonService {

    private static final int MIN_BULK_PERSONS = 2;
    private static final int MAX_BULK_PERSONS = 100;
    private static final String FEMALE = "female";
    private static final List<String> PHONE_PREFIXES = List.of(
            "2", "30", "31", "40", "41", "42", "50", "51", "52", "53", "60", "61", "71", "81", "91",
            "92", "93", "342", "344", "345", "346", "347", "348", "349", "356", "357", "359", "362",
            "365", "366", "389", "398", "431", "441", "462", "466", "468", "472", "474", "476", "478",
            "485", "486", "488", "489", "493", "494", "495", "496", "498", "499", "542", "543", "545",
            "551", "552", "556", "571", "572", "573", "574", "577", "579", "584", "586", "587", "589",
            "597", "598", "627", "629", "641", "649", "658", "662", "663", "664", "665", "667", "692",
            "693", "694", "697", "771", "772", "782", "783", "785", "786", "788", "789", "826", "827",
            "829"
    );
    private static final char[] STREET_CHARS = (
            " abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZæøåÆØÅ"
    ).toCharArray();
    private static final char[] LOWERCASE_LETTERS = (
            "abcdefghijklmnopqrstuvwxyzøæå"
    ).toCharArray();

    private final PersonNameService personNameService;
    private final PostalCodeRepository postalCodeRepository;

    public FakePersonService(PersonNameService personNameService, PostalCodeRepository postalCodeRepository) {
        this.personNameService = personNameService;
        this.postalCodeRepository = postalCodeRepository;
    }

    public String getFakeCpr() {
        GeneratedPerson person = generatePerson();
        return person.cpr();
    }

    public NameGenderResponse getNameGender() {
        PersonName personName = personNameService.getRandomName();
        return new NameGenderResponse(personName.firstName(), personName.lastName(), personName.gender());
    }

    public NameGenderDobResponse getNameGenderDob() {
        PersonName personName = personNameService.getRandomName();
        LocalDate birthDate = randomBirthDate();
        return new NameGenderDobResponse(
                personName.firstName(),
                personName.lastName(),
                personName.gender(),
                birthDate.toString()
        );
    }

    public FakePerson getCprNameGender() {
        GeneratedPerson person = generatePerson();
        return new FakePerson(person.cpr(), person.name().firstName(), person.name().lastName(), person.name().gender(), null, null, null);
    }

    public FakePerson getCprNameGenderDob() {
        GeneratedPerson person = generatePerson();
        return new FakePerson(
                person.cpr(),
                person.name().firstName(),
                person.name().lastName(),
                person.name().gender(),
                person.birthDate().toString(),
                null,
                null
        );
    }

    public Address getAddress() {
        return randomAddress();
    }

    public String getPhoneNumber() {
        return randomPhoneNumber();
    }

    public FakePerson getFakePerson() {
        GeneratedPerson person = generatePerson();
        return new FakePerson(
                person.cpr(),
                person.name().firstName(),
                person.name().lastName(),
                person.name().gender(),
                person.birthDate().toString(),
                randomAddress(),
                randomPhoneNumber()
        );
    }

    public List<FakePerson> getFakePersons(int amount) {
        if (amount < MIN_BULK_PERSONS || amount > MAX_BULK_PERSONS) {
            throw new IllegalArgumentException("Parameter n must be between 2 and 100.");
        }

        return IntStream.range(0, amount)
                .mapToObj(index -> getFakePerson())
                .toList();
    }

    private GeneratedPerson generatePerson() {
        PersonName personName = personNameService.getRandomName();
        LocalDate birthDate = randomBirthDate();
        String cpr = generateCpr(birthDate, personName.gender());
        return new GeneratedPerson(personName, birthDate, cpr);
    }

    private LocalDate randomBirthDate() {
        LocalDate start = LocalDate.of(1900, 1, 1);
        LocalDate end = LocalDate.now();
        long days = ChronoUnit.DAYS.between(start, end);
        long randomDays = ThreadLocalRandom.current().nextLong(days + 1);
        return start.plusDays(randomDays);
    }

    private String generateCpr(LocalDate birthDate, String gender) {
        StringBuilder cpr = new StringBuilder();
        cpr.append(String.format("%02d%02d%02d", birthDate.getDayOfMonth(), birthDate.getMonthValue(), birthDate.getYear() % 100));
        cpr.append(randomDigit());
        cpr.append(randomDigit());
        cpr.append(randomDigit());

        int lastDigit = ThreadLocalRandom.current().nextInt(10);
        if (FEMALE.equals(gender) && lastDigit % 2 != 0) {
            lastDigit = (lastDigit + 1) % 10;
        }
        if (!FEMALE.equals(gender) && lastDigit % 2 == 0) {
            lastDigit = (lastDigit + 1) % 10;
        }
        cpr.append(lastDigit);
        return cpr.toString();
    }

    private Address randomAddress() {
        PostalCode postalCode = postalCodeRepository.findRandomPostalCode()
                .orElseThrow(() -> new IllegalStateException("No postal codes found in database."));
        String houseNumber = Integer.toString(ThreadLocalRandom.current().nextInt(1, 1000));
        if (ThreadLocalRandom.current().nextInt(10) < 2) {
            houseNumber += Character.toUpperCase(randomLowercaseLetter());
        }

        Object floor = ThreadLocalRandom.current().nextInt(10) < 3
                ? "st"
                : ThreadLocalRandom.current().nextInt(1, 100);

        int doorType = ThreadLocalRandom.current().nextInt(1, 21);
        Object door;
        if (doorType < 8) {
            door = "th";
        } else if (doorType < 15) {
            door = "tv";
        } else if (doorType < 17) {
            door = "mf";
        } else if (doorType < 19) {
            door = ThreadLocalRandom.current().nextInt(1, 51);
        } else {
            StringBuilder value = new StringBuilder().append(randomLowercaseLetter());
            if (doorType == 20) {
                value.append('-');
            }
            value.append(ThreadLocalRandom.current().nextInt(1, 1000));
            door = value.toString();
        }

        return new Address(
                randomText(40, true),
                houseNumber,
                floor,
                door,
                postalCode.getPostalCode(),
                postalCode.getTownName()
        );
    }

    private String randomPhoneNumber() {
        String prefix = PHONE_PREFIXES.get(ThreadLocalRandom.current().nextInt(PHONE_PREFIXES.size()));
        StringBuilder phone = new StringBuilder(prefix);
        while (phone.length() < 8) {
            phone.append(randomDigit());
        }
        return phone.toString();
    }

    private String randomText(int length, boolean includeDanishCharacters) {
        char[] source = includeDanishCharacters ? STREET_CHARS : " abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        StringBuilder text = new StringBuilder();
        text.append(source[ThreadLocalRandom.current().nextInt(1, source.length)]);
        for (int i = 1; i < length; i++) {
            text.append(source[ThreadLocalRandom.current().nextInt(source.length)]);
        }
        return text.toString();
    }

    private char randomLowercaseLetter() {
        return LOWERCASE_LETTERS[ThreadLocalRandom.current().nextInt(LOWERCASE_LETTERS.length)];
    }

    private int randomDigit() {
        return ThreadLocalRandom.current().nextInt(10);
    }

    private record GeneratedPerson(PersonName name, LocalDate birthDate, String cpr) {
    }
}
