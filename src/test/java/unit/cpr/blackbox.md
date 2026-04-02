### Black box technique: CPR

| Partition | Description | Boundary values | Representative test values |
|---|---:|---:|---|
| 9 → MAX INTEGER (invalid) | Too long | 20 | 20 |
| 8 (valid) | Expected valid length | 8 | 7, 8, 9 |
| 1–7 (invalid) | Too short | 1, 7 | 0,1,2,6,7,8 |
| 0 | Empty | 0 | 0,1 |

**List of test values**
- Valid: 8
- Invalid: 0, 1, 2, 6, 7, 8, 9, 10, 20

**Notes / Edge cases**
1. Invalid day (embedded in CPR if applicable)
2. Invalid month (embedded in CPR if applicable)