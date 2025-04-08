package com.example.deepakmediaplayer.utils;


import java.util.ArrayList;
import java.util.List;

public class FileNode {
    public String name;
    public boolean isDirectory;
    public List<FileNode> children;
    public FileNode parent;

    public FileNode(String name, boolean isDirectory) {
        this.name = name;
        this.isDirectory = isDirectory;
        this.children = new ArrayList<>();
        this.parent = null;
    }

    public String getPath() {
        return name;
    }
}