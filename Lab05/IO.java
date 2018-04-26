package Lab05;

import Constants.Constants;

import java.io.*;
import java.util.ArrayList;
import java.util.TreeMap;

public class IO implements Constants {
    /**
     * Writes inv index to file
     *
     * @param file           new file
     * @param inverted_index index to be written
     * @throws IOException
     */
    static void writeFile(File file, TreeMap<String, ArrayList<Integer>> inverted_index) throws IOException {
        System.out.println("Gonna create file: " + file.getAbsolutePath());

        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        PrintWriter pw = new PrintWriter(bw);
        for (String key : inverted_index.keySet()) {
            StringBuilder entry = new StringBuilder(key + ":");
            for (Integer id : inverted_index.get(key)) {
                entry.append(id).append(",");
            }
            entry.append("\n");
            pw.print(entry);
        }
        pw.close();
        bw.close();
        System.out.println("File created");
    }

    /**
     * @param file with inverted index( term: pos1,pos2,pos3,..)
     * @return inverted index
     */
    public static TreeMap<String, ArrayList<Integer>> readFile(File file) {
        TreeMap<String, ArrayList<Integer>> res = new TreeMap<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                String[] terms = PARSE_EXPR.split(line);
                ArrayList<Integer> values = new ArrayList<>();
                for (int i = 1; i < terms.length; i++) {
                    values.add(Integer.valueOf(terms[i]));
                }
                values.trimToSize();
                res.put(terms[0], values);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    static void deleteFile(File file) {
        file.delete();
        System.out.println("File deleted successfully");
    }


    static File[] getFiles(String folderpath) {//gets all the txt files in all the directories
        ArrayList<File> aux = new ArrayList<>();
        getFilesL(folderpath, aux);
        return aux.toArray(new File[0]);
    }


    public static void getFilesL(String filepath, ArrayList<File> files) {
        File f = new File(filepath);
        File[] list = f.listFiles();
        for (File file : list) {
            if (file.isFile() && file.getName().endsWith(".txt")) files.add(file);
            else if (file.isDirectory()) getFilesL(file.getAbsolutePath(), files);

        }
    }


    public static void merge(ArrayList<File> files, String path) throws InterruptedException {
        ThreadGroup threadGroup = new ThreadGroup("superBlock");
        if (files.size() == 0) {
            try {
                writeFile(new File(path), readFile(new File(output_folder + "Block0\\1.txt")));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Gonna merge  " + files.size() / 2 + "files");
        for (int i = 0; i < files.size() / 2; i++) {
            Thread t;
            if (path.equals("")) {
                t = new Thread(threadGroup, new Merger(files.get(i), files.get(i + files.size() / 2)));
            } else
                t = new Thread(threadGroup, new Merger(files.get(i), files.get(i + files.size() / 2), path));
            t.start();
        }
        while (threadGroup.activeCount() > 0) {
            Thread.sleep(3000);
            System.gc();
        }
    }


    public static void main(String[] args) {
        //parallel indexing using threads
        String folderpath = "C:\\TextCollection";

        File[] files = getFiles(input_folder);
        int[] ids = new int[files.length];
        for (int i = 0; i < files.length; i++) {
            ids[i] = i + 1;//or just i
        }

        try {
            InvertedIndex index = new InvertedIndex(files, ids);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("Done. Successfully..");
        }

        System.out.println("Bye-bye");
    }
}
