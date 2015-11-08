Tsz Kit Lo, 912404574
Chi Kei Loi, 912371115
Michelle Chan, 997458024
Cameron Willment, 997966820


Part 5:

new BNF rules added:

statement ::= assignment | print | do | if | for
for ::= '&' id '=' number '?' id '^' number '?' id '$'

For loops typically have the same format. They begin with 'for(' and end with a closing parenthesis. There may be different variables and numbers within the for loop. If we detect a '&', we know that we are beginning a for loop, and must insert 'for('. Next, we are looking for an assignment or initialization of a variable that we have previously declared. Each instance of the loop increments a number, and this is done until we reach a limit that we set. '$' must be included, otherwise we detect a syntax error. Without a close to the for loop, the rest of program will not work, so we should identify the error and exit.



