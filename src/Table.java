import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class Table extends Application {

    private TableView table = new TableView();
    static String dataDirectoryPath = "C:\\Users\\jp183\\Desktop\\Courses\\Software Systems Dev and Integration\\CSCI2020u_Assignments\\CSCI2020u_assignment1\\assignment1_data\\data";

    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(new Group());
        stage.setTitle("Table View Sample");
        stage.setWidth(300);
        stage.setHeight(500);

        final Label label = new Label("Address Book");
        label.setFont(new Font("Arial", 20));

        table.setEditable(true);

        TableColumn firstNameCol = new TableColumn("First Name");
        TableColumn lastNameCol = new TableColumn("Last Name");
        TableColumn emailCol = new TableColumn("Email");

        table.getColumns().addAll(firstNameCol, lastNameCol, emailCol);

        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(label, table);

        ((Group) scene.getRoot()).getChildren().addAll(vbox);

        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) throws IOException {


        MailAnalyzer mailAnalyzer = new MailAnalyzer();


        // Get file set from each directory
        File[] trainSpamFiles = mailAnalyzer.scan_directory(dataDirectoryPath + "\\train\\spam");
        File[] trainHamFiles = mailAnalyzer.scan_directory(dataDirectoryPath + "\\train\\ham");
        File[] trainHam2Files = mailAnalyzer.scan_directory(dataDirectoryPath + "\\train\\ham2");


        // Generate word frequency map for each set of files
        HashMap<String, Integer> trainSpamFrequencyMap = mailAnalyzer.build_frequency_map(trainSpamFiles);
        HashMap<String, Integer> trainHamFrequencyMap = mailAnalyzer.build_frequency_map(trainHamFiles);
        HashMap<String, Integer> trainHam2FrequencyMap = mailAnalyzer.build_frequency_map(trainHam2Files);
        // Merge ham and ham2 file sets
        trainHamFrequencyMap = mailAnalyzer.merge_frequency_maps(trainHamFrequencyMap, trainHam2FrequencyMap);


        // Generate word probability map from frequency maps
        HashMap<String, Double> Pr_WS = mailAnalyzer.build_word_probability_map(trainSpamFrequencyMap, trainSpamFiles.length);
        HashMap<String, Double> Pr_WH = mailAnalyzer.build_word_probability_map(trainHamFrequencyMap, trainHamFiles.length);


        // Generate map where key is a word and value is the probability that a file is spam given that it contains that key/word
        HashMap<String, Double> Pr_SW = mailAnalyzer.build_file_is_spam_probability_map(Pr_WS, Pr_WH);


        File[] testSpamFiles = mailAnalyzer.scan_directory(dataDirectoryPath + "\\test\\spam");
        File[] testHamFiles = mailAnalyzer.scan_directory(dataDirectoryPath + "\\test\\ham");
        ArrayList<TestFile> testSpamResults = mailAnalyzer.convert_to_TestFile_list(testSpamFiles, "Spam");
        ArrayList<TestFile> testHamResults = mailAnalyzer.convert_to_TestFile_list(testHamFiles, "Ham");

        System.out.println("Entering probability computation phase...");
        mailAnalyzer.compute_spam_probability(Pr_SW, testSpamResults);
        mailAnalyzer.compute_spam_probability(Pr_SW, testHamResults);



        for (int i = 0; i < testSpamResults.size(); i++)
        {
            testSpamResults.get(i).printInfo();
            System.out.println();
        }

        for (int i = 0; i < testHamResults.size(); i++)
        {
            testHamResults.get(i).printInfo();
            System.out.println();
        }


        Application.launch(args);
    }

}
