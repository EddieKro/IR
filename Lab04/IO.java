package Lab04;

import java.io.*;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.regex.Pattern;

public class IO {
    private static final Pattern EXPRESSION = Pattern.compile("[^a-zA-Zа-яА-Я]+");
    private static final Pattern SPACE = Pattern.compile(" ");
    private static final Pattern STAR = Pattern.compile("[*]");

    private static PrefixTreeDictionary prefixTreeDictionary = new PrefixTreeDictionary();//local instance
    private static K_GramIndex k_gramIndex = new K_GramIndex();
    private static PermutationIndex permutationIndex = new PermutationIndex();

    private static void addFileToPrefixTree(File f) throws IOException {//builds prefix tree dictionary
        BufferedReader br = new BufferedReader(new FileReader(f));

        if (!f.exists()) return;

        String t;
        while ((t = br.readLine()) != null) {//very simple
            String buf0 = t;
            buf0 = buf0.toLowerCase();
            String[] buf = EXPRESSION.split(buf0);
            for (String s : buf) {
                prefixTreeDictionary.addWord(s);
            }
        }

    }


    private static void formCollection4ThreeGramIndex(File f) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(f));

        if (!f.exists()) return;

        String t;
        while ((t = br.readLine()) != null) {
            String buf0 = t;
            buf0 = buf0.toLowerCase();
            String[] buf = EXPRESSION.split(buf0);

            for (String s : buf) {
                if (k_gramIndex.getIndex().containsKey(s)) continue; //for duplicates
                TreeSet<String> set = new TreeSet<>();//form Set

                for (int i = -1; i < s.length() - 1; i++) {
                    StringBuilder entry = new StringBuilder();

                    if (i < 0) {
                        entry.append("$");
                    } else entry.append(s.charAt(i));

                    entry.append(s.charAt(i + 1));

                    if (i + 2 > s.length() - 1) {
                        entry.append("$");
                    } else entry.append(s.charAt(i + 2));

                    set.add(entry.toString());
                }
                k_gramIndex.getIndex().put(s, set);
            }
        }

    }

    private static void formCollection4PermutationIndex(File f) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(f));

        if (!f.exists()) return;

        String t;
        while ((t = br.readLine()) != null) {
            String buf0 = t;
            buf0 = buf0.toLowerCase();
            String[] buf = EXPRESSION.split(buf0);

            for (String s : buf) {
                if (permutationIndex.getIndex().containsKey(s)) continue;
                String curr = s + "$";
                TreeSet<String> res = new TreeSet<>();
                for (int i = -1; i < s.length(); i++) {
                    if (i >= 0) {
                        curr = curr + curr.charAt(0);
                        curr = curr.substring(1, curr.length());
                    }
                    res.add(curr);
                }
                permutationIndex.getIndex().put(s, res);//adds <K,V> pair to the index

            }
        }

    }

    private static ArrayList<String> jokerQueries(String query) {

        ArrayList<String> res = new ArrayList<>();

        if (query.equals("")) {
            res.add("Incorrect query");
            return res;
        }

        query = query.toLowerCase();

        String[] spacetokens = SPACE.split(query);

        ArrayList<String> tokenAL = new ArrayList<>();//3-grams of query

        for (String token : spacetokens) {

            if (token.equals("and") || token.equals("or")) continue;
            String[] tokens = STAR.split(token);//splits by star symbol
            if (tokens[0].length() < 2 || tokens[tokens.length - 1].length() < 2) {//instead of IllegalArgException
                res.add("Incorrect query");
                return res;
            }

            tokens[0] = "$" + tokens[0];
            tokens[tokens.length - 1] = tokens[tokens.length - 1] + "$";

            for (int i = 0; i < tokens.length; i++) {//splits every token
                tokenAL.addAll(splitWord(tokens[i]));
            }

            for (String key : k_gramIndex.getIndex().keySet()) {

                boolean flag = true;

                for (String s : tokenAL) {
                    if (!k_gramIndex.getIndex().get(key).contains(s)) {
                        flag = false;
                    }
                }
                if (flag) {
                    res.add(key);
                }
            }
        }

        return res;
    }

    private static ArrayList<String> splitWord(String word) {
        ArrayList<String> res = new ArrayList<>();

        for (int i = 0; i < word.length() - 2; i++) {
            StringBuilder sb = new StringBuilder();

            sb.append(word.charAt(i));
            sb.append(word.charAt(i + 1));
            sb.append(word.charAt(i + 2));

            res.add(sb.toString());
        }
        return res;
    }


    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Welcome. Please, input path to the folder with files");
        String filepath = br.readLine();
        File file = new File(filepath);
        if (!file.exists() || !file.isDirectory()) {//default path
            filepath = "C:\\Users\\alexa\\IdeaProjects\\InformationRetrieval\\src\\Lab01_Dictionary\\files\\";//default filepath
            file = new File(filepath);
        }


        File[] listOfFiles = file.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".txt");
            }
        });//reads all .txt files in a Directory and adds them to the collection
        // prefix tree


        assert listOfFiles != null;

        char c;
        System.out.println("To build prefix tree input 1, to build permutation index input 2, to three-gram index, input 3;");
        c = br.readLine().charAt(0);
        if (c == '1') {

            for (File f : listOfFiles) {
                addFileToPrefixTree(f);
                System.out.println("Prefix tree based on: " + f.getName() + " was built successfully");
            }

            System.out.println("All files were added successfully!\nNo queries here. Try another option of a menu");

        } else if (c == '2') {

            for (File f : listOfFiles) {
                formCollection4PermutationIndex(f);
                System.out.println("permutation index based on: " + f.getName() + " was built successfully");

            }

            System.out.println("All files were added successfully!\nNo queries here. Try another option of a menu");


        } else if (c == '3') {

            for (File f : listOfFiles) {
                formCollection4ThreeGramIndex(f);
                System.out.println("Three-gram index based on: " + f.getName() + " was built successfully");
            }

            System.out.println("All files were added successfully!\nNow let`s have some query time");

            System.out.println("Please, input query. To exit, input '-'");
            String query;
            while (!(query = br.readLine()).equals("-")) {

                ArrayList<String> results = jokerQueries(query);

                System.out.println("And here are the results:");
                for (String s : results) {
                    System.out.println(s);
                }
                System.out.println("Please, input query. To exit, input '-'");
            }
            System.out.println("Thanks for the attention!");

        }

    }

}
