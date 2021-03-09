package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 * The purpose of Controller is to control the events triggered in the run window
 */
public class Controller {
    @FXML
    private TableView tableView;
    @FXML
    private TableColumn fileColumn;
    @FXML
    private TableColumn actualClassColumn;
    @FXML
    private TableColumn spamProbabilityColumn;
    @FXML
    private Button trainDataBtn;
    @FXML
    private Button testDataBtn;
    @FXML
    private TextField trainDataPath;
    @FXML
    private TextField testDataPath;

    @FXML private TextField accuracyField;
    @FXML private TextField precisionField;

    WordCounter wordCounter = new WordCounter();

    /**
     * TrainData calls functions from WordCounter to parse the training folder and to calculate the probability
     * a file is spam
     * @param e - the event triggered in the run window
     * @throws IOException
     */
    public void TrainData(ActionEvent e) throws IOException {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File("."));
        File mainDirectory = directoryChooser.showDialog(null);

        String path = mainDirectory.getAbsolutePath();
        trainDataPath.setText(path);

        if (mainDirectory != null) {
            wordCounter.parseTrainingFolder(mainDirectory);
            wordCounter.probabiltyFileIsSpam();

            System.out.println(wordCounter.probabilitySpamWords);
        } else {
            System.exit(0);
        }
    }

    /**
     * TestData calls functions from WordCounter to parse the test folder and to calculate the probability
     * a file is spam
     * @param e - the event triggered in the run window
     * @throws IOException
     */
    public void TestData(ActionEvent e) throws IOException {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File("."));
        File mainDirectory = directoryChooser.showDialog(null);
        String path = mainDirectory.getAbsolutePath();
        testDataPath.setText(path);
        DecimalFormat df = new DecimalFormat("0.00000");
        if (mainDirectory != null) {
            parseTestingFolder(mainDirectory);
            wordCounter.accuracy = (wordCounter.numTruePositives + wordCounter.numTrueNegatives) / (double) wordCounter.numFiles;
            wordCounter.precision = (wordCounter.numTruePositives) / (wordCounter.numFalsePositives + wordCounter.numTrueNegatives);

            accuracyField.setText(df.format(wordCounter.accuracy));
            precisionField.setText(df.format(wordCounter.precision));

            fileColumn.setCellValueFactory(new PropertyValueFactory<>("filename"));
            actualClassColumn.setCellValueFactory(new PropertyValueFactory<>("actualClass"));
            spamProbabilityColumn.setCellValueFactory(new PropertyValueFactory<>("spamProbability"));
        } else {
            System.exit(0);
        }
    }

    /**
     * This function parses the test folder
     * @param file - File the test folder
     * @throws IOException
     */
    public void parseTestingFolder(File file) throws IOException {
        String fileName = file.getPath();
        if (file.isDirectory()) {
            File[] content = file.listFiles();
            for (File current: content) {
                parseTestingFolder(current);
            }
        } else {
            double probabilitySpam = 0.0;

            probabilitySpam = wordCounter.probabilitySpamFile(file);

            if (fileName.contains("ham")) {
                tableView.getItems().add(new TestFile(file.getName(), probabilitySpam, "ham"));
            } else if (fileName.contains("spam")) {
                tableView.getItems().add(new TestFile(file.getName(), probabilitySpam, "spam"));
            }
        }
    }
}
