package Assignment_2;

import java.io.*;
import java.net.*;
import java.util.*;

/*
 * Implementation of a Group Chat Server in Java
 *
 * The server listens for new client connections, maintains a list of active clients,
 * and relays messages from any client to all connected clients.
 *
 * The server uses multi-threading to handle multiple clients concurrently.
 */
public class GroupChatServer {
    private static final int PORT = 50000;
    // Set to keep track of all active client output streams
    private static Set<PrintWriter> clientWriters = Collections.synchronizedSet(new HashSet<>());

    public static void main(String[] args) {
        System.out.println("GroupChatServer started on port " + PORT);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            // Continuously listen for new client connections
            while (true) {
                new ClientHandler(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * Handles communication with a single client.
     * Each client gets its own thread, which listens for incoming messages
     * and relays them to all other connected clients.
     */
    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                // Initialize input and output streams for the client
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                
                // Add the client's writer to the set of active writers
                synchronized (clientWriters) {
                    clientWriters.add(out);
                }

                // Read the client's name
                String name = in.readLine();
                broadcast("[SERVER]: " + name + " has joined the chat.");
                
                String message;
                // Continuously read and broadcast messages from this client
                while ((message = in.readLine()) != null) {
                    broadcast(name + ": " + message);
                }
            } catch (IOException e) {
                System.out.println("Client disconnected.");
            } finally {
                // Remove client from active writers and close socket
                synchronized (clientWriters) {
                    clientWriters.remove(out);
                }
                try { socket.close(); } catch (IOException e) {}
            }
        }

        /*
         * Sends a message to all connected clients.
         */
        private void broadcast(String message) {
            synchronized (clientWriters) {
                for (PrintWriter writer : clientWriters) {
                    writer.println(message);
                }
            }
        }
    }
}
