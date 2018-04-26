package HW1;

import Constants.Constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;

import static Lab05.IO.readFile;

public class BooleanRetrieval implements Constants {

    static TreeMap<String, ArrayList<Integer>> invertedIndex;
    static ArrayList<Integer> docs;
    static ArrayList<Integer> res;

    static void analyze_matrix(ArrayList<String> queries, ArrayList<String> modifiers, boolean[][] matr) {
        res = new ArrayList<>();
        for (int i = 0; i < matr[0].length; i++) {
            boolean belongs = matr[0][i];
            for (int j = 0; j < matr.length; j++) {
                {
                    if (j != matr.length - 1) {
                        switch (modifiers.get(j)) {
                            case "and":
                                belongs = belongs & matr[j + 1][i];
                                break;
                            case "or":
                                belongs = belongs | matr[j + 1][i];
                                break;
                            default://not
                                belongs = belongs & !matr[j + 1][i];
                                break;
                        }
                    }
                }
            }
            if (belongs) res.add(docs.get(i));
        }
        res.trimToSize();
    }

    static void formMatrix(ArrayList<String> queries, ArrayList<String> modifiers) {
        System.out.println("Matrix (term - document) looks like:\n");
        boolean[][] bool_matrix = new boolean[queries.size()][docs.size()];
        for (int i = 0; i < queries.size(); i++) {
            System.out.print(queries.get(i) + ": ");
            for (int j = 0; j < docs.size(); j++) {
                if (invertedIndex.get(queries.get(i)).contains(docs.get(j))) bool_matrix[i][j] = true;
                else bool_matrix[i][j] = false;
                System.out.print(bool_matrix[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
        analyze_matrix(queries, modifiers, bool_matrix);
    }

    static void getResults(ArrayList<String> queries, ArrayList<String> modifiers) {
        docs = new ArrayList<>();
        for (String query : queries) {
            {
                if (invertedIndex.containsKey(query)) {
                    ArrayList<Integer> buf = invertedIndex.get(query);
                    for (int i : buf) {
                        if (!docs.contains(i)) docs.add(i);
                    }
                }
            }
            Collections.sort(docs);
        }

        formMatrix(queries, modifiers);
    }

    private static void handleQuery(String line) {
        String[] buf = EXPRESSION.split(line);
        ArrayList<String> query = new ArrayList<>();
        ArrayList<String> modif = new ArrayList<>();
        StringBuilder query_term = new StringBuilder();
        for (int i = 0; i < buf.length; i++) {
            if (buf[i].toLowerCase().equals("and") || buf[i].toLowerCase().equals("or") || buf[i].toLowerCase().equals("not")) {
                query_term.deleteCharAt(query_term.length() - 1);
                query.add(query_term.toString());
                query_term.setLength(0);
                query_term = new StringBuilder();
                switch (buf[i].toLowerCase()) {
                    case "and":
                        modif.add("and");
                        break;
                    case "or":
                        modif.add("or");
                        break;
                    case "not":
                        modif.add("not");
                        break;
                }
            } else {
                query_term.append(buf[i]).append(" ");
                if (i == buf.length - 1) {
                    query_term.deleteCharAt(query_term.length() - 1);
                    query.add(query_term.toString());
                }
            }
        }

        query.trimToSize();
        modif.trimToSize();
        getResults(query, modif);
        if (res.size() != 0) {
            System.out.println("Results:");
            for (int i : res) {
                System.out.println("Document #" + i + " matches your query");
            }
        } else System.out.println("No documents match your query");
    }

    public static void main(String[] args) throws IOException {
        invertedIndex = readFile(new File(inverted_index_address));
        System.out.println("Welcome. Please, input queries. To exit, please, input'0'");
        String line;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (!(line = br.readLine()).equals("0")) {
            handleQuery(line);
        }
        System.out.println("Thanks for the attention. Bye");
    }

}
