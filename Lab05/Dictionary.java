package Lab05;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

// Dictionary: Term - ID
public class Dictionary {
    HashMap<String, ArrayList<Integer>> dictionary;//Hash>Tree here

    public Dictionary() {
        dictionary = new HashMap<>();//ArrayLists are initialized later;
    }

    public void addTerm(String term, int id) {
        ArrayList<Integer> buf;
        if (!dictionary.containsKey(term)) {
            buf = new ArrayList<>(1);
        } else {
            buf = dictionary.get(term);
        }
        buf.add(id);
        buf.trimToSize();//not to mix up with indexes
        dictionary.put(term, buf);
    }

    public void mergeDictionaries(Dictionary dict) {//adds entries from another dictionary to a current one
        for (String key : dict.getDictionaryKeys()) {
            ArrayList<Integer> buf;
            if (dictionary.containsKey(key)) {//merge
                buf = dictionary.get(key);
                ArrayList<Integer> buf2 = dict.getDictionary().get(key);
                buf.addAll(buf2);
            } else {
                buf = dict.getDictionary().get(key);
            }
            dictionary.put(key, buf);
        }
    }

    private HashMap<String, ArrayList<Integer>> getDictionary() {
        return dictionary;
    }

    private Set<String> getDictionaryKeys() {
        return dictionary.keySet();
    }

}
