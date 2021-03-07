package sample;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;


/**
 * The purpose of this class is to collect a set of hamWords and spamWords
 * Then with those two sets (maps) to find the probability that a word appears in a ham or a spam file
 * HamOutput.csv and SpamOutput.csv hold the respective words
 */

public class ProbabilityTester {

    private static Map<String, Integer> hamWords;
    private static Map<String, Integer> spamWords;
    private static Map<String, Integer> temporaryMap;

    public ProbabilityTester(){
        hamWords = new TreeMap<>();
        spamWords = new TreeMap<>();
        temporaryMap = new TreeMap<>();
    }

    /**
     * This function reads from a CSV file.
     * First looks for a word [a-zA-Z]
     * Next looks for a number [0-9]
     * Put the word into a map with the number beside it
     * @param file - CSV file
     * @throws IOException
     */
    public static void parseCSV(File file) throws IOException{
        String temp = "";
        int number = 0;
        Scanner scanner = new Scanner(file);
        scanner.useDelimiter(",");
        // scanning token by token
        while (scanner.hasNext()){
            String  token = scanner.next();
            if (WordCounter.isValidWord(token)){
                temp = token;
            }
            if (isNumber(token)){
                number = Integer.parseInt(token);
                addWord(temp, number);
            }
        }
    }

    private static boolean isNumber (String input){
        String allNumbers = "^[1-9]+$";
        // returns true if the word is composed by only letters otherwise returns false;
        return input.matches(allNumbers);
    }

    private static void addWord(String word, int number){
        temporaryMap.put(word, number);
    }

    public static void main(String[] args){

        WordCounter wc = new WordCounter();
        try {
            // Open ham and spam outputs
            File ham = new File("HamOutput.csv");
            File spam = new File("SpamOutput.csv");

            // Parse the csv files and copy the contents into ham and spam maps
            parseCSV(ham);
            hamWords.putAll(temporaryMap);
            parseCSV(spam);
            spamWords.putAll(temporaryMap);

            /**
            // parse ham and put all words into hamWords
            wc.parseFile(ham);
            hamWords.putAll(wc.getWordCounts());

            // parse spam and put all words into spamWords
            wc.parseFile(spam);
            spamWords.putAll(wc.getWordCounts());
             */
        } catch(FileNotFoundException e){
            System.err.println("Cannot find file");
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
