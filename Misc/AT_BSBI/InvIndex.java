package Misc.AT_BSBI;

import java.io.*;
import java.util.*;

public class InvIndex {
    public InvIndex(File path, double max_block_size_mb, int nThreads) throws InterruptedException {
        this(FilePathToArr.getArr(path), max_block_size_mb, nThreads);
    }

    public InvIndex(File[] docs, double max_block_size_mb, int nThreads) throws InterruptedException {
        Stack<File>[] fileBlocks = new Stack[nThreads];
        for (int i = 0; i < nThreads; i++)
            fileBlocks[i] = new Stack<>();
        Arrays.sort(docs, Comparator.reverseOrder());
        double[] fileBlockSize = new double[nThreads];
        int lowestWeightIndex = 0;
        double lowestWeight = 0;
        for (File f : docs) {
            //System.out.println(f.length());
            f.length();
            for (int i = 0; i < nThreads; i++) {
                if (lowestWeight > fileBlockSize[i]) {
                    lowestWeight = fileBlockSize[i];
                    lowestWeightIndex = i;
                }
            }
            fileBlocks[lowestWeightIndex].add(f);
            fileBlockSize[lowestWeightIndex] += f.length() / 1000.0;
            lowestWeight = fileBlockSize[lowestWeightIndex];
        }
        ThreadGroup invIndexGroup = new ThreadGroup("inverted index");
        for (int i = 0; i < nThreads; i++) {
            File[] temp = fileBlocks[i].toArray(new File[fileBlocks[i].size()]);
            Thread t = new Thread(invIndexGroup, new InvIndBlock(i, temp, max_block_size_mb));
            t.start();
        }
        while (invIndexGroup.activeCount() > 0) {
            Thread.sleep(30000);
            System.gc();
        }
        //merge
        try {
            merge(new File("./parallel/block"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void merge(File path) throws IOException {
        File[] blocks = path.listFiles();
        System.out.print("blocks length:" + blocks.length);
        BufferedReader[] br = new BufferedReader[blocks.length];
        for (int i = 0; i < br.length; i++)
            br[i] = new BufferedReader(new FileReader(blocks[i]));
        String[] line = new String[br.length];
        FileWriter fw = new FileWriter(new File("./parallel/invertedIndex/invIndex.txt"));
        PrintWriter pw = new PrintWriter(fw);
        for (int i = 0; i < line.length; i++)
            line[i] = br[i].readLine();
        while (checkNull(line)) {
            /*String[][] terms=new String[line.length][2];
            for(int i=0;i<line.length;i++){
                String[] temp=line[i].split("-");
                terms[i][0]=temp[0];
                terms[i][1]=temp[1];
            }*/
            StringBuilder sb = new StringBuilder();
            String closestString = null;
            for (int i = 0; i < line.length; i++)
                if (line[i] != null) {
                    if (closestString == null)
                        closestString = line[i].split("-")[0];
                    else if (closestString.compareTo(line[i].split("-")[0]) > 0)
                        closestString = line[i].split("-")[0];
                }
            Stack<String> docID = new Stack<>();
            Stack<Integer> indexes = new Stack<>();
            sb.append(closestString).append("-");
            for (int i = 0; i < line.length; i++)
                if (line[i] != null)
                    if (closestString.equals(line[i].split("-")[0])) {
                        indexes.push(i);
                        //System.out.println(line[i]);
                        String[] docsID = line[i].split("-")[1].split(" ");
                        for (int j = 0; j < docsID.length; j++)
                            if (!docID.contains(docsID[j])) {
                                docID.push(docsID[j]);
                                sb.append(docsID[j] + " ");
                            }
                    }
            pw.print(sb.toString() + "\n");
            for (Integer i : indexes)
                line[i.intValue()] = br[i.intValue()].readLine();
        }
        pw.close();
        fw.close();
    }

    private boolean checkNull(String[] line) {
        for (String l : line)
            if (l != null)
                return true;
        return false;
    }

    public InvIndex(File path) throws InterruptedException {
        this(path, 4000.0, 4);
    }

    public static void getFileArr(File path) {
        //todo return file[]

    }

    public static void main(String[] args) throws InterruptedException {

        long start = System.currentTimeMillis();
        try {
            InvIndex test = new InvIndex(FilePathToArr.getArr(new File("C:\\Users\\alexa\\IdeaProjects\\IR\\src\\Files\\Input\\")), 150.0, 4);
        } finally {
            System.out.println((System.currentTimeMillis() - start) / 1000.0);
        }

    }
}

class FilePathToArr {
    private static ArrayList<File> files = new ArrayList<>();

    public static File[] getArr(File path) {
        for (File f : path.listFiles()) {
            if (f.isDirectory())
                getArr(f);
            else files.add(f);
        }
        return files.toArray(new File[files.size()]);
    }

}