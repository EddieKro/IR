package Lab05;

import Constants.Constants;
import com.google.common.collect.TreeMultimap;

import java.io.*;
import java.util.Stack;

public class BSBIBlock implements Runnable, Constants {
    private int id;
    private File[] block;
    private int block_id = 0;
    private int current_file = 0;

    BSBIBlock(int id, File[] block) {
        this.id = id;
        this.block = block;
    }

    private void createBlock(Stack<File> stack) throws IOException {
        TreeMultimap<String, Integer> invertedIndBlock = TreeMultimap.create();
        File block = new File(filepath + id + block_id + ".txt");
        System.out.println("File: " + block.getAbsolutePath() + " created successfully");
        block_id++;
        System.out.println(stack.size());
        while (!stack.isEmpty()) {
            File doc = stack.pop();
            System.out.println("stack size:" + stack.size());
            try {
                BufferedReader br = new BufferedReader(new FileReader(doc));
                String line;
                while ((line = br.readLine()) != null) {
                    String[] terms = PARSE_EXPR.split(line);
                    for (String term : terms) {
                        if (!term.equals("")) {
                            invertedIndBlock.put(term, current_file);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            current_file++;
        }
        try {
            if (block.exists()) {
                BufferedWriter bw = new BufferedWriter(new FileWriter(block));
                PrintWriter pw = new PrintWriter(bw);
                for (String key : invertedIndBlock.keySet()) {
                    String val = key + " - ";
                    for (Integer i : invertedIndBlock.get(key)) {
                        val += id + "." + i + " ";
                    }
                    val += "\n";
                    pw.print(val);
                }
                pw.close();
                bw.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        double current_size = 0.0;
        Stack<File> block_stack = new Stack<>();
        for (File file : block) {
            if (current_size + (file.length() / DIVIDER) > MAX_BLOCK_SIZE) {
                current_size = 0.0;
                try {
                    createBlock(block_stack);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                current_size += file.length() / DIVIDER;
                block_stack.add(file);
            }
        }
    }
}
