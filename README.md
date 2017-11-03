# BefungeInterpreter
An interpreter for the esoteric coding language Befunge.
(info on the language here: https://esolangs.org/wiki/Befunge)

Specifics of my implementation:

- The grid is initially only as large as your code.
- If you try to place a value outside of the current grid, the grid will automatically extend to fit the new point.
- The program counter (represented by an 'X' when you print the code) wraps around based on the current size of the grid.

To run the program, input a file name as an argument to main() and enter 'run' into the terminal. (For example, to run the test.txt code, the argument should be `test.txt` )
To run the program using the .jar file, do `java -jar BefungeInterpreter.jar test.txt`. (To run another befunge program, replace `test.txt` with the appropriate file name)

Privided sample programs:

- test.txt : a simple game ("Less or More") taken from the esolangs site.
- Collatz.txt : prints out the collatz sequence starting at a user-provided number
- ReadAndSay.txt : prints out a user-provided number of lines of the "read-and-say series"

I plan to add more options for viewing the code while it runs in the future.