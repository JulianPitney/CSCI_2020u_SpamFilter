import java.io.File;
import java.io.IOException;
import java.util.HashMap;






public class Main {

   static String dataDirectory = "C:\\Users\\jp183\\Desktop\\Courses\\Software Systems Dev and Integration\\CSCI2020u_Assignments\\CSCI2020u_assignment1\\assignment1_data\\data";


    public static void main (String[] args) throws IOException {


        MailAnalyzer mailAnalyzer = new MailAnalyzer();


        // Get file set from each directory
        File[] trainSpamFiles = mailAnalyzer.scan_directory(dataDirectory + "\\train\\spam");
        File[] trainHamFiles = mailAnalyzer.scan_directory(dataDirectory + "\\train\\ham");
        File[] trainHam2Files = mailAnalyzer.scan_directory(dataDirectory + "\\train\\ham2");


        // Generate word frequency map for each set of files
        HashMap<String, Integer> trainSpamFrequencyMap = mailAnalyzer.build_frequency_map(trainSpamFiles);
        HashMap<String, Integer> trainHamFrequencyMap = mailAnalyzer.build_frequency_map(trainHamFiles);
        HashMap<String, Integer> trainHam2FrequencyMap = mailAnalyzer.build_frequency_map(trainHam2Files);

        // Merge ham and ham2 file sets
        trainHamFrequencyMap = mailAnalyzer.merge_frequency_maps(trainHamFrequencyMap, trainHam2FrequencyMap);


        // Generate word probability map from frequency maps
        HashMap<String, Double> wordProbabilitySpam = mailAnalyzer.build_word_probability_map(trainSpamFrequencyMap, trainSpamFiles.length);
        HashMap<String, Double> wordProbabilityHam = mailAnalyzer.build_word_probability_map(trainHamFrequencyMap, trainHamFiles.length);


        HashMap<String, Double> fileSpamProbabilityMap = mailAnalyzer.build_file_spam_probability_map(wordProbabilitySpam, wordProbabilityHam);

        for (String word: fileSpamProbabilityMap.keySet()){

            System.out.println(word + " " + fileSpamProbabilityMap.get(word));
        }

    }
}
