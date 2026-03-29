package dk.fakeinfo.model;

public record NameGenderDobResponse(
        String firstName,
        String lastName,
        String gender,
        String birthDate
) {
}
