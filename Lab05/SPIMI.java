package Lab05;

import java.util.*;
import java.io.*;
import java.util.regex.Pattern;

interface Constantable {
    String filepath = "C:\\Users\\alexa\\IdeaProjects\\IR\\src\\Files\\Output\\";//path to an output folder with new files
    String filename = "invertedIndex.txt";
    Pattern PARSE_EXPR = Pattern.compile("[\\W,]+");//read inv index file
    Pattern EXPRESSION = Pattern.compile("[^a-zA-Zа-яА-Я]+");//Split pattern
    double DIVIDER = 100000.0;//MB->B
    double MAX_BLOCK_SIZE = 10.0;//MB-> almost a const [cause has to be initialized]

}
/*
class Constants {
    String filepath = "C:\\Users\\alexa\\IdeaProjects\\IR\\src\\Files\\Output\\";//path to an output folder with new files
    String filename = "invertedIndex.txt";
    final Pattern PARSE_EXPR = Pattern.compile("[\\W,]+");//read inv index file
    final Pattern EXPRESSION = Pattern.compile("[^a-zA-Zа-яА-Я]+");//Split pattern
    final double DIVIDER = 100000.0;//MB->B
    double MAX_BLOCK_SIZE;//MB-> almost a const [cause has to be initialized]

    Constants() {
    }

}*/

class SPIMI_BLOCK implements Runnable, Constantable {

    //private Constants constants = new Constants();
    private static final Pattern EXPRESSION = Pattern.compile("[^a-zA-Zа-яА-Я]+");//Split pattern
    // private final double MAX_BLOCK_SIZE;//MB
   // private final double DIVIDER = 100000.0;//MB->B
    private int id;
    private File[] block;
    private int blockID;
    private String[] docID;
    private int current;
    //  private String filepath = "C:\\Users\\alexa\\IdeaProjects\\IR\\src\\Files\\Output\\";//path to a folder with new files

    public SPIMI_BLOCK(int id, File[] block, double max_block_size) {
        //constants.MAX_BLOCK_SIZE = max_block_size;
        // MAX_BLOCK_SIZE = max_block_size;
        this.id = id;
        this.block = block;
        docID = new String[block.length];
    }


    @Override
    public void run() {
        double currentSize = 0.0;
        Stack<File> blockStack = new Stack<>();
        for (File f : block) {
            System.out.println("Current size: "+currentSize);
            if (currentSize + (f.length() / DIVIDER) > MAX_BLOCK_SIZE) {
                currentSize = 0.0;
                try {
                    createBlock(blockStack);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            currentSize += f.length() / DIVIDER;
            System.out.println("File " + f.getName() + " of size: " + currentSize + " added");
            blockStack.push(f);
        }
    }

    private void createBlock(Stack<File> fileStack) throws IOException {
        TreeMap<String, ArrayList<Integer>> invertedIndex = new TreeMap<>();
        File block = new File(filepath + "block#" + id + ".txt");

        while (!fileStack.empty()) {
            File f = fileStack.pop();
            docID[current] = (current) + "$" + f;

            BufferedReader br = new BufferedReader(new FileReader(f));
            String buf;
            while ((buf = br.readLine()) != null) {
                String[] tokens = EXPRESSION.split(buf);
                for (String token : tokens) {
                    if (!invertedIndex.containsKey(token)) {
                        ArrayList<Integer> aux = new ArrayList<>();
                        aux.add(current);
                        invertedIndex.put(token, aux);
                    } else invertedIndex.get(token).add(current);
                }
            }
            current++;
        }
        try {
            BufferedWriter br = new BufferedWriter(new FileWriter(block));
            PrintWriter pr = new PrintWriter(br);
            for (String key : invertedIndex.keySet()) {
                String entry = key + ":[";
                for (int val : invertedIndex.get(key))
                    entry += val + ",";
                entry += "]\n";
                pr.print(entry);
            }
            pr.close();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

public class SPIMI implements Constantable {

    private boolean arrayIsEmpty(String[] array) {
        for (String s : array) {
            if (s != null) return true;
        }
        return false;
    }

    private void merge(File filepath) throws IOException {
        File[] blocks = filepath.listFiles();
        BufferedReader[] br = new BufferedReader[blocks.length];
        for (int i = 0; i < br.length; i++) {
            br[i] = new BufferedReader(new FileReader(blocks[i]));
        }
        String[] buf = new String[br.length];
        FileWriter fw = new FileWriter(filepath + "\\" + filename);
        PrintWriter pw = new PrintWriter(fw);
        for (int i = 0; i < buf.length; i++)
            buf[i] = br[i].readLine();

        //TODO: merge inverted indexes from txt
        while (arrayIsEmpty(buf)) {
            StringBuilder sb = new StringBuilder();
            String closest = null;
            for (int i = 0; i < buf.length; i++) {
                if (buf[i] != null) {
                    String[] aux = PARSE_EXPR.split(buf[i]);
                    if (closest == null)
                        closest = aux[0];//key
                    else if (closest.compareTo(aux[0]) > 0)
                        closest = aux[0];
                }
            }
            Stack<String> docID = new Stack<>();
            Stack<Integer> indexes = new Stack<>();

            sb.append(closest).append(":[");
            for (int i = 0; i < buf.length; i++)
                if (buf[i] != null) {
                    String[] aux = PARSE_EXPR.split(buf[i]);
                    assert closest != null;
                    if (closest.equals(aux[0])) {
                        indexes.push(i);
                        //String[] ids aux[1]--aux[n-1]
                        for (int j = 1; j < aux.length; j++) {
                            if (!docID.contains(aux[j])) {
                                docID.push(aux[j]);
                                sb.append(aux[j]).append(",");
                            }
                        }
                    }
                }

            pw.print(sb.toString() + "]\n");
            for (Integer i : indexes) {
                buf[i.intValue()] = br[i.intValue()].readLine();

            }
        }
        pw.close();
        fw.close();
    }


    public SPIMI(File[] files, double max_block_size, int threads) throws InterruptedException {
        Stack<File>[] fBlocks = new Stack[threads];
        for (int i = 0; i < threads; i++)
            fBlocks[i] = new Stack<File>();
        Arrays.sort(files, Comparator.reverseOrder());
        for (File file : files) {
            System.out.println(file.getName());
        }
        double[] blockSize = new double[threads];
        int lowestWeightInd = 0;
        double lowestWeight = 0;
        for (File f : files) {
            //f.length();
            for (int i = 0; i < threads; i++) {
                if (lowestWeight > blockSize[i]) {
                    lowestWeight = blockSize[i];
                    lowestWeightInd = i;
                }
            }
            fBlocks[lowestWeightInd].add(f);
            blockSize[lowestWeightInd] += f.length();
            lowestWeight = blockSize[lowestWeightInd];
        }

        ThreadGroup indGroup = new ThreadGroup("inverted index");
        for (int i = 0; i < threads; i++) {
            File[] tmp = fBlocks[i].toArray(new File[fBlocks[i].size()]);
            Thread t = new Thread(indGroup, new SPIMI_BLOCK(i, tmp, max_block_size));
            t.start();
        }
        while (indGroup.activeCount() > 0) {
            Thread.sleep(10000);
            System.gc();//garbage
        }

        try {
            merge(new File(filepath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
