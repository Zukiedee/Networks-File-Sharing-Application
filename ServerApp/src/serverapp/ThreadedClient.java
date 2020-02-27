package serverapp;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * Handles multi-threading of server system.
 * @author Zukiswa PC
 */
public class ThreadedClient implements Runnable {

    private Socket clientSocket;
    private BufferedReader in = null;
    private static String currentDirectory;
    private static String FileNotFound = "404 File Not Found";

    /**
     * Constructor method. Initializes client socket.
     * @param client Client Socket
     */
    public ThreadedClient(Socket client) {
        this.clientSocket = client;
        currentDirectory = System.getProperty("user.dir");

    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String clientCmdSelection;
            while ((clientCmdSelection = in.readLine()) != null) {
                switch (clientCmdSelection) {
                    //client uploaded a file
                    case "1":
                        receiveFile();
                        continue;
                    //send file to client
                    case "2":
                        String outGoingFileName;
                        while ((outGoingFileName = in.readLine()) != null) {
                            sendFile(outGoingFileName);
                        }
                        continue;
                    case "3":
                        System.exit(1);
                        break;
                    default:
                        System.out.println("Invalid command received.");
                        continue;
                }
            }

        } catch (IOException ex) {
            System.err.println("Exception: " + ex);
        }
    }

    /**
     * File sent by client to server.
     */
    public void receiveFile() {
        try {
            int bytesRead;

            DataInputStream clientData = new DataInputStream(clientSocket.getInputStream());

            String fileName = clientData.readUTF();
            FileOutputStream out = new FileOutputStream(currentDirectory + "\\src\\serverapp\\"+fileName);

            long size = clientData.readLong();
            byte[] buffer = new byte[1024];
            while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                out.write(buffer, 0, bytesRead);
                size -= bytesRead;
            }

            out.close();
            //clientData.close();

            System.out.println("File: " + fileName + " received from client.");
        } catch (IOException ex) {
            System.err.println("Client error. Connection closed.");
        }
    }

    /**
     * Send file to client from server.
     * @param fileName Name of file being sent to client.
     */
    public void sendFile(String fileName) {
        try {
            //Read in file
            File myFile = new File(fileName);  
            byte[] mybytearray = new byte[(int) myFile.length()];

            FileInputStream fileInputStream = new FileInputStream(myFile);
            BufferedInputStream buffer = new BufferedInputStream(fileInputStream);

            DataInputStream dataInput = new DataInputStream(buffer);
            dataInput.readFully(mybytearray, 0, mybytearray.length);

            //handle file send over socket
            OutputStream output = clientSocket.getOutputStream();  

            //Sending file name and file size to the server
            DataOutputStream dataOutpt = new DataOutputStream(output); 
            dataOutpt.writeUTF(myFile.getName());
            dataOutpt.writeLong(mybytearray.length);
            dataOutpt.write(mybytearray, 0, mybytearray.length);
            dataOutpt.flush();
            System.out.println("File: " + fileName + " sent to client.");
            
        } catch (Exception e) {
            System.err.println(FileNotFound);
        }
    }
}
