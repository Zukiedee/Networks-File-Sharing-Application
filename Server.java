import java.util.*;
import java.net.*;
import java.io.*;

public class Server {
    private static ServerSocket server;
    
    private static int port = 8000;
    
    public static void main(String args[]) throws IOException, ClassNotFoundException{

        //create the socket server object
        server = new ServerSocket(port);
        //keep listens indefinitely until receives 'exit' call or program terminates
        System.out.println("Server is running. Waiting for client connection...");
        //creating socket and waiting for client connection
        Socket socket = server.accept();

        DataInputStream is = new DataInputStream(socket.getInputStream());
        String read = is.readUTF();
        System.out.println("File is Transferred");
        FileOutputStream os = new FileOutputStream("/Users/simnikiwe/Downloads/network/data.txt");

        byte []bt = read.getBytes();
        os.write(read.getBytes());
        
        //close the ServerSocket object
        server.close();
    }
    
}