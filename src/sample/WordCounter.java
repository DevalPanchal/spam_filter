package sample;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * The purpose of WordCounter is to calculate the probability that a file is spam. By first creating a map of word
 * frequencies and then calculating the probability of those words being spam or ham. Then finally calculating the
 * probability that a file is spam using the calculated probabilities.
 */
public class WordCounter {

    private Map<String, Integer> trainHamFreq;
    private Map<String, Integer> trainSpamFreq;

    private Map<String, Double> hamProbability;
    private Map<String, Double> spamProbability;

    public Map<String, Double> probabilitySpamWords;
    final double DETECTOR_THRESHOLD = 0.5;
    public double accuracy;
    public double precision;

    public int numFiles;
    public double numTruePositives = 0.0;
    public double numTrueNegatives = 0.0;
    public double numFalsePositives = 0.0;

    /**
     * Constructor
     * This function initiates the maps as tree maps
     */
    public WordCounter() {
        trainHamFreq = new TreeMap<>();
        trainSpamFreq = new TreeMap<>();
        hamProbability = new TreeMap<>();
        spamProbability = new TreeMap<>();
        probabilitySpamWords = new TreeMap<>();
    }

    /**
     * This function opens the files contained within the train folder
     * @param file - the file folder /data/train
     * @throws IOException
     */
    public void parseTrainingFolder(File file) throws IOException {
        String fileName = file.getPath();
        if (file.isDirectory()) {
            if (fileName.contains("ham")) {
                System.out.println("This is a ham folder");
                hamFrequency(file);
            } else if (fileName.contains("ham2")) {
                System.out.println("This is a ham folder");
                hamFrequency(file);
            } else if (fileName.contains("spam")) {
                System.out.println("This is a spam folder");
                spamFrequency(file);
            }
            File[] content = file.listFiles();
            for (File current: content) {
                parseTrainingFolder(current);
            }
        }
    }

    /**
     * This function scans each file within the ham folder and counts the frequency of the word
     * @param file - ham file
     * @throws IOException
     */
    public void hamFrequency(File file) throws IOException {
        File[] content = file.listFiles();

        for(File current: content) {
            Map<String, Integer> temporaryMap = new TreeMap<String, Integer>();
            Scanner scan = new Scanner(current);
            while(scan.hasNext()) {
                String token = scan.next();
                if (isValidWord(token)) {
                    countWordHam(token);
                }
            }
            probabilityWordInHam(content.length);
        }
    }

    /**
     * his function scans each file within the spam folder and counts the frequency of the word
     * @param file - spam file
     * @throws IOException
     */
    public void spamFrequency(File file) throws IOException {
        File[] content = file.listFiles();
        for (File current: content) {
            Map<String, Integer> temporaryMap = new TreeMap<String, Integer>();
            Scanner scan = new Scanner(current);
            while(scan.hasNext()) {
                String token = scan.next();
                if (isValidWord(token)) {
                    countWordSpam(token);
                }
            }
            probabilityWordInSpam(content.length);
        }
    }

    /**
     * This function calculates the formula Pr(Wi|H)
     * number of ham files containing word_i / number of ham files
     * @param numFiles - number of ham files
     */
    public void probabilityWordInHam(double numFiles) {
        for (Map.Entry<String, Integer> entry: trainHamFreq.entrySet()) {
            double probability = (double) entry.getValue() / numFiles;
            hamProbability.put(entry.getKey(), probability);
        }
    }

    /**
     * This function counts the words in the ham folder
     * @param word - the key is a string "word"
     */
    public void countWordHam(String word) {
        if (trainHamFreq.containsKey(word)) {
            int previous = trainHamFreq.get(word);
            trainHamFreq.put(word, previous + 1);
        } else {
            trainHamFreq.put(word, 1);
        }
    }

    // Pr(Wi|S)
    /**
     * This function calculates the formula Pr(Wi|S)
     * number of spam files containing word_i / number of spam files
     * @param numFiles - number of spam files
     */
    public void probabilityWordInSpam(double numFiles) {
        for (Map.Entry<String, Integer> entry: trainSpamFreq.entrySet()) {
            double probability = (double) entry.getValue() / numFiles;
            spamProbability.put(entry.getKey(), probability);
        }
    }

    /**
     * This function calculates Pr(S|Wi)
     * Pr(Wi|S) / (Pr(Wi|S) + Pr(Wi|H))
     */
    public void probabiltyFileIsSpam() {
        for (Map.Entry<String, Double> entry: spamProbability.entrySet()) {
            if (hamProbability.containsKey(entry.getKey())) {
                double probability = entry.getValue() / ( entry.getValue() + hamProbability.get(entry.getKey()) );
                probabilitySpamWords.put(entry.getKey(), probability);
            }
        }
    }

    /**
     * This function counts the words in the spam folder
     * @param word - key is a string "word"
     */
    public void countWordSpam(String word) {
        if (trainSpamFreq.containsKey(word)) {
            int previous = trainSpamFreq.get(word);
            trainSpamFreq.put(word, previous + 1);
        } else {
            trainSpamFreq.put(word, 1);
        }
    }

    /**
     * This function uses regex to determine whether the string input is a word
     * @param word - string input
     * @return - boolean true if input is a word and false if not a word
     */
    private boolean isValidWord(String word){
        String allLetters = "^[a-zA-Z]+$";
        // returns true if the word is composed by only letters otherwise returns false;
        return word.matches(allLetters);
    }

    /**
     * This function calculates the probability that a file is spam, calls probabilitySummation
     * @param file - input File
     * @return - double the calculated probability
     * @throws FileNotFoundException
     */
    public double probabilitySpamFile(File file) throws IOException {
        String fileName = file.getPath();
        double probability = 0.0;
        double N = 0.0;

        Scanner scan = new Scanner(file);
        while(scan.hasNext()) {
            String token = scan.next();
            if (isValidWord(token)) {
                if (probabilitySpamWords.containsKey(token)) {
                    N += probabilitySummation(token);
                }
            }
        }
        probability = 1 / (1 + Math.pow(Math.E, N));

        if (fileName.contains("ham") && probability > DETECTOR_THRESHOLD) {
            numFalsePositives++;
        } else if (fileName.contains("spam") && probability > DETECTOR_THRESHOLD) {
            numTruePositives++;
        } else if (fileName.contains("ham") && probability < DETECTOR_THRESHOLD) {
            numTrueNegatives++;
        }
        numFiles++;
        return probability;
    }

    /**
     * This function calculates 1 iteration of the summation ln(1-Pr(S|Wi))-ln(Pr(S|Wi))
     * @param word - key string "word"
     * @return double result of the calculation
     */
    public double probabilitySummation(String word) {
        double result = Math.log(1 - probabilitySpamWords.get(word) - Math.log(probabilitySpamWords.get(word)));
        return result;
    }

    public static void main(String[] args) {
        if(args.length < 2){
            System.err.println("Usage: java WordCounter <inputDir> <inputDir2>");
            System.exit(0);
        }

        File dataDir = new File(args[0]);
        File dataDir2 = new File(args[1]);
        //File dataDir3 = new File(args[2]);

        WordCounter wordCounter = new WordCounter();
        System.out.println("Hello");
        try {
            wordCounter.parseTrainingFolder(dataDir);
            wordCounter.parseTrainingFolder(dataDir2);
            //wordCounter.parseFile(dataDir3);

            wordCounter.probabiltyFileIsSpam();
            System.out.println("Probability file is spam: " + wordCounter.probabilitySpamWords);
        }catch(FileNotFoundException e) {
            System.err.println("Invalid input dir: " + dataDir.getAbsolutePath());
            e.printStackTrace();
        }catch(IOException e) {
            e.printStackTrace();
        }
    }
}