package Lab07;

import Constants.Constants;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.TreeMap;

public class Zone implements Constants {
    String author;
    String title;
    String annotation;
    String body;//text itself

    Document d;//

    private String getTextC(String s) {
        Node n = d.getElementsByTagName(s).item(0);
        String lR = n.getTextContent().replaceAll("^\\s+", "");
        return lR.replaceAll("\\s+$", "");
    }

    public Zone(Document d) {
        this.d = d;
        NodeList descElement = d.getElementsByTagName("description");//for author and title
        //get_author
        author = getTextC("first-name") + " " + getTextC("middle-name") + " " + getTextC("last-name");
        //get_title
        title = getTextC("book-title");
        //get_annotation
        annotation = getTextC("annotation");
        //get_body
        body = getTextC("body");
    }

    public double calculateWeight(String query) {
        double res = 0.0;
        if (author.contains(query)) res += 0.3;
        if (title.contains(query)) res += 0.2;
        if (annotation.contains(query)) res += 0.2;
        if (body.contains(query)) res += 0.4;
        return res;
    }
}

