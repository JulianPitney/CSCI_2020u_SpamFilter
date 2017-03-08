import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;



public class MailAnalyzer {



    public File[] scan_directory(String directoryInput) {

        File directory = new File(directoryInput);
        File[] files = directory.listFiles();
        return files;
    }

    public HashMap<String, Integer> build_frequency_map (File[] fileInput) throws IOException {

        String fileContents;
        String[] words;
        HashMap<String, Integer> frequencyMapOutput = new HashMap<>();

        // Loop over each file
        for (int i = 0; i < fileInput.length; i++)
        {

            // Read contents of current file into string
            fileContents = new String(Files.readAllBytes(fileInput[i].toPath()));

            // Split contents into words
            words = fileContents.split("[-!~.,:;\\s]+");

            // Unique words in current file
            HashSet<String> thisFilesUniqueWords = new HashSet<>();

            // Add all words from current file into HashSet (HashSet.add() will only add value if it's not already present)
            for (int z = 0; z < words.length; z++)
            {
                thisFilesUniqueWords.add(words[z]);
            }


            // Iterate across every unique word found in the current file.
            // If word is not in our frequencyMap, add it.
            // If word is in our frequencyMap, increment it's value.
            for (String s : thisFilesUniqueWords) {

                if (frequencyMapOutput.putIfAbsent(s, 1) != null)
                {
                    frequencyMapOutput.put(s, frequencyMapOutput.get(s) + 1);
                }
            }
        }

        // Return HashMap where keys are unique words from all files
        // and values are number of files the word occurs in.
        return frequencyMapOutput;
    }

    public HashMap<String, Integer> merge_frequency_maps(HashMap<String, Integer> map1, HashMap<String, Integer> map2)
    {
        for (String s : map1.keySet()) {

            if (map2.putIfAbsent(s, map1.get(s)) != null)
            {
                map2.put(s, map2.get(s) + map1.get(s));
            }
        }

        return map2;
    }



    // Creates a probability map where key is the word and value is the probability that the word will appear in the set of files
    // that was used to generate frequencyMapInput.
    public HashMap<String, Double> build_word_probability_map (HashMap<String, Integer> frequencyMapInput, int numberOfFiles)
    {
        HashMap<String, Double> probabilityMapOutput = new HashMap<>();

        // Loop across each key in frequencyMapInput.
        for (String s : frequencyMapInput.keySet())
        {
            // Calculate probability that the word will appear in any given file from the file set.
            double wordProbability = (double)frequencyMapInput.get(s)  / (double)numberOfFiles;

            // Add word + probability to new map.
            probabilityMapOutput.put(s, wordProbability);
        }

        return probabilityMapOutput;
    }



    public HashMap<String, Double> build_file_is_spam_probability_map (HashMap<String, Double> spamWordProbabilityMap, HashMap<String, Double> hamWordProbabilityMap)
    {
        HashMap<String, Double> fileIsSpamProbabilityMapOutput = new HashMap<>();

        for (String s: spamWordProbabilityMap.keySet())
        {
            double spamWordProbability = spamWordProbabilityMap.get(s);
            double hamWordProbability = hamWordProbabilityMap.getOrDefault(s, 0.0);
            double fileSpamProbability = (spamWordProbability / (spamWordProbability + hamWordProbability));

            fileIsSpamProbabilityMapOutput.put(s, fileSpamProbability);
        }

        return fileIsSpamProbabilityMapOutput;
    }


    public ArrayList<TestFile> convert_to_TestFile_list (File[] inputFiles, String fileType)
    {
        ArrayList<TestFile> outputArr = new ArrayList<>(inputFiles.length);

        for (int i = 0; i < inputFiles.length; i++)
        {
            TestFile temp = new TestFile(inputFiles[i].getName(), 0.0, fileType, inputFiles[i]);
            outputArr.add(i, temp);
        }

        return outputArr;
    }



    public void compute_spam_probability (HashMap<String, Double> PrSWMap, ArrayList<TestFile> inputFiles) throws IOException {

        String fileContents;
        String[] words;
        double fileIsSpamProbabilty;

        for (int i = 0; i < inputFiles.size(); i++)
        {
            // Read contents of current file into string
            fileContents = new String(Files.readAllBytes(inputFiles.get(i).getActualFile().toPath()));

            // Split contents into words
            words = fileContents.split("[-!~.,:;\\s]+");
            double n = 0.0;

            for (int x = 0; x < words.length; x++)
            {
                Double currentWordProb = PrSWMap.get(words[x]);

                if (currentWordProb != null)
                {

                    // Ensure currentWordProb != 1.0 because n becomes -INFINITY in this case
                    if (currentWordProb == 1.0)
                    {
                        currentWordProb = 0.9;
                    }

                    n += ((Math.log(1.0 - currentWordProb)) - (Math.log(currentWordProb)));
                }
            }


            fileIsSpamProbabilty = ((1.00) / (1.00 + Math.pow(Math.E, n)));
            inputFiles.get(i).setSpamProbability(fileIsSpamProbabilty);

        }
    }


    public ArrayList<TestFile> analyze (String dataDirectoryPath) throws IOException {

        // Get file set from each directory
        File[] trainSpamFiles = this.scan_directory(dataDirectoryPath + "\\train\\spam");
        File[] trainHamFiles = this.scan_directory(dataDirectoryPath + "\\train\\ham");
        File[] trainHam2Files = this.scan_directory(dataDirectoryPath + "\\train\\ham2");


        // Generate word frequency map for each set of files
        HashMap<String, Integer> trainSpamFrequencyMap = null;
        trainSpamFrequencyMap = this.build_frequency_map(trainSpamFiles);

        HashMap<String, Integer> trainHamFrequencyMap = null;
        trainHamFrequencyMap = this.build_frequency_map(trainHamFiles);

        HashMap<String, Integer> trainHam2FrequencyMap = null;
        trainHam2FrequencyMap = this.build_frequency_map(trainHam2Files);

        // Merge ham and ham2 file sets
        trainHamFrequencyMap = this.merge_frequency_maps(trainHamFrequencyMap, trainHam2FrequencyMap);


        // Generate word probability map from frequency maps
        HashMap<String, Double> Pr_WS = this.build_word_probability_map(trainSpamFrequencyMap, trainSpamFiles.length);
        HashMap<String, Double> Pr_WH = this.build_word_probability_map(trainHamFrequencyMap, trainHamFiles.length);


        // Generate map where key is a word and value is the probability that a file is spam given that it contains that key/word
        HashMap<String, Double> Pr_SW = this.build_file_is_spam_probability_map(Pr_WS, Pr_WH);


        File[] testSpamFiles = this.scan_directory(dataDirectoryPath + "\\test\\spam");
        File[] testHamFiles = this.scan_directory(dataDirectoryPath + "\\test\\ham");
        ArrayList<TestFile> testSpamResults = this.convert_to_TestFile_list(testSpamFiles, "Spam");
        ArrayList<TestFile> testHamResults = this.convert_to_TestFile_list(testHamFiles, "Ham");


        this.compute_spam_probability(Pr_SW, testSpamResults);
        this.compute_spam_probability(Pr_SW, testHamResults);

        for (int i = 0; i < testHamResults.size(); i++)
        {
            testSpamResults.add(testHamResults.get(i));
        }

        return testSpamResults;
    }


    // Calculates the percent of all files whose spam probability was greater than 50% in the correct direction.
    public Double calculate_percent_correct(ArrayList<TestFile> inputResults)
    {
        int correctFiles = 0;
        double percentCorrect;

        for (int i = 0; i < inputResults.size(); i++)
        {
            TestFile temp = inputResults.get(i);

            if (temp.getActualClass() == "Spam")
            {
                if (temp.getSpamProbabilityDouble() > 0.5)
                {
                    correctFiles++;
                }
            }
            else if (temp.getActualClass() == "Ham")
            {
                if (temp.getSpamProbabilityDouble() < 0.5)
                {
                    correctFiles++;
                }
            }
        }

        return percentCorrect = (100)*((double)correctFiles / (double)inputResults.size());

    }
}
