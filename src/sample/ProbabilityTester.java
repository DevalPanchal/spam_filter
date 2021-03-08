package sample;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.Math;
import java.util.*;


/**
 * The purpose of this class is to collect a set of hamWords and spamWords
 * Then with those two sets (maps) to find the probability that a word appears in a ham or a spam file
 * HamOutput.csv and SpamOutput.csv hold the respective words
 */

public class ProbabilityTester {

    private static Map<String, Integer> hamWords;
    private static Map<String, Integer> spamWords;
    public static Map<String, Integer> temporaryMap;
    private static Map<String, Double> hamProb;
    private static Map<String, Double> spamProb;
    private static Map<String, Double> probabilitySW;
    private static List<String> wordList = new ArrayList<>();
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
//        scanner.useDelimiter(",");
        // scanning token by token
        while (scanner.hasNext()){
            String token = scanner.nextLine();
//            System.out.println(token.split(", ")[0]);
//            System.out.println(token.split(", ")[1]);
            if (WordCounter.isValidWord(token.split(", ")[0])){
                if (!wordList.contains(token.split(", ")[0])){
                    wordList.add(token.split(", ")[0]);
                }
                temp = token.split(", ")[0];
            }
            number = Integer.parseInt(token.split(", ")[1]);
//            System.out.println(temp);
//            System.out.println(number);
            addWord(temp, number);

        }
    }

//    private static boolean isNumber (String input){
//        String allNumbers = "^[1-9]+$";
//        // returns true if the word is composed by only letters otherwise returns false;
//        return input.matches(allNumbers);
//    }

    private static void addWord(String word, int number){
        temporaryMap.put(word, number);
    }

    private static int countFiles(File folder){
        File[] a = folder.listFiles();
//        System.out.printf("files in folder: %d\n", size.length);
        assert a != null;
        return a.length;
    }

    /**
     * This function finds the probability of ham or spam containing the word
     * The probability is calculated by
     * (number of ham/spam files containing word) / (number of ham/spam files)
     * @param word
     */
    private static void probabilityContains(String word){
        double probHam = 0.0;
        double probSpam = 0.0;

        if (hamWords.containsKey(word) && spamWords.containsKey(word)){
//            System.out.println("Option both");
            probHam = (double) hamWords.get(word) / (double) numHamFiles;
            probSpam = (double) spamWords.get(word) / (double) numSpamFiles;
        } else if (!hamWords.containsKey(word)){
//            System.out.println("Option ham");
            probSpam = 100.0;
            probHam = 0.0;
        } else if (!spamWords.containsKey(word)) {
//            System.out.println("Option spam");
            probSpam = 0.0;
            probHam = 100.0;
        }
        hamProb.put(word, probHam);
        spamProb.put(word, probSpam);
    }

    private static double probabilityMap(String word){
        probabilityContains(word);
        double probSW = spamProb.get(word) / (spamProb.get(word) + hamProb.get(word));
        probabilitySW.put(word, probSW);
        return probSW;
    }

    private static double summation (int N){
        double result = 0.0;
//        for (Map.Entry<String, Double> word : probabilitySW.entrySet()){
//            result += Math.log(1-word.getValue())-Math.log(word.getValue());
//        }
        for (int i = 0; i < N; i++){
            double wordProb = probabilityMap(wordList.get(i));
            result += Math.log(1-wordProb)-Math.log(wordProb);
        }
        return 1 / (1 + Math.exp(result));
    }


    public static void main(String[] args){
        ProbabilityTester pt = new ProbabilityTester();
        System.out.println("Starting Probability Tester");
        WordCounter wc = new WordCounter();
        try {
            // Main folder is spam_filter
            String testPath = ".\\data\\test\\";
            String filePath = ".\\src\\sample\\";
            System.out.println("Opening files");
            // Open ham and spam outputs
            File ham = new File(filePath+"HamOutput.csv");
            File spam = new File(filePath+"SpamOutput.csv");
            File hamFolder = new File(testPath+"ham");
            File spamFolder = new File(testPath+"spam");

            // Parse the csv files and copy the contents into ham and spam maps
            System.out.println("Parsing ham");
            parseCSV(ham);
            System.out.println("Copying ham");
            hamWords.putAll(temporaryMap);
            temporaryMap.clear();
//            System.out.println(hamWords);
            System.out.println("Parsing spam");
            parseCSV(spam);
            System.out.println("Copying spam");
            spamWords.putAll(temporaryMap);
//            System.out.println(spamWords);
            // Count the files in each test folder
            System.out.println("Counting");
            numHamFiles = countFiles(hamFolder);
            numSpamFiles = countFiles(spamFolder);

        } catch(FileNotFoundException e){
            System.err.println("Cannot find file");
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }

        System.out.println("Calculating summation");
        int n = wordList.size();
        System.out.println(summation(n));
    }
}
