package com.example.deepakmediaplayer.utils;


import java.util.List;

public class FileStructureBuilder {

    public static FileNode buildFileStructure(List<String> filePaths) {
        FileNode root = new FileNode("storage", true);

        for (String path : filePaths) {
            String[] parts = path.split("/");
            addPath(root, parts, 1);
        }

        return root;
    }


    private static void addPath(FileNode current, String[] parts, int index) {
        if (index == parts.length) return;

        String part = parts[index];
        FileNode nextNode = null;

        for (FileNode child : current.children) {
            if (child.name.equals(part)) {
                nextNode = child;
                break;
            }
        }

        if (nextNode == null) {
            nextNode = new FileNode(part, index < parts.length - 1);
            nextNode.parent = current;
            current.children.add(nextNode);
        }

        addPath(nextNode, parts, index + 1);
    }
}