package Assignment_2;

import java.io.*;
import java.net.*;

/*
 * Implementation of a Group Chat Client in Java
 *
 * The client connects to the GroupChatServer, sends the user's name upon connection,
 * and continuously listens for messages from the server while also allowing user input.
 *
 * Uses multi-threading to handle simultaneous reading from the server and user input.
 */
public class GroupChatClient {
    public static void main(String[] args) {
        // Ensure correct usage with server address and port
        if (args.length != 2) {
            System.out.println("Usage: java GroupChatClient <server_address> <port>");
            return;
        }

        String serverAddress = args[0];
        int port = Integer.parseInt(args[1]);
        
        // Attempt to connect to the server and initialize I/O streams
        try (Socket socket = new Socket(serverAddress, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {
            
            // Prompt user for a name and send it to the server
            System.out.print("Enter your name: ");
            String name = userInput.readLine();
            out.println(name);

            /*
             * Create a separate thread to listen for incoming messages from the server.
             * This allows continuous reception of messages while enabling user input simultaneously.
             */
            Thread listener = new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        System.out.println(message);
                    }
                } catch (IOException e) {
                    System.out.println("Disconnected from server.");
                }
            });
            listener.start();
            
            // Main thread reads user input and sends messages to the server
            String userMessage;
            while ((userMessage = userInput.readLine()) != null) {
                out.println(userMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
