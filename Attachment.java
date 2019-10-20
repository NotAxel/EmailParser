package com.crisisgo.libs.cmailkit;

public class Attachment {

    private String name;
    private String file_extension;
    private byte[] byte_array; // converts to a base 64 array
    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileExtension() {
        return file_extension;
    }

    public void setFileExtension(String fileExtension) {
        this.file_extension = fileExtension;
    }

    public byte[] getByteArray() {
        return byte_array;
    }

    public void setByteArray(byte[] byteArray) {
        this.byte_array = byteArray;
    }
}

