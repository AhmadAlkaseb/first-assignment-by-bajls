### Black box technique: Addresses

#### Address line / Number / City

| Field | Partition | Description | Boundary values | Representative test values |
|---|---|---:|---:|---|
| Address lines | 1 → MAX Integer (valid) | 1+ chars | 1,10 | 0,1,2,10 |
| Number | 1 → MAX Integer (valid) | Numeric house number | 1,10 | 0,1,2,10 |
| City | 1 → MAX Integer (valid) | City name length | 1,10 | 0,1,2,10 |

#### Floor

| Partition | Description | Boundary values | Representative test values |
|---|---:|---:|---|
| `ST` (valid) | Text floor indicator | `ST` | `ST` |
| 1 → 2 (valid) | Numeric floor (1–2 valid) | 1,2 | 0,1,2,3 |

#### Door

| Partition | Description | Boundary values | Representative test values |
|---|---:|---:|---|
| `th`, `mf`, `tv` (valid) | Letter codes | - | `th`, `mf`, `tv` |
| number (1–50 valid) | Numeric door number | 1,50 | 0,1,2,10 |

#### Letter-dash-3digits (special door format)

| Partition | Description | Boundary values | Representative test values |
|---|---:|---:|---|
| 4 (valid) | Format like `A-123` | 4 | valid example |
| 1–3 (invalid) | Too short | 1,3 | 0,1,2,3,4 |

#### Postal code

| Partition | Description | Boundary values | Representative test values |
|---|---:|---:|---|
| 5 → MAX Integer (valid) | Typical 5-digit postal code | 5,10 | 4,5,6,10 |
| 1–3 (invalid) | Too short | 1,3 | 0,1,2,3,4 |

**List of test values**
- Address fields valid: 1,2,10
- Invalid: 0 for fields that must be present; postal code and door-format specific invalids as above

**Edge cases**
1. Numeric-only addresses
2. Postal code using letters
3. Floor values other than documented codes