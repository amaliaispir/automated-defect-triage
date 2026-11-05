package proiect_triaj_defecte;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Clasa IntrareIstoric reprezinta o singura modificare in istoricul unui defect.
 * Face parte dintr-o relatie de COMPOZITIE cu Defect - intrarile de istoric
 * nu exista in afara unui defect (se nasc si mor odata cu defectul lor).
 */
public class IntrareIstoric {

    private int id;
    private LocalDateTime dataModificare;
    private String actiune;        // ex: "Status actualizat", "Defect preluat"
    private String efectuatDe;     // numele utilizatorului care a facut modificarea
    private String valoareVeche;   // valoarea de dinainte de modificare
    private String valoareNoua;    // valoarea dupa modificare

    public IntrareIstoric() {
        this.id = 0;
        this.dataModificare = LocalDateTime.now();
        this.actiune = "";
        this.efectuatDe = "";
        this.valoareVeche = "";
        this.valoareNoua = "";
    }

    public IntrareIstoric(int id, String actiune, String efectuatDe,
                          String valoareVeche, String valoareNoua) {
        this.id = id;
        this.dataModificare = LocalDateTime.now();
        this.actiune = actiune;
        this.efectuatDe = efectuatDe;
        this.valoareVeche = valoareVeche;
        this.valoareNoua = valoareNoua;
    }

    /**
     * Formateaza intrarea intr-un text pentru afisare in raport.
     *
     * @return un string formatat cu detaliile intrarii
     */
    public String afiseazaIntrare() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(dataModificare.format(formatter)).append("] ");
        sb.append(efectuatDe).append(" - ");
        sb.append(actiune);
        if (valoareVeche != null && !valoareVeche.isEmpty()
                && valoareNoua != null && !valoareNoua.isEmpty()) {
            sb.append(" (").append(valoareVeche).append(" -> ").append(valoareNoua).append(")");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return afiseazaIntrare();
    }

    // Getters si setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getDataModificare() {
        return dataModificare;
    }

    public void setDataModificare(LocalDateTime dataModificare) {
        this.dataModificare = dataModificare;
    }

    public String getActiune() {
        return actiune;
    }

    public void setActiune(String actiune) {
        this.actiune = actiune;
    }

    public String getEfectuatDe() {
        return efectuatDe;
    }

    public void setEfectuatDe(String efectuatDe) {
        this.efectuatDe = efectuatDe;
    }

    public String getValoareVeche() {
        return valoareVeche;
    }

    public void setValoareVeche(String valoareVeche) {
        this.valoareVeche = valoareVeche;
    }

    public String getValoareNoua() {
        return valoareNoua;
    }

    public void setValoareNoua(String valoareNoua) {
        this.valoareNoua = valoareNoua;
    }
}