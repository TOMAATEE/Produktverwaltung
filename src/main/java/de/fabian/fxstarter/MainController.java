package de.fabian.fxstarter;

import de.fabian.fxstarter.assets.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.*;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MainController implements Initializable {
    private final Produktverwaltung verwaltung = new Produktverwaltung(Produkt.class);
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
    @FXML
    private Menu menuFile;
    @FXML
    private MenuItem fileSettings;
    @FXML
    private MenuItem fileClose;
    @FXML
    private Menu menuHelp;
    @FXML
    private MenuItem helpAbout;
    @FXML
    private Label headerLbl;
    @FXML
    private Label stateLbl;
    @FXML
    private Label nameLbl;
    @FXML
    private Label descLbl;
    @FXML
    private Label amountLbl;
    @FXML
    private Label priceLbl;
    @FXML
    private Label stockLbl;
    @FXML
    private Label expiryLbl;
    @FXML
    private Label excelLbl;
    @FXML
    private Button resetBtn;
    @FXML
    private Button editBtn;
    @FXML
    private Button saveBtn;
    @FXML
    private Button deleteBtn;
    @FXML
    private Button excelImportBtn;
    @FXML
    private Button excelExportBtn;
    @FXML
    private TableView<Produkt> product_table;
    @FXML
    private TableColumn<Produkt, String> table_name;
    @FXML
    private TableColumn<Produkt, String> table_desc;
    @FXML
    private TableColumn<Produkt, Double> table_price;
    @FXML
    private TableColumn<Produkt, Integer> table_amount;
    @FXML
    private TableColumn<Produkt, LocalDate> table_expiry;
    @FXML
    private TableColumn<Produkt, LocalDate> table_inv_date;
    @FXML
    private TextField name;
    @FXML
    private TextArea beschreibung;
    @FXML
    private Spinner<Integer> menge;
    @FXML
    private Spinner<Double> preis;
    @FXML
    private DatePicker imBestandSeit;
    @FXML
    private DatePicker verfallsdatum;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) { // "Constructor"
        menge.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 2_147_483_647));
        preis.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 2_147_483_647));
        product_table.setPlaceholder(new Label());
        if (!Settings.loadSettings()) initSettings();
        addDynamicLanguageChange();
        verwaltung.readFromFile();
        setLanguage();
    }

    private void setLanguage() {
        Localizer.setLocale(((Language)Settings.getOption("language")).getLocale());
    }

    @FXML
    private void clear() { //alle Eingabefelder zurücksetzen
        name.clear();
        beschreibung.clear();
        menge.getValueFactory().setValue(0);
        preis.getValueFactory().setValue(0.0);
        imBestandSeit.setValue(null);
        verfallsdatum.setValue(null);
        product_table.getSelectionModel().clearSelection();
        Localizer.bind(stateLbl, "new");
    }

    @FXML
    private void delete() {
        verwaltung.remove(product_table.getSelectionModel().getSelectedItem());
        autoSaveToFile(verwaltung.getProductFilePath());
        show();
    }

    @FXML
    private void save() {
        //setze das Datum, heutiges Datum, wenn kein Datum angegeben wurde
        LocalDate bestand = imBestandSeit.getValue();
        if (bestand == null) bestand = LocalDate.now();

        Produkt p = new Produkt(name.getText(), beschreibung.getText(), menge.getValue(), preis.getValue(), verfallsdatum.getValue(), bestand);

        verwaltung.add(p);
        autoSaveToFile(verwaltung.getProductFilePath());
        if (stateLbl.getText().equals(Localizer.getString("edit"))) delete(); // lösche den alten Eintrag, wenn ein Produkt bearbeitet wird
        else show();
        clear();
    }

    @FXML
    private void edit() { // produktdaten werden in die Eingabefelder geladen
        Localizer.bind(stateLbl, "edit");
        try {
            Produkt p = product_table.getSelectionModel().getSelectedItem();
            name.setText(p.getName());
            beschreibung.setText(p.getBeschreibung());
            menge.getValueFactory().setValue(p.getMenge());
            preis.getValueFactory().setValue(p.getPreis());
            imBestandSeit.setValue(p.getImBestandSeit());
            verfallsdatum.setValue(p.getVerfallsdatum());
        } catch (NullPointerException e) {
            error(new NullPointerException(Localizer.getString("err.edit")));
        }
    }

    @FXML
    private void exportToExcel() {
        try {
            verwaltung.exportToExcel(Localizer.getString("date.format"));
        } catch (IllegalAccessException e) { // Excel Tabelle blockiert Java Zugriff
            error(new IllegalAccessException(Localizer.getString("err.excel.IAE")));
        } catch (IOException e) { // Kein Schreibzugriff
            error(new IOException(Localizer.getString("err.file.IOE")));
        }
    }

    @FXML
    private void importFromExcel() {
        try {
            verwaltung.importFromExcel();
            saveToFile(verwaltung.getProductFilePath());
        } catch (IOException e) { // Datei kann nicht geöffnet werden oder ist nicht vorhanden
            error(new IOException(Localizer.getString("err.excel.IOE")));
        } catch (ReflectiveOperationException e) { // Fehler beim Import aufgetreten. Excel Datei und Importformat stimmen nicht überein
            error(new ReflectiveOperationException(Localizer.getString("err.excel.ROE")));
        }
        show();
    }

    @FXML
    private void openSettings() {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("settings-view.fxml")));
            Stage stage = new Stage();
            stage.setTitle(Localizer.getString("settings"));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());
            stage.setScene(scene);
            stage.show();
        }
        catch (IOException e) { // Fehler beim Erstellen der Fehlermeldung
            error(new IOException(Localizer.getString("err.settings")));
        }
    }

    private void show() { // befülle und formatiere die Tabelle
        product_table.setItems(FXCollections.observableList(verwaltung.getProdukte()));
        produktTableIntegerFormatter(table_amount);
        produktTableCurrencyFormatter(table_price);
        produktTableDateFormatter(table_expiry, DateTimeFormatter.ofPattern(Localizer.getString("date.format")));
        produktTableDateFormatter(table_inv_date, DateTimeFormatter.ofPattern(Localizer.getString("date.format")));
    }

    public static void error(Exception exception) {
        try {
            ErrorController.setException(exception);
            Parent root = FXMLLoader.load(Objects.requireNonNull(MainController.class.getResource("error-view.fxml")));
            Stage stage = new Stage();
            stage.setTitle("Error");
            stage.setScene(new Scene(root));
            stage.show();
        }
        catch (IOException e) { // Fehler beim Erstellen der Fehlermeldung
            e.printStackTrace();
        }
    }

    private void initSettings() { // Standard Settings, falls die Settings-Datei gelöscht wurde oder das Programm das erste Mal gestartet wird
        Settings.add("autosave", false);
        Settings.add("language", Language.getLanguage(Locale.of(Locale.getDefault().getLanguage())));
        Settings.add("excelFilePath", System.getProperty("user.home") + "\\Produktverwaltung\\data.xlsx");
        Settings.add("settingsFilePath", System.getProperty("user.home") + "\\Produktverwaltung\\settings.ser");
        Settings.add("productFilePath", System.getProperty("user.home") + "\\Produktverwaltung\\data.ser");
        SettingsController.saveSettings();
    }

    private void addDynamicLanguageChange() {
        Map<Labeled, String> labeledMap = new HashMap<>(Map.of(
                headerLbl, "title",
                stateLbl, "new",
                nameLbl, "name",
                descLbl, "desc",
                amountLbl, "amount",
                priceLbl, "price",
                stockLbl, "stock",
                expiryLbl, "expiry",
                excelLbl, "excel",
                resetBtn, "reset"
                ));
        labeledMap.put(editBtn, "btn.edit");
        labeledMap.put(saveBtn, "save");
        labeledMap.put(deleteBtn, "delete");
        labeledMap.put(excelImportBtn, "import");
        labeledMap.put(excelExportBtn, "export");
        labeledMap.put((Label)product_table.getPlaceholder(), "table.empty");

        Map<TableColumn, String> tableColumnMap = Map.of(
                table_name, "name",
                table_desc, "desc",
                table_amount, "amount",
                table_price, "price",
                table_inv_date, "stock",
                table_expiry, "expiry"
                );
        Map<MenuItem, String> menuMap = new HashMap<>(Map.of(
                menuFile, "file",
                fileSettings, "settings",
                fileClose, "close",
                menuHelp, "help",
                helpAbout, "about"));

        labeledMap.forEach(Localizer::bind);
        tableColumnMap.forEach(Localizer::bind);
        menuMap.forEach(Localizer::bind);
        Localizer.setLocale(((Language)Settings.getOption("language")).getLocale());
    }

    private void produktTableDateFormatter(TableColumn<Produkt, LocalDate> column, DateTimeFormatter dateTimeFormatter) {
        column.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty) { // Zeilen ohne Inhalt
                    setText("");
                } else if (date == null) { // unverderbliche Produkte
                    setText("-");
                } else {
                    setText(dateTimeFormatter.format(date));
                }
            }
        });
    }

    private void produktTableIntegerFormatter(TableColumn<Produkt, Integer> column) {
        column.setCellFactory(col -> {
            TableCell<Produkt, Integer> cell = new TableCell<>(){
                @Override
                public void updateItem(Integer number, boolean empty) {
                    super.updateItem(number, empty);
                    if (!empty) {
                        setText("" + number);
                    }
                }
            };
            cell.setAlignment(Pos.TOP_RIGHT);

            return cell;
        });
    }

    private void produktTableCurrencyFormatter(TableColumn<Produkt, Double> column) {
        column.setCellFactory(col -> {
            TableCell<Produkt, Double> cell = new TableCell<>(){
                @Override
                public void updateItem(Double number, boolean empty) {
                    super.updateItem(number, empty);
                    if (!empty) {
                        setText(currencyFormatter.format(number));
                    }
                }
            };
            cell.setAlignment(Pos.TOP_RIGHT);

            return cell;
        });
    }

    private void autoSaveToFile(String filePath) {
        if ((boolean)Settings.getOption("autosave"))saveToFile(filePath);
    }

    private void saveToFile(String filePath) {
        try {
            verwaltung.saveToFile(filePath);
        } catch (IOException e) { // Kein Schreibzugriff
            error(new IOException(Localizer.getString("err.file.IOE")));
        }
    }

    public void closeApp() {
        Platform.exit();
    }
}