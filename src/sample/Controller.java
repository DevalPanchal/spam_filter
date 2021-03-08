package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.io.IOException;

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

    WordCounter wordCounter = new WordCounter();

    public void TrainData(ActionEvent e) throws IOException {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File("."));
        File mainDirectory = directoryChooser.showDialog(null);
        if (mainDirectory != null) {
            String path = mainDirectory.getAbsolutePath();
            trainDataPath.setText(path);

            wordCounter.parseTrainingFolder(mainDirectory);
            wordCounter.probabiltyFileIsSpam();

            System.out.println(wordCounter.probabilitySpamWords);
        } else {
            System.out.println("Directory not valid!");
        }
    }

    public void TestData(ActionEvent e) throws IOException {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File("."));
        File mainDirectory = directoryChooser.showDialog(null);

        if (mainDirectory != null) {
            String path = mainDirectory.getAbsolutePath();
            testDataPath.setText(path);

            wordCounter.parseTestingFolder(mainDirectory);


        }
    }
}
