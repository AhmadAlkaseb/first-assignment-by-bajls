### Black box technique of: CPR

**Equivalence partitions:       Test case values        Boundary values         Test case values**
9 → MAX INTEGER (invalid)       20                      9                       8 9 10
8 (valid)                       8                       8                       7 8 9
1-7 (invalid)                   5                       1 7                     0 1 2 6 7 8
0                               0                       0                       0 1

List of test case values:
Valid:      8
Invalid:    0, 1, 2, 6, 7, 8, 9, 10, 20

### Since we cannot have strings that are negative in length, we choose not to do that.

### Edge cases
1. Invalid day
2. Invalid month