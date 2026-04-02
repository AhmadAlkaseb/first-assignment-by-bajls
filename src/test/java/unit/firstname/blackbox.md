### Black box technique: First name

| Partition | Description | Boundary values | Representative test values |
|---|---:|---:|---|
| 1 → MAX-INTEGER (valid) | Min 1 char up to practical max | 1, 10 | 0,1,2,10 |
| 0 (invalid) | Empty string | 0 | 0,1 |

**List of test values**
- Valid: 1, 2, 10
- Invalid: 0

**Notes / Edge cases**
1. Numeric-only names — expect failure
2. Non-alphabetic characters — validate policy (accept/reject)