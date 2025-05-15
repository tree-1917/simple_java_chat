import java.io.*;
import java.net.*;

public class Client {
    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : DEFAULT_HOST;
        int port = args.length > 1 ? Integer.parseInt(args[1]) : DEFAULT_PORT;

        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader consoleIn = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("✅ Connected to server at " + host + ":" + port);
            System.out.println("📤 Type messages (or 'exit' to quit):");

            // Thread to listen for server messages
            new Thread(() -> {
                try {
                    String serverResponse;
                    while ((serverResponse = in.readLine()) != null) {
                        System.out.println("📥 Server: " + serverResponse);
                    }
                } catch (IOException e) {
                    if (!socket.isClosed()) {
                        System.err.println("⚠️  Lost connection to server: " + e.getMessage());
                    }
                }
            }).start();

            // Main thread for user input
            String userInput;
            while ((userInput = consoleIn.readLine()) != null) {
                if (userInput.equalsIgnoreCase("exit")) {
                    out.println("exit"); // Notify server before exiting
                    System.out.println("👋 Disconnecting...");
                    break;
                }
                out.println(userInput);
            }
        } catch (UnknownHostException e) {
            System.err.println("❌ Unknown host: " + host);
        } catch (ConnectException e) {
            System.err.println("❌ Connection refused. Is the server running?");
        } catch (IOException e) {
            System.err.println("⚠️  Client error: " + e.getMessage());
        }
    }
}
