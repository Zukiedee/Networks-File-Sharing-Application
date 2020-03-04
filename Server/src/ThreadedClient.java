package server;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;

/**
 * Handles multi-threading of server system.
 * Handles each individual client response.
 * Server is able to receive and send files to the client.
 * @authors Zukiswa Lobola, Mbaliyethemba Shangase and Simnikiwe Khonto.
 */
public final class ThreadedClient implements Runnable {

    private final Socket clientSocket;
    private BufferedReader in;
    private static String currentDirectory;
    private static final String fileNotFound = "404 Not Found";
    private final DataInputStream clientData;
    private final PrintStream output;
    private final String fileDirectorySplit;
    private boolean protect = false;
    private String pass;
    private boolean protectedFile;
    private String fileName;

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
        
        output.println(newClient());
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            while (true) {
                String line = in.readLine();
                switch (line) {
                    //client uploaded a file
                    case "1":
                        receiveFile(null);
                        continue;

                    //query list of files from server
                    case "2":
                        String[] array;
                        File file = new File(currentDirectory + fileDirectorySplit + "files");
                        array = file.list();
                        String list = Arrays.toString(array).substring(1, Arrays.toString(array).length()-1);
                        output.println(list);   
                        continue;
                        
                    //send file to client
                    case "3":
                        String outGoingFileName = in.readLine();
                        if(outGoingFileName != null) {
                            sendFile(outGoingFileName);
                        }
                        continue;
                        
                    //client exited
                    case "4":
                        System.out.println(clientSocket + " logged off.");
                        in.close();
                        clientData.close();
                        clientSocket.close();
                        break;
                            //no password
                                       
                    default:
                        if (line.startsWith("1 yes")){ //- upload file with password
                            int last = line.indexOf(";");
                            String pword = line.substring(last+1);
                            receiveFile(pword);
                        }
                        
                        else if(line.startsWith("request permission")){
                            int start = line.indexOf(";");
                            fileName = line.substring(start+1).trim();
                            if (filePermissions(fileName)){
                                output.println("yes password");
                            }
                            else { output.println("no password");
                                sendFile(fileName);
                            }
                            
                        }
                        else if(line.startsWith("password verification")){
                            int start = line.indexOf(";");  
                            String key = line.substring(start+1).trim();
                            if(key.trim().equals(pass)){
                                int end = key.indexOf(";");
                                sendFile(key.substring(0, end).trim());
                            }
                        }   
                }  
            }
            
        } catch (IOException ex) {}
    }
    
    /**
     * Outputs command options for client to choose from.
     * @return 
     * @throws IOException 
     */
    private String newClient() throws IOException {
        return "Successfully connected to server.";
    }

    /**
     * File sent by client to server.These files are saved in the Server directory
     * @throws java.io.IOException
     */
    private void receiveFile(String password) throws IOException {       
        String fileName = "";
        int bytesRead;
        try {
            fileName = clientData.readUTF();
            //saves file to server directory
            if (password != null){
                
                //String dir = currentDirectory + fileDirectorySplit+ "protect.txt";
                //File protectedFiles = new File(dir)
                
                try{ 
                    FileWriter w = new FileWriter("protect.txt");
                    PrintWriter pw = new PrintWriter(w);
                    pw.println("\n" + fileName + ";"+ password);
                    pw.close();
                }
                catch (Exception e){
                    System.err.println("Could not write to password file");
                }
               
            }
            try (FileOutputStream fileOutput = new FileOutputStream(currentDirectory + fileDirectorySplit + "files" + fileDirectorySplit +fileName)) {
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
    
    private boolean filePermissions(String fileName) throws FileNotFoundException, IOException{
        FileReader fr = new FileReader("protect.txt");
        BufferedReader br = new BufferedReader(fr);
        protectedFile = false;
        String str;
        while ((str = br.readLine())!=null){
            //search for file name
            if (str.startsWith(fileName)){
                protectedFile = true;
                pass = str;
                return true;
            }
        }
        return false;
    }

    /**
     * Send file to client from server.
     * @param fileName Name of file being sent to client.
     * @throws java.io.FileNotFoundException
     */
    private void sendFile(String fileName) throws FileNotFoundException, IOException {        
        try {
            File sendFile = new File(currentDirectory + fileDirectorySplit + "files" + fileDirectorySplit + fileName);  
           
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
            output.println(fileNotFound);
        }
    }
}
