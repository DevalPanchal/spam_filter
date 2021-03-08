package sample;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class WordCounter {

    private Map<String, Integer> trainHamFreq;
    private Map<String, Integer> trainSpamFreq;

    private Map<String, Double> hamProbability;
    private Map<String, Double> spamProbability;

    public Map<String, Double> probabilitySpamWords;

    private int numHamFiles = 0;
    private int numSpamFiles = 0;

    public WordCounter() {
        trainHamFreq = new TreeMap<>();
        trainSpamFreq = new TreeMap<>();
        hamProbability = new TreeMap<>();
        spamProbability = new TreeMap<>();
        probabilitySpamWords = new TreeMap<>();
    }

    public void parseFile(File file) throws IOException {
        //System.out.println("Starting parsing the file: " + file.getAbsolutePath());
        String fileName = file.getPath();
        if(file.isDirectory()){
            //parse each file inside the directory
            File[] content = file.listFiles();
            for(File current: content){
                parseFile(current);
            }
        }else{
            Scanner scanner = new Scanner(file);
            // scanning token by token
            while (scanner.hasNext()) {
                String token = scanner.next();
                if (isValidWord(token)) {
                    if (fileName.contains("ham")) {
                        hamFrequency(file);
                    } else if (fileName.contains("ham2")) {
                        hamFrequency(file);
                    } else if (fileName.contains("spam")) {
                        spamFrequency(file);
                    }
                }
            }
        }
    }

    public void parseTrainingFolder(File file) throws IOException {
        String fileName = file.getPath();
        if (file.isDirectory()) {
            if (fileName.contains("ham")) {
                System.out.println("This is a ham folder");
                hamFrequency(file);
            } else if (fileName.contains("ham2")) {
                System.out.println("This is another ham folder");
                hamFrequency(file);
            } else if (fileName.contains("spam")) {
                System.out.println("This is a spam folder");
                spamFrequency(file);
            } else {
                File[] content = file.listFiles();
                for (File current: content) {
                    parseTrainingFolder(current);
                }
            }
        }
    }

    public void parseTestingFolder(File file) throws IOException {
        String fileName = file.getPath();
        if (file.isDirectory()) {
            File[] content = file.listFiles();
            for (File current: content) {
                parseTestingFolder(file);
            }
        } else {
            double probabilitySpam = 0.0;
            try {
                probabilitySpam = probabilitySpamFile(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public double probabilitySpamFile(File file) throws FileNotFoundException {
        double probability = 0.0;
        double N = 0.0;
        double threshold = 0.5;

        Scanner scan = new Scanner(file);
        while(scan.hasNext()) {
            String token = scan.next();
            if (isValidWord(token)) {
                if (probabilitySpamWords.containsKey(token)) {
                    N += Math.log(1 - probabilitySpamWords.get(token) - Math.log(probabilitySpamWords.get(token)));
                }
            }
        }
        probability = 1 / (1 + Math.pow(Math.E, N));
        return probability;
    }

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
            for (Map.Entry<String, Integer> entry: temporaryMap.entrySet()) {
                if (trainHamFreq.containsKey(entry.getKey())) {
                    int previous = trainHamFreq.get(entry.getKey());
                    trainHamFreq.put(entry.getKey(), previous + 1);
                } else {
                    trainHamFreq.put(entry.getKey(), 1);
                }
            }
            probabilityWordInHam(content.length);
        }
    }

    public void spamFrequency(File file) throws IOException {
        File[] content = file.listFiles();
        for (File current: content) {
            Map<String, Integer> temporaryMap = new TreeMap<String, Integer>();
            Scanner scan = new Scanner(current);
            while(scan.hasNext()) {
                String token = scan.next();
                if (isValidWord(token)) {
                    containsWord(temporaryMap, token);
                }
            }
            for (Map.Entry<String, Integer> entry: temporaryMap.entrySet()) {
                if (trainSpamFreq.containsKey(entry.getKey())) {
                    int previous = trainSpamFreq.get(entry.getKey());
                    trainSpamFreq.put(entry.getKey(), previous + 1);
                } else {
                    trainSpamFreq.put(entry.getKey(), 1);
                }
            }
            probabilityWordInSpam(content.length);
        }
    }

    public void containsWord(Map<String, Integer> temp, String word) {
        if (!temp.containsKey(word)) {
            temp.put(word, 1);
        }
    }

    // Pr(Wi|H)
    public void probabilityWordInHam(double numFiles) {
        for (Map.Entry<String, Integer> entry: trainHamFreq.entrySet()) {
            double probability = (double) entry.getValue() / numFiles;
            hamProbability.put(entry.getKey(), probability);
        }
    }

    // count the words in ham folder
    public void countWordHam(String word) {
        if (trainHamFreq.containsKey(word)) {
            int previous = trainHamFreq.get(word);
            trainHamFreq.put(word, previous + 1);
        } else {
            trainHamFreq.put(word, 1);
        }
    }

    public void countWordHam2(String word, Map<String, Integer> temp) {
        for (Map.Entry<String, Integer> entry: temp.entrySet()) {
            if (trainHamFreq.containsKey(word)) {
                int previous = trainHamFreq.get(word);
                trainHamFreq.put(word, previous + 1);
            } else {
                trainHamFreq.put(word, 1);
            }
        }
    }

    // Pr(Wi|S)
    public void probabilityWordInSpam(double numFiles) {
        for (Map.Entry<String, Integer> entry: trainSpamFreq.entrySet()) {
            double probability = (double) entry.getValue() / numFiles;
            spamProbability.put(entry.getKey(), probability);
        }
    }

    // Pr(S|Wi)
    public void probabiltyFileIsSpam() {
        for (Map.Entry<String, Double> entry: spamProbability.entrySet()) {
            if (hamProbability.containsKey(entry.getKey())) {
                double probability = entry.getValue() / ( entry.getValue() + hamProbability.get(entry.getKey()) );
                probabilitySpamWords.put(entry.getKey(), probability);
            }
        }
    }

    // Count the words in spam folder
    public void countWordSpam(String word) {
        if (trainSpamFreq.containsKey(word)) {
            int previous = trainSpamFreq.get(word);
            trainSpamFreq.put(word, previous + 1);
        } else {
            trainSpamFreq.put(word, 1);
        }
    }

    private boolean isValidWord(String word){
        String allLetters = "^[a-zA-Z]+$";
        // returns true if the word is composed by only letters otherwise returns false;
        return word.matches(allLetters);
    }

//    private void countWord(String word){
//        if(wordCounts.containsKey(word)){
//            int previous = wordCounts.get(word);
//            wordCounts.put(word, previous+1);
//        }else{
//            wordCounts.put(word, 1);
//        }
//    }

//    private void hamFrequency(String word) {
//        if (trainHamFreq.containsKey(word)) {
//            int previous = trainHamFreq.get(word);
//            trainHamFreq.put(word, previous + 1);
//        } else {
//            trainHamFreq.put(word, 1);
//        }
//    }
//
//    private void spamFrequency(String word) {
//        if (trainSpamFreq.containsKey(word)) {
//            int previous = trainSpamFreq.get(word);
//            trainSpamFreq.put(word, previous + 1);
//        } else {
//            trainSpamFreq.put(word, 1);
//        }
//    }

//    private void ProbabilityWordInSpam(String word, double numFiles) {
//        double probability = (double) trainHamFreq.get(word) / numFiles;
//        spamProbability.put(word, probability);
//    }
//
//    private void ProbabilityWordInHam(String word, double numFiles) {
//        double probability = (double) trainSpamFreq.get(word) / numFiles;
//        hamProbability.put(word, probability);
//    }



//    public void outputWordCount(int minCount, File output) throws IOException{
//        System.out.println("Saving word counts to file:" + output.getAbsolutePath());
//        System.out.println("Total words:" + wordCounts.keySet().size());
//
//        if (!output.exists()){
//            output.createNewFile();
//            if (output.canWrite()){
//                PrintWriter fileOutput = new PrintWriter(output);
//
//                Set<String> keys = wordCounts.keySet();
//                Iterator<String> keyIterator = keys.iterator();
//
//                while(keyIterator.hasNext()){
//                    String key = keyIterator.next();
//                    int count = wordCounts.get(key);
//                    // testing minimum number of occurances
//                    if(count>=minCount){
//                        fileOutput.println(key + ", " + count);
//                    }
//                }
//                fileOutput.close();
//            }
//        }else{
//            System.out.println("Error: the output file already exists: " + output.getAbsolutePath());
//        }
//    }

    public static void main(String[] args) {
        if(args.length < 1){
            System.err.println("Usage: java WordCounter <inputDir> <inputDir2>");
            System.exit(0);
        }

        File dataDir = new File(args[0]);
        File dataDir2 = new File(args[1]);
        //File dataDir3 = new File(args[2]);

        WordCounter wordCounter = new WordCounter();
        System.out.println("Hello");
        try{
            wordCounter.parseTrainingFolder(dataDir);
            wordCounter.parseTrainingFolder(dataDir2);
            //wordCounter.parseFile(dataDir3);

//            System.out.println("Ham Frequency: " + wordCounter.trainHamFreq);
//            System.out.println("Spam Frequency: " + wordCounter.trainSpamFreq);
//
//            System.out.println("Ham Probability: " + wordCounter.hamProbability);
//            System.out.println("Spam Probability: " + wordCounter.spamProbability);

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