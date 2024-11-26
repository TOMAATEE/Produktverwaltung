package de.fabian.fxstarter.assets;

import java.io.IOException;
import java.util.List;

public class Produktverwaltung extends FileManager<Produkt> {
    private String productFilePath = System.getProperty("user.home") + "/Produktverwaltung/data.ser";
    private String excelFilePath = System.getProperty("user.home") + "/Produktverwaltung/data.xlsx";

    public Produktverwaltung(Class<Produkt> type) {
        super(type);
    }

    public Produktverwaltung(Class<Produkt> type, String filePath) {
        super(type, filePath);
    }

    public void exportToExcel(String dateFormat) throws IllegalAccessException, IOException {
        super.exportToExcel(getList(), excelFilePath, dateFormat);
    }

    public void importFromExcel() throws ReflectiveOperationException, IOException {
        super.importFromExcel(excelFilePath);
    }

    public void readFromFile() {
        super.readFromFile(productFilePath);
    }

    public void saveToFile() throws IOException {
        super.saveToFile(productFilePath);
    }

    public void remove(Produkt produkt) {
        getList().remove(produkt);
    }

    public List<Produkt> getProdukte() {
        return getList();
    }

    public String getProductFilePath() {
        return productFilePath;
    }

    public void setProductFilePath(String productFilePath) {
        this.productFilePath = productFilePath;
    }

    public String getExcelFilePath() {
        return excelFilePath;
    }

    public void setExcelFilePath(String excelFilePath) {
        this.excelFilePath = excelFilePath;
    }
}
