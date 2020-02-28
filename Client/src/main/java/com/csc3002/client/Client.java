package com.csc3002.client;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;
import javax.swing.JFileChooser;

/**
 * Client Class
 * @author Zukiswa PC
 */
public class Client {
    private static Socket socket;
    private static String fileName;
    private static BufferedReader input;
    private static PrintStream output;
    private static final String fileNotFound = "404 Not Found";
    private static String currentDirectory;
    private static JFileChooser browser;

    public static void main(String[] args) throws IOException {
        //Get the IP address and port number from the client
        System.out.print("Enter IP Address: ");
        Scanner keys = new Scanner(System.in);
        String ipAddress = keys.nextLine();
        
        System.out.print("Enter port number: ");
        int port = keys.nextInt();
        
        connectClient(ipAddress, port);
        
        browser = new JFileChooser();

        //initialize input & outputs
        input = new BufferedReader(new InputStreamReader(System.in));
        output = new PrintStream(socket.getOutputStream());

        
        while(true) {
            try {
                switch (Integer.parseInt(userCommands())) {
                    //upload file command
                    case 1:
                        output.println("1");
                        if (upload()) {
                            System.out.println(fileName +" successfully sent to Server.");
                        }
                        else 
                            System.out.println("Upload unsuccessful");
                        continue;
                    case 2:
                        output.println("2");
                        continue;
                    //download file command
                    case 3:
                        output.println("3");
                        System.out.print("Enter file name: ");
                        fileName = input.readLine();
                        output.println(fileName);
                        download(fileName);
                        continue;
                    //Close client socket
                   case 4:
                        output.println("4");
                        //System.exit(1);  
                       break;
                   default:
                }
                input.close();
                output.close();
                socket.close();
                System.exit(0);
            } catch (IOException | NumberFormatException e) {
                System.err.println("Error: Invalid Input.\n" + e);
            }
        }
    }
    
    /**
     * Connect client to server using the IP Address and port number/
     * @param IP Server IP Address
     * @param port Server port number
     */
    private static void connectClient(String IP, int port) throws IOException{
        try {
            socket = new Socket(IP, port);
        } catch (IOException e) {
            System.err.println("Cannot connect to the server, try again later.");
            System.exit(1);
        }
    }

    /**
     * Outputs command options for user to choose from.
     * @return User selected command
     * @throws IOException 
     */
    public static String userCommands() throws IOException {
        System.out.println("1. Upload file.");
        System.out.println("2. Query list of files");
        System.out.println("3. Download file.");
	System.out.println("4. Exit.");
        System.out.println("Choose command: ");

        return input.readLine();
    }

    /**
     * Uploads client file to the server.Assumption: the file you want to upload is in the same file directory 
 as the client source file.
     * @return true if upload successful
     */
    public static boolean upload() {
        try {           
            //Get file using JChooser
            int response = browser.showOpenDialog(null);
            
            if (response == JFileChooser.APPROVE_OPTION){
                //Read in file from file location
                File uploadFile = browser.getSelectedFile();
                int start = uploadFile.getAbsolutePath().lastIndexOf("/");
                fileName = uploadFile.getAbsolutePath().substring(start+1);
                //if file does not exist, returns back to the loop
                if(!uploadFile.exists()) {
                    System.out.println(fileNotFound);
                    return false;
                }

                byte[] bytes = new byte[(int) uploadFile.length()];

                FileInputStream fileInputStream = new FileInputStream(uploadFile);
                BufferedInputStream buffer = new BufferedInputStream(fileInputStream);

                DataInputStream dataInput = new DataInputStream(buffer);
                dataInput.readFully(bytes, 0, bytes.length);

                //handle file send over socket            
                OutputStream outputStream = socket.getOutputStream();

                //Sending file name and file size to the server
                DataOutputStream dataOutput = new DataOutputStream(outputStream);
                dataOutput.writeUTF(uploadFile.getName());
                dataOutput.writeLong(bytes.length);
                dataOutput.write(bytes, 0, bytes.length);
                dataOutput.flush();
                return true;   
            }
            else {
                System.out.println("Upload was cancelled");
                return false;
            }
           
        } catch (IOException e) {
            System.err.println("Exception: "+e);
            return false;
        }
    }

    /**
     * Download file from server
     * @param fileName Name of the file client wants to download.
     */
    public static void download(String fileName) {
        //int response = browser.showSaveDialog(null);

        try {
            int bytesRead;
            
            try (InputStream inputStream = socket.getInputStream()) {
                DataInputStream clientData = new DataInputStream(inputStream);
                fileName = clientData.readUTF();
                
                FileOutputStream fileOutput = new FileOutputStream(currentDirectory + "/" + fileName);
                    long size = clientData.readLong();
                    byte[] buffer = new byte[1024];
                    while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                        fileOutput.write(buffer, 0, bytesRead);
                        size -= bytesRead;
                    }
                    // Close file output and input stream
                
            }

            System.out.println(fileName +" received from Server.");
        
        } catch (IOException ex) {
		System.out.println("Exception: "+ex);
         }
    }
}