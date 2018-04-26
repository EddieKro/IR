package Lab04;

import java.util.TreeMap;
import java.util.TreeSet;

public class K_GramIndex {

    private TreeMap<String, TreeSet<String>> index;

    public K_GramIndex() {
        index = new TreeMap<>();
    }

    public TreeMap<String, TreeSet<String>> getIndex() {
        return index;
    }

}