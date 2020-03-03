package server; 

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Arrays;

/**
 * Handles multi-threading of server system.
 * Handles each individual client response
 * @authors Zukiswa Lobola, Mbaliyethemba Shangase and Simnikiwe Khonto.
 */
public class ThreadedClient implements Runnable {

    private final Socket clientSocket;
    private BufferedReader in;
    private static String currentDirectory;
    private static final String fileNotFound = "404 Not Found";
    private final DataInputStream clientData;
    private final PrintStream output;
    private final String fileDirectorySplit;

    /**
     * Constructor method.Initializes client socket.
     * @param client Client Socket
     * @throws java.io.IOException
     */
    public ThreadedClient(Socket client) throws IOException {
        this.clientSocket = client;
        in = null;
        currentDirectory = System.getProperty("user.dir");
        clientData = new DataInputStream(clientSocket.getInputStream());
        output = new PrintStream(clientSocket.getOutputStream());
        String os = System.getProperty("os.name");
        if (os.startsWith("Win")) fileDirectorySplit = "\\";
        else fileDirectorySplit = "/";
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
                            String[] list;
                            File file = new File(currentDirectory);
                            list = file.list();
                            System.out.println(currentDirectory);
                            output.println(Arrays.toString(list).substring(1, Arrays.toString(list).length()-1));
                            
                            continue;
                        //send file to client
                        case "3":
                            String outGoingFileName = in.readLine();
                            if(outGoingFileName != null) {
                                sendFile(outGoingFileName);
                            }
                            continue;
                        //Exit
                        case "4":
                            System.out.println(clientSocket + " logged off.");
                            in.close();
                            clientData.close();
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
        try {
            fileName = clientData.readUTF();
            //saves file to server directory
            try (FileOutputStream fileOutput = new FileOutputStream(currentDirectory + fileDirectorySplit +fileName)) {
                long size = clientData.readLong();
                byte[] buffer = new byte[1024];
                while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                    fileOutput.write(buffer, 0, bytesRead);
                    size -= bytesRead;
                }  
                fileOutput.close();         
            }
            catch (Exception e){
                System.err.println("Error:" + e);
            }
        }
        catch (IOException e){
            System.err.println("Error: " + e);
        }
        System.out.println(fileName + " received from " + clientSocket);
    }

    /**
     * Send file to client from server.
     * @param fileName Name of file being sent to client.
     * @throws java.io.FileNotFoundException
     */
    public void sendFile(String fileName) throws FileNotFoundException, IOException {
        try {
            File sendFile = new File(currentDirectory + fileDirectorySplit + fileName);  
           
            if(!sendFile.exists()) {
                output.println(fileNotFound);
                return;
            }
            else {
                output.println("File OK");
            }
            byte[] bytes = new byte[(int) sendFile.length()];

            FileInputStream fileInputStream = new FileInputStream(sendFile);
            BufferedInputStream buffer = new BufferedInputStream(fileInputStream);

            DataInputStream dataInput = new DataInputStream(buffer);
            dataInput.readFully(bytes, 0, bytes.length);

            //handle file send over socket
            OutputStream outputStream = clientSocket.getOutputStream();  

            //Sending file name and file size to the server
            DataOutputStream dataOutpt = new DataOutputStream(outputStream); 
            dataOutpt.writeUTF(sendFile.getName());
            dataOutpt.writeLong(bytes.length);
            dataOutpt.write(bytes, 0, bytes.length);
            dataOutpt.flush();
                
            System.out.println(fileName + " sent to " + clientSocket + ".");
            
        } catch (IOException e) {
            System.err.println(fileNotFound);
        }
    }
}
