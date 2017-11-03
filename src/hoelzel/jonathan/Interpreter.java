package hoelzel.jonathan;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;

public class Interpreter {

    private enum Dir{
        UP, DOWN, LEFT, RIGHT
    }

    private ArrayList<ArrayList<Character>> grid;
    private ArrayList<Integer> stack = new ArrayList<Integer>();
    private int x = 0;
    private int y = 0;
    private int height;
    private int width;
    private Dir direction = Dir.RIGHT;
    private boolean stringMode = false;
    private Scanner in;
    private PrintStream out;
    private boolean done = false;

    public Interpreter(String code, InputStream inStream, PrintStream outStream){
        out = outStream;
        in = new Scanner(inStream);

        String[] lines = code.split(System.getProperty("line.separator"));
        grid = new ArrayList<ArrayList<Character>>();
        height = lines.length;
        width = 0;
        for (String line : lines){
            Character[] charArr = new Character[line.length()];
            if (charArr.length > width) width = charArr.length;

            for (int i = 0; i < line.length(); i ++){
                charArr[i] = line.charAt(i);
            }

            grid.add(new ArrayList<Character>(Arrays.asList(charArr)));
        }
    }

    public boolean done(){
        return done;
    }

    public void step(){
        if (done) return;
        evalPosition();
        if (done) return;
        move();
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < grid.size(); i ++){
            ArrayList<Character> row = grid.get(i);

            for (int j = 0; j < row.size(); j ++){
                Character c = (i == y && j == x)? 'X' : row.get(j);
                builder.append(c);
            }

            // add X later if it is past the end of the row
            if (i == y && x >= row.size()){
                for (int n = row.size(); n < x; n ++){
                    builder.append(' ');
                }
                builder.append('X');
            }

            builder.append('\n');
        }

        builder.append("Stack: ");
        for (int n : stack){
            builder.append(n);
            builder.append(" ");
        }
        builder.append('\n');

        return builder.toString();
    }

    private void evalPosition(){
        char gridVal = getGridVal(x, y);

        if (gridVal == '\"'){
            stringMode = !stringMode;
        } else if (stringMode) {
            pushStack(gridVal);
        } else {
            switch (gridVal){
                case ' ':
                    break;
                case '+':
                    pushStack(popStack() + popStack());
                    break;
                case '-':
                    int a = popStack();
                    int b = popStack();
                    pushStack(b - a);
                    break;
                case '*':
                    pushStack(popStack() * popStack());
                    break;
                case '/':
                    a = popStack();
                    b = popStack();
                    if (a == 0) pushStack(getUserInt());
                    else pushStack(b / a);
                    break;
                case '%':
                    a = popStack();
                    b = popStack();
                    pushStack(b % a);
                    break;
                case '!':
                    pushStack(popStack() == 0? 1 : 0);
                    break;
                case '`':
                    a = popStack();
                    b = popStack();
                    pushStack((b > a)? 1 : 0);
                    break;
                case '>':
                    direction = Dir.RIGHT;
                    break;
                case '<':
                    direction = Dir.LEFT;
                    break;
                case '^':
                    direction = Dir.UP;
                    break;
                case 'v':
                    direction = Dir.DOWN;
                    break;
                case '?':
                    int r = (int)(rand() * 4);
                    if (r == 0) direction = Dir.DOWN;
                    else if (r == 1) direction = Dir.LEFT;
                    else if (r == 2) direction = Dir.RIGHT;
                    else direction = Dir.UP;
                    break;
                case '_':
                    if (popStack() == 0) direction = Dir.RIGHT;
                    else direction = Dir.LEFT;
                    break;
                case '|':
                    if (popStack() == 0) direction = Dir.DOWN;
                    else direction = Dir.UP;
                    break;
                case ':':
                    duplicateTopStack();
                    break;
                case '\\':
                    swapStack();
                    break;
                case '$':
                    popStack();
                    break;
                case '.':
                    out.print(popStack() + " ");
                    break;
                case ',':
                    out.print((char)popStack());
                    break;
                case '#':
                    move();
                    break;
                case 'g':
                    int y = popStack();
                    int x = popStack();
                    pushStack(getGridVal(x, y));
                    break;
                case 'p':
                    y = popStack();
                    x = popStack();
                    char v = (char)popStack();
                    setGridVal(x, y, v);
                    break;
                case '&':
                    pushStack(getUserInt());
                    break;
                case '~':
                    pushStack(getUserChar());
                    break;
                case '@':
                    done = true;
                    break;
                default:
                    try {
                        pushStack(Integer.parseInt("" + gridVal));
                    } catch (NumberFormatException ex){
                        //out.println("Warning: illegal command executed");
                    }
                    break;
            }
        }
    }

    private double rand(){
        return Math.random();
    }

    /**
     * Sets the grid position at (x, y) to c.  Extends the width and height of the space if necessary
     */
    private void setGridVal(int x, int y, char c){
        if (x >= width) width = x+1;
        if (y >= height) {
            while (grid.size() <= y){
                grid.add(new ArrayList<Character>());
            }

            height = y+1;
        }

        List<Character> row = grid.get(y);
        while (row.size() <= x){
            row.add(' ');
        }

        row.set(x, c);
    }

    private char getGridVal(int x, int y){
        try {
            return grid.get(y).get(x);
        } catch (IndexOutOfBoundsException ex){
            return ' ';
        }
    }

    /**
     * Moves the program counter in the correct direction, accounting for wraparound
     */
    private void move(){
        switch (direction) {
            case UP:
                y --;
                if (y < 0) y += height;
                break;
            case DOWN:
                y ++;
                if (y >= height) y -= height;
                break;
            case LEFT:
                x --;
                if (x < 0) x += width;
                break;
            case RIGHT:
                x ++;
                if (x >= width) x -= width;
                break;
            default:
                out.println("The instruction pointer is moving in an invalid direction");
        }
    }

    /**
     * @return a character input by the user
     */
    private char getUserChar(){
        out.println();
        out.print("Enter a character: ");
        String s = in.nextLine();
        if (s.length() == 1) return s.charAt(0);
        else {
            System.out.println("Only enter one character at a time.");
            return getUserChar();
        }
    }

    /**
     * @return an integer input by the user
     */
    private int getUserInt(){
        out.println();
        out.print("Enter an integer: ");
        int ret;
        String s = in.nextLine();

        try {
            ret = Integer.parseInt(s);
        } catch (NumberFormatException ex){
            out.println("Invalid input.");
            return getUserInt();
        }
        return ret;
    }

    /**
     * removes and returns the top value from the stack
     */
    private int popStack(){
        if (stack.isEmpty()) {
            //System.out.println("Warning: attempting to pop from empty stack");
            return 0;
        }

        int ret = stack.get(stack.size() - 1);
        stack.remove(stack.size() - 1);
        return ret;
    }

    private void pushStack(int i){
        stack.add(i);
    }

    private void duplicateTopStack(){
        if (stack.isEmpty()) {
            stack.add(0);
            //System.out.println("Warning: attempting to duplicate top element of empty stack");
        }
        else stack.add(stack.get(stack.size() - 1));
    }

    private void swapStack(){
        if (stack.size() < 2) {
            if (!stack.isEmpty()) stack.add(0);
            //System.out.println("Warning: attempting to swap elements of empty stack");
        }
        else {
            int temp = stack.get(stack.size() - 1);
            stack.set(stack.size() - 1, stack.get(stack.size() - 2));
            stack.set(stack.size() - 2, temp);
        }
    }
}
