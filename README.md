# Networks-File-Sharing-Application

A client-server file sharing application that makes use of TCP sockets. 
The server can send recieve files to clients. 

Client capabilities:
- Upload files to the server
- Query server for list of available files
- Dowload a file from server

Server capabilities:
- Accept and parse client requests
- Get requested file from server's file system and send to client
- Multithreaded server cable of serving multiple requests simultaneously

Application features:
- Considers privacy/confidentiality through use of file sharing permissions?
- File visibility/downloadabilty to certain clients?
- Shared secret-key to be used to download files?
