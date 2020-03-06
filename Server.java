//package server; 

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * Server class.   
 * @authors Zukiswa Lobola, Mbaliyethemba Shangase and Simnikiwe Khonto.
 */
public class Server {
    private static ServerSocket serverSocket;
    private static Socket clientSocket = null;

    public Server () throws IOException {
        //Get port number from the user        
        System.out.print("Enter port number: ");
        Scanner keys = new Scanner(System.in);
        int port = keys.nextInt();
        
        connectServer(port); 
        acceptClients();
    }
    public static void main(String[] args) throws IOException  {
        Server server = new Server();
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
        } catch (IOException e) {
            System.err.println("Port already in use.\nTry using another port number.");
            Server server = new Server();
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

            } catch (IOException e) {
                System.err.println("Error: "+ clientSocket + " could not connect.\n" + e);
            }
        }
    }
}
