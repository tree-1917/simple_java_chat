import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class Server {
    private static final int DEFAULT_PORT = 8080;
    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final ExecutorService threadPool = Executors.newCachedThreadPool();
    private static volatile boolean isRunning = true;

    public static void main(String[] args) {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_PORT;
        
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("🚀 Server started on " + DEFAULT_HOST + ":" + port);
            System.out.println("📢 Waiting for clients... (Type 'shutdown' to stop the server)");

            // Shutdown hook for Ctrl+C
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                isRunning = false;
                threadPool.shutdown();
                System.out.println("\n🔴 Server shutting down...");
            }));

            // Console input for server commands (e.g., "shutdown")
            new Thread(() -> handleConsoleInput(serverSocket)).start();

            // Accept client connections
            while (isRunning) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("\n✅ New client connected: " + clientSocket.getRemoteSocketAddress());
                    threadPool.execute(() -> handleClient(clientSocket));
                } catch (SocketException e) {
                    if (isRunning) System.err.println("⚠️  Socket error: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("❌ Server error: " + e.getMessage());
        } finally {
            threadPool.shutdown();
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
            
            String clientName = clientSocket.getRemoteSocketAddress().toString();
            System.out.println("👋 [" + clientName + "] Ready for messages.");

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.equalsIgnoreCase("exit")) {
                    System.out.println("👋 [" + clientName + "] Disconnected.");
                    break;
                }
                System.out.println("📩 [" + clientName + "] Says: " + inputLine);
                out.println("Server received: '" + inputLine + "'");
            }
        } catch (IOException e) {
            System.err.println("⚠️  Client error: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("⚠️  Failed to close client socket: " + e.getMessage());
            }
        }
    }

    private static void handleConsoleInput(ServerSocket serverSocket) {
        try (BufferedReader consoleIn = new BufferedReader(new InputStreamReader(System.in))) {
            while (isRunning) {
                String command = consoleIn.readLine();
                if (command != null && command.equalsIgnoreCase("shutdown")) {
                    isRunning = false;
                    serverSocket.close();
                    threadPool.shutdown();
                    System.out.println("🔴 Server shutdown initiated.");
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("⚠️  Console input error: " + e.getMessage());
        }
    }
}
