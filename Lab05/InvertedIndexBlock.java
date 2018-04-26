package Lab05;

import Constants.Constants;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static Lab05.IO.merge;
import static Lab05.IO.writeFile;

public class InvertedIndexBlock implements Runnable, Constants {
    private TreeMap<File, Integer> files_indexes_Map;

    //0_11.txt means a file is an 11th block created by 0th stack
    private int thread_id;//
    private int block_id;//

    InvertedIndexBlock(Stack<File> files, Stack<Integer> ids, int thread_id) {
        files_indexes_Map = new TreeMap<>();
        for (File f : files) {
            files_indexes_Map.put(f, ids.pop());
        }
        this.thread_id = thread_id;
        this.block_id = 0;
    }

    @Override
    public void run() {
        double current_size = 0.0;
        TreeMap<File, Integer> block_files_indexes = new TreeMap<>();
        while (!files_indexes_Map.isEmpty()) {
            Iterator<File> iter = files_indexes_Map.keySet().iterator();
            while (iter.hasNext()) {
                File curr = iter.next();
                if ((current_size + (curr.length() / DIVIDER)) > MAX_BLOCK_SIZE) {
                    current_size = 0.0;
                    System.out.println("Gonna create block");
                    createBlock(block_files_indexes);
                    System.out.println("Block created");
                    block_files_indexes.clear();
                    iter.remove();
                } else {
                    current_size += curr.length() / DIVIDER;
                    block_files_indexes.put(curr, files_indexes_Map.get(curr));
                    iter.remove();
                    System.out.println("Thread " + thread_id + " has " + files_indexes_Map.size() + "files left");
                    if (files_indexes_Map.isEmpty()) {//last iteration special
                        createBlock(block_files_indexes);
                    }
                }
            }
            try {//merge created files into one
                String res_address = output_folder + "Block" + thread_id + "\\1.txt";
                mergeBlocksIntoSuperBlock(res_address);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("All files proceeded. Thread quits");
    }

    /**
     * forms inv_index
     *
     * @param block_map lists of files to be processed
     */
    private void createBlock(TreeMap<File, Integer> block_map) {
        TreeMap<String, ArrayList<Integer>> inverted_index_structure = new TreeMap<>();
        //read
        int cnt = 0;
        for (File doc : block_map.keySet()) {
            System.out.println("Reading file from " + thread_id + "th thread. Iteration:" + cnt);
            try {
                BufferedReader br = new BufferedReader(new FileReader(doc));
                String line;
                while ((line = br.readLine()) != null) {
                    String[] terms = EXPRESSION.split(line);
                    for (String term : terms) {
                        if (!term.equals("")) {
                            term = term.toLowerCase();
                            if (!inverted_index_structure.keySet().contains(term)) {
                                ArrayList<Integer> buf = new ArrayList<>();
                                buf.add(block_map.get(doc));
                                inverted_index_structure.put(term, buf);
                            } else {
                                if (!inverted_index_structure.get(term).contains(block_map.get(doc)))
                                    inverted_index_structure.get(term).add(block_map.get(doc));
                            }
                        }
                    }
                }
                cnt++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        block_id++;
        String filepath = (output_folder + "\\Block" + thread_id + "\\" + block_id + ".txt");
        String directory = output_folder + "\\Block" + thread_id;
        if (!new File(directory).exists()) {
            try {
                Files.createDirectory(Paths.get(directory));
                if (inverted_index_structure.size() > 0) writeFile(new File(filepath), inverted_index_structure);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Block created");
    }


    private ArrayList<File> getSpecialFiles(File f) {
        ArrayList<File> files = new ArrayList<>();
        for (File file : Objects.requireNonNull(f.listFiles())) {//forms list
            if (file.isFile() && file.getName().contains(thread_id + "_") && file.getName().endsWith(".txt")) {
                files.add(file);
            }
        }
        files.trimToSize();
        return files;
    }

    private boolean hasMoreThanOne(File f) {
        int cnt = 0;
        for (File file : Objects.requireNonNull(f.listFiles())) {
            if (file.isFile() && file.getName().endsWith(".txt")) cnt++;
        }
        if (cnt <= 1) return false;
        return false;
    }

    /**
     * Merges all the files in a directory into a one file using threads
     */
    public void mergeBlocksIntoSuperBlock(String res_address) throws InterruptedException {//gets all files in the output folder
        System.out.println("Merging block");

        String path = output_folder + "\\Block" + thread_id;
        while (hasMoreThanOne(new File(path))) {
            System.out.println("Passed control");
            merge(getSpecialFiles(new File(path)), res_address);
        }
        System.out.println("Block merged;");
    }


}
