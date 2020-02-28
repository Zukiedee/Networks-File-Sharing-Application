package clientapp;

import java.io.Serializable;
import javax.swing.ImageIcon;

public class Data implements Serializable {

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ImageIcon getImage() {
        return image;
    }

    public void setImage(ImageIcon image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return name;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }
    
    public void setPermissions(String[] clients){
        this.permissions = clients;
    }
    

    private String status;
    private ImageIcon image;
    private byte[] file;
    private String name;
    private String[] permissions;
}

class Clients {
    
    private String username;
    
    public Clients (String name){
        this.username = name;
    }
    public String getName() {
        return username;
    }

    public void setName(String name) {
        this.username = name;
    }
    
}