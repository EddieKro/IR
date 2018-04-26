package Lab07;

import Constants.Constants;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;

public class IO implements Constants {
    static ZoneHandler zh;
    static File[] files;

    public static TreeMap<String, Integer> getTermSet(Document d) {//Term - frequency
        TreeMap<String, Integer> res = new TreeMap<>();

        Node node = d.getElementsByTagName("body").item(0);
        String[] aux = EXPRESSION.split(node.getTextContent());
        for (String s : aux) {
            if (res.containsKey(s)) {
                res.put(s, res.get(s) + 1);
            } else res.put(s, 1);
        }

        return res;
    }

    public static File[] getFB2Files(String path) {
        ArrayList<File> files = new ArrayList<>();
        getFB2FilesL(path, files);
        return files.toArray(new File[0]);
    }

    private static void getFB2FilesL(String path, ArrayList<File> files) {
        File f = new File(filepath);
        File[] list = f.listFiles();
        for (File file : list) {
            if (file.isFile() && file.getName().endsWith(".fb2")) files.add(file);
            else if (file.isDirectory()) getFB2FilesL(file.getAbsolutePath(), files);
        }
    }

    public static Document parse(File file) {
        Document doc;
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            doc = documentBuilder.parse(file);
            doc.getDocumentElement().normalize();
            return doc;
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void handleQuery(String query) {
        String[] tokens = EXPRESSION.split(query);
        TreeMap<Double, String> scoringMap = new TreeMap<>(Collections.reverseOrder());
        for (String s : tokens) {
            for (Zone zone : zh.index.keySet()) {
                zone.calculateWeight(s);
                scoringMap.put(zone.calculateWeight(s), files[zh.index.get(zone)].getName());
            }
        }

        System.out.println("Here are the results of the query:");

        for (Double key : scoringMap.keySet()) {
            System.out.println("Document: " + scoringMap.get(key) + " ; score=" + key);
        }
    }


    public static void main(String[] args) throws IOException {

        files = getFB2Files(input_folder_fb2);
        Document[] documents = new Document[files.length];

        for (int i = 0; i < documents.length; i++) {
            documents[i] = parse(files[i]);
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        zh = new ZoneHandler(documents);

        System.out.println("Please, input query. To exit, input '0':");
        String query;
        while (!(query = br.readLine()).equals("0")) {
            handleQuery(query);
        }
        System.out.println("Thanks for the attention. Bye");

    }
}
