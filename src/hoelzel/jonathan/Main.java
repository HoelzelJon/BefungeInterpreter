package hoelzel.jonathan;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        if (args.length < 1){
            System.out.println("Please include a file to interpret");
            return;
        }

        String path = args[0];

        byte[] encoded;
        try{
            encoded = Files.readAllBytes(Paths.get(path));
        } catch (IOException ex){
            System.out.println("Error reading from file");
            return;
        }

        String code = new String(encoded, Charset.defaultCharset());
        Interpreter inter = new Interpreter(code, System.in, System.out);

        boolean running = false;
        boolean runningSteps = false;
        int steps = -1;
        int runWait = 5;
        while (!inter.done()){
            if (running && !(runningSteps && steps <= 0)) {
                try {
                    Thread.sleep(runWait);
                } catch (InterruptedException ex){
                    // do nothing
                }
                if (runningSteps) steps --;
                inter.step();
            }
            else {
                System.out.print(">> ");
                String input = in.nextLine();
                if (input.equalsIgnoreCase("S") || input.equalsIgnoreCase("step")){
                    inter.step();
                } else if (input.equalsIgnoreCase("r") || input.equalsIgnoreCase("run")){
                    runningSteps = false;
                    running = true;
                } else if (input.substring(0, 1).equalsIgnoreCase("r") || (input.length() >= 3 && input.substring(0, 3).equalsIgnoreCase("run"))){
                    String num = input.substring(input.indexOf(' ') + 1);
                    boolean valid = num.length() > 0;
                    try {
                        steps = Integer.parseInt(num);
                    } catch (NumberFormatException ex) {
                        valid = false;
                    }

                    if (valid){
                        runningSteps = true;
                        running = true;
                    } else  System.out.println("Invalid command. expected \"(r)un [x]\", where [x] is some integer");
                } else if (input.equalsIgnoreCase("q") || input.equalsIgnoreCase("quit")){
                    System.out.println("Quitting the program.");
                    return;
                } else if (input.equalsIgnoreCase("p") || input.equalsIgnoreCase("print")){
                    System.out.println(inter);
                } else if(input.equalsIgnoreCase("h") || input.equalsIgnoreCase("help")){
                    System.out.println("Commands: \n" +
                            " (r)un [x]: runs the program for x steps (if no x provided, program runs until termination)\n" +
                            " (s)tep : advances 1 step through the program\n" +
                            " (p)rint : print out the current state of the program\n" +
                            " (q)uit : quits the program");
                }
                else System.out.println("Unrecognized command. Try \"help\" for a list of commands.");
            }
        }
        System.out.println();
        System.out.println("The program has ended.");
    }
}
