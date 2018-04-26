package Lab06;

import Lab05.InvertedIndex;

import static Lab05.IO.readFile;

import java.io.*;
import java.util.ArrayList;
import java.util.TreeMap;

public class InvertedIndexCompression {
    TreeMap<String, ArrayList<Integer>> invertedIndex;

    InvertedIndexCompression(File f, File path) {
        invertedIndex = readFile(f);
        invertedIndex = compressIntervals(invertedIndex);
        TreeMap<String, String> inv_index_encoded = variableBytesEncoding(invertedIndex);
        write(inv_index_encoded, path);
    }

    String encodeNumber(int num) {
        String buf = Integer.toBinaryString(num);

        if (buf.length() < 8) {
            StringBuilder res = new StringBuilder(buf);
            while (res.length() != 7) {
                res.insert(0, "0");
            }
            res.insert(0, "1");
            return res.toString();
        } else {
            StringBuilder res = new StringBuilder();
            boolean flag = false;
            for (int i = buf.length(); i >= 0; i -= 7) {
                StringBuilder b;
                if (i >= 6) {
                    b = new StringBuilder(buf.substring(i - 7, i));
                    if (!flag) {
                        System.out.println("B bef: " + b.toString());
                        b.insert(0, "1");
                        System.out.println("B aft: " + b.toString());
                        flag = true;
                    } else b.insert(0, "0");
                } else {
                    b = new StringBuilder(buf.substring(0, i));
                    while (b.length() != 8) {
                        b.insert(0, "0");
                    }
                }
                res.insert(0, b.toString());
            }
            return res.toString();
        }

    }

    TreeMap<String, String> variableBytesEncoding(TreeMap<String, ArrayList<Integer>> invInd) {
        TreeMap<String, String> res = new TreeMap<>();
        for (String key : invInd.keySet()) {
            StringBuilder binary_value = new StringBuilder();
            for (Integer i : invInd.get(key)) {
                String code = encodeNumber(i);
                binary_value.append(code);
            }
            res.put(key, binary_value.toString());
        }
        return res;
    }

    TreeMap<String, ArrayList<Integer>> compressIntervals(TreeMap<String, ArrayList<Integer>> invInd) {
        for (String key : invInd.keySet()) {
            ArrayList<Integer> buf = invInd.get(key);
            for (int i = 1; i < buf.size(); i++) {
                buf.set(i, buf.get(i) - buf.get(i - 1));
            }
            invInd.put(key, buf);
        }
        return invInd;
    }


    void write(TreeMap<String, String> inv_ind, File path) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(path));
            PrintWriter pw = new PrintWriter(bw);
            for (String key : inv_ind.keySet()) {
                String entry = key + ":" + inv_ind.get(key) + "\n";
                pw.print(entry);
            }
            pw.close();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void decode(File f) {

    }

    public static void main(String[] args) {

    }
}
