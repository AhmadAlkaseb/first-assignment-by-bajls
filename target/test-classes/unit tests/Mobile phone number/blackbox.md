### Black box technique of: Mobile phone number

**Equivalence partitions:       Test case values        Boundary values         Test case values**
9 -> MAX INTEGER (invalid)      20                      9                       8 9 10
8 (valid)                       8                       8                       7 8 9
1-7 (invalid)                   5                       1 7                     0 1 2 6 7 8
0                               0                       0                       0 1

Valid:      8
Invalid:    0, 1, 2, 6, 7, 8, 9, 10, 20


Country codes
4 -> MAX INTEGER (invalid)      20                      4                       3 4 5
1 -> 3 (valid)                  2                       1 2                     1 2 3
0 (invalid)                     0                       0                       0 1

Valid:      1, 2, 3
Invalid:    0, 4, 5, 20

### Since we cannot have strings that are negative in length, we choose not to do that.

### Edge cases
1. Invalid country code
2. Invalid phone number length
3. Phone number with non-numeric characters
4. Phone number with leading zeros
5. Country code with leading zeros
6. Country code with non-numeric characters