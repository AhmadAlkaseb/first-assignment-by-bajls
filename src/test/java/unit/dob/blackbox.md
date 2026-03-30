### Black box technique of: Date of birth

**Equivalence partitions:       Test case values        Boundary values         Test case values**
Year
3 → MAX INTEGER (invalid)       5                       3                       2 3 4 
2 (valid)                       2                       2                       1 2 3
1 (invalid)                     1                       1                       0 1 2
0 (invalid)                     0                       0                       0 1

Month
3 → MAX INTEGER (invalid)       5                       3                       2 3 4   
2 (valid)                       2                       2                       1 2 3
1 (invalid)                     1                       1                       0 1 2
0 (invalid)                     0                       0                       0 1

Day
3 → MAX INTEGER (invalid)       5                       3                       2 3 4
2 (valid)                       2                       2                       1 2 3
1 (invalid)                     1                       1                       0 1 2
0 (invalid)                     0                       0                       0 1

List of test case values:
Valid:      2
Invalid:    0, 1, 2, 3, 4, 5

### Since we cannot have strings that are negative in length, we choose not to do that.

### Edge cases
1. Invalid day
2. Invalid month
3. Invalid year