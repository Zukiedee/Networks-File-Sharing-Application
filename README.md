# Networks: File Sharing Application

A client-server file sharing application that makes use of TCP connections from socket programming. 

## Installing
- Download and open the ClientApp and ServerApp file projects

## Built With
* [Netbeans](https://netbeans.org/downloads/8.2/) - Dependency management



## Client and Server Capabilities
### Client:
* Upload files to the server
* Query server for list of available files
* Dowload a file from server

### Server:
* Accept and parse client requests
* Get requested file from server's file system
```
Outputs "404 Not Found" if file not present
```
* Send recieve files to/from clients
* Multithreaded server cable of serving multiple requests simultaneously

## Application features:
* Considers privacy/confidentiality through use of file sharing permissions?
* File visibility/downloadabilty to certain clients?
* Shared secret-key to be used to download files?
