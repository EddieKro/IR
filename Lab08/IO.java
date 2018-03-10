package Lab08;

import Constants.Constants;
import org.w3c.dom.Document;
import sun.reflect.generics.tree.Tree;

import java.io.*;
import java.util.*;

import static Lab07.IO.getFB2Files;
import static Lab07.IO.parse;


public class IO implements Constants {
    static File[] files;
    static Doc[] documents;
    static Doc[] leading;

    static void initDocuments(Document[] docs) {
        documents = new Doc[docs.length];
        for (int i = 0; i < docs.length; i++) {
            documents[i] = new Doc(docs[i], i);
        }
    }

    static void selectRandomOnes() {
        int quantity = (int) Math.sqrt(documents.length);
        leading = new Doc[quantity];
        int cnt = 0;
        while (cnt != quantity) {
            Random random = new Random();
            int rand = random.nextInt(documents.length);
            if (!documents[rand].isLeading()) {
                documents[rand].isLeader();

                leading[cnt] = documents[rand];
                cnt++;
            }
        }
    }


    static double findCosineSimilarity(Doc l, Doc f) {

        Set<String> terms = new HashSet<>();
        terms.addAll(l.getTerms().keySet());
        terms.addAll(f.getTerms().keySet());

        double numerator = 0.0;
        double denominator1 = 0.0;
        double denominator2 = 0.0;

        for (String term : terms) {
            numerator += l.getWeight(term) * f.getWeight(term);
            denominator1 += l.getWeight(term) * l.getWeight(term);
            denominator2 += f.getWeight(term) * f.getWeight(term);
        }
        if (denominator1 == 0 || denominator2 == 0) return 0.0;
        return numerator / (Math.sqrt(denominator1) * Math.sqrt(denominator2));
    }

    static void findClosestLeader(Doc d) {
        TreeMap<Double, Integer> list = new TreeMap<>();
        for (int i = 0; i < leading.length; i++) {
            double b = findCosineSimilarity(leading[i], d);
            if (b != 0.0) list.put(b, leading[i].getId());
        }

        d.isFollower();
        /*for (Double w : list.keySet()) {
            System.out.println(list.get(w));
        }
        //System.out.print("Leading val:");
        //System.out.println(list.firstEntry().getValue());
        */
        documents[list.firstEntry().getValue()].addFollower(d.getId());
        try {

        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("leading ids:");
            for (Double key : list.keySet()) {
                System.out.print(list.get(key) + " ");
            }
            System.out.println();
        }
    }

    static void selectFollowers() {
        int quantity = documents.length - leading.length;
        int i = 0;
        while (quantity != 0) {
            if (!documents[i].isLeading()) {
                findClosestLeader(documents[i]);
                quantity--;
            }
            i++;
        }
    }

    public static void outputViaRanging(Doc leader, String query) {
        TreeMap<Double, String> scoringMap = new TreeMap<>();
        scoringMap.put(leader.zone.calculateWeight(query), files[leader.getId()].getName());
        for (int id : leader.getFollowers()) {
            scoringMap.put(documents[id].zone.calculateWeight(query), files[documents[id].getId()].getName());
        }

        for (Double d : scoringMap.keySet()) {
            System.out.println("Document: " + scoringMap.get(d) + " ;score= " + d);
        }
    }

    public static void main(String[] args) throws IOException {
        //choosing sqr(N) random documents
        files = getFB2Files(input_folder_fb2);
        Document[] docs = new org.w3c.dom.Document[files.length];
        for (int i = 0; i < docs.length; i++) {
            docs[i] = parse(files[i]);
        }
        System.out.println("Documents chosen");
        initDocuments(docs);

        System.out.println("Documents inited");
        selectRandomOnes();
        System.out.println("Random files selected");
        //select followers
        selectFollowers();
        System.out.println("Followers inited");

        //find closest to q
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Please, input query. To exit, input '0':");
        String query;
        while (!(query = br.readLine()).equals("0")) {
            //choose a most suitable leader
            Doc q = new Doc(query);
            TreeMap<Double, Integer> list = new TreeMap<>(Collections.reverseOrder());
            for (Doc l : leading) {
                double b = findCosineSimilarity(l, q);
                if (b != 0.0) list.put(b, l.getId());
            }
            Doc suitable = documents[list.firstEntry().getValue()];
            outputViaRanging(suitable, query);
        }
        //output via weighted ranking
    }
}
