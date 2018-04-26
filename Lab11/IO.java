package Lab11;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;

public class IO {

    public static File getFile() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Welcome. Please, input path to a file: ");
        String filepath = br.readLine();
        File file = new File(filepath);
        if (!file.exists() || !file.isDirectory()) {//default path
            filepath = "C:\\Users\\alexa\\IdeaProjects\\InformationRetrieval\\src\\Mor.fb2";//default filepath
            file = new File(filepath);
        }
        return file;
    }

    public static void parse(Document doc) {

        try {
            NodeList descNodeList = doc.getElementsByTagName("description");//necessary?
            NodeList textNodeList = doc.getElementsByTagName("body");

            for (int i = 0; i < descNodeList.getLength(); i++) {
                //work with desc
            }

            for (int i = 0; i < textNodeList.getLength(); i++) {
                //work with data
            }

            System.out.println("Root: " + doc.getDocumentElement().getNodeName());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws IOException {
        File input = getFile();
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document doc = documentBuilder.parse(input);
            doc.getDocumentElement().normalize();//
            //parse(doc);

        } catch (Exception e) {
            e.printStackTrace();
        }




    }
}
