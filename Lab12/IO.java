package Lab12;

import Constants.Constants;
import Lab07.Zone;
import Lab07.ZoneHandler;
import org.w3c.dom.Document;
import smile.nlp.relevance.BM25;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.TreeMap;


public class IO implements Constants {
    static ZoneHandler zh;
    static File[] files;
    static BM25 bm25;

    private static int calcFrequency(String term) {
        return 9;
    }

    private static int calcAverageLength() {
        int avg = 0;
        for (File f : files) {
            avg += f.length();
        }
        return avg / files.length;
    }

    private static void handleQuery(String query) {
        String[] tokens = EXPRESSION.split(query);
        TreeMap<Double, String> scoringMap = new TreeMap<>(Collections.reverseOrder());
        for (String s : tokens) {
            for (Zone zone : zh.getIndex().keySet()) {

                bm25.score(calcFrequency(s), files.length, calcAverageLength());
                zone.calculateWeight(s);
                scoringMap.put(zone.calculateWeight(s), files[zh.getIndex().get(zone)].getName());
            }
        }
        System.out.println("Here are the results of the query:");

        for (Double key : scoringMap.keySet()) {
            System.out.println("Document: " + scoringMap.get(key) + " ; score=" + key);
        }
    }


    public static void main(String[] args) throws IOException {
        bm25 = new BM25();
        files = Lab07.IO.getFB2Files(input_folder_fb2);
        Document[] documents = new Document[files.length];

        for (int i = 0; i < documents.length; i++) {
            documents[i] = Lab07.IO.parse(files[i]);
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        zh = new ZoneHandler(documents);

        System.out.println("Please, input query. To exit, input '0':");
        String query;
        while (!(query = br.readLine()).equals("0")) {
            handleQuery(query);
        }
        System.out.println("Thanks for the attention. Bye");

    }
}
