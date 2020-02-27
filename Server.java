import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Server {
   public static void main(String[] args) throws Exception {
      //Initialize Sockets
        ServerSocket ssock = new ServerSocket(6000);
        Socket socket = ssock.accept();
        System.out.println("The ChatApp server is running...");
        System.out.println("Connected: " + socket);
        
        byte[] contents = new byte[10000];
        
        BufferedReader inputbuffer = null;
        
        inputbuffer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        
        String num = inputbuffer.readLine();
        if(num.equals("1")){
        
        String outfile = inputbuffer.readLine();
        
        //Initialize the FileOutputStream to the output file's full path.
        FileOutputStream fos = new FileOutputStream(outfile);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        InputStream is = socket.getInputStream();
        
        //No of bytes read in one read() call
        int bytesRead = 0; 
        
        while((bytesRead=is.read(contents))!=-1)
            bos.write(contents, 0, bytesRead); 
        
        bos.flush(); 
        socket.close(); 
        
        System.out.println("File saved successfully!");
	}
	else if(num.equals(2)){
      //Specify the file
      String outfile = inputbuffer.readLine();
        File file = new File(outfile);
        FileInputStream fis = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(fis); 
          
      //Get socket's output stream
        OutputStream os = socket.getOutputStream();
                
        //Read File Contents into contents array 
        byte[] content;
        long fileLength = file.length(); 
        long current = 0;
         
        long start = System.nanoTime();
        while(current!=fileLength){ 
            int size = 10000;
            if(fileLength - current >= size)
                current += size;    
            else{ 
                size = (int)(fileLength - current); 
                current = fileLength;
            } 
            content = new byte[size]; 
            bis.read(content, 0, size); 
            os.write(content);
            System.out.println("Sending file ... "+(current*100)/fileLength+"% complete!");
        }   
        
        os.flush(); 
        //File transfer done. Close the socket connection!
        socket.close();
        ssock.close();
        System.out.println("File sent succesfully!");
	}
   }
}
