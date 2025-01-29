package Assignment_1;

/*
 * Implementation of a two-way message server in Java
 */

 import java.io.*;
 import java.net.*;
 
 public class TwoWayMesgServer {
	 public static void main(String args[]) {
		 if (args.length != 2) {
			 System.out.println("usage: java TwoWayMesgServer <port> <serverName>");
			 System.exit(1);
		 }
 
		 int serverPort = Integer.parseInt(args[0]);
		 String serverName = args[1];
 
		 try {
			 ServerSocket serverSock = new ServerSocket(serverPort);
			 System.out.println("Waiting for a client ...");
 
			 Socket clientSock = serverSock.accept();
			 System.out.println("Connected to a client at ('" +
					 ((InetSocketAddress) clientSock.getRemoteSocketAddress()).getAddress().getHostAddress()
					 + "', '" +
					 ((InetSocketAddress) clientSock.getRemoteSocketAddress()).getPort()
					 + "')");
 
			 serverSock.close();
 
			 BufferedReader fromClientReader = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));
			 PrintWriter toClientWriter = new PrintWriter(clientSock.getOutputStream(), true);
			 BufferedReader fromServerInput = new BufferedReader(new InputStreamReader(System.in));
 
			 while (true) {
				 // Read message from client
				 String clientMessage = fromClientReader.readLine();
 
				 if (clientMessage == null) {
					 System.out.println("Client closed connection");
					 break;
				 }
 
				 System.out.println("Client: " + clientMessage);
 
				 // Get response from server user
				 System.out.print("You: ");
				 String serverMessage = fromServerInput.readLine();
 
				 if (serverMessage == null) {
					 System.out.println("Closing connection");
					 break;
				 }
 
				 // Send response to client
				 toClientWriter.println(serverName + ": " + serverMessage);
			 }
 
			 clientSock.close();
		 } catch (Exception e) {
			 System.out.println(e);
		 }
	 }
 }
 