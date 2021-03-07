package sample;

import java.io.*;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class Probabilities {

    private static Map<String, Integer> hamWords;
    private static Map<String, Integer> spamWords;
    private static Map<String, Integer> temporaryMap;
    private static Map<String, Double> hamProb;
    private static Map<String, Double> spamProb;
    private static Map<String, Double> probabilitySW;
    private static int numHamFiles;
    private static int numSpamFiles;

    public Probabilities() {
        hamWords = new TreeMap<>();
        spamWords = new TreeMap<>();
        temporaryMap = new TreeMap<>();
        hamProb = new TreeMap<>();
        spamProb = new TreeMap<>();
        probabilitySW = new TreeMap<>();
    }

    public Map<String, Double> getProbabilitySW() {
        return probabilitySW;
    }

    public void parseFile(File file) throws IOException {
        System.out.println("Starting parsing the file: " + file.getAbsolutePath());
        String temp = "";
        int number = 0;
        Scanner scan = new Scanner(file);
        scan.useDelimiter(",");
        while(scan.hasNext()) {
            String token = scan.next();
            if (isValidWord(token)) {
                temp = token;
            }
            if (isNumber(token)) {
                number = Integer.parseInt(token);
                addWord(temp, number);
            }
        }
    }

    public static boolean isValidWord(String word){
        String allLetters = "^[a-zA-Z]+$";
        // returns true if the word is composed by only letters otherwise returns false;
        return word.matches(allLetters);

    }

    private static boolean isNumber (String input){
        String allNumbers = "^[1-9]+$";
        // returns true if the word is composed by only letters otherwise returns false;
        return input.matches(allNumbers);
    }

    private static void addWord(String word, int number) {
        temporaryMap.put(word, number);
    }

    private static int countFiles(File folder) {
        File[] size = folder.listFiles();
        System.out.printf("files in folder: %d\n", size.length);
        return size.length;
    }

    private static void probabilityContains(String word) {
        double probHam = (double) hamWords.get(word) / (double) numHamFiles;
        double probSpam = (double) spamWords.get(word) / (double) numSpamFiles;
        hamProb.put(word, probHam);
        spamProb.put(word, probSpam);
    }

    private static void probability(String word){
        double probSW = spamProb.get(word) / (spamProb.get(word) + hamProb.get(word));
        probabilitySW.put(word, probSW);
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java WordCounter <inputDir> <inputFile>");
        }

        File hamFolder = new File(args[0]);
        File hamFile1 = new File(args[1]);

        Probabilities probabilities = new Probabilities();
        System.out.println("I really hope this works.");
        try {
            probabilities.parseFile(hamFile1);


            System.out.println("Counting...");
            numHamFiles = countFiles(hamFolder);
        } catch(FileNotFoundException e){
            System.err.println("Invalid input dir: " + hamFile1.getAbsolutePath());
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }

    }
}
