package Assignment_2;

import java.io.*;
import java.net.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class GroupChatServer {
    private static final int PORT = 12345;
    private static CopyOnWriteArrayList<ClientHandler> clients = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        System.out.println("Group Chat Server is running...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }

    static void broadcast(String message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    static void removeClient(ClientHandler client) {
        clients.remove(client);
    }
}

class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String clientName;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            clientName = in.readLine(); // Read client's name
            System.out.println(clientName + " has joined the chat.");
            GroupChatServer.broadcast(clientName + " has joined the chat.", this);

            String message;
            while ((message = in.readLine()) != null) {
                System.out.println(clientName + ": " + message);
                GroupChatServer.broadcast(clientName + ": " + message, this);
            }
        } catch (IOException e) {
            System.out.println(clientName + " has disconnected.");
        } finally {
            GroupChatServer.removeClient(this);
            GroupChatServer.broadcast(clientName + " has left the chat.", this);
            closeResources();
        }
    }

    void sendMessage(String message) {
        out.println(message);
    }

    private void closeResources() {
        try {
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.out.println("Error closing socket: " + e.getMessage());
        }
    }
}
