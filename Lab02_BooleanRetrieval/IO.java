package Lab02_BooleanRetrieval;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/*
    In any unclear situation - use TreeSet
 */

public class IO {

    public static final Pattern EXPRESSION = Pattern.compile("[^a-zA-Z]+");

    private static TreeSet<String> tokenize(File F) {
        try {
            final Scanner sc = new Scanner(F);
            TreeSet<String> doc = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);//ignore lowercase
            while (sc.hasNext()) {
                doc.addAll(Arrays.asList(EXPRESSION.split(sc.nextLine())));
            }
            doc.remove("");
            System.out.println("Doc contains " + doc.size() + " unique words");
            return doc;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

    }

    private static void toFile(HashMap<String, ArrayList<String>> res) throws IOException {
        FileWriter fileWriter = new FileWriter("collection.txt");//hello, Java 7

        for (String key : res.keySet()) {
            fileWriter.write(key + ": ");
            for (String s : res.get(key)) {
                fileWriter.write(s + ", ");
            }
            fileWriter.write(System.getProperty("line.separator"));
        }
    }

    public static void main(String[] args) throws IOException {


        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Welcome. Please, input path to the folder with files");
        String filepath = br.readLine();

        File file = new File(filepath);
        if (!file.exists() || !file.isDirectory()) {//default path
            filepath = "C:\\Users\\alexa\\IdeaProjects\\InformationRetrieval\\src\\Lab01_Dictionary\\files\\";//default filepath
            file = new File(filepath);
        }

        File[] listOfFiles = file.listFiles();//reads all files in a Directory and add them to dict

        for (File f : listOfFiles) {//prints all the file names -don`t know for what
            System.out.println(f.toString());
        }
        HashMap<String, TreeSet<String>> docs = new HashMap<>();
        for (File f : listOfFiles) {
            //tokenizes
            docs.put(f.getName(), tokenize(f));
            System.out.println("file" + f.getName() + " tokenize(d) ");
        }

        //also add them to a greater structure
        TreeSet<String> dict = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (String k : docs.keySet()) {
            dict.addAll(docs.get(k));
        }

        HashMap<String, ArrayList<String>> result = new HashMap<>();

        for (String key : dict) {//form inverted index
            ArrayList<String> buf = new ArrayList<>();
            for (String k : docs.keySet()) {
                if (docs.get(k).contains(key)) {
                    buf.add(k);
                }
            }
            result.put(key, buf);
        }

        toFile(result);//writes to file


    }

}
