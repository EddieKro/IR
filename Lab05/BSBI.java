package Lab05;

import Constants.Constants;

import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Stack;

class BSBI implements Constants {

    BSBI(File[] docs, String output_folder, int threads_num, double max_block_size) throws InterruptedException {

        Arrays.sort(docs, Comparator.reverseOrder());

        double lowest_weight = 0.0;
        int lowest_weight_index = 0;

        Stack<File>[] blocks = new Stack[threads_num];
        double[] blocks_size = new double[threads_num];
        for (int i = 0; i < blocks.length; i++) {
            blocks[i] = new Stack<>();
        }
        for (File file : docs) {
            for (int i = 0; i < threads_num; i++) {
                //blocks_size[i] = file.length();
                if (lowest_weight > blocks_size[i]) {
                    lowest_weight = blocks_size[i];
                    lowest_weight_index = i;
                }
            }
            blocks[lowest_weight_index].add(file);
            blocks_size[lowest_weight_index] += file.length();
            lowest_weight = blocks_size[lowest_weight_index];
        }

        ThreadGroup invIndexGroup = new ThreadGroup("Inverted index");
        for (int i = 0; i < threads_num; i++) {
            File[] temp = blocks[i].toArray(new File[blocks[i].size()]);
            Thread t = new Thread(invIndexGroup, new BSBIBlock(i, temp));
            t.start();
            System.out.println("thread [" + i + "] started");
        }
        while (invIndexGroup.activeCount() > 0) {
            Thread.sleep(3000);
            System.gc();
        }

        try {
            merge(new File(filepath));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean isNull(String[] line) {
        for (String s : line) {
            if (s != null) return true;
        }
        return false;
    }

    private void merge(File path) throws IOException {
        File[] blocks = path.listFiles();
        BufferedReader[] br = new BufferedReader[blocks.length];
        for (int i = 0; i < br.length; i++) {
            br[i] = new BufferedReader(new FileReader(blocks[i]));
        }
        String[] line = new String[br.length];
        FileWriter fw = new FileWriter(new File(filepath + "/parallel/invertedIndex/invIndex.txt"));
        PrintWriter pw = new PrintWriter(fw);
        for (int i = 0; i < line.length; i++)
            line[i] = br[i].readLine();
        while (isNull(line)) {
            StringBuilder sb = new StringBuilder();
            String closest = null;
            for (int i = 0; i < line.length; i++) {
                if (closest == null)
                    closest = line[i].split("-")[0];
                else if (closest.compareTo(line[i].split("-")[0]) > 0)
                    closest = line[i].split("-")[0];
            }
            Stack<String> docID = new Stack<>();
            Stack<Integer> indexes = new Stack<>();
            sb.append(closest).append("-");
            for (int i = 0; i < line.length; i++)
                if (line[i] != null)
                    if (closest.equals(line[i].split("-")[0])) {
                        indexes.push(i);
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

}

