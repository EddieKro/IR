package Lab08;

import Lab07.Zone;
import org.w3c.dom.Document;
import sun.reflect.generics.tree.Tree;

import javax.print.attribute.standard.DocumentName;

import static Constants.Constants.EXPRESSION;
import static Lab07.IO.getTermSet;
import static Lab07.IO.parse;

import java.io.*;
import java.util.ArrayList;
import java.util.TreeMap;

public class Doc {
    private int id;
    Zone zone;
    private boolean isLeading;
    private boolean isFollowing;
    private ArrayList<Integer> followers;
    private TreeMap<String, Integer> terms;
    private TreeMap<String, Double> weights;

    Doc(String query) {
        terms = new TreeMap<>();
        String[] buf = EXPRESSION.split(query);
        for (String s : buf) {
            if (terms.containsKey(s)) terms.put(s, terms.get(s) + 1);
            else terms.put(s, 1);
        }
        weights = calculateWeights();
    }

    Doc(Document f, int id) {
        zone = new Zone(f);
        this.id = id;
        isLeading = true;
        terms = getTermSet(f);
    }

    private TreeMap<String, Double> calculateWeights() {
        TreeMap<String, Double> res = new TreeMap<>();
        for (String term : res.keySet()) {
            double frequency = terms.size() / terms.get(term);
            double weight;
            if (frequency > 0) weight = 1 + Math.log(frequency);
            else weight = 0;
            weights.put(term, weight);
        }
        return res;
    }

    void isLeader() {
        isLeading = true;
        isFollowing = false;
        followers = new ArrayList<>();
        followers.trimToSize();
    }

    void addFollower(int f_id) {
        followers.add(f_id);
    }

    void isFollower() {
        isFollowing = true;
    }

    public boolean isFollowing() {
        return isFollowing;
    }

    public boolean isLeading() {
        return isLeading;
    }

    public ArrayList<Integer> getFollowers() {
        return followers;
    }

    public TreeMap<String, Integer> getTerms() {
        return terms;
    }

    public TreeMap<String, Double> getWeights() {
        return weights;
    }

    public double getWeight(String t) {
        if (weights.containsKey(t)) {
            return weights.get(t);
        }
        return 0.0;
    }

    public int getId() {
        return id;
    }
}
