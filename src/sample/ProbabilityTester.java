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
    private static Map<String, Double> hamProb;
    private static Map<String, Double> spamProb;
    private static Map<String, Double> probabilitySW;
    private static int numHamFiles;
    private static int numSpamFiles;

    public ProbabilityTester(){
        hamWords = new TreeMap<>();
        spamWords = new TreeMap<>();
        temporaryMap = new TreeMap<>();
        hamProb = new TreeMap<>();
        spamProb = new TreeMap<>();
        probabilitySW = new TreeMap<>();
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

    private static int countFiles(File folder){
        return folder.listFiles().length;
    }

    /**
     * This function finds the probability of ham or spam containing the word
     * The probability is calculated by
     * (number of ham/spam files containing word) / (number of ham/spam files)
     * @param word
     */
    private static void probabilityContains(String word){
        double probHam = (double) hamWords.get(word) / (double) numHamFiles;
        double probSpam = (double) spamWords.get(word) / (double) numSpamFiles;
        hamProb.put(word, probHam);
        spamProb.put(word, probSpam);
    }

    private static void probability(String word){
        double probSW = spamProb.get(word) / (spamProb.get(word) + hamProb.get(word));
        probabilitySW.put(word, probSW);
    }

    public static void main(String[] args){

        WordCounter wc = new WordCounter();
        try {
            String testPath = "..\\..\\data\\test";

            // Open ham and spam outputs
            File ham = new File("HamOutput.csv");
            File spam = new File("SpamOutput.csv");
            File hamFolder = new File(testPath+"ham");
            File spamFolder = new File(testPath+"spam");

            // Parse the csv files and copy the contents into ham and spam maps
            parseCSV(ham);
            hamWords.putAll(temporaryMap);
            parseCSV(spam);
            spamWords.putAll(temporaryMap);

            // Count the files in each test folder
            numHamFiles = countFiles(hamFolder);
            numSpamFiles = countFiles(spamFolder);

        } catch(FileNotFoundException e){
            System.err.println("Cannot find file");
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
