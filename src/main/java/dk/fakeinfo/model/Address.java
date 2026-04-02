package dk.fakeinfo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Address(
        String street,
        String number,
        Object floor,
        



        Object door,
        @JsonProperty("postal_code") String postalCode,
        @JsonProperty("town_name") String townName
) {
}
