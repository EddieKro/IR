package Lab04;

import java.util.TreeMap;
import java.util.TreeSet;

public class PermutationIndex {

    private TreeMap<String, TreeSet<String>> index;

    public PermutationIndex() {
        index = new TreeMap<>();
    }

    public TreeMap<String, TreeSet<String>> getIndex() {
        return index;
    }
}
