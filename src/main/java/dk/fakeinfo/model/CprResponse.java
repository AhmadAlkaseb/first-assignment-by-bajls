package dk.fakeinfo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CprResponse(@JsonProperty("cpr") String cpr) {
}
