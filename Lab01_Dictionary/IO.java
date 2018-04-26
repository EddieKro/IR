package Lab01_Dictionary;

import java.io.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;
import java.util.regex.Pattern;

class Dictionary {//tiny Dictionary class
    private String[] words;//dictionary itself
    private boolean isEmpty;//bool

    public Dictionary() {
        words = new String[0];//new array
        isEmpty = true;
    }

    public String[] getWords() {//getters
        return words;
    }

    public int getLength() {
        return words.length;
    }

    public void setWords(String[] arr) {//setter
        words = arr;
    }

    public void add(ArrayList<String> arrayList) {//adds all the words from a text file
        if (!isEmpty) {//if there is smth in dictionary
            Collections.addAll(arrayList, words);
            arrayList = removeUniqueValues(arrayList);
        }

        words = arrayList.toArray(new String[arrayList.size()]);
        isEmpty = false;
    }

    public static ArrayList<String> removeUniqueValues(ArrayList<String> arrayList) {
        arrayList.trimToSize();
        Collections.sort(arrayList);
        //arrayList.sort(String::compareToIgnoreCase); - alternative sorting method
        int cnt = -1;
        while (cnt != 0) {//removes duplicates
            cnt = 0;
            for (int i = 0; i < arrayList.size() - 1; i++) {
                if (arrayList.get(i).equals(arrayList.get(i + 1))) {
                    arrayList.remove(i + 1);
                    arrayList.trimToSize();
                    cnt++;
                }
            }
            arrayList.trimToSize();
        }
        return arrayList;
    }

    public void toFile() {//creates a txt-file from dictionary
        Path out = Paths.get("dictionary.txt");
        try {
            Files.write(out, Arrays.asList(words), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "Number of words:" + words.length;
    }
}


public class IO {

    static final Pattern SPACE = Pattern.compile(" ");

    public static void addFileToDictionary(File F, Dictionary D) {
        try {
            final Scanner sc = new Scanner(F);
            ArrayList<String> al = new ArrayList<>();//al is used to get all the words in one iteration

            while (sc.hasNext()) {
                al.addAll(Arrays.asList(SPACE.split(sc.nextLine())));//add all words in file to arraylist

            }

            Dictionary.removeUniqueValues(al);   //remove unique values
            System.out.println("Words in a file:" + al.size());
            D.add(al);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        Dictionary dictionary = new Dictionary();
        System.out.println("Welcome. Please, input path to the folder with files");
        String filepath = br.readLine();

        File file = new File(filepath);
        if (!file.exists() || !file.isDirectory()) {
            filepath = "C:\\Users\\alexa\\IdeaProjects\\InformationRetrieval\\src\\Lab01_Dictionary\\files\\";//default filepath
            file = new File(filepath);
        }

        File[] listOfFiles = file.listFiles();//reads all files in a Directory and add them to dict

        for (File f : listOfFiles) {
            System.out.println(f.toString());
        }
        for (File f : listOfFiles) {
            addFileToDictionary(f, dictionary);
            System.out.println("file" + f.getName() + " successfully added");
            System.out.println("Dict size:" + dictionary.getLength());
        }
        ArrayList<String> buf = new ArrayList<>();
        Collections.addAll(buf, dictionary.getWords());
        Collections.sort(buf);
        buf = Dictionary.removeUniqueValues(buf);
        dictionary.setWords(buf.toArray(new String[buf.size()]));
        dictionary.toFile();
        System.out.println(dictionary.toString());


    }
}
