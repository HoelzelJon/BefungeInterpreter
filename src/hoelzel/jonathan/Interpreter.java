package hoelzel.jonathan;

import javax.swing.*;
import java.awt.*;
import java.io.PrintStream;
import java.util.*;
import java.util.List;

public class Interpreter {
    private enum Dir{
        UP, DOWN, LEFT, RIGHT
    }

    private List<List<Character>> grid;
    private List<Integer> stack = new ArrayList<Integer>();
    private int x = 0;
    private int y = 0;
    private int height;
    private int width;
    private Dir direction = Dir.RIGHT;
    private boolean stringMode = false;
    private Scanner in;
    private PrintStream out;
    private boolean done = false;

    private JFrame frame;

    public Interpreter(String code, JFrame jframe, Scanner scan, PrintStream outStream){
        frame = jframe;
        frame.add(new InterpreterComponent());

        out = outStream;
        in = scan;

        String[] lines = code.split(System.getProperty("line.separator"));
        grid = new ArrayList<List<Character>>();
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

        frame.setSize(500, 500);
        frame.setVisible(true);
    }

    public boolean done(){
        return done;
    }

    public void step(){
        if (done) return;
        evalPosition();
        if (!done){
            move();
        }
    }

    public void refreshPane(){
        frame.repaint();
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < grid.size(); i ++){
            List<Character> row = grid.get(i);

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

        builder.append(getStackString());
        return builder.toString();
    }

    private String getStackString(){
        StringBuilder builder = new StringBuilder();

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
                    int yCoord = popStack();
                    int xCoord = popStack();
                    pushStack(getGridVal(xCoord, yCoord));
                    break;
                case 'p':
                    yCoord = popStack();
                    xCoord = popStack();
                    char v = (char)popStack();
                    setGridVal(xCoord, yCoord, v);
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
                        out.println("Warning: encountered an illegal command at (" + x + ", " + y + ")");
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

        boolean validInput = false;
        int ret = 0;

        while (!validInput){
            out.print("Enter an integer: ");

            if (in.hasNextInt()){
                validInput = true;
                ret = in.nextInt();
            }
            in.nextLine();
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

    private class InterpreterComponent extends Component {
        private static final double FONT_MULT = 0.85;
        private static final double RIGHT_SHIFT_MULT = 0.2;
        private static final double DOWN_SHIFT_MULT = 0.8;

        private static final int STACK_FONT_SIZE = 30;
        private static final double STACK_HEIGHT_MULT = 1.2;
        private static final int STACK_HEIGHT = (int)(STACK_FONT_SIZE * STACK_HEIGHT_MULT);

        @Override
        public void paint(Graphics g){
            Graphics2D g2 = (Graphics2D) g;

            Rectangle bounds = getBounds();

            g2.setFont(new Font("Default", Font.PLAIN, STACK_FONT_SIZE));

            String stackString = "ERROR";
            boolean gotString = false;
            while (!gotString){
                boolean failed = false;
                try {
                    stackString = getStackString();
                } catch (ConcurrentModificationException ex){
                    failed = true;
                }
                gotString = !failed;
            }
            g2.drawString(stackString, 0, bounds.height - 10);

            double squareSize = Math.min((double)(bounds.height - STACK_HEIGHT) / height, (double)bounds.width / width);

            g2.setColor(Color.CYAN);
            g2.fillRect((int)(x * squareSize), (int)(y * squareSize), (int)squareSize, (int)squareSize);

            g2.setColor(Color.BLACK);
            Font gridFont = new Font("Default", Font.PLAIN, (int)(squareSize * FONT_MULT));
            g2.setFont(gridFont);

            // draw vertical lines
            for (int i = 1; i <= width; i ++){
                int xPos = (int)(i * squareSize);
                g2.drawLine(xPos, 0, xPos, (int)(squareSize * height));
            }

            // draw horizontal lines
            for (int i = 1; i <= height; i ++){
                int yPos = (int)(i * squareSize);
                g2.drawLine(0, yPos, (int)(squareSize * width), yPos);
            }

            int rightShift = (int)(squareSize * RIGHT_SHIFT_MULT);
            int downShift = (int)(squareSize * DOWN_SHIFT_MULT);

            for (int yPos = 0; yPos < height; yPos ++){
                List<Character> row = grid.get(yPos);
                for (int xPos = 0; xPos < row.size(); xPos ++){
                    char c = row.get(xPos);

                    if (!gridFont.canDisplay(c)){
                        g2.setColor(Color.RED);
                        g2.drawString("" + (int)c, (int)(xPos * squareSize) + rightShift, (int)(yPos * squareSize) + downShift);
                    } else if (c != ' '){
                        g2.setColor(Color.BLACK);
                        g2.drawString("" + c, (int)(xPos * squareSize) + rightShift, (int)(yPos * squareSize) + downShift);
                    }
                }
            }
        }
    }
}
