package de.fabian.fxstarter;

import de.fabian.fxstarter.assets.*;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.*;

import java.io.*;
import java.net.URL;
import java.util.*;

public class SettingsController implements Initializable {
    @FXML
    private Label autosaveLbl;
    @FXML
    private ToggleButton autosaveSwitch;
    @FXML
    private Label languageLbl;
    @FXML
    private ComboBox<Language> languagePicker;
    @FXML
    private Label excelLbl;
    @FXML
    private TextField excelInput;
    @FXML
    private Button excelFileChoose;
    @FXML
    private Label settingLbl;
    @FXML
    private TextField settingsInput;
    @FXML
    private Button settingFileChoose;
    @FXML
    private Label productLbl;
    @FXML
    private TextField productInput;
    @FXML
    private Button productFileChoose;
    @FXML
    private Button save;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        upgradeToggleButton(autosaveSwitch);
        autosaveSwitch.setSelected((boolean)Settings.getOption("autosave"));
        addDynamicLanguageChange();
        setInputFilePaths();
        initLanguageBox();
    }

    @FXML
    private void toggleAutosave() {
        Settings.set("autosave", autosaveSwitch.selectedProperty().getValue());
    }

    @FXML
    private void setLanguage() {
        Language language = languagePicker.getSelectionModel().getSelectedItem();
        Localizer.setLocale(language.getLocale());
        Settings.set("language", language);
    }

    @FXML
    private void saveAndClose() {
        SettingsController.saveSettings();
        ((Stage)save.getScene().getWindow()).close();
    }

    @FXML
    private void fileChooserExcel() {
        openFileChooser(excelInput, "excelFilePath");
    }

    @FXML
    private void fileChooserSettings() {
        openFileChooser(settingsInput, "settingsFilePath");
    }

    @FXML
    private void fileChooserProducts() {
        openFileChooser(productInput, "productFilePath");
    }

    public static void saveSettings() {
        try {
            Settings.saveSettings();
        } catch (IOException e) { // Fehler beim Speichern der Einstellungen
            MainController.error(new IOException(Localizer.getString("err.file.IOE")));
        }
    }

    private void setInputFilePaths() {
        excelInput.setText((String)Settings.getOption("excelFilePath"));
        settingsInput.setText((String)Settings.getOption("settingsFilePath"));
        productInput.setText((String)Settings.getOption("productFilePath"));
    }

    private void openFileChooser(TextField textField, String setting) {
        Setting<String> set = (Setting<String>) Settings.getSetting(setting);
        if (set != null) openFileChooser(textField, set);
    }

    private void openFileChooser(TextField textField, Setting<String> setting) {
        String initFilePath = setting.getOption();
        initFilePath = initFilePath.substring(0, initFilePath.lastIndexOf('\\'));
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(initFilePath));
        File selectedFile = fileChooser.showOpenDialog(textField.getScene().getWindow());
        if (selectedFile != null) {
            String filePath = selectedFile.getAbsolutePath();
            setting.setOption(filePath);
            textField.setText(filePath);
        }
    }

    private void upgradeToggleButton(ToggleButton toggleButton) {
        Circle thumb = new Circle(10, Paint.valueOf("white"));
        thumb.getStyleClass().add("thumb");
        Pane thumbContainer = new Pane(thumb);
        toggleButton.setGraphic(thumbContainer);
    }

    private void addDynamicLanguageChange() {
        Map<Labeled, String> labeledMap = new HashMap<>(Map.of(
                autosaveLbl, "autosave",
                languageLbl, "language",
                excelLbl, "excel.file",
                settingLbl, "setting.file",
                productLbl, "product.file",
                excelFileChoose, "browse",
                settingFileChoose, "browse",
                productFileChoose, "browse",
                save, "save"
        ));

        labeledMap.forEach(Localizer::bind);
        Localizer.setLocale(((Language)Settings.getOption("language")).getLocale());
    }

    private void initLanguageBox() {
        ObservableList<Language> options = FXCollections.observableArrayList(
                Language.GERMAN,
                Language.ENGLISH
        );
        languagePicker.setItems(options);
        //setze die ComboBox auf die gespeicherte Sprache
        languagePicker.getSelectionModel().select((Language) Settings.getOption("language"));
    }
}
