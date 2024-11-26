package de.fabian.fxstarter.assets;

import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.scene.control.*;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.Callable;

public class Localizer {
    private static final ObjectProperty<Locale> locale = new SimpleObjectProperty<>(getDefaultLocale());
    private static String bundleName;

    private Localizer() {}

    static {
        locale.addListener((observable, oldValue, newValue) -> Locale.setDefault(newValue));
    }

    public static List<Locale> getSupportedLocales() {
        return new ArrayList<>(Arrays.asList(Locale.ENGLISH, Locale.GERMAN));
    }

    public static Locale getDefaultLocale() {
        Locale sysDefault = Locale.getDefault();
        return getSupportedLocales().contains(sysDefault) ? sysDefault : Locale.ENGLISH;
    }

    public static void setLocale(Locale locale) {
        Localizer.locale.set(locale);
        Locale.setDefault(locale);
    }

    public static void setBundle(String bundle) {
        bundleName = bundle;
    }

    public static String getBundleName() {
        return bundleName;
    }

    public static ResourceBundle getBundle() {
        return ResourceBundle.getBundle(bundleName);
    }

    public static ObjectProperty<Locale> getLocaleProperty() {
        return locale;
    }

    public static Locale getLocale() {
        return locale.get();
    }

    public static String getString(String key, Object... args) {
        ResourceBundle bundle = ResourceBundle.getBundle(bundleName, getLocale());
        return MessageFormat.format(bundle.getString(key), args);
    }

    public static StringBinding createStringBinding(String key, Object... args) { // fixed args
        return Bindings.createStringBinding(() -> getString(key, args), locale);
    }

    public static StringBinding createStringBinding(Callable<String> func) { // recalculate args on every call
        return Bindings.createStringBinding(func, locale);
    }

    public static Labeled bind(Labeled toLabel, String key, Object... args) {
        toLabel.textProperty().bind(createStringBinding(key, args));
        return toLabel;
    }

    public static TableColumn bind(TableColumn toLabel, String key, Object... args) {
        toLabel.textProperty().bind(createStringBinding(key, args));
        return toLabel;
    }

    public static MenuItem bind(MenuItem toLabel, String key, Object... args) {
        toLabel.textProperty().bind(createStringBinding(key, args));
        return toLabel;
    }
}