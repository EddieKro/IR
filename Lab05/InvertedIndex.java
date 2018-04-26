package Lab05;

import Constants.Constants;

import java.io.*;
import java.util.*;

import static Lab05.IO.*;

/**
 * Gets lists of files and their ids
 */
public class InvertedIndex implements Constants {
    InvertedIndex(File[] files, int[] ids) throws InterruptedException {
        double lowest_weight = 0.0;
        int lowest_weight_index = 0;

        Arrays.sort(files, Collections.reverseOrder());
        Stack<File>[] blocks = new Stack[THREADS];
        Stack<Integer>[] indexes = new Stack[THREADS];
        double[] block_sizes = new double[THREADS];
        for (int i = 0; i < blocks.length; i++) {
            blocks[i] = new Stack<>();
            indexes[i] = new Stack<>();
        }
        for (int i = 0; i < files.length; i++) {
            for (int j = 0; j < THREADS; j++) {
                if (lowest_weight > block_sizes[j]) {
                    lowest_weight = block_sizes[j];
                    lowest_weight_index = j;
                }
            }
            blocks[lowest_weight_index].add(files[i]);
            indexes[lowest_weight_index].add(i);
            block_sizes[lowest_weight_index] += files[i].length();
            lowest_weight = block_sizes[lowest_weight_index];
        }

        ThreadGroup invIndexGroup = new ThreadGroup("Inverted index");
        for (int i = 0; i < THREADS; i++) {
            System.out.println("THREADS:" + THREADS);
            Thread t = new Thread(invIndexGroup, new InvertedIndexBlock(blocks[i], indexes[i], i));
            t.start();
            System.out.println("thread [" + i + "] started");
        }
        while (invIndexGroup.activeCount() > 0) {
            Thread.sleep(3000);
            System.gc();
        }

        mergeIntoSingleFile();
    }

    /**
     * gets all non-empty .txt files in folder
     *
     * @param f folder-file
     * @return subfiles
     */
    private static ArrayList<File> getsubfiles(File f) {
        ArrayList<File> files = new ArrayList<>();
        for (File file : Objects.requireNonNull(f.listFiles())) {
            if (file.isFile() && file.getName().endsWith(".txt")) files.add(file);
        }
        files.trimToSize();
        return files;
    }

    /**
     * @param f - folder
     * @return true/false (more than one file left/no)
     */
    private boolean hasMoreThanOne(File f) {
        System.out.println("searching fo files in" + f.getName());
        int cnt = 0;
        for (File file : Objects.requireNonNull(f.listFiles())) {
            if (file.isFile() && file.getName().endsWith(".txt")) cnt++;
        }
        return cnt > 1;
    }

    /**
     * forms one file from many
     *
     * @throws InterruptedException
     */

    private void collectGarbage() {
        String output_foldr = "C:\\Users\\alexa\\IdeaProjects\\InformationRetrieval_backdoor\\src\\Files\\Output";

        File files = new File(output_foldr);
        for (File f : Objects.requireNonNull(files.listFiles())) {
            if (f.isDirectory()) {
                f.delete();
            }
        }
    }

    private void mergeIntoSingleFile() throws InterruptedException {//merge n files into invertedIndex.txt
        System.out.println("Gonna created one file to rule them all");

        String invIndexPath = output_folder + "invertedIndex.txt";

        ArrayList<File> blocks = getsubfiles(new File(output_folder));
        merge(blocks, invIndexPath);

        System.out.println("One file to rule them all created!");
        collectGarbage();
    }

}
