package Lab04;


import java.util.ArrayList;

class TrieNode {

    boolean isUsed = false;//used for in-depth search

    char c;//stored data symbol

    int count; //number of times char exists - is needed while deleting words
    int t_cnt = 0; //total number of children used for in-depth search
    int h_cnt = 0;  //number of visited children - used for in-depth search

    TrieNode parent;//used for in-depth search
    TrieNode[] child;//possibly 52 links: A-Z, a-z - children (used for in-depth search)

    public TrieNode() {
        c = Character.MIN_VALUE;
        child = new TrieNode[52];
    }

    public TrieNode(char ch) {
        c = ch;
        child = new TrieNode[52];
        count = 1;
    }

}

class Trie {//the quickest structure; stores root to the nodes
    TrieNode root;//first node

    public Trie() {
        root = new TrieNode();
    }
}

public class PrefixTreeDictionary {//SearchDictionary will be built on Trie
    ArrayList<String> res = new ArrayList<>();//result - is stored locally to make recursion easier
    Trie wordTrie;

    public PrefixTreeDictionary() {
        this.wordTrie = new Trie();
    }

    public void addWord(String word) {

        TrieNode current = wordTrie.root;
        int id;
        for (int i = 0; i < word.length(); i++) {// in links[] a-z are indexed links[0]...links[25], A-Z - links[25]-links[51]
            char c = word.charAt(i);
            if (65 <= (int) c && (int) c <= 90) {
                id = (int) c - 39;
            } else id = (int) c - 97;//cause all other symbols are omitted by regex

            if (current.child[id] == null) {
                current.child[id] = new TrieNode(c);//adds symbol and creates array of links on further ones
                current.t_cnt++;//number of children++
            } else {
                current.child[id].count += 1;//count++ - will be helpful at deleting words

            }
            current.child[id].parent = current;//setting parent
            current = current.child[id];//linking down below
        }

    }


    public String delWord(String word) {// deletes only unique values

        TrieNode current = wordTrie.root;

        int cnt = 0;
        int id;
        while (current.h_cnt != 0) {

            for (int i = 0; i < word.length(); i++) {
                char c = word.charAt(i);
                if (65 <= (int) c && (int) c <= 90) {
                    id = (int) c - 39;
                } else id = (int) c - 97;
                if (current.child[id] == null) {
                    return "No such word in a dictionary. Nothing to delete";
                } else {
                    current.child[id].count--;
                    current = current.child[id];
                }
            }
        }
        if (cnt != word.length()) {
            return "No such word in a dictionary. Nothing to delete";
        } else {

            for (int i = word.length() - 1; i >= 0; i--) {
                char c = word.charAt(i);
                if (65 <= (int) c && (int) c <= 90) {
                    id = (int) c - 39;
                } else id = (int) c - 97;
                current = current.parent;//moving upwards
                if (current.child[id].count == 0) {
                    current.child[id] = null;
                }
            }
        }

        return "Word deleted";
    }


    public Iterable<String> query(String query) {

        TrieNode current = wordTrie.root;
        int id;

        for (int i = 0; i < query.length(); i++) {//choosing the right branch to pick all the words from
            char c = query.charAt(i);

            if (65 <= (int) c && (int) c <= 90) {
                id = (int) c - 39;
            } else id = (int) c - 97;

            if (current.child[id] == null) {
                return null;
            }//further tree is empty - return err message
            current = current.child[id];//linking down below

        }
        depthFirstSearch(current, query);//recursively forms an array list with res strings
        return res;
    }

    private void depthFirstSearch(TrieNode base, String query) {//a variation of it
        String word = query;

        while (base.h_cnt != base.t_cnt) {

            for (int i = 0; i < 52; i++) {
                if (word.charAt(word.length() - 1) != base.c) {
                    String aux = "";
                    for (int j = 0; j < word.length() - 1; j++) {
                        aux += word.charAt(j);
                    }
                    word = aux;//to avoid unnecessary symbols
                }
                if (base.child[i] != null) {
                    word += base.child[i].c;
                    if (base.child[i].t_cnt != 0) {
                        depthFirstSearch(base.child[i], word);//recursion
                    } else {//add word to a res
                        res.add(word);
                        base.child[i].isUsed = true;
                        base.h_cnt++;
                        String aux = "";
                        for (int j = 0; j < word.length() - 1; j++) {
                            aux += word.charAt(j);
                        }
                        word = aux;//to ignore last symbol
                    }
                }


            }
        }
        base.isUsed = true;
        base.parent.h_cnt++;//most important line

    }

}

