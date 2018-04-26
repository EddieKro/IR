package Lab06;

import java.io.*;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.regex.Pattern;

class CoordinateIndexStruct {
    private TreeMap<String, Integer> counter;//String - key; Integer - count

    CoordinateIndexStruct(TreeMap<String, Integer> counter, TreeMap<String, TreeMap<Integer, ArrayList<Integer>>> indexes) {
        this.counter = counter;
    }

    public void addAnotherCoordinateIndexStruct(CoordinateIndexStruct c) {//for updates
        if (c == null) return;
        setCounterMap(c.getCounter());
    }

    public void setCounterMap(TreeMap<String, Integer> map) {
        for (String key : map.keySet()) {
            if (counter.containsKey(key)) counter.put(key, counter.get(key) + map.get(key));
            else counter.put(key, map.get(key));
        }
    }

    public TreeMap<String, Integer> getCounter() {
        return counter;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String key : counter.keySet()) {
            if (key.equals("")) {
            } else sb.append(key).append(",").append(counter.get(key)).append(System.getProperty("line.separator"));
        }
        return sb.toString();
    }

    public void toFile() throws IOException {
        FileWriter fileWriter = new FileWriter("coordIndexNew.txt");
        fileWriter.write(toString());
    }
}


public class DictionaryCompression {

    private static final Pattern EXPRESSION = Pattern.compile("[^a-zA-Zа-яА-Я]+");

    static int[] pointers;
    static String Index;

    public static CoordinateIndexStruct tokenizePositionally(File f, int id) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(f));

        if (!f.exists()) {//checks for an empty file
            return null;
        }

        TreeMap<String, Integer> counterMap = new TreeMap<>();
        TreeMap<String, TreeMap<Integer, ArrayList<Integer>>> docCoordsMap = new TreeMap<>();
        String t;
        while ((t = br.readLine()) != null) {

            String buf0 = t;
            buf0 = buf0.toLowerCase();//changes every word to lower case
            String[] buf = EXPRESSION.split(buf0);//regexp

            int index = 0;//global index for correct indexing

            for (int i = 0; i < buf.length; i++) {
                if (counterMap.containsKey(buf[i]))
                    counterMap.put(buf[i], counterMap.get(buf[i]) + 1);
                else counterMap.put(buf[i], 1);//number

                if (docCoordsMap.containsKey(buf[i])) docCoordsMap.get(buf[i]).get(id).add(i + index);
                else {
                    if (buf[i].equals("")) continue;
                    TreeMap<Integer, ArrayList<Integer>> bufmap = new TreeMap<>();
                    ArrayList<Integer> al = new ArrayList<>();
                    al.add(i + index);
                    bufmap.put(id, al);
                    docCoordsMap.put(buf[i], bufmap);
                }

                index += buf.length;
            }
        }

        if (counterMap.size() == 0) {//returns null if a map is empty
            return null;
        }

        return new CoordinateIndexStruct(counterMap, docCoordsMap);
    }

    private static String formString(CoordinateIndexStruct coordinateIndexStruct) {

        StringBuilder buf = new StringBuilder();

        int index = 0;
        int i = 0;

        for (String s : coordinateIndexStruct.getCounter().keySet()) {
            buf.append(s);
            pointers[i] = index;
            index += s.length();
            i++;
        }


        return buf.toString();
    }

    public static void stringToFile(String line) throws IOException {
        FileWriter fileWriter = new FileWriter("String.txt");
        fileWriter.write(line);

    }


    public static void main(String[] args) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Welcome. Please, input path to the folder with files");
        String filepath = br.readLine();

        File file = new File(filepath);
        if (!file.exists() || !file.isDirectory()) {//default path

            filepath = "C:\\Users\\USER\\workspace\\IR_Pr01\\src\\file\\";
            file = new File(filepath);
        }

        File[] listOfFiles = file.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".txt");
            }
        });//reads all .txt files in a Directory and add them to the collection


        for (File f : listOfFiles) {
            System.out.println(f.getName());
        }

        CoordinateIndexStruct coordIndexStruct = null;
        for (int i = 0; i < listOfFiles.length; i++) {
            if (coordIndexStruct == null) coordIndexStruct = tokenizePositionally(listOfFiles[i], i);
            else coordIndexStruct.addAnotherCoordinateIndexStruct(tokenizePositionally(listOfFiles[i], i));
        }

        coordIndexStruct.toFile();
        System.out.println(coordIndexStruct.toString());

        pointers = new int[coordIndexStruct.getCounter().size()];
        Index = formString(coordIndexStruct);

        stringToFile(Index);
        System.out.println(Index.substring(pointers[10], pointers[11]));//for example
    }

}
