package proiect_triaj_defecte;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Clasa Raport reprezinta documentul final generat dupa rezolvarea unui defect.
 * Contine tot istoricul defectului de la aparitie pana la rezolvare,
 * inclusiv justificarea triajului si toate comentariile.
 *
 * Relatii:
 *   - ASOCIATIE cu Defect (raportul descrie un defect specific)
 */
public class Raport {

    private int id;
    private LocalDateTime dataGenerarii;
    private Defect defectulRaportat;       // asociatie cu Defect
    private String continut;
    private String justificareTriaj;
    private List<IntrareIstoric> istoricComplet;

    public Raport() {
        this.id = 0;
        this.dataGenerarii = LocalDateTime.now();
        this.defectulRaportat = null;
        this.continut = "";
        this.justificareTriaj = "";
        this.istoricComplet = new ArrayList<>();
    }

    public Raport(int id, Defect defect, String justificareTriaj) {
        this.id = id;
        this.dataGenerarii = LocalDateTime.now();
        this.defectulRaportat = defect;
        this.justificareTriaj = justificareTriaj;
        this.istoricComplet = new ArrayList<>();
        if (defect != null && defect.getIstoric() != null) {
            // copiem o referinta la istoric pentru a-l putea afisa
            this.istoricComplet.addAll(defect.getIstoric());
        }
        this.continut = "";
    }

    /**
     * Genereaza continutul textual al raportului.
     * Construieste documentul final cu toate informatiile despre defect.
     *
     * Pasii executiei:
     *   1. Verifica daca defectul exista
     *   2. Adauga antetul (titlu, ID, data generare)
     *   3. Adauga datele de baza ale defectului
     *   4. Adauga clasificarea (severitate, prioritate, componenta)
     *   5. Adauga informatii despre raportor si inginer
     *   6. Adauga datele temporale (raportare, rezolvare, durata)
     *   7. Adauga justificarea triajului
     *   8. Adauga istoricul complet
     *   9. Adauga comentariile
     *  10. Salveaza textul in atributul 'continut'
     *
     * @return continutul raportului ca string
     */
    public String genereazaContinut() {
        if (defectulRaportat == null) {
            return "Eroare: nu se poate genera raport fara un defect asociat.";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        StringBuilder sb = new StringBuilder();

        // Pasul 2: antet
        sb.append("===========================================\n");
        sb.append("       RAPORT FINAL DEFECT #").append(defectulRaportat.getId()).append("\n");
        sb.append("===========================================\n");
        sb.append("Raport generat la: ").append(dataGenerarii.format(formatter)).append("\n");
        sb.append("ID raport: ").append(id).append("\n\n");

        // Pasul 3: date de baza
        sb.append("--- INFORMATII DEFECT ---\n");
        sb.append("Titlu: ").append(defectulRaportat.getTitlu()).append("\n");
        sb.append("Descriere: ").append(defectulRaportat.getDescriere()).append("\n");
        sb.append("Mod reproducere: ").append(defectulRaportat.getModReproducere()).append("\n");
        sb.append("Linia de productie: ").append(defectulRaportat.getNumarLinie()).append("\n");
        sb.append("Cod utilaj: ").append(defectulRaportat.getCodUtilaj()).append("\n\n");

        // Pasul 4: clasificare
        sb.append("--- CLASIFICARE ---\n");
        sb.append("Componenta afectata: ").append(defectulRaportat.getComponentaAfectata()).append("\n");
        sb.append("Severitate: ").append(defectulRaportat.getSeveritate()).append("\n");
        sb.append("Prioritate: ").append(defectulRaportat.getPrioritate()).append("\n");
        sb.append("Status final: ").append(defectulRaportat.getStatus()).append("\n\n");

        // Pasul 5: persoane implicate
        sb.append("--- PERSOANE IMPLICATE ---\n");
        if (defectulRaportat.getRaportor() != null) {
            sb.append("Raportor: ").append(defectulRaportat.getRaportor().getNume());
            sb.append(" (").append(defectulRaportat.getRaportor().getEmail()).append(")\n");
        } else {
            sb.append("Raportor: necunoscut\n");
        }
        if (defectulRaportat.getInginerAsignat() != null) {
            Inginer ing = defectulRaportat.getInginerAsignat();
            sb.append("Inginer asignat: ").append(ing.getNume());
            sb.append(" (specializare: ").append(ing.getSpecializare()).append(")\n");
        } else {
            sb.append("Inginer asignat: niciunul\n");
        }
        sb.append("\n");

        // Pasul 6: cronologie
        sb.append("--- CRONOLOGIE ---\n");
        sb.append("Data raportarii: ");
        if (defectulRaportat.getDataRaportarii() != null) {
            sb.append(defectulRaportat.getDataRaportarii().format(formatter));
        } else {
            sb.append("necunoscuta");
        }
        sb.append("\n");
        sb.append("Data rezolvarii: ");
        if (defectulRaportat.getDataRezolvarii() != null) {
            sb.append(defectulRaportat.getDataRezolvarii().format(formatter));
            sb.append("\nTimp total rezolvare: ");
            sb.append(defectulRaportat.calculeazaTimpRezolvare()).append(" minute");
        } else {
            sb.append("nerezolvat inca");
        }
        sb.append("\n\n");

        // Pasul 7: justificare triaj
        sb.append("--- JUSTIFICARE TRIAJ AUTOMAT ---\n");
        if (justificareTriaj != null && !justificareTriaj.isEmpty()) {
            sb.append(justificareTriaj).append("\n");
        } else {
            sb.append("(fara justificare disponibila)\n");
        }
        sb.append("\n");

        // Pasul 8: istoric complet
        sb.append("--- ISTORIC COMPLET ---\n");
        if (istoricComplet.isEmpty()) {
            sb.append("(nu exista intrari in istoric)\n");
        } else {
            int nr = 1;
            for (IntrareIstoric intrare : istoricComplet) {
                sb.append(nr).append(". ").append(intrare.afiseazaIntrare()).append("\n");
                nr++;
            }
        }
        sb.append("\n");

        // Pasul 9: comentarii
        sb.append("--- COMENTARII ---\n");
        List<Comentariu> comentarii = defectulRaportat.getComentarii();
        if (comentarii.isEmpty()) {
            sb.append("(fara comentarii)\n");
        } else {
            int nr = 1;
            for (Comentariu c : comentarii) {
                sb.append(nr).append(". ").append(c.afiseazaComentariu()).append("\n");
                nr++;
            }
        }
        sb.append("\n===========================================\n");
        sb.append("            SFARSIT RAPORT\n");
        sb.append("===========================================\n");

        // Pasul 10: salveaza in atribut
        this.continut = sb.toString();
        return this.continut;
    }

    /**
     * Exporta raportul - returneaza continutul gata pentru afisare/salvare.
     * Daca raportul nu a fost inca generat, il genereaza automat.
     *
     * @return continutul raportului
     */
    public String exporta() {
        if (continut == null || continut.isEmpty()) {
            genereazaContinut();
        }
        return continut;
    }

    public void afisareRaport() {
        System.out.println(exporta());
    }

    /**
     * Salveaza raportul ca fisier text.
     * Daca directorul nu exista, il creeaza.
     * Daca fisierul exista deja, il suprascrie.
     *
     * @param caleFisier calea completa unde se salveaza fisierul (ex: "rapoarte/raport_001.txt")
     * @return true daca salvarea a reusit, false in caz de eroare
     */
    public boolean salveazaInFisier(String caleFisier) {
        if (caleFisier == null || caleFisier.isEmpty()) {
            System.out.println("Eroare: calea fisierului nu poate fi goala.");
            return false;
        }

        // genereaza continutul daca nu a fost generat inca
        String text = exporta();

        // creeaza directorul parinte daca nu exista
        File fisier = new File(caleFisier);
        File director = fisier.getParentFile();
        if (director != null && !director.exists()) {
            boolean creat = director.mkdirs();
            if (!creat) {
                System.out.println("Eroare: nu s-a putut crea directorul " + director.getPath());
                return false;
            }
        }

        // scrie continutul in fisier
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fisier))) {
            writer.write(text);
            System.out.println("Raport salvat cu succes in: " + fisier.getAbsolutePath());
            return true;
        } catch (IOException e) {
            System.out.println("Eroare la salvarea raportului: " + e.getMessage());
            return false;
        }
    }

    /**
     * Salveaza raportul intr-un director, generand automat un nume de fisier.
     * Numele se construieste din ID-ul defectului si data generarii.
     *
     * @param director directorul unde se salveaza fisierul (ex: "rapoarte/")
     * @return calea completa a fisierului salvat, sau null in caz de eroare
     */
    public String salveazaInDirector(String director) {
        if (director == null || director.isEmpty()) {
            director = "rapoarte";
        }

        String numeFisier = genereazaNumeFisier();
        String calecompleta = director + File.separator + numeFisier;

        if (salveazaInFisier(calecompleta)) {
            return calecompleta;
        }
        return null;
    }

    /**
     * Genereaza un nume de fisier unic pentru raport.
     * Format: raport_defect_<id>_<timestamp>.txt
     *
     * @return numele de fisier generat
     */
    private String genereazaNumeFisier() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = dataGenerarii.format(formatter);
        int idDefect = defectulRaportat != null ? defectulRaportat.getId() : 0;
        return "raport_defect_" + idDefect + "_" + timestamp + ".txt";
    }

    @Override
    public String toString() {
        if (defectulRaportat == null) {
            return "Raport #" + id + " (fara defect asociat)";
        }
        return "Raport #" + id + " pentru defectul #" + defectulRaportat.getId()
                + " (generat la " + dataGenerarii + ")";
    }

    // Getters si setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getDataGenerarii() {
        return dataGenerarii;
    }

    public void setDataGenerarii(LocalDateTime dataGenerarii) {
        this.dataGenerarii = dataGenerarii;
    }

    public Defect getDefectulRaportat() {
        return defectulRaportat;
    }

    public void setDefectulRaportat(Defect defectulRaportat) {
        this.defectulRaportat = defectulRaportat;
    }

    public String getContinut() {
        return continut;
    }

    public void setContinut(String continut) {
        this.continut = continut;
    }

    public String getJustificareTriaj() {
        return justificareTriaj;
    }

    public void setJustificareTriaj(String justificareTriaj) {
        this.justificareTriaj = justificareTriaj;
    }

    public List<IntrareIstoric> getIstoricComplet() {
        return istoricComplet;
    }

    public void setIstoricComplet(List<IntrareIstoric> istoricComplet) {
        this.istoricComplet = istoricComplet;
    }
}