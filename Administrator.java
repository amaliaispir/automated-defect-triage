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
 * Clasa Administrator reprezinta utilizatorul cu drepturi de configurare
 * a sistemului. Mosteneste de la Utilizator (relatie de generalizare).
 */
public class Administrator extends Utilizator {

    private List<String> permisiuni;
    
    public Administrator() {
        super();
        this.permisiuni = new ArrayList<>();
        // permisiuni implicite pentru orice administrator
        this.permisiuni.add("ADAUGARE_INGINER");
        this.permisiuni.add("CONFIGURARE_REGULI");
        this.permisiuni.add("VIZUALIZARE_RAPOARTE");
    }

    public Administrator(int id, String nume, String email, String parola) {
        super(id, nume, email, parola);
        this.permisiuni = new ArrayList<>();
        this.permisiuni.add("ADAUGARE_INGINER");
        this.permisiuni.add("CONFIGURARE_REGULI");
        this.permisiuni.add("VIZUALIZARE_RAPOARTE");
    }

    /**
     * Adauga un inginer in lista de ingineri a sistemului.
     * Verifica daca administratorul are permisiunea necesara.
     *
     * @param inginer inginerul de adaugat
     * @param listaIngineri lista globala de ingineri
     * @return true daca a fost adaugat cu succes, false altfel
     */
    public boolean adaugaInginer(Inginer inginer, List<Inginer> listaIngineri) {
        if (!esteAutentificat) {
            System.out.println("Eroare: trebuie sa va autentificati pentru a adauga ingineri.");
            return false;
        }
        if (!permisiuni.contains("ADAUGARE_INGINER")) {
            System.out.println("Nu aveti permisiunea de a adauga ingineri.");
            return false;
        }
        if (inginer == null || listaIngineri == null) {
            return false;
        }
        listaIngineri.add(inginer);
        System.out.println("Inginer adaugat: " + inginer.getNume());
        return true;
    }

    /**
     * Genereaza un raport global cu statisticile sistemului.
     * Numara defectele pe fiecare status si returneaza un sumar text.
     *
     * @param toateDefectele lista tuturor defectelor din sistem
     * @return un string cu statisticile
     */
    public String genereazaRaportGlobal(List<Defect> toateDefectele) {
        if (!esteAutentificat) {
            return "Eroare: trebuie sa va autentificati pentru a genera rapoarte.";
        }
        if (!permisiuni.contains("VIZUALIZARE_RAPOARTE")) {
            return "Nu aveti permisiunea de a vizualiza rapoarte.";
        }

        int total = toateDefectele.size();
        int noi = 0, inLucru = 0, rezolvate = 0, inchise = 0, respinse = 0;

        for (Defect d : toateDefectele) {
            switch (d.getStatus()) {
                case NOU:
                case IN_TRIAJ:
                    noi++;
                    break;
                case ASIGNAT:
                case IN_LUCRU:
                    inLucru++;
                    break;
                case REZOLVAT:
                    rezolvate++;
                    break;
                case INCHIS:
                    inchise++;
                    break;
                case RESPINS:
                    respinse++;
                    break;
            }
        }

        StringBuilder raport = new StringBuilder();
        raport.append("=== RAPORT GLOBAL ===\n");
        raport.append("Generat de: ").append(this.nume).append("\n");
        raport.append("Total defecte: ").append(total).append("\n");
        raport.append("  - Noi / in triaj: ").append(noi).append("\n");
        raport.append("  - In lucru: ").append(inLucru).append("\n");
        raport.append("  - Rezolvate: ").append(rezolvate).append("\n");
        raport.append("  - Inchise: ").append(inchise).append("\n");
        raport.append("  - Respinse: ").append(respinse).append("\n");

        return raport.toString();
    }

    /**
     * Salveaza raportul global ca fisier text pe disc.
     * Genereaza intai continutul cu genereazaRaportGlobal(), apoi il scrie in fisier.
     * Daca directorul nu exista, il creeaza. Daca fisierul exista deja, il suprascrie.
     *
     * @param toateDefectele lista tuturor defectelor din sistem
     * @param caleFisier calea completa unde se salveaza (ex: "rapoarte/global_2026.txt")
     * @return true daca salvarea a reusit, false in caz de eroare
     */
    public boolean salveazaRaportGlobal(List<Defect> toateDefectele, String caleFisier) {
        if (!esteAutentificat) {
            System.out.println("Eroare: trebuie sa va autentificati pentru a salva rapoarte.");
            return false;
        }
        if (caleFisier == null || caleFisier.isEmpty()) {
            System.out.println("Eroare: calea fisierului nu poate fi goala.");
            return false;
        }

        // genereaza continutul raportului
        String continut = genereazaRaportGlobal(toateDefectele);

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
            writer.write(continut);
            System.out.println("Raport global salvat cu succes in: " + fisier.getAbsolutePath());
            return true;
        } catch (IOException e) {
            System.out.println("Eroare la salvarea raportului global: " + e.getMessage());
            return false;
        }
    }

    /**
     * Salveaza raportul global intr-un director, generand automat un nume de fisier.
     * Numele se construieste din timestamp pentru a evita coliziuni.
     *
     * @param toateDefectele lista tuturor defectelor din sistem
     * @param director directorul unde se salveaza fisierul (ex: "rapoarte/")
     * @return calea completa a fisierului salvat, sau null in caz de eroare
     */
    public String salveazaRaportGlobalInDirector(List<Defect> toateDefectele, String director) {
        if (director == null || director.isEmpty()) {
            director = "rapoarte";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        String numeFisier = "raport_global_" + timestamp + ".txt";
        String caleCompleta = director + File.separator + numeFisier;

        if (salveazaRaportGlobal(toateDefectele, caleCompleta)) {
            return caleCompleta;
        }
        return null;
    }

    /**
     * Configureaza o noua regula de triaj si o adauga in lista asistentului.
     *
     * @param asistent asistentul caruia i se adauga regula
     * @param cuvantCheie cuvantul cheie care declanseaza regula
     * @param componenta componenta sugerata
     * @param severitate severitatea sugerata
     */
    public void configureazaReguli(AsistentTriaj asistent, String cuvantCheie,
                                   String componenta, Severitate severitate) {
        if (!esteAutentificat) {
            System.out.println("Eroare: trebuie sa va autentificati pentru a configura reguli.");
            return;
        }
        if (!permisiuni.contains("CONFIGURARE_REGULI")) {
            System.out.println("Nu aveti permisiunea de a configura reguli.");
            return;
        }
        RegulaTriaj regula = new RegulaTriaj();
        regula.setCuvantCheie(cuvantCheie);
        regula.setComponentaSugerata(componenta);
        regula.setSeveritateSugerata(severitate);
        asistent.getReguli().add(regula);
    }

    /**
     * Suprascrie afisarea detaliilor pentru a include informatii despre permisiuni.
     */
    @Override
    public void afisareDetalii() {
        super.afisareDetalii();
        System.out.println("Rol: Administrator");
        System.out.println("Permisiuni: " + permisiuni);
    }

    // Getters si setters
    public List<String> getPermisiuni() {
        return permisiuni;
    }

    public void setPermisiuni(List<String> permisiuni) {
        this.permisiuni = permisiuni;
    }
}