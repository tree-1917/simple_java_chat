import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        try (Socket socket = new Socket("127.0.0.1", 8080);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader consoleIn = new BufferedReader(new InputStreamReader(System.in))) {
            
            System.out.println("Connected to server");
            
            while (true) {
                System.out.print("Enter message: ");
                String message = consoleIn.readLine();
                
                if (message.equalsIgnoreCase("exit")) {
                    System.out.println("Exiting the chat...");
                    break;
                }
                
                out.println(message);
                
                // Read server response
                String response = in.readLine();
                System.out.println(response);
            }
        } catch (IOException e) {
            System.err.println("Client exception: " + e.getMessage());
        }
    }
}
