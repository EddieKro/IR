package Lab11;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.regex.Pattern;


public class Practice11 {

    private static final Pattern PATTERN = Pattern.compile("[^a-zA-Zа-яА-Я]+");

    public static ArrayList<String> getQueryRes(Document doc, String[] query){

        ArrayList<String> res = new ArrayList<>();

       NodeList descElements = doc.getElementsByTagName("description").item(0).getChildNodes();
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
        for (String s : res) {
            String[] aux = PATTERN.split(s);
            String out = "";
            for (String p : aux) {
                out += p + " ";
            }
            System.out.println( out);
        }


        return res;
    }

    public static void main(String[] args) throws IOException, IllegalAccessException, InstantiationException {


        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Please, input path to the folder with files:");
        String filepath = br.readLine();
        File file = new File(filepath);
        if (!file.exists() || !file.isDirectory()) {
            filepath = "C:\\Users\\USER\\workspace\\IR_Pr01\\src\\file\\text.fb2";
            file = new File(filepath);
        }

        Document doc = Document.class.newInstance();

        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            doc = documentBuilder.parse(file);
            doc.getDocumentElement().normalize();//
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Doc read successfully. \nNow,please, input buf:");
        String buf = "";

        while (!buf.toLowerCase().equals("stop")) {
            buf = br.readLine();
            String[] query = PATTERN.split(buf);

            ArrayList<String> res = getQueryRes(doc, query);

            System.out.println("\nResults:");
            for (String s: res){
                System.out.println(s);
            }

            for (String s : query) System.out.println(s);
            if (!buf.equals("stop")) System.out.print("To exit, input 'stop'");
        }

        System.out.print("\nBye");
    }

}
