package com.example.deepakmediaplayer.data.model;


public class FileItem {
    private String name;
    private boolean isDirectory;
    private String path;
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public FileItem(String name, boolean isDirectory, String path,int id) {
        this.name = name;
        this.isDirectory = isDirectory;
        this.path = path;
        this.id=id;

    }

    public String getName() {
        return name;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public String getPath() {
        return path;
    }
}