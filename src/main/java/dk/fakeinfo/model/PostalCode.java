package dk.fakeinfo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "postal_code")
public class PostalCode {

    @Id
    @Column(name = "postal_code", nullable = false, length = 4)
    private String postalCode;

    @Column(name = "town_name", nullable = false, length = 64)
    private String townName;

    protected PostalCode() {
    }

    public PostalCode(String postalCode, String townName) {
        this.postalCode = postalCode;
        this.townName = townName;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getTownName() {
        return townName;
    }
}
