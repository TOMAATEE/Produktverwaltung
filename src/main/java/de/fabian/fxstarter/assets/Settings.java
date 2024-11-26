package de.fabian.fxstarter.assets;

import java.io.IOException;
import java.util.*;

public class Settings {
    private static String settingsFilePath = System.getProperty("user.home") + "/Produktverwaltung/settings.ser";
    private static final FileManager<Setting> fileManager = new FileManager<>(Setting.class);

    private Settings() {}

    public static void saveSettings() throws IOException {
        fileManager.saveToFile(settingsFilePath, fileManager.getList());
    }

    public static boolean loadSettings() {
        return fileManager.readFromFile(settingsFilePath);
    }

    public static Setting<?> getSetting(String name) {
        for (Setting<?> s : fileManager.getList()) {
            if (s.getName().equalsIgnoreCase(name)) return s;
        }
        return null;
    }

    public static Object getOption(String name) {
            return Objects.requireNonNull(getSetting(name)).getOption();
    }

    public static void set(String name, Object option) {
        set(new Setting<>(name, option));
    }

    public static void set(Setting<?> setting) {
        List<Setting> list = fileManager.getList();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getName().equals(setting.getName())) {
                list.set(i, setting);
                break;
            }
        }
    }

    public static void add(String name, Object option) {
        add(new Setting<>(name, option));
    }

    public static void add(Setting<?> setting) {
        fileManager.add(setting);
    }

    public static String getFilePath() {
        return settingsFilePath;
    }

    public static void setFilePath(String settingsFilePath) {
        Settings.settingsFilePath = settingsFilePath;
    }
}
