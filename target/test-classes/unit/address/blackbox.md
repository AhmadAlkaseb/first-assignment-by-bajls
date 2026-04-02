### Black box technique of: Addresses

**Equivalence partitions:       Test case values        Boundary values         Test case values**
Adresses
1 → MAX Integer (valid)         10                      1                       0 1 2
0 (invalid)                     0                       0                       0 1

List of test case values:
Valid:      1, 2, 10
Invalid:    0

Number
1 → MAX Integer (valid)         10                      1                       0 1 2
0 (invalid)                     0                       0                       0 1

List of test case values:
Valid:      1, 2, 10
Invalid:    0

Floor
ST (valid)                      ST                      ST                      ST   
1 → 2 (valid)                   2                       1 2                     0 1 2 3 
0 (invalid)                     0                       0                       0 1

List of test case values:
Valid:      ST, 1, 2
Invalid:    0, 3

Door
th (valid)                      th                      th                      th
mf (valid)                      mf                      mf                      mf
tv (valid)                      tv                      tv                      tv
number
1 → 50 (valid)                  10                      1                       0 1 2
0 (invalid)                     0                       0                       0 1

List of test case values:
Valid:      th, mf, tv, 1, 2, 10
Invalid:    0

letter followed by a dash and 3 digits
4 (valid)                       4                       4                       3 4 5
1-3 (invalid)                   2                       1 3                     0 1 2 3 4
0 (invalid)                     0                       0                       0 1

List of test case values:
Valid:      4
Invalid:    0, 1, 2, 3, 5

Postal code
5 → MAX Integer (valid)         10                      5                       4 5 6
4 (valid)                       4                       4                       3 4 5
1 → 3 (invalid)                 2                       1 3                     0 1 2 3 4
0 (invalid)                     0                       0                       0 1

List of test case values:
Valid:      4, 5, 6, 10
Invalid:    0, 1, 2, 3

City
1 → MAX Integer (valid)         10                      1                       0 1 2
0 (invalid)                     0                       0                       0 1

List of test case values:
Valid:      1, 2, 10
Invalid:    0

### Since we cannot have strings that are negative in length, we choose not to do that.

### Edge cases
1. Addresses as numbers
2. Postal code as characters
3. Floor as characters other than st