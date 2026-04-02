### Black box technique: Mobile phone number

#### Phone number length

| Partition | Description | Boundary values | Representative test values |
|---|---:|---:|---|
| 9 → MAX INTEGER (invalid) | Too long | 20 | 20 |
| 8 (valid) | Expected length | 8 | 7,8,9 |
| 1–7 (invalid) | Too short | 1,7 | 0,1,2,6,7,8 |
| 0 | Empty | 0 | 0,1 |

**Country code length**

| Partition | Description | Boundary values | Representative test values |
|---|---:|---:|---|
| 4 → MAX INTEGER (invalid) | Too long | 20 | 20 |
| 1–3 (valid) | Typical country codes | 1,2,3 | 1,2,3 |
| 0 (invalid) | Empty | 0 | 0,1 |

**List of test values**
- Phone valid: 8
- Phone invalid: 0,1,2,6,7,8,9,10,20
- Country code valid: 1,2,3
- Country code invalid: 0,4,5,20

**Edge cases**
1. Invalid country code
2. Invalid phone number length
3. Non-numeric characters in phone number
4. Leading zeros in phone number or country code