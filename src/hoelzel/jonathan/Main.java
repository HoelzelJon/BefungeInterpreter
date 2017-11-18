package hoelzel.jonathan;

import javax.swing.*;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
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

        Scanner in = new Scanner(System.in);

        JFrame frame = new JFrame(path);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        String code = new String(encoded, Charset.defaultCharset());
        Interpreter inter = new Interpreter(code, frame, in, System.out);

        boolean running = false;
        boolean runningSteps = false;
        int steps = -1;
        int runWait = 0;

        while (!inter.done()){
            if (running && !(runningSteps && steps <= 0)) {
                if (runningSteps) steps --;

                if (runWait > 0){
                    try {
                        Thread.sleep(runWait);
                    } catch (InterruptedException ex){
                        // do nothing
                    }
                }

                inter.step();
            }
            else {
                System.out.println();
                System.out.print(">> ");
                String input = in.nextLine();
                if (input.length() == 0) continue;
                if (input.equalsIgnoreCase("S") || input.equalsIgnoreCase("step")){
                    inter.step();
                } else if (input.equalsIgnoreCase("r") || input.equalsIgnoreCase("run")){
                    boolean validInput = false;

                    while (!validInput) {
                            System.out.print("Number of steps to run (-1 to run until program ends): ");
                            if (in.hasNextInt()){
                                validInput = true;
                                int numSteps = in.nextInt();
                                in.nextLine();

                                if (numSteps < 0){
                                    running = true;
                                    runningSteps = false;
                                } else {
                                    running = true;
                                    runningSteps = true;
                                    steps = numSteps;
                                }
                            } else {
                                in.nextLine();
                                System.out.println("You must input an integer.");
                            }
                    }

                    validInput = false;
                    while (!validInput) {
                        System.out.print("Time per step (in miliseconds): ");
                        if (in.hasNextInt()) {
                            validInput = true;
                            runWait = in.nextInt();
                            in.nextLine();
                        } else {
                            in.nextLine();
                            System.out.println("You must input an integer.");
                        }
                    }
                } else if (input.equalsIgnoreCase("q") || input.equalsIgnoreCase("quit")){
                    System.out.println("Quitting the program.");
                    return;
                } else if (input.equalsIgnoreCase("p") || input.equalsIgnoreCase("print")){
                    System.out.println(inter);
                } else if(input.equalsIgnoreCase("h") || input.equalsIgnoreCase("help")){
                    System.out.println("Commands: \n" +
                            " (r)un : runs the program (either until it ends, or for a user-specified number of steps\n" +
                            " (s)tep : advances 1 step through the program\n" +
                            " (p)rint : print out the current stack\n" +
                            " (q)uit : quits the program");
                }
                else System.out.println("Unrecognized command. Try \"help\" for a list of commands.");
            }
        }
        System.out.println();
        System.out.println("The program has ended.");

        //frame.dispose();
        in.close();
    }
}
