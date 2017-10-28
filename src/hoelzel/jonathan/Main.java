package hoelzel.jonathan;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

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

        String code = new String(encoded, Charset.defaultCharset());
        Interpreter inter = new Interpreter(code, System.in, System.out);

        while (!inter.done()){
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                System.out.println("Interrupt exception occurred");
            }
            inter.step();
        }
    }
}
