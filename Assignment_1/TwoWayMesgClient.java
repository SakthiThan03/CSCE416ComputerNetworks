package Assignment_1;

/*
 * Implementation of a two-way message client in Java
 */

 import java.io.*;
 import java.net.*;
 
 public class TwoWayMesgClient {
	 public static void main(String args[]) {
		 if (args.length != 3) {
			 System.out.println("usage: java TwoWayMesgClient <server name> <server port> <clientName>");
			 System.exit(1);
		 }
 
		 String serverName = args[0];
		 int serverPort = Integer.parseInt(args[1]);
		 String clientName = args[2];
 
		 try {
			 Socket sock = new Socket(serverName, serverPort);
			 System.out.println("Connected to server at ('" + serverName + "', '" + serverPort + "')");
 
			 PrintWriter toServerWriter = new PrintWriter(sock.getOutputStream(), true);
			 BufferedReader fromServerReader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			 BufferedReader fromUserReader = new BufferedReader(new InputStreamReader(System.in));
 
			 while (true) {
				 // Read input from user
				 System.out.print("You: ");
				 String line = fromUserReader.readLine();
 
				 if (line == null) {
					 System.out.println("Closing connection");
					 break;
				 }
 
				 // Send message to server
				 toServerWriter.println(clientName + ": " + line);
 
				 // Receive response from server
				 String serverResponse = fromServerReader.readLine();
 
				 if (serverResponse == null) {
					 System.out.println("Server closed connection");
					 break;
				 }
 
				 System.out.println(serverResponse);
			 }
 
			 sock.close();
		 } catch (Exception e) {
			 System.out.println(e);
		 }
	 }
 }
 