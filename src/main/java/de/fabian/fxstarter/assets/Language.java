package de.fabian.fxstarter.assets;

import java.util.Locale;

public enum Language {
    ENGLISH(Locale.of("en")),
    FRENCH(Locale.of("fr")),
    GERMAN(Locale.of("de")),
    ITALIAN(Locale.of("it")),
    CHINESE(Locale.of("zh")),
    JAPANESE(Locale.of("ja")),
    KOREAN(Locale.of("ko")),
    SPANISH(Locale.of("es")),
    RUSSIAN(Locale.of("ru")),
    PORTUGUESE(Locale.of("pt")),
    ARABIC(Locale.of("ar")),
    DUTCH(Locale.of("nl")),
    SWEDISH(Locale.of("sv")),
    NORWEGIAN(Locale.of("no")),
    POLISH(Locale.of("pl")),
    DANISH(Locale.of("da")),
    FINNISH(Locale.of("fi")),
    TURKISH(Locale.of("tr")),
    GREEK(Locale.of("el")),
    HEBREW(Locale.of("he")),
    HINDI(Locale.of("hi")),
    VIETNAMESE(Locale.of("vi")),
    THAI(Locale.of("th")),
    CZECH(Locale.of("cs")),
    HUNGARIAN(Locale.of("hu")),
    SLOVAK(Locale.of("sk")),
    SLOVENIAN(Locale.of("sl")),
    UKRAINIAN(Locale.of("uk")),
    INDONESIAN(Locale.of("id")),
    FILIPINO(Locale.of("fil"));

    private final String language;
    private final Locale locale;

    Language(Locale locale) {
        this.language = locale.getDisplayLanguage(locale);
        this.locale = locale;
    }

    public Locale getLocale() {
        return locale;
    }

    public static Language getLanguage(Locale locale) {
        for (Language language : Language.values()) {
            if (language.getLocale().equals(locale)) {
                return language;
            }
        }
        return null; // Keine passende Sprache gefunden
    }

    @Override
    public String toString() {
        return language;
    }
}
