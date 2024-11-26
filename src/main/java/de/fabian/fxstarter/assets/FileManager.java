package de.fabian.fxstarter.assets;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.*;
import java.lang.reflect.Field;
import java.time.*;
import java.util.*;

public class FileManager<T extends Serializable> {

    private List<T> list;
    private final Class<T> type;

    public FileManager(Class<T> type) {
        this.type = type;
        list = new ArrayList<>();
    }

    public FileManager(Class<T> type, String filePath) {
        this.type = type;
        readFromFile(filePath);
    }

    public void add(T object) {
        list.add(object);
    }

    public void saveToFile(String filePath, List<T> list) throws IOException {
        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath))) {
            out.writeObject(list);
        }
    }

    public void saveToFile(String filePath) throws IOException {
        saveToFile(filePath, list);
    }

    public boolean readFromFile(String filePath) {
        try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath))) {
            list = (List<T>) in.readObject();
            return true;
        } catch (Exception e) {
            list = new ArrayList<>();
            return false;
        }
    }

    public void exportToExcel(List<T> objects, String filePath) throws IllegalAccessException, IOException {
        exportToExcel(objects, filePath, null);
    }

    public void exportToExcel(List<T> objects, String filePath, String dateFormat) throws IllegalAccessException, IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(type.getSimpleName() + "s");

        CellStyle dateCellStyle = workbook.createCellStyle();
        if (dateFormat != null) dateCellStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat(dateFormat)); // Anpassbares Datumsformat

        // Header-Row erstellen
        Row headerRow = sheet.createRow(0);
        Field[] fields = type.getDeclaredFields();

        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);  // Zugriff auf private Felder
            headerRow.createCell(i).setCellValue(fields[i].getName());
            sheet.autoSizeColumn(i);
        }

        // Datenzeilen ausfüllen
        int rowNum = 1;
        for (T obj : objects) {
            Row row = sheet.createRow(rowNum++);
            for (int i = 0; i < fields.length; i++) {
                Cell cell = row.createCell(i);
                Object value = fields[i].get(obj);

                if (value == null) value = "-";

                if (dateFormat != null) formatCellSwitch(cell, value, dateCellStyle);
                else formatCellSwitch(cell, value);
            }
        }

        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        }
    }

    private void formatCellSwitch(Cell cell, Object value) {
        formatCellSwitch(cell, value, null);
    }

    private void formatCellSwitch(Cell cell, Object value, CellStyle dateCellStyle) {
        switch (value) {
            case LocalDate localDate -> {
                Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                cell.setCellValue(date);
                if (dateCellStyle != null) cell.setCellStyle(dateCellStyle);
            }
            case Integer integer -> cell.setCellValue(integer);
            case Double d -> cell.setCellValue(d);
            case null, default -> cell.setCellValue("" + value);
        }
    }

    public void importFromExcel(String filePath) throws ReflectiveOperationException, IOException {
        List<T> objects = new ArrayList<>();

        try (FileInputStream fileIn = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fileIn)) {

            Sheet sheet = workbook.getSheetAt(0);
            Field[] fields = type.getDeclaredFields();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                T obj = type.getDeclaredConstructor().newInstance();

                for (int j = 0; j < fields.length; j++) {
                    fields[j].setAccessible(true);
                    Cell cell = row.getCell(j);
                    Field field = fields[j];

                    try {
                        if (field.getType() == Date.class) {
                            field.set(obj, cell.getDateCellValue());
                        } else if (field.getType() == LocalDate.class) {
                            field.set(obj, cell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                        } else if (field.getType() == LocalDateTime.class) {
                            field.set(obj, cell.getLocalDateTimeCellValue());
                        } else if (field.getType() == Integer.class || field.getType() == int.class) {
                            field.set(obj, (int) cell.getNumericCellValue());
                        } else if (field.getType() == Double.class || field.getType() == double.class) {
                            field.set(obj, cell.getNumericCellValue());
                        } else if (field.getType() == Boolean.class || field.getType() == boolean.class) {
                            field.set(obj, cell.getBooleanCellValue());
                        } else {
                            field.set(obj, cell.getStringCellValue());
                        }
                    } catch (Exception e) {
                        field.set(obj, null);
                    }
                }
                objects.add(obj);
            }
        }
        list = objects;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}