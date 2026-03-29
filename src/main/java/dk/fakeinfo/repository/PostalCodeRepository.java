package dk.fakeinfo.repository;

import dk.fakeinfo.model.PostalCode;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostalCodeRepository extends JpaRepository<PostalCode, String> {

    @Query(value = """
            SELECT postal_code, town_name
            FROM postal_code
            ORDER BY random()
            LIMIT 1
            """, nativeQuery = true)
    Optional<PostalCode> findRandomPostalCode();
}
