package Lab05;

import Constants.Constants;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeMap;

import static Lab05.IO.deleteFile;
import static Lab05.IO.readFile;
import static Lab05.IO.writeFile;

public class Merger implements Runnable, Constants {
    private File first;
    private File second;
    private String new_filepath;


    Merger(File f1, File f2, String path) {
        first = f1;
        second = f2;
        new_filepath = path;
    }

    Merger(File f1, File f2) {
        first = f1;
        second = f2;
        new_filepath = f1.getAbsolutePath();
    }


    @Override
    public void run() {
        System.out.println("Gonna merge two files");
        TreeMap<String, ArrayList<Integer>> first_file = readFile(first);
        TreeMap<String, ArrayList<Integer>> second_file = readFile(second);
        //read first, read second
        //add second to first
        for (String key : second_file.keySet()) {//merge itself
            if (first_file.containsKey(key)) {
                first_file.get(key).addAll(second_file.get(key));
            } else first_file.put(key, second_file.get(key));
        }
        //write to a new file
        File f = new File(new_filepath);
        System.out.println("File " + f.getName() + "created");
        System.out.println("Creating a file");
        try {
            writeFile(f, first_file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Files " + first.getName() + " and " + second.getName() + " were merged into " + f.getName());
        deleteFile(first);
        deleteFile(second);
    }
}
