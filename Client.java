import java.util.*;
import java.net.*;
import java.io.*;


public class Client {

    public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException, InterruptedException{
        
        Socket socket = new Socket("localhost", 8000);
        BufferedReader bufred = new BufferedReader(new FileReader("/Users/simnikiwe/Downloads/network/data.txt"));

        byte []bt = new byte[900];
        String eachline = bufred.readLine();
        DataOutputStream os = new DataOutputStream(socket.getOutputStream());
        os.writeUTF(eachline);
        System.out.println("Completely transferred!");
    }
}