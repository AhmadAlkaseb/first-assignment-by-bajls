### Black box technique: Date of birth

#### Year / Month / Day (same partitioning applied to each component)

| Partition | Description | Boundary values | Representative test values |
|---|---:|---:|---|
| 3 → MAX INTEGER (invalid) | Too large | 5 | 5, 4 |
| 2 (valid) | Expected valid length | 2 | 1, 2, 3 |
| 1 (invalid) | Too short | 1 | 0,1,2 |
| 0 (invalid) | Empty | 0 | 0,1 |

**List of test values**
- Valid: 2 (for each of year, month, day where 2-digit expected)
- Invalid: 0, 1, 2, 3, 4, 5 (use as appropriate per field)

**Edge cases**
1. Invalid day (e.g., 31 in April)
2. Invalid month (e.g., 00 or 13)
3. Invalid year (out-of-range or century ambiguity)