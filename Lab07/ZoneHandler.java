package Lab07;

import org.w3c.dom.Document;
import sun.reflect.generics.tree.Tree;

import java.util.ArrayList;
import java.util.TreeMap;

public class ZoneHandler {
    TreeMap<String, ArrayList<Integer>> dictionary;//collection term -> doc_ids
    TreeMap<Zone, Integer> index;//collection zone->doc_ids

    public ZoneHandler(Document[] documents) {
        index = new TreeMap<>();
        for (int i = 0; i < documents.length; i++) {
            Zone zone = new Zone(documents[i]);
            index.put(zone, i);
        }
    }

    public TreeMap<Zone, Integer> getIndex() {
        return index;
    }

}
