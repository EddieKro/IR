package Constants;

import java.util.regex.Pattern;

/**
 * Interface for quick access to global constants
 */
public interface Constants {
    String filepath = "C:\\Users\\alexa\\IdeaProjects\\InformationRetrieval_backdoor\\src\\Files\\Output\\";//path to an output folder with new files
    String input_folder_fb2 = "";
    String input_folder = "C:\\TextCollection\\test";
    String output_folder = "C:\\Users\\alexa\\IdeaProjects\\InformationRetrieval_backdoor\\src\\Files\\Output\\";

    String filename = "invertedIndex.txt";
    String inverted_index_address = "C:\\TextCollection\\invertedIndexTest.txt";
    String parallel_inverted_index_address = "C:\\TextCollection\\invertedIndex.txt";

    Pattern PARSE_EXPR = Pattern.compile("[\\W,:]+");//read inv index file
    Pattern EXPRESSION = Pattern.compile("[^a-zA-Zа-яА-Я]+");//Split pattern

    int THREADS = 12;

    double DIVIDER = 1000000.0;//MB->B
    double MAX_BLOCK_SIZE = 150.0;//MB-> almost a const [cause has to be initialized]

}

