# Fake Info API

Maven-baseret Java REST API i mappen `first-assignment-by-bajls`, der genererer falske oplysninger om danske personer.

## Teknologi

- Java 17
- Maven
- Spring Boot
- Hibernate / Spring Data JPA
- PostgreSQL

## Datakilder

- Navne og køn læses fra `data/person-names.json`
- Postnumre og bynavne læses fra PostgreSQL-tabellen `postal_code`

## Databaseopsætning

Applikationen bruger fast konfiguration i `application.yml` og forventer:

- host: `localhost`
- port: `5432`
- database: `addresses`
- user: `postgres`
- password: `postgres`

Tabelstruktur:

```sql
CREATE TABLE postal_code (
    postal_code CHAR(4) PRIMARY KEY,
    town_name VARCHAR(64) NOT NULL
);
```

Importér seeddata fra [db/postgres/addresses-postgres.sql](/c:/Users/baban/OneDrive/Skrivebord/fake_info-main/first-assignment-by-bajls/db/postgres/addresses-postgres.sql).

## Kør projektet

```bash
mvn clean package
mvn spring-boot:run
```

Når applikationen kører, kan du åbne brugerfladen på:

```text
http://localhost:8080/
```

Frontend og backend kører sammen i samme Spring Boot-applikation.

## CI/CD

Projektet er sat op med GitHub Actions i `.github/workflows`.

- `ci.yml` kører ved push til `main` og ved pull requests mod `main`. Den bygger projektet med `mvn verify` og validerer også Docker-buildet.

Det betyder, at vi allerede nu har kontinuerlig integration.

## API endpoints

| Method | Endpoint |
|------|--------|
| GET | `/cpr` |
| GET | `/name-gender` |
| GET | `/name-gender-dob` |
| GET | `/cpr-name-gender` |
| GET | `/cpr-name-gender-dob` |
| GET | `/address` |
| GET | `/phone` |
| GET | `/person` |
| GET | `/person?n=2..100` |
