package Misc;

import sun.reflect.generics.tree.Tree;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;
import java.util.regex.Pattern;

public class Test {
    private static final Pattern PATTERN = Pattern.compile("[\\W,]+");
    private static final Pattern EXPRESSION = Pattern.compile("[^a-zA-Zа-яА-Я]+");//Split pattern

   /* public static String encodeNumber(int num) {
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

    }*/

    public static void main(String[] args) throws IOException {


        TreeMap<Double, String> m = new TreeMap<>(Collections.reverseOrder());
        m.put(0.25, "A");
        m.put(0.67, "B");
        m.put(0.5, "C");

        System.out.println(m);
        System.out.println(new StringBuilder(Integer.toBinaryString(6)));

        System.out.print("\nBye");

    }
}
