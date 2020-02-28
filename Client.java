import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.Socket;
import java.io.BufferedReader;
import java.util.*;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

public class Client { 
    
    public static void main(String[] args) throws Exception{
		
		BufferedReader messagebuffered = null;
		BufferedReader in = null;
		PrintWriter out = null;
		messagebuffered = new BufferedReader(new InputStreamReader(System.in));
	    System.out.println("Pass the server IP as the sole command line argument");
	    Scanner scanner = new Scanner(System.in);
        
        //Initialize socket
        Socket socket = new Socket(scanner.nextLine(), 6000);
        System.out.println("Choose option below:");
        System.out.println("1: Upload");
        System.out.println("2: Download");
        System.out.println("3: Query");
        String opt = messagebuffered.readLine();
        
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	    out = new PrintWriter(socket.getOutputStream(), true);
        if(opt.equals("1")){
			out.println(opt);
			System.out.println("Enter filename:");
			String filename = scanner.nextLine();
			System.out.println("Enter output file:");
			String user = messagebuffered.readLine();
			out.println(user);
			//Specify the file
			File file = new File(filename);
			FileInputStream fis = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(fis); 
			
			//Get socket's output stream
			OutputStream os = socket.getOutputStream();
			
			//Read File Contents into contents array 
			byte[] contents;
			long fileLength = file.length(); 
			long current = 0;
			long start = System.nanoTime();
			while(current!=fileLength){ 
				int size = 10000;
				if(fileLength - current >= size){
					current += size;
				}    
				else{ 
					size = (int)(fileLength - current); 
					current = fileLength;
					} 
				contents = new byte[size]; 
				bis.read(contents, 0, size); 
				os.write(contents);
				System.out.println("Sending file ... "+(current*100)/fileLength+"% complete!");
				}   
			os.flush(); 
			//File transfer done. Close the socket connection!
			socket.close();
			//ssock.close();
			System.out.println("File sent succesfully!");
			}
			else if(opt.equals("2")){
				out.println(opt);
				System.out.println("Enter filename:");
				String user = messagebuffered.readLine();
				out.println(user);
				System.out.println("Enter output file:");
				String filename = scanner.nextLine();
				byte[] contents = new byte[10000];
				//Initialize the FileOutputStream to the output file's full path.
				FileOutputStream fos = new FileOutputStream(filename);
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
			else if(opt.equals("3")){
				out.println(opt);
				String line;
				while((line = in.readLine()) != null){
					System.out.println(line);
				}
			}
			}
		}
