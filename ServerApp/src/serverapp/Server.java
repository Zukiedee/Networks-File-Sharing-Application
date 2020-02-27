package serverapp;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * Server class. 
 * @author Zukiswa PC
 */
public class Server {
    private static ServerSocket serverSocket;
    private static Socket clientSocket = null;

    public static void main(String[] args) throws IOException {
        //Get port number from the user
        System.out.print("Enter port number: ");
        Scanner keys = new Scanner(System.in);
        int port = keys.nextInt();
        
        connectServer(port); 
  
        acceptClients();
    }
    
    /**
     * Connect the server to the user given port number.
     * @param port Port number to connect Server on.
     * @throws IOException 
     */
    private static void connectServer(int port) throws IOException{
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server is running..");
        } catch (Exception e) {
            System.err.println("Port already in use.");
            System.exit(1);
        }        
    }
    
    /**
     * Accepts multiple client connections on the server. 
     */
    private static void acceptClients(){
        while (true) {
            try {
                clientSocket = serverSocket.accept();
                System.out.println("Accepted connection: " + clientSocket);

                Thread server = new Thread(new ThreadedClient(clientSocket));
                server.start();

            } catch (Exception e) {
                System.err.println("Error: "+ clientSocket + " could not connect.");
            }
        }
    }
}