# BefungeInterpreter
An interpreter for the esoteric coding language Befunge.
(info on the language here: https://esolangs.org/wiki/Befunge)

In this implementation, if you try to place a value outside of the current grid, the grid will automatically extend to fit the new point.  The program counter wraps around based on the current size of the grid. 

To run the program, input a file name as an argument to main(). (For example, to run the test.txt code, the ergument should be `src\hoelzel\jonathan\test.txt` )

The test.txt program is a simple game  ("Less or More") taken from the esolangs site.

I plan to add more options for viewing the code as it runs in the future.