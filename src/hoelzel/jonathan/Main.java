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
        while (!inter.done()){
            if (running) {
                try {
                    Thread.sleep(5);
                } catch (InterruptedException ex){
                    // do nothing
                }
                inter.step();
            }
            else {
                System.out.print(">> ");
                String input = in.nextLine();
                if (input.equalsIgnoreCase("S") || input.equalsIgnoreCase("step")){
                    inter.step();
                } else if (input.equalsIgnoreCase("r") || input.equalsIgnoreCase("run")){
                    running = true;
                } else if (input.equalsIgnoreCase("q") || input.equalsIgnoreCase("quit")){
                    return;
                } else if (input.equalsIgnoreCase("p") || input.equalsIgnoreCase("print")){
                    System.out.println(inter);
                } else if(input.equalsIgnoreCase("h") || input.equalsIgnoreCase("help")){
                    System.out.println("Commands: \n" +
                            " (r)un : runs the program\n" + "" +
                            " (s)tep : advances 1 step through the program\n" +
                            " (p)rint : print out the current state of the program\n" +
                            " (q)uit : quits the program");
                }
                else System.out.println("Unrecognized command. Try \"help\" for a list of commands.");
            }
        }
        System.out.println("The program has ended.");
    }
}
