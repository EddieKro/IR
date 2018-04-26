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
        for (Doc leader : leading) {
            double b = findCosineSimilarity(leader, d);
            if (b != 0.0) list.put(b, d.getId());
        }
        d.isFollower();
        leading[list.firstEntry().getValue()].addFollower(d.getId());
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
        Document[] documents = new org.w3c.dom.Document[files.length];
        for (int i = 0; i < documents.length; i++) {
            documents[i] = parse(files[i]);
        }
        initDocuments(documents);
        selectRandomOnes();

        //select followers
        selectFollowers();
        //find closest to q
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Please, input query. To exit, input '0'");
        String query;
        while (!(query = br.readLine()).equals("0")) {
            //choose a most suitable leader
            Doc q = new Doc(query);
            TreeMap<Double, Integer> list = new TreeMap<>(Collections.reverseOrder());
            for (Doc l : leading) {
                double b = findCosineSimilarity(l, q);
                if (b != 0.0) list.put(b, l.getId());
            }
            Doc suitable = leading[list.firstEntry().getValue()];
            outputViaRanging(suitable, query);
        }
        //output via weighted ranking
    }
}
