package com.csc3002.server;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import javax.swing.JFileChooser;

/**
 * Handles multi-threading of server system.
 * Handles each individual client response
 * @author Zukiswa PC
 */
public class ThreadedClient implements Runnable {

    private final Socket clientSocket;
    private BufferedReader in = null;
    private static String currentDirectory;
    private static final String FileNotFound = "404 Not Found";

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
            
            while (true) {
                switch (in.readLine()) {
                    //client uploaded a file
                    case "1":
                        receiveFile();
                        continue;
                    
                    //query list of files from server
                    case "2":
                        
                        continue;
                    //send file to client
                    case "3":
                        String outGoingFileName;
                        while ((outGoingFileName = in.readLine()) != null) {
                            sendFile(outGoingFileName);
                        }
                        continue;
                    //Exit
                    case "4":
                        in.close();
                        clientSocket.close();
                        break;
                    default:
                        System.err.println("Invalid command received.");
                     }
                
            }
        } catch (IOException ex) {
            //System.err.println("Exception: " + ex);
        }
    }

    /**
     * File sent by client to server.These files are saved in the Server directory
     * @throws java.io.IOException
     */
    public void receiveFile() throws IOException {
        String fileName = "";
        int bytesRead;
        try ( DataInputStream clientData = new DataInputStream(clientSocket.getInputStream())) {
            fileName = clientData.readUTF();
            try (FileOutputStream fileOutput = new FileOutputStream(currentDirectory +"/" +fileName)) {
                long size = clientData.readLong();
                byte[] buffer = new byte[1024];
                while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                    fileOutput.write(buffer, 0, bytesRead);
                    size -= bytesRead;
                }   //Close file input and output stream
                fileOutput.close();
                clientData.close();
                
            }
            catch (Exception e){
                System.err.println("Exce:" + e);
            }
        }
        catch (Exception e){
            System.err.println(e);
        }
        System.out.println(fileName + " received from client.");
    }

    /**
     * Send file to client from server.
     * @param fileName Name of file being sent to client.
     */
    public void sendFile(String fileName) {
        try {
            //Read in file from file location
            JFileChooser browser = new JFileChooser();
            int response = browser.showSaveDialog(null);
            
            if (response == JFileChooser.APPROVE_OPTION){
                //Read in file from file location
                File sendFile = browser.getSelectedFile();
                
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
                
                System.out.println(fileName + " sent to client.");
            }
            else {
                System.out.println("Send was cancelled");
            }
        } catch (IOException e) {
            System.err.println(FileNotFound);
        }
    }
}
