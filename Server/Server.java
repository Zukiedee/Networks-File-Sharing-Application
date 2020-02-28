/*
 * Server.java
 * 
 * Copyright 2020 Zukiswa Lobola <lblzuk002@sl-dual-167>
 * 
 */
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;

public class Server {
	static int PORT = 8889;
	
	public static void main (String[] args) throws IOException{
		ServerSocket listener = new ServerSocket(PORT);
		
		System.out.println("[Server] Waiting for client connection ...");
		Socket client = listener.accept();
		
		System.out.println("[Server] Connected to a client");
		
		DataInputStream din = new DataInputStream(client.getInputStream());
		DataOutputStream dout = new DataOutputStream(client.getOutputStream());

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		String msgin = "", msgout="";
		
		
		
	}
}

