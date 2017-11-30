# BefungeInterpreter
An interpreter for the esoteric coding language Befunge (specifically, Befunge-93).
(info on the language here: https://esolangs.org/wiki/Befunge)

Specifics of my implementation:

- The grid is initially only as large as your code.
- If you try to place a value outside of the current grid, the grid will automatically extend to fit the new point.
- The program counter (represented by the cyan-colored square in the display) wraps around based on the current size of the grid.
- The code grid is stored as Java chars, so values from 0-65,535 should be stored there properly.
- If a value in the code is not printable, its integer value is displayed in red instead.
- Values on the stack are stored as Java ints, so values from -(2^31) to (2^31 - 1) should be stored there without overflow problems.
- If the interpreter encounters an invalid command, then it will print a message stating this, but will otherwise continue as if the space were blank.

To run the program, input a file name as an argument to main(). (For example, to run the Collatz.txt code, the argument should be `Collatz.txt` )
To run the program using the .jar file, do `java -jar BefungeInterpreter.jar Collatz.txt`. (To run another befunge program, replace `Collatz.txt` with the appropriate file name)

The program opens a window that graphically displays your code.  You can control the execution of the code through the terminal.
Once the program is running, type 'h' or 'help' into the terminal for a list of commands.

Provided sample programs:

- HelloWorld.txt : a simple "Hello World" program (taken from the esolangs site)
- Collatz.txt : prints out the collatz sequence starting at a user-provided number
- ReadAndSay.txt : prints out a user-provided number of lines of the "read-and-say series"