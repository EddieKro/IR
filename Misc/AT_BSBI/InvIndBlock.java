package Misc.AT_BSBI;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.TreeMultimap;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.*;

public class InvIndBlock implements Runnable {
    private final double MAX_BLOCK_SIZE_MB;
    private int id;
    private File[] block;
    private int blockID;
    private String[] docID;
    private int currentFile;

    public InvIndBlock(int id, File[] block, double max_block_size_mb) {
        MAX_BLOCK_SIZE_MB = max_block_size_mb;
        this.id = id;
        this.block = block;
        docID = new String[block.length];
    }

    public InvIndBlock(int id, File[] block) {
        this(id, block, 4000.0);
    }

    @Override
    public void run() {
        double currentBlockSize = 0.0;
        Stack<File> fileBlockStack = new Stack<>();
        for (File f : block) {
            if (currentBlockSize + (f.length() / 1000000.0) > MAX_BLOCK_SIZE_MB) {
                currentBlockSize = 0.0;
                try {
                    createBlock(fileBlockStack);
                } catch (FileNotFoundException e) {
                    // e.printStackTrace();
                }
            }
            currentBlockSize += f.length() / 1000000.0;
            System.out.println("added " + f + " " + currentBlockSize);
            fileBlockStack.push(f);
        }
        if (!fileBlockStack.empty())

            try {
                createBlock(fileBlockStack);
            } catch (FileNotFoundException e) {

            }

        System.out.println("---------------" + id + " finished-----------------");
    }

    private void createBlock(Stack<File> fileBlockStack) throws FileNotFoundException {
        TreeMultimap<String, Integer> invIndexBlock = TreeMultimap.create();
        File block = new File("./parallel/block/" + id + blockID + ".txt");
        System.out.print("File created");
        blockID++;
        while (!fileBlockStack.empty()) {
            File doc = fileBlockStack.pop();
            docID[currentFile] = (currentFile) + "#" + doc;
            System.out.println(doc + " " + id);
            try {
                BufferedReader br = new BufferedReader(new FileReader(doc));
                String line;
                while ((line = br.readLine()) != null) {
                    String[] words = line.split("\\W");
                    for (String k : words)
                        if (!k.equals(""))
                            invIndexBlock.put(k, currentFile);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            currentFile++;
        }
        try {
            if (block.exists()) {


                BufferedWriter br = new BufferedWriter(new FileWriter(block));
                PrintWriter pr = new PrintWriter(br);
                for (String k : invIndexBlock.keySet()) {
                    String index = k + "-";
                    for (Integer i : invIndexBlock.get(k))
                        index += id + "." + i + " ";
                    index += "\n";
                    pr.print(index);
                }
                pr.close();
                br.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}