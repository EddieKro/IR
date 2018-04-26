package Misc;

import Constants.Constants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class XMLTester implements Constants {
    private static final Pattern SPACE = Pattern.compile(" ");

    private static final Pattern PATTERN = Pattern.compile("[^a-zA-Zа-яА-Я\n]+");

    private static String getTextC(Document d, String s) {
        Node n = d.getElementsByTagName(s).item(0);
        String lR = n.getTextContent().replaceAll("^\\s+", "");
        return lR.replaceAll("\\s+$", "");
    }

    public static void main(String[] args) {
        Document doc;
        String filepath = "C:\\Users\\alexa\\IdeaProjects\\InformationRetrieval\\src\\Mor.fb2";//default filepath
        File file = new File(filepath);

        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            doc = documentBuilder.parse(file);
            doc.getDocumentElement().normalize();//xml struct is ready

            ArrayList<String> res = new ArrayList<>();

            String[] query = {"ученик", "свист"};

            Node descElements = doc.getElementsByTagName("first-name").item(0);

            //String content = doc.getElementsByTagName("first-name").item(0).getTextContent() + doc.getElementsByTagName("middle-name").item(0).getTextContent() + doc.getElementsByTagName("last-name").item(0).getTextContent();

            String content = getTextC(doc, "first-name") + " " + getTextC(doc, "last-name");

            System.out.println(content);
            System.out.println(getTextC(doc, "book-title"));
            System.out.println(getTextC(doc, "lang"));
            System.out.println(getTextC(doc, "body"));
            // System.out.println(descElements.getNodeName() + "||" + descElements.getTextContent());
            //descElements = descElements.item(0).getChildNodes();
            /*for (int i = 0; i < descElements.getLength(); i++) {
                Node n = descElements.item(i);
                if (n.getNodeType() == Node.ELEMENT_NODE) {
                    for (int j = 0; j < n.getChildNodes().getLength(); j++) {
                        if (n.getNodeType() == Node.ELEMENT_NODE)
                            System.out.println(n.getChildNodes().item(j).getNodeName() + "||| " + n.getChildNodes().item(j).getTextContent());
                    }
                }
            }*/
/*
            for (int i = 0; i < descElements.getLength(); i++) {
                Node node = descElements.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    for (int j = 0; j < node.getChildNodes().getLength(); j++) {
                        if (node.getNodeType() == Node.ELEMENT_NODE) {
                            if (node.getChildNodes().item(j).getTextContent().contains(query[0]))
                                res.add(node.getChildNodes().item(j).getTextContent());
                        }

                    }
                }
            }
            for (int j = 1; j < query.length; j++) {
                NodeList textElements = doc.getElementsByTagName("p");
                for (int i = 0; i < textElements.getLength(); i++) {
                    Node node = textElements.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        if (node.getTextContent().contains(query[j])) {
                            if (res.contains(query[0])) {
                                res.remove(query[0]);
                                res.add(query[0] + " " + node.getTextContent().trim());
                            } else res.add(node.getTextContent().trim());
                        }
                    }
                }

            }
            res.trimToSize();
            for (int i = 0; i < res.size(); i++) {
                String out = "";
                String[] aux = PATTERN.split(res.get(i));
                for (String s : aux) {
                    out += s + " ";
                }
                res.remove(i);
                res.add(out);
            }
            for (String s : res) {
                String[] aux = PATTERN.split(s);
                String out = "";
                for (String p : aux) {
                    out += p + " ";
                }
                System.out.println(out);
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
