package de.fabian.fxstarter.assets;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class Produkt implements Serializable {
    private String name;
    private String beschreibung;
    private int menge;
    private double preis;
    private LocalDate verfallsdatum;
    private final LocalDate imBestandSeit;

    public Produkt() {imBestandSeit = LocalDate.now();}

    public Produkt(String name, String beschreibung, int menge, double preis) {
        this(name, beschreibung, menge, preis, null);
    }

    public Produkt(String name, String beschreibung, int menge, double preis, LocalDate verfallsdatum) {
        this(name, beschreibung, menge, preis, verfallsdatum, LocalDate.now());
    }

    public Produkt(String name, String beschreibung, int menge, double preis, LocalDate verfallsdatum, LocalDate imBestandSeit) {
        this.name = name;
        this.beschreibung = beschreibung;
        this.menge = menge;
        this.preis = preis;
        this.verfallsdatum = verfallsdatum;
        this.imBestandSeit = imBestandSeit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public int getMenge() {
        return menge;
    }

    public void setMenge(int menge) {
        this.menge = menge;
    }

    public double getPreis() {
        return preis;
    }

    public void setPreis(double preis) {
        this.preis = preis;
    }

    public LocalDate getVerfallsdatum() {
        return verfallsdatum;
    }

    public void setVerfallsdatum(LocalDate verfallsdatum) {
        this.verfallsdatum = verfallsdatum;
    }

    public LocalDate getImBestandSeit() {
        return imBestandSeit;
    }

    public String get(String attribute) {
        return switch (attribute.toLowerCase()) {
            case "name" -> getName();
            case "beschreibung" -> getBeschreibung();
            case "menge" -> "" + getMenge();
            case "preis" -> "" + getPreis();
            case "bestandsdatum" -> "" + getImBestandSeit();
            case "verfallsdatum" -> "" + verfallsdatum;
            default -> null;
        };
    }

    public Object[] getAll() {
        return new Object[]{name, beschreibung, menge, preis, verfallsdatum, imBestandSeit};
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(getClass().getSimpleName()).append("{");
        sb.append("name='").append(name);
        sb.append("', beschreibung='").append(beschreibung);
        sb.append("', menge=").append(menge);
        sb.append(", preis=").append(preis);
        sb.append(", Verfallsdatum=").append(verfallsdatum);
        sb.append(", imBestandSeit=").append(imBestandSeit).append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Produkt produkt = (Produkt) o;
        return menge == produkt.menge && Double.compare(preis, produkt.preis) == 0 && Objects.equals(name, produkt.name) && Objects.equals(beschreibung, produkt.beschreibung) && Objects.equals(verfallsdatum, produkt.verfallsdatum) && Objects.equals(imBestandSeit, produkt.imBestandSeit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, beschreibung, menge, preis, verfallsdatum, imBestandSeit);
    }
}
