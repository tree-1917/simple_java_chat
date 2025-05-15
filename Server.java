import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    public static void main(String[] args) {
        ExecutorService pool = Executors.newCachedThreadPool();
        
        try (ServerSocket listener = new ServerSocket(8080)) {
            System.out.println("Server is listening on 127.0.0.1:8080");
            
            while (true) {
                Socket socket = listener.accept();
                System.out.println("New connection: " + socket.getRemoteSocketAddress());
                pool.execute(() -> handleClient(socket));
            }
        } catch (IOException e) {
            System.err.println("Server exception: " + e.getMessage());
        }
    }
    
    private static void handleClient(Socket socket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader consoleIn = new BufferedReader(new InputStreamReader(System.in))) {
            
            while (true) {
                // Wait for client message
                String clientMessage = in.readLine();
                if (clientMessage == null || clientMessage.equalsIgnoreCase("exit")) {
                    System.out.println("Client disconnected.");
                    break;
                }
                
                System.out.println("User Say: " + clientMessage);
                
                // Get server response
                System.out.print("Server: Enter your reply: ");
                System.out.flush();
                String response = consoleIn.readLine();
                
                System.out.println("Server sending reply: " + response);
                out.println(response);
            }
        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Error closing socket: " + e.getMessage());
            }
        }
    }
}
