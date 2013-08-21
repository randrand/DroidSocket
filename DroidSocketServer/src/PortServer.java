/* 
 * Server for Android practice project 2
 * 
 * Keeps track and returns the number of "Pokes" the server has received
 * from the client.
 * 
 * Author: JJ
 * 
 */

import java.net.*;
import java.io.*;

// Accept connection request and messages from client
public class PortServer {
	static ServerSocket serverSocket = null;
	static Socket clientSocket = null;
	static int serverPort = 4444; // HARD CODED server port number
	
	public static void main(String[] args) throws IOException {
		// Try to create a socket
		try {
			serverSocket = new ServerSocket(serverPort);	
		} catch (IOException e) {
			System.err.println("Fail to create socket on port: "+serverPort);
			System.exit(1);
		}

		System.out.println("Server started!");
        
		// Wait and accept new pokes
		while(true) {
			try {
				clientSocket = serverSocket.accept();
			} catch (IOException e) {
				System.err.println("Accept failed.");
				System.exit(1);
			}
			System.out.println("Client connected, client IP: " + 
					clientSocket.getRemoteSocketAddress());

			// Create new buffered reader and printer for I/O across socket
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(
					new InputStreamReader(clientSocket.getInputStream()));
			String inputLine, output;
			// Create new protocol to generate output
			PortServerProtocol kkp = new PortServerProtocol();

			inputLine = in.readLine();
			
			// If client sent the "Poke", send back the poke count
			if(inputLine.equals("Poke")) {
				System.out.println("Server recieved Poke!");
				output = kkp.processInput();
				out.println(output);
			}
			else {
				System.out.println("client sent invalid message: "
						+ inputLine + "\nDisconnecting...");
			}

			// Close connection with client after each poke
			out.close();
			in.close();
			clientSocket.close();
		}
	}
}