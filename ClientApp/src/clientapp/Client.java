package clientapp;

import java.net.*;
import java.io.*;
import java.util.*;

/**
 * Client Class
 * @author Zukiswa PC
 */
public class Client {
    private static Socket socket;
    private static String fileName;
    private static BufferedReader input;
    private static PrintStream output;
    private static String FileNotFound = "404 File Not Found";
    private static String currentDirectory;

    public static void main(String[] args) throws IOException {
        //Get the IP address and port number from the client
        System.out.print("Enter IP Address: ");
        Scanner keys = new Scanner(System.in);
        String ipAddress = keys.nextLine();
        
        System.out.print("Enter port number: ");
        int port = keys.nextInt();
        
        connectClient(ipAddress, port);
        
        currentDirectory = System.getProperty("user.dir");

        //initialize input & outputs
        input = new BufferedReader(new InputStreamReader(System.in));
        output = new PrintStream(socket.getOutputStream());

        
        while(true) {
            try {
                switch (Integer.parseInt(userCommands())) {
                    //upload file command
                    case 1:
                        output.println("1");
                        upload();
                        continue;
                    //download file command
                    case 2:
                        output.println("2");
                        System.out.print("Enter file name: ");
                        fileName = input.readLine();
                        output.println(fileName);
                        download(fileName);
                        continue;
                    //exit client
                   case 3:
                         socket.close();
                        System.exit(1);
                   default:
                       continue;
                }
            } catch (Exception e) {
                System.err.println("Error: Invalid Input.");
            }
        }   
    }
    
    /**
     * 
     * @param IP Server IP Address
     * @param port Server port number
     */
    private static void connectClient(String IP, int port) throws IOException{
        try {
            socket = new Socket(IP, port);
        } catch (Exception e) {
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
        System.out.println("2. Download file.");
	System.out.println("3. Exit.");
        System.out.println("Choose command: ");

        return input.readLine();
    }

    /**
     * Uploads client file to the server.
     * Assumption: the file you want to upload is in the same file directory 
     * as the client source file.
     */
    public static void upload() {
        try {
            System.out.print("Enter file name: ");
            fileName = input.readLine();

            //Read in file from file location
            File uploadFile = new File(currentDirectory + "\\files\\" + fileName);
	    
            //if file does not exist, returns back to the loop
            if(!uploadFile.exists()) {
		System.out.println(FileNotFound);
		return;
            }

            byte[] bytes = new byte[(int) uploadFile.length()];
            
            FileInputStream fileInputStream = new FileInputStream(uploadFile);
            BufferedInputStream buffer = new BufferedInputStream(fileInputStream);

            DataInputStream dataInput = new DataInputStream(buffer);
            dataInput.readFully(bytes, 0, bytes.length);

            //handle file send over socket            
            OutputStream output = socket.getOutputStream();

            //Sending file name and file size to the server
            DataOutputStream dataOutput = new DataOutputStream(output);
            dataOutput.writeUTF(uploadFile.getName());
            dataOutput.writeLong(bytes.length);
            dataOutput.write(bytes, 0, bytes.length);
            dataOutput.flush();
            
            //int start = fileName.lastIndexOf("\\");
            //String name = fileName.substring(start+1);
            System.out.println("File: "+ fileName +" sent to Server.");
            
        } catch (Exception e) {
            System.err.println("Exception: "+e);
        }
    }

    /**
     * Download file from server
     * @param fileName Name of the file client wants to download.
     */
    public static void download(String fileName) {
        try {
            int bytesRead;
            
            InputStream inputStream = socket.getInputStream();

            DataInputStream clientData = new DataInputStream(inputStream);
            fileName = clientData.readUTF();
            
            FileOutputStream fileOutput = new FileOutputStream(currentDirectory + "\\downloads\\"+ fileName);
            
            long size = clientData.readLong();
            byte[] buffer = new byte[1024];
            while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                fileOutput.write(buffer, 0, bytesRead);
                size -= bytesRead;
            }

            // Close file output and input stream
            fileOutput.close();
            inputStream.close();

            System.out.println("File "+fileName+" received from Server.");
        
        } catch (IOException ex) {
		System.out.println("Exception: "+ex);
         }
    }
}