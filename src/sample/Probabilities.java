package sample;

import java.io.*;
import java.util.*;

public class Probabilities {

    private  Map<String, Integer> trainHamFreq;
    private  Map<String, Integer> trainSpamFreq;
    private  Map<String, Integer> temporaryMap;
    private  Map<String, Double> spamProb;
    private  Map<String, Double> hamProb;
    private  Map<String, Double> probabilitySW;
    private  int numHamFiles;
    private  int numSpamFiles;

    public Probabilities() {
        trainHamFreq = new TreeMap<>();
        trainSpamFreq = new TreeMap<>();
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

//        String line = "";
//
//        try {
//            BufferedReader reader = new BufferedReader(new FileReader(file));
//
//            while((line = reader.readLine()) != null) {
//                // parsing the email
//                String[] email = line.split(",");
//                // the number of occurences of the word
//                int number = Integer.parseInt(email[1].trim());
//                //System.out.println("Ham [email: " + email[0] + ", occurences: " + email[1] + "]");
//                // Put the lines read in a number
//                temporaryMap.put(email[0],  number);
//
//                double probabilityHam = (double) temporaryMap.get(email[0]) / (double) 2501;
//                hamProb.put(email[0], probabilityHam);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    public boolean isValidWord(String word){
        String allLetters = "^[a-zA-Z]+$";
        // returns true if the word is composed by only letters otherwise returns false;
        return word.matches(allLetters);
    }

    private boolean isNumber(String input){
        String allNumbers = "^[1-9]+$";
        // returns true if the word is composed by only letters otherwise returns false;
        return input.matches(allNumbers);
    }

    private void addWord(String word, int number) {
        temporaryMap.put(word, number);
    }

    private int countFiles(File folder) {
        File[] size = folder.listFiles();
        System.out.printf("files in folder: %d\n", size.length);
        return size.length;
    }

//    public void probabilityContains(String word) {
//        double probHam = (double) trainHamFreq.get(word) / (double) numHamFiles;
//        double probSpam = (double) trainSpamFreq.get(word) / (double) numSpamFiles;
//        hamProb.put(word, probHam);
//        spamProb.put(word, probSpam);
//    }

    public void probabilityWordInSpam(String word) {
        double probabilitySpam = (double) trainSpamFreq.get(word) / (double) numSpamFiles;
        spamProb.put(word, probabilitySpam);
    }

    public void probabilityWordInHam(String word) {
        double probabilityHam = (double) trainHamFreq.get(word) / (double) numHamFiles;
        hamProb.put(word, probabilityHam);
        //System.out.println(hamProb);
    }

    private void probability(String word){
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
            //System.out.println("Display Temporary Map: " + probabilities.temporaryMap);

            //System.out.println("Displaying probability: " + probabilities.hamProb);

            System.out.println("Counting...");
            probabilities.numHamFiles = probabilities.countFiles(hamFolder);
        } catch(FileNotFoundException e) {
            System.err.println("Invalid input dir: " + hamFile1.getAbsolutePath());
            e.printStackTrace();
        }catch(IOException e) {
            e.printStackTrace();
        }
    }
}