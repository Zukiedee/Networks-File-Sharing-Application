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
                    //Exit
                    case "3":
                        System.exit(1);
                        break;
                    default:
                        System.err.println("Invalid command received.");
                        continue;
                }
            }
        } catch (IOException ex) {
            System.err.println("Exception: " + ex);
        }
    }

    /**
     * File sent by client to server.
     * These files are saved in the "files" folder of the project.
     */
    public void receiveFile() {
        try {
            int bytesRead;

            //Gets the file data from the 
            DataInputStream clientData = new DataInputStream(clientSocket.getInputStream());
            String fileName = clientData.readUTF();
            
            FileOutputStream fileOutput = new FileOutputStream(currentDirectory + "\\files\\"+fileName);

            long size = clientData.readLong();
            byte[] buffer = new byte[1024];
            while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                fileOutput.write(buffer, 0, bytesRead);
                size -= bytesRead;
            }
            
            //Close file input and output stream
            fileOutput.close();
            clientData.close();

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
            //Read in file from file location
            File sendFile = new File(currentDirectory + "\\files\\" + fileName);  

            //if file does not exist, returns back to the loop
            if(!sendFile.exists()) {
		System.out.println(FileNotFound);
		return;
            }
       
            byte[] bytes = new byte[(int) sendFile.length()];

            FileInputStream fileInputStream = new FileInputStream(sendFile);
            BufferedInputStream buffer = new BufferedInputStream(fileInputStream);

            DataInputStream dataInput = new DataInputStream(buffer);
            dataInput.readFully(bytes, 0, bytes.length);

            //handle file send over socket
            OutputStream output = clientSocket.getOutputStream();  

            //Sending file name and file size to the server
            DataOutputStream dataOutpt = new DataOutputStream(output); 
            dataOutpt.writeUTF(sendFile.getName());
            dataOutpt.writeLong(bytes.length);
            dataOutpt.write(bytes, 0, bytes.length);
            dataOutpt.flush();
            
            System.out.println("File: " + fileName + " sent to client.");
            
        } catch (Exception e) {
            System.err.println(FileNotFound);
        }
    }
}
