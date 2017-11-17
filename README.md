# BefungeInterpreter
An interpreter for the esoteric coding language Befunge.
(info on the language here: https://esolangs.org/wiki/Befunge)

Specifics of my implementation:

- The grid is initially only as large as your code.
- If you try to place a value outside of the current grid, the grid will automatically extend to fit the new point.
- The program counter (represented by the cyan-colored square in the display) wraps around based on the current size of the grid.
- The code grid is stored as Java chars, so values from 0-65,535 should be stored there properly.
- If a value in the code is less than 32 (' '), its integer value is displayed in red instead.
- Values on the stack are stored as Java ints, so values from (-2^31) to (2^31 - 1) should be stored there without overflow.

To run the program, input a file name as an argument to main(). (For example, to run the test.txt code, the argument should be `test.txt` )
To run the program using the .jar file, do `java -jar BefungeInterpreter.jar test.txt`. (To run another befunge program, replace `test.txt` with the appropriate file name)

Once the program is running, type 'h' or 'help' into the terminal for a list of commands.

Provided sample programs:

- test.txt : a simple game ("Less or More") taken from the esolangs site.
- Collatz.txt : prints out the collatz sequence starting at a user-provided number
- ReadAndSay.txt : prints out a user-provided number of lines of the "read-and-say series"

I will probably add the stack to the JFrame display soon.