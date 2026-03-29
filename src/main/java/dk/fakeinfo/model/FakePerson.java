package dk.fakeinfo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FakePerson(
        @JsonProperty("cpr") String cpr,
        String firstName,
        String lastName,
        String gender,
        String birthDate,
        Address address,
        String phoneNumber
) {
}
