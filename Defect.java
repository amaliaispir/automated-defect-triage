package proiect_triaj_defecte;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Clasa Defect reprezinta un defect raportat in sistem.
 *
 * Relatii:
 *   - COMPOZITIE cu IntrareIstoric (istoricul nu exista fara defect)
 *   - COMPOZITIE cu Comentariu (comentariile nu exista fara defect)
 *   - ASOCIATIE cu Raportor (defectul are un raportor)
 *   - ASOCIATIE cu Inginer (defectul poate fi asignat unui inginer)
 */
public class Defect {

    private int id;
    private String titlu;
    private String descriere;
    private String modReproducere;
    private String componentaAfectata;   // ex: "RobotSudura", "BandaTransportoare", "PresaCaroserie"
    private int numarLinie;            
    private String codUtilaj;           
    private Severitate severitate;
    private Prioritate prioritate;
    private Status status;
    private LocalDateTime dataRaportarii;
    private LocalDateTime dataRezolvarii;

    // asociatii
    private Raportor raportor;
    private Inginer inginerAsignat;

    // compozitii - listele se nasc odata cu defectul
    private List<IntrareIstoric> istoric;
    private List<Comentariu> comentarii;

    public Defect() {
        this.id = 0;
        this.titlu = "";
        this.descriere = "";
        this.modReproducere = "";
        this.componentaAfectata = "Necategorizat";
        this.numarLinie = 0;
        this.codUtilaj = "";
        this.severitate = Severitate.MINORA;
        this.prioritate = Prioritate.MEDIE;
        this.status = Status.NOU;
        this.dataRaportarii = LocalDateTime.now();
        this.dataRezolvarii = null;  // nu e rezolvat la creare
        this.raportor = null;
        this.inginerAsignat = null;
        this.istoric = new ArrayList<>();
        this.comentarii = new ArrayList<>();

        // adauga prima intrare in istoric: crearea defectului
        IntrareIstoric intrareInitiala = new IntrareIstoric();
        intrareInitiala.setActiune("Defect creat");
        intrareInitiala.setEfectuatDe("Sistem");
        intrareInitiala.setValoareNoua(Status.NOU.toString());
        this.istoric.add(intrareInitiala);
    }

    public Defect(int id, String titlu, String descriere, String modReproducere) {
        this();  // apeleaza constructorul implicit pentru initializarea listelor
        this.id = id;
        this.titlu = titlu;
        this.descriere = descriere;
        this.modReproducere = modReproducere;
    }

    /**
     * Actualizeaza statusul defectului si inregistreaza modificarea in istoric.
     *
     * @param statusNou noul status
     * @param efectuatDe numele celui care face modificarea
     */
    public void actualizeazaStatus(Status statusNou, String efectuatDe) {
        Status statusVechi = this.status;
        this.status = statusNou;

        IntrareIstoric intrare = new IntrareIstoric();
        intrare.setDataModificare(LocalDateTime.now());
        intrare.setActiune("Status modificat");
        intrare.setEfectuatDe(efectuatDe);
        intrare.setValoareVeche(statusVechi.toString());
        intrare.setValoareNoua(statusNou.toString());
        this.istoric.add(intrare);

        // daca s-a marcat ca rezolvat, retine si data rezolvarii
        if (statusNou == Status.REZOLVAT && this.dataRezolvarii == null) {
            this.dataRezolvarii = LocalDateTime.now();
        }
    }

    /**
     * Adauga o intrare in istoricul defectului.
     *
     * @param intrare intrarea de adaugat
     */
    public void adaugaIntrareIstoric(IntrareIstoric intrare) {
        if (intrare != null) {
            this.istoric.add(intrare);
        }
    }

    /**
     * Calculeaza timpul total de rezolvare a defectului, in minute.
     * Daca defectul nu este inca rezolvat, returneaza -1.
     *
     * @return numarul de minute scurse intre raportare si rezolvare, sau -1
     */
    public long calculeazaTimpRezolvare() {
        if (dataRezolvarii == null) {
            return -1;
        }
        Duration durata = Duration.between(dataRaportarii, dataRezolvarii);
        return durata.toMinutes();
    }

    /**
     * Verifica daca defectul este rezolvat sau inchis.
     *
     * @return true daca statusul este REZOLVAT sau INCHIS
     */
    public boolean esteRezolvat() {
        return status == Status.REZOLVAT || status == Status.INCHIS;
    }

    public void afisareDetalii() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        System.out.println("=== Defect #" + id + " ===");
        System.out.println("Titlu: " + titlu);
        System.out.println("Descriere: " + descriere);
        System.out.println("Linia de productie: " + numarLinie);
        System.out.println("Cod utilaj: " + codUtilaj);
        System.out.println("Componenta: " + componentaAfectata);
        System.out.println("Severitate: " + severitate);
        System.out.println("Prioritate: " + prioritate);
        System.out.println("Status: " + status);
        System.out.println("Data raportarii: " + dataRaportarii.format(formatter));
        if (raportor != null) {
            System.out.println("Raportor: " + raportor.getNume());
        }
        if (inginerAsignat != null) {
            System.out.println("Inginer asignat: " + inginerAsignat.getNume());
        }
        if (dataRezolvarii != null) {
            System.out.println("Data rezolvarii: " + dataRezolvarii.format(formatter));
            System.out.println("Timp rezolvare: " + calculeazaTimpRezolvare() + " minute");
        }
    }

    @Override
    public String toString() {
        return "Defect #" + id + " [" + status + "] - " + titlu;
    }

    // Getters si setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitlu() {
        return titlu;
    }

    public void setTitlu(String titlu) {
        this.titlu = titlu;
    }

    public String getDescriere() {
        return descriere;
    }

    public void setDescriere(String descriere) {
        this.descriere = descriere;
    }

    public String getModReproducere() {
        return modReproducere;
    }

    public void setModReproducere(String modReproducere) {
        this.modReproducere = modReproducere;
    }

    public String getComponentaAfectata() {
        return componentaAfectata;
    }

    public void setComponentaAfectata(String componentaAfectata) {
        this.componentaAfectata = componentaAfectata;
    }

    public int getNumarLinie() {
        return numarLinie;
    }

    public void setNumarLinie(int numarLinie) {
        this.numarLinie = numarLinie;
    }

    public String getCodUtilaj() {
        return codUtilaj;
    }

    public void setCodUtilaj(String codUtilaj) {
        this.codUtilaj = codUtilaj;
    }

    public Severitate getSeveritate() {
        return severitate;
    }

    public void setSeveritate(Severitate severitate) {
        this.severitate = severitate;
    }

    public Prioritate getPrioritate() {
        return prioritate;
    }

    public void setPrioritate(Prioritate prioritate) {
        this.prioritate = prioritate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getDataRaportarii() {
        return dataRaportarii;
    }

    public void setDataRaportarii(LocalDateTime dataRaportarii) {
        this.dataRaportarii = dataRaportarii;
    }

    public LocalDateTime getDataRezolvarii() {
        return dataRezolvarii;
    }

    public void setDataRezolvarii(LocalDateTime dataRezolvarii) {
        this.dataRezolvarii = dataRezolvarii;
    }

    public Raportor getRaportor() {
        return raportor;
    }

    public void setRaportor(Raportor raportor) {
        this.raportor = raportor;
    }

    public Inginer getInginerAsignat() {
        return inginerAsignat;
    }

    public void setInginerAsignat(Inginer inginerAsignat) {
        this.inginerAsignat = inginerAsignat;
    }

    public List<IntrareIstoric> getIstoric() {
        return istoric;
    }

    public void setIstoric(List<IntrareIstoric> istoric) {
        this.istoric = istoric;
    }

    public List<Comentariu> getComentarii() {
        return comentarii;
    }

    public void setComentarii(List<Comentariu> comentarii) {
        this.comentarii = comentarii;
    }
}