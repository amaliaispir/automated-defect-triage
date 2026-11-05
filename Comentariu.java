package proiect_triaj_defecte;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Clasa Comentariu reprezinta un mesaj postat pe un defect.
 * Face parte dintr-o relatie de COMPOZITIE cu Defect - comentariile
 * nu exista in afara unui defect.
 */
public class Comentariu {

    private int id;
    private String text;
    private String autor;
    private LocalDateTime dataPostarii;

    public Comentariu() {
        this.id = 0;
        this.text = "";
        this.autor = "";
        this.dataPostarii = LocalDateTime.now();
    }

    public Comentariu(int id, String text, String autor) {
        this.id = id;
        this.text = text;
        this.autor = autor;
        this.dataPostarii = LocalDateTime.now();
    }

    public String afiseazaComentariu() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        return "[" + dataPostarii.format(formatter) + "] " + autor + ": " + text;
    }

    @Override
    public String toString() {
        return afiseazaComentariu();
    }

    // Getters si setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public LocalDateTime getDataPostarii() {
        return dataPostarii;
    }

    public void setDataPostarii(LocalDateTime dataPostarii) {
        this.dataPostarii = dataPostarii;
    }
}