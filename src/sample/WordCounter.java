package sample;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class WordCounter {

    private Map<String, Integer> wordCounts;

    private Map<String, Integer> trainHamFreq;
    private Map<String, Integer> trainSpamFreq;
    private Map<String, Double> hamProbability;
    private Map<String, Double> spamProbability;
    private Map<String, Double> probabilityOfSpamFile;

    public WordCounter(){
        wordCounts = new TreeMap<>();
        trainHamFreq = new TreeMap<>();
        trainSpamFreq = new TreeMap<>();
        hamProbability = new TreeMap<>();
        spamProbability = new TreeMap<>();
        probabilityOfSpamFile = new TreeMap<>();
    }

    public Map<String, Integer> getWordCounts() {
        return wordCounts;
    }

    public Map<String, Integer> getTrainHamFreq() {
        return trainHamFreq;
    }

    public Map<String, Integer> getTrainSpamFreq() {
        return trainSpamFreq;
    }

    public void parseFile(File file) throws IOException{
        //System.out.println("Starting parsing the file:" + file.getAbsolutePath());
        File[] content = file.listFiles();
        String filePathName = file.getPath();
        if(file.isDirectory()){
            //parse each file inside the directory
            System.out.println(content.length);
            for(File current: content) {
                parseFile(current);
            }
        }else{
            Scanner scanner = new Scanner(file);
            // scanning token by token
            while (scanner.hasNext()){
                String  token = scanner.next();
                if (isValidWord(token)) {
                    if (filePathName.contains("ham")) {
                        hamFrequency(token);
                        probabilityWordAppearsInHam(token, 2500);
                    } else if (filePathName.contains("ham2")) {
                        hamFrequency(token);
                        probabilityWordAppearsInHam(token, 250);
                    } else if (filePathName.contains("spam")) {
                        spamFrequency(token);
                        probabilityWordAppearsInSpam(token);
                    }
//                    System.out.println("spam: " + spamProbability.get(token));
//                    System.out.println("ham: " + hamProbability.get(token));
                }
            }
        }
    }

    public static boolean isValidWord(String word) {
        String allLetters = "^[a-zA-Z]+$";
        // returns true if the word is composed by only letters otherwise returns false;
        return word.matches(allLetters);
    }

    private void countWord(String word){
        if(wordCounts.containsKey(word)){
            int previous = wordCounts.get(word);
            wordCounts.put(word, previous+1);
        }else{
            wordCounts.put(word, 1);
        }
    }

    public void hamFrequency(String word) {
        if (trainHamFreq.containsKey(word)) {
            int previous = trainHamFreq.get(word);
            trainHamFreq.put(word, previous+1);
        } else {
            trainHamFreq.put(word, 1);
        }
    }

    public void spamFrequency(String word) {
        if (trainSpamFreq.containsKey(word)) {
            int previous = trainSpamFreq.get(word);
            trainSpamFreq.put(word, previous+1);
        } else {
            trainSpamFreq.put(word, 1);
        }
    }

    public void probabilityWordAppearsInHam(String word, double numFiles) {
        double probability = (double) trainHamFreq.get(word) / (double) numFiles;
        hamProbability.put(word, probability);
    }

    public void probabilityWordAppearsInSpam(String word) {
        double probability = (double) trainSpamFreq.get(word) / (double) 500;
        spamProbability.put(word, probability);
    }

    public void probabilityFileIsSpam(String word) {
        double probability = spamProbability.get(word) / spamProbability.get(word) + hamProbability.get(word);
        probabilityOfSpamFile.put(word, probability);
    }

    public void outputWordCount(int minCount, File output) throws IOException{
        System.out.println("Saving word counts to file:" + output.getAbsolutePath());
        System.out.println("Total words:" + wordCounts.keySet().size());

        if (!output.exists()) {
            output.createNewFile();
            if (output.canWrite()) {
                PrintWriter fileOutput = new PrintWriter(output);

                Set<String> keys = wordCounts.keySet();
                Iterator<String> keyIterator = keys.iterator();

                while(keyIterator.hasNext()) {
                    String key = keyIterator.next();
                    int count = wordCounts.get(key);
                    // testing minimum number of occurances
                    if(count>=minCount){
                        fileOutput.println(key + ", " + count);
                    }
                }
                fileOutput.close();
            }
        } else {
            System.out.println("Error: the output file already exists: " + output.getAbsolutePath());
        }
    }

    //main method
    public static void main(String[] args) {
        if(args.length < 1){
            System.err.println("Usage: java WordCounter <inputDir>");
            System.exit(0);
        }

        File dataDir = new File(args[0]);
        File dataDir2 = new File(args[1]);
        //File outFile = new File(args[1]);

        WordCounter wordCounter = new WordCounter();
        System.out.println("Hello");
        try{
            wordCounter.parseFile(dataDir);
            wordCounter.parseFile(dataDir2);
            //System.out.println("Testing if this works: " + wordCounter.hamProbability);
            //System.out.println("HOPING: " + wordCounter.trainHamFreq);
            //System.out.println("REALLY AM HOPING: " + wordCounter.trainSpamFreq);

//            System.out.println("hamProbability: " + wordCounter.hamProbability);
//            System.out.println("spamProbability: " + wordCounter.spamProbability);

            System.out.println("probabilityOfSpamFile: " + wordCounter.probabilityOfSpamFile);
            //wordCounter.parseFile(dataDir2);
            //wordCounter.outputWordCount(2, outFile);
        }catch(FileNotFoundException e) {
            System.err.println("Invalid input dir: " + dataDir.getAbsolutePath());
            e.printStackTrace();
        }catch(IOException e) {
            e.printStackTrace();
        }
    }
}