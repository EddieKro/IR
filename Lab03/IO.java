package Lab03;

import java.io.*;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;

class CoordinateIndexStruct {
    private TreeMap<String, Integer> counter;//String - key; Integer - count
    private TreeMap<String, TreeMap<Integer, ArrayList<Integer>>> indexes;// String - key; Integer - doc id; ArrayList - list of docs

    CoordinateIndexStruct() {
        counter = new TreeMap<>();
        indexes = new TreeMap();//merely to put smth lately
    }

    CoordinateIndexStruct(TreeMap<String, Integer> counter, TreeMap<String, TreeMap<Integer, ArrayList<Integer>>> indexes) {
        this.counter = counter;
        this.indexes = indexes;
    }

    public void addAnotherCoordinateIndexStruct(CoordinateIndexStruct c) {//for updates
        if (c == null) return;
        setCounterMap(c.getCounter());
        setIndexesMap(c.getIndexes());
    }

    //setters
    public void setCounterMap(TreeMap<String, Integer> map) {
        for (String key : map.keySet()) {
            if (counter.containsKey(key)) counter.put(key, counter.get(key) + map.get(key));
            else counter.put(key, map.get(key));
        }
    }

    public void setIndexesMap(TreeMap<String, TreeMap<Integer, ArrayList<Integer>>> map) {
        for (String key : map.keySet()) {
            if (indexes.containsKey(key)) {
                TreeMap<Integer, ArrayList<Integer>> buf = indexes.get(key);
                buf.putAll(map.get(key));
                indexes.put(key, buf);
            } else {
                indexes.put(key, map.get(key));
            }
        }
    }

    //getters
    public TreeMap<String, Integer> getCounter() {
        return counter;
    }

    public TreeMap<String, TreeMap<Integer, ArrayList<Integer>>> getIndexes() {
        return indexes;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String key : indexes.keySet()) {
            sb.append(key).append(",").append(counter.get(key)).append(":").append(System.getProperty("line.separator")).append("{");
            for (int index : indexes.get(key).keySet()) {
                indexes.get(key).get(index).trimToSize();//trim arraylist 4 fast count
                sb.append(index).append(",").append(indexes.get(key).get(index).size()).append(":[");
                for (int i = 0; i < indexes.get(key).get(index).size(); i++) {
                    sb.append(indexes.get(key).get(index).get(i));
                    if (i != indexes.get(key).get(index).size() - 1) sb.append(",");
                    else sb.append("];").append(System.getProperty("line.separator"));
                }
            }
        }

        return sb.toString();
    }


    public String searchByWords(String[] query) {

        String res = "";

        for (String s : query) {
            StringBuilder sb = new StringBuilder();

            if (indexes.containsKey(s)) {

                sb.append(sb.append(s)).append(",").append(counter.get(s)).append(":\n").append("{");
                for (int index : indexes.get(s).keySet()) {

                    indexes.get(s).get(index).trimToSize();
                    sb.append(index).append(",").append(indexes.get(s).size()).append(":[");
                    for (int i = 0; i < indexes.get(s).get(index).size(); i++) {
                        sb.append(indexes.get(s).get(index).get(i));
                        if (i != indexes.get(s).get(index).size() - 1) sb.append(",");
                        else sb.append("];\n");
                    }

                }
                res = res.concat(sb.toString());
            } else res = res.concat("\nNo such word " + s + " found\n");

        }

        return res;
    }


    public void toFile() throws IOException {
        FileWriter fileWriter = new FileWriter("coordIndex.txt");
        fileWriter.write(toString());
    }
}

public class IO {

    //Побудувати:
    // +двослівний індекс//byword index
    // +координатний інвертований індекс//positional index

    //Реалізувати:
    // +фразовий пошук
    // пошук з урахуванням відстані


    private static final Pattern EXPRESSION = Pattern.compile("[^a-zA-Zа-яА-Я]+");

    public static ArrayList<String> searchByword(TreeSet<String> collection, String query) {
        TreeSet<String> tqu = new TreeSet<>();

        String[] buf = EXPRESSION.split(query);

        for (int i = 0; i < query.length() - 1; i++) {
            tqu.add(buf[i].concat(" ").concat(buf[i + 1]));
        }

        ArrayList<String> res = new ArrayList<>();

        for (String s : tqu) {
            if (collection.contains(s)) {
                res.add("Collection contains: " + s);
            } else res.add("Collection doesn't contain " + s);
        }

        return res;

    }

    public static TreeSet<String> tokenizeByword(File f) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(f));

        if (!f.exists()) {//checks for an empty file
            return null;
        }

        TreeSet<String> res = new TreeSet<>();

        while (!br.readLine().equals("")) {

            String buf0 = br.readLine();
            buf0 = buf0.toLowerCase();//changes every word to lower case
            String[] buf = EXPRESSION.split(buf0);//regexp

            for (int i = 0; i < buf.length - 1; i++) {
                res.add(buf[i].concat(" ").concat(buf[i + 1]));
            }

        }

        if (res.size() == 0) {
            return null;
        }
        return res;
    }

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


    public static void main(String[] args) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Welcome. Please, input path to the folder with files");
        String filepath = br.readLine();

        File file = new File(filepath);
        if (!file.exists() || !file.isDirectory()) {//default path
            filepath = "C:\\Users\\alexa\\IdeaProjects\\InformationRetrieval\\src\\Lab01_Dictionary\\files\\";//default filepath
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
        // tokenize
        for (int i = 0; i < listOfFiles.length; i++) {
            if (coordIndexStruct == null) coordIndexStruct = tokenizePositionally(listOfFiles[i], i);
            else coordIndexStruct.addAnotherCoordinateIndexStruct(tokenizePositionally(listOfFiles[i], i));
            // each doc possesses its unique id
        }

        coordIndexStruct.toFile();
        System.out.println(coordIndexStruct.toString());


        TreeSet<String> s = null;
        for (File f : listOfFiles) {
            if (s == null) s = tokenizeByword(f);
            else s.addAll(tokenizeByword(f));
        }

        System.out.println("Now, let`s test some queries! To exit, input 42");
        System.out.println("Please, input any query:\n");

        String q = br.readLine();
        while (!q.equals("42")) {
            System.out.println("Coord search:");
            System.out.println(coordIndexStruct.searchByWords(EXPRESSION.split(q)));
            System.out.println("Phrasal search:\n");
            System.out.println(searchByword(s, q));

        }


    }
}