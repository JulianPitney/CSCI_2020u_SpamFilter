import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;


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
                    if (currentWordProb == 1.0)
                    {
                        currentWordProb = 0.9;
                    }

                    n += ((Math.log(1.0 - currentWordProb)) - (Math.log(currentWordProb)));
                }
            }

            System.out.println("n=" + n);
            fileIsSpamProbabilty = ((1.00) / (1.00 + Math.pow(Math.E, n)));


            inputFiles.get(i).setSpamProbability(fileIsSpamProbabilty);

        }
    }
}
