package Lab11;

import java.util.TreeMap;

public class DictionaryDataStruct {

    TreeMap<String, Integer> dictionary;//word-weight;

    public DictionaryDataStruct() {
        dictionary = new TreeMap<>();
    }

    public DictionaryDataStruct(TreeMap<String, Integer> _dictionary) {//anti-c++ styling
        dictionary = _dictionary;
    }

    public TreeMap<String, Integer> getDictionary() {
        return dictionary;
    }

    public void setDictionary(TreeMap<String, Integer> dictionary) {
        this.dictionary = dictionary;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String k : dictionary.keySet()) {
            stringBuilder.append("Term: ").append(k).append("; Weight(").append(k).append(")=").append(dictionary.get(k));
        }
        return stringBuilder.toString();
    }
}
