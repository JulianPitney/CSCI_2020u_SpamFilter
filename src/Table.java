import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;



public class Table extends Application {

    private TableView<TestFile> table = new TableView<>();

    // Launch javaFX thread
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {

        // Getting a bunch of javaFX fields and whatever ready
        Scene scene = new Scene(new Group());
        stage.setTitle("File Filter");
        stage.setWidth(800);
        stage.setHeight(800);

        final Label label = new Label("Results");
        label.setFont(new Font("Arial", 20));

        final HBox hb = new HBox();

        final TextField dataPath = new TextField();
        dataPath.setMinWidth(200);
        dataPath.setPromptText("Enter Data Directory Path");
        dataPath.setEditable(true);

        final TextField percentCorrect = new TextField();
        percentCorrect.setMinWidth(200);
        percentCorrect.setPromptText("%Correct Files");
        percentCorrect.setEditable(false);


        // Button that sets dataDirectoryPath and then runs the probability analysis on the data it finds.
        // Also dumps the results into the table.
        final Button setPathButton = new Button("Set Path and Analyze!");
        setPathButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override public void handle(ActionEvent e) {

                String dataDirectoryPath = dataPath.getText();

                MailAnalyzer mailAnalyzer = new MailAnalyzer();
                ArrayList<TestFile> analysisResults = null;

                try
                {
                    // Running analysis on files
                    analysisResults = mailAnalyzer.analyze(dataDirectoryPath);
                }
                catch (IOException e1)
                {
                    System.out.println("Probably an invalid path (If you're running windows make sure you use \\\\" +
                            "instead of \\, since it's an escape character and this thing isn't screening path inputs)");
                }


                // Calculating % of files whose spam probability was greater than 50% in the correct direction, and then displaying to textfield
                percentCorrect.setText(String.valueOf(mailAnalyzer.calculate_percent_correct(analysisResults)));
                dataPath.clear();

                // Giving results of analysis to table
                final ObservableList<TestFile> data = FXCollections.observableArrayList(analysisResults);
                table.setEditable(true);


                // Everything below here is just more javaFX setting up stuff or giving references to other stuff
                TableColumn fileNameCol = new TableColumn("File");
                fileNameCol.setMinWidth(300);
                fileNameCol.setCellValueFactory(new PropertyValueFactory<TestFile, String>("fileName"));

                TableColumn actualClassCol = new TableColumn("Actual Class");
                actualClassCol.setMinWidth(100);
                actualClassCol.setCellValueFactory(new PropertyValueFactory<TestFile, String>("actualClass"));

                TableColumn spamProbCol = new TableColumn("Spam Probability");
                spamProbCol.setMinWidth(340);
                spamProbCol.setCellValueFactory(new PropertyValueFactory<TestFile, String>("spamProbability"));

                table.setItems(data);
                table.getColumns().addAll(fileNameCol, actualClassCol, spamProbCol);
            }
        });

        hb.getChildren().addAll(percentCorrect, dataPath, setPathButton);
        hb.setSpacing(3);

        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(label, table, hb);

        ((Group) scene.getRoot()).getChildren().addAll(vbox);

        stage.setScene(scene);
        stage.show();
    }
}
