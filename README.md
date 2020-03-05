# Networks: File Sharing Application

A client-server file sharing application that makes use of TCP connections from socket programming. 

## Installing
- Download and unzip the client and server files. The application is designed to be run on a UNIX operating system and hence makes use of a makefile to run the application via the terminal command line.

## Built With
* [Netbeans](https://netbeans.org/downloads/8.2/) - Dependency management

## Client and Server

### Server:
* Accept and parse client requests
* Get requested file from server's file system
* Send recieve files to/from clients
* Multithreaded server cable of serving multiple requests simultaneously

### Client:
* Upload files to the server
* Query server for list of available files
* Dowload a file from server

## Compilation and Running the Application
### Steps to run the server:
- Step 1: navigate to the server direcory in the terminal
- Step 2: make
- Step 3: make run
```
Example: make
         make run
```
- Step 4: Enter port number
```
Example: Output:  Enter Port number: 
         Input:   5555
         Output:  Server is running..
```

### Steps to run the client:
- Step 1: navigate to the client direcory in the terminal
- Step 2: make
- Step 3: make run
- Step 4: Client GUI should be active 
```
Example: make
         make run
```
Enter IP Address and Port number and then when the client is connected to the server, the following output will appear:
```
Output: Successfully connected to server!
                  ....
```

## Unix Terminal Commands:

make: compiles java files in src directory into bin directory
```
make
```
make run: runs the server or client class
```
make run
```
make clean: deletes the class files in the bin directory
```
make clean
```
make docs: creates the java docs in doc directory 
```
make docs
```



## Application features:
* Considers privacy/confidentiality through use of file sharing permissions
