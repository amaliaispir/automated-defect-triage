package proiect_triaj_defecte;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Clasa AsistentTriaj reprezinta asistentul virtual care clasifica
 * automat defectele si le asigneaza inginerilor potriviti.
 *
 * Relatii:
 *   - COMPOZITIE cu RegulaTriaj (regulile apartin asistentului)
 *   - ASOCIATIE cu Defect (asistentul triaza defecte)
 */
public class AsistentTriaj {

    private int id;
    private String nume;
    private List<RegulaTriaj> reguli;     // compozitie - regulile traiesc cu asistentul
    private int defecteProcessate;        // contor pentru statistici

    public AsistentTriaj() {
        this.id = 0;
        this.nume = "Asistent Virtual";
        this.reguli = new ArrayList<>();
        this.defecteProcessate = 0;
        initializeazaReguliImplicite();
    }

    public AsistentTriaj(int id, String nume) {
        this.id = id;
        this.nume = nume;
        this.reguli = new ArrayList<>();
        this.defecteProcessate = 0;
        initializeazaReguliImplicite();
    }

    /**
     * Adauga un set de reguli implicite la crearea asistentului.
     * Regulile sunt specifice unei fabrici auto - acopera defecte
     * de mentenanta a utilajelor de pe linia de productie.
     * Asa, asistentul poate functiona imediat fara configurare manuala.
     */
    private void initializeazaReguliImplicite() {
        // Defecte CRITICE - opresc productia, necesita interventie urgenta
        reguli.add(new RegulaTriaj(1, "scurgere ulei", "SistemHidraulic", Severitate.CRITICA));
        reguli.add(new RegulaTriaj(2, "supraincalzire", "Cuptor", Severitate.CRITICA));
        reguli.add(new RegulaTriaj(3, "blocare", "BandaTransportoare", Severitate.CRITICA));
        reguli.add(new RegulaTriaj(4, "scurtcircuit", "SistemElectric", Severitate.CRITICA));
        reguli.add(new RegulaTriaj(5, "explozie", "PresaCaroserie", Severitate.CRITICA));

        // Defecte MAJORE - afecteaza calitatea sau ritmul productiei
        reguli.add(new RegulaTriaj(6, "vibratii", "RobotSudura", Severitate.MAJORA));
        reguli.add(new RegulaTriaj(7, "eroare PLC", "Automatizari", Severitate.MAJORA));
        reguli.add(new RegulaTriaj(8, "presiune", "SistemHidraulic", Severitate.MAJORA));
        reguli.add(new RegulaTriaj(9, "calibrare", "RobotSudura", Severitate.MAJORA));
        reguli.add(new RegulaTriaj(10, "senzor defect", "Automatizari", Severitate.MAJORA));

        // Defecte MINORE - workaround posibil, productia continua
        reguli.add(new RegulaTriaj(11, "zgomot", "BandaTransportoare", Severitate.MINORA));
        reguli.add(new RegulaTriaj(12, "uzura", "BandaTransportoare", Severitate.MINORA));
        reguli.add(new RegulaTriaj(13, "lubrifiere", "SistemMecanic", Severitate.MINORA));

        // Defecte COSMETICE - probleme minore de aspect
        reguli.add(new RegulaTriaj(14, "pata vopsea", "PistolVopsire", Severitate.COSMETICA));
        reguli.add(new RegulaTriaj(15, "afisaj", "PanouControl", Severitate.COSMETICA));
    }

    /**
     * Triaza un defect: ii atribuie componenta, severitatea, prioritatea
     * si il asigneaza unui inginer potrivit. Genereaza si o justificare.
     *
     * Pasii executiei:
     *   1. Verifica daca defectul este valid (nu e null, are descriere)
     *   2. Schimba statusul defectului in IN_TRIAJ
     *   3. Sugereaza componenta afectata pe baza regulilor
     *   4. Calculeaza severitatea defectului
     *   5. Calculeaza prioritatea defectului
     *   6. Asigneaza inginerul cel mai potrivit
     *   7. Daca s-a gasit un inginer, schimba statusul in ASIGNAT
     *      altfel, lasa statusul IN_TRIAJ pentru asignare manuala
     *   8. Genereaza justificarea triajului
     *   9. Adauga o intrare in istoricul defectului
     *  10. Incrementeaza contorul defectelor procesate
     *
     * @param defect defectul de triat
     * @param ingineri lista inginerilor disponibili pentru asignare
     * @return true daca triajul s-a incheiat cu succes, false altfel
     */
    public boolean triazaDefect(Defect defect, List<Inginer> ingineri) {
        // validare
        if (defect == null) {
            return false;
        }
        if (defect.getDescriere() == null || defect.getDescriere().isEmpty()) {
            return false;
        }

        // marcheaza inceputul triajului
        defect.actualizeazaStatus(Status.IN_TRIAJ, this.nume);

        // sugereaza componenta
        String componenta = sugereazaComponenta(defect);
        defect.setComponentaAfectata(componenta);

        // calculeaza severitatea
        Severitate severitate = calculeazaSeveritate(defect);
        defect.setSeveritate(severitate);

        // calculeaza prioritatea
        Prioritate prioritate = calculeazaPrioritate(defect);
        defect.setPrioritate(prioritate);

        // gaseste inginerul potrivit
        Inginer inginerAles = asigneazaInginer(defect, ingineri);

        // actualizeaza statusul in functie de rezultat
        if (inginerAles != null) {
            defect.setInginerAsignat(inginerAles);
            inginerAles.getDefecteAsignate().add(defect);
            inginerAles.setIncarcareCurenta(inginerAles.getIncarcareCurenta() + 1);
            defect.actualizeazaStatus(Status.ASIGNAT, this.nume);
        }

        // justificare + intrare in istoric
        String justificare = genereazaJustificare(defect);
        IntrareIstoric intrare = new IntrareIstoric();
        intrare.setDataModificare(LocalDateTime.now());
        intrare.setActiune("Triaj automat: " + justificare);
        intrare.setEfectuatDe(this.nume);
        defect.adaugaIntrareIstoric(intrare);

        // contorizare
        this.defecteProcessate++;

        return true;
    }

    /**
     * Sugereaza componenta afectata pe baza regulilor.
     * Parcurge toate regulile si returneaza componenta primei reguli care se aplica.
     * Daca nicio regula nu se aplica, returneaza "Necategorizat".
     *
     * @param defect defectul de analizat
     * @return numele componentei sugerate
     */
    public String sugereazaComponenta(Defect defect) {
        for (RegulaTriaj regula : reguli) {
            if (regula.seAplica(defect)) {
                return regula.getComponentaSugerata();
            }
        }
        return "Necategorizat";
    }

    /**
     * Calculeaza severitatea defectului.
     * Parcurge toate regulile aplicabile si retine severitatea cea mai grava.
     *
     * Logica: daca apar mai multe reguli, se ia "cea mai severa"
     *
     * @param defect defectul de analizat
     * @return severitatea calculata
     */
    public Severitate calculeazaSeveritate(Defect defect) {
        Severitate severitateMaxima = Severitate.COSMETICA;  // pornim de la cea mai mica

        for (RegulaTriaj regula : reguli) {
            if (regula.seAplica(defect)) {
                Severitate severitateRegula = regula.getSeveritateSugerata();
                // ordinea in enum: CRITICA(0), MAJORA(1), MINORA(2), COSMETICA(3)
                // deci severitatea mai mica in ordinal = mai grava
                if (severitateRegula.ordinal() < severitateMaxima.ordinal()) {
                    severitateMaxima = severitateRegula;
                }
            }
        }
        return severitateMaxima;
    }

    /**
     * Calculeaza prioritatea defectului.
     * Combinatie intre severitate si componenta:
     *   - Severitate CRITICA + componenta critica (BandaTransportoare, RobotSudura,
     *     PresaCaroserie - care opresc productia) -> URGENTA
     *   - Severitate CRITICA -> INALTA
     *   - Severitate MAJORA -> INALTA
     *   - Severitate MINORA -> MEDIE
     *   - Severitate COSMETICA -> SCAZUTA
     *
     * @param defect defectul de analizat
     * @return prioritatea calculata
     */
    public Prioritate calculeazaPrioritate(Defect defect) {
        Severitate severitate = defect.getSeveritate();
        String componenta = defect.getComponentaAfectata();

        // componente care opresc complet linia de productie - prioritate maxima
        boolean componentaCritica = componenta.equals("BandaTransportoare")
                || componenta.equals("RobotSudura")
                || componenta.equals("PresaCaroserie")
                || componenta.equals("Cuptor");

        switch (severitate) {
            case CRITICA:
                return componentaCritica ? Prioritate.URGENTA : Prioritate.INALTA;
            case MAJORA:
                return Prioritate.INALTA;
            case MINORA:
                return Prioritate.MEDIE;
            case COSMETICA:
                return Prioritate.SCAZUTA;
            default:
                return Prioritate.MEDIE;
        }
    }

    /**
     * Alege inginerul cel mai potrivit pentru defect.
     *
     * Criteriile de selectie, in ordine de prioritate:
     *   1. Specializare potrivita componentei defectului
     *   2. Nivel de experienta suficient pentru severitatea defectului:
     *        - CRITICA  → nivel minim 3
     *        - MAJORA   → nivel minim 2
     *        - MINORA / COSMETICA → orice nivel (minim 1)
     *   3. Incarcarea curenta minima (cel mai liber inginer)
     *
     * In caz ca nu gaseste inginerul ideal:
     *   - Intai cauta specializare + nivel necesar
     *   - Apoi specializare + orice nivel
     *   - Apoi orice specializare + nivel necesar
     *   - In ultima instanta, orice inginer cu incarcarea minima
     *
     * @param defect defectul de asignat
     * @param ingineri lista inginerilor disponibili
     * @return inginerul ales sau null daca lista e goala
     */
    public Inginer asigneazaInginer(Defect defect, List<Inginer> ingineri) {
        if (ingineri == null || ingineri.isEmpty()) {
            return null;
        }

        String specializareNecesara = determinaSpecializareNecesara(defect.getComponentaAfectata());
        int nivelMinim = determinaNivelMinim(defect.getSeveritate());

        // incercarea 1: specializare potrivita + nivel suficient + incarcare minima
        Inginer inginerAles = cautaInginer(ingineri, specializareNecesara, nivelMinim);

        // incercarea 2: specializare potrivita, ignoram nivelul
        if (inginerAles == null) {
            System.out.println("Atentie: nu s-a gasit inginer cu specializarea '"
                    + specializareNecesara + "' si nivel minim " + nivelMinim
                    + ". Se cauta dupa specializare fara restrictie de nivel.");
            inginerAles = cautaInginer(ingineri, specializareNecesara, 1);
        }

        // incercarea 3: orice specializare, dar cu nivelul necesar
        if (inginerAles == null) {
            System.out.println("Atentie: nu s-a gasit inginer cu specializarea '"
                    + specializareNecesara + "'. Se cauta orice inginer cu nivel minim "
                    + nivelMinim + ".");
            inginerAles = cautaInginer(ingineri, null, nivelMinim);
        }

        // incercarea 4 (last resort): orice inginer cu incarcarea minima
        if (inginerAles == null) {
            System.out.println("Atentie: nu s-a gasit inginer cu criteriile necesare. "
                    + "Se asigneaza inginerul cu incarcarea minima.");
            inginerAles = cautaInginer(ingineri, null, 1);
        }

        return inginerAles;
    }

    /**
     * Metoda helper care cauta inginerul optim dintr-o lista dupa
     * specializare si nivel minim, alegand cel cu incarcarea minima.
     *
     * @param ingineri lista din care se cauta
     * @param specializare specializarea cautata, sau null pentru orice specializare
     * @param nivelMinim nivelul minim de experienta acceptat
     * @return inginerul gasit sau null daca nu exista niciunul potrivit
     */
    private Inginer cautaInginer(List<Inginer> ingineri, String specializare, int nivelMinim) {
        Inginer inginerAles = null;
        int incarcareMinima = Integer.MAX_VALUE;

        for (Inginer inginer : ingineri) {
            boolean specializarePotrivita = specializare == null
                    || inginer.getSpecializare().equalsIgnoreCase(specializare);
            boolean nivelSuficient = inginer.getNivelExperienta() >= nivelMinim;
            boolean maiLiber = inginer.getIncarcareCurenta() < incarcareMinima;

            if (specializarePotrivita && nivelSuficient && maiLiber) {
                incarcareMinima = inginer.getIncarcareCurenta();
                inginerAles = inginer;
            }
        }

        return inginerAles;
    }

    /**
     * Determina nivelul minim de experienta necesar in functie de severitate.
     *
     * CRITICA   → nivel 3
     * MAJORA    → nivel 2+
     * MINORA    → nivel 1+
     * COSMETICA → nivel 1+
     *
     * @param severitate severitatea defectului
     * @return nivelul minim de experienta necesar (1-5)
     */
    public int determinaNivelMinim(Severitate severitate) {
        switch (severitate) {
            case CRITICA:  return 3;
            case MAJORA:   return 2;
            case MINORA:
            case COSMETICA:
            default:       return 1;
        }
    }

    /**
     * Mapeaza componenta afectata la specializarea inginerului necesara.
     *
     * @param componenta numele componentei afectate
     * @return specializarea de inginer necesara
     */
    public String determinaSpecializareNecesara(String componenta) {
        if (componenta == null) {
            return "Mecanic";
        }
        switch (componenta) {
            case "RobotSudura":
                return "Robotica";
            case "BandaTransportoare":
            case "PresaCaroserie":
            case "SistemMecanic":
            case "Cuptor":
            case "PistolVopsire":
                return "Mecanic";
            case "SistemHidraulic":
                return "Hidraulic";
            case "SistemElectric":
            case "PanouControl":
                return "Electric";
            case "Automatizari":
                return "Automatizari";
            default:
                return "Mecanic";  // default - inginer mecanic generalist
        }
    }

    /**
     * Genereaza un text explicativ cu toate deciziile luate la triaj.
     * Acest text este util pentru raporturile finale.
     *
     * @param defect defectul triat
     * @return text cu justificarea triajului
     */
    public String genereazaJustificare(Defect defect) {
        int nivelMinimNecesar = determinaNivelMinim(defect.getSeveritate());
        StringBuilder sb = new StringBuilder();
        sb.append("Defect clasificat ca ").append(defect.getSeveritate());
        sb.append(", prioritate ").append(defect.getPrioritate());
        sb.append(", componenta '").append(defect.getComponentaAfectata()).append("'");
        sb.append(". Nivel minim de experienta necesar: ").append(nivelMinimNecesar);

        if (defect.getInginerAsignat() != null) {
            Inginer ing = defect.getInginerAsignat();
            sb.append(". Asignat lui ").append(ing.getNume());
            sb.append(" (specializare: ").append(ing.getSpecializare());
            sb.append(", nivel experienta: ").append(ing.getNivelExperienta()).append("/5");
            sb.append(", incarcare: ").append(ing.getIncarcareCurenta()).append(" defecte)");
            if (ing.getNivelExperienta() < nivelMinimNecesar) {
                sb.append(" - ATENTIE: inginerul nu atinge nivelul minim necesar,");
                sb.append(" asignat din lipsa de alternative");
            }
        } else {
            sb.append(". Nu s-a gasit un inginer disponibil - necesita asignare manuala");
        }

        return sb.toString();
    }

    /**
     * Genereaza raportul final pentru un defect rezolvat.
     * Aceasta metoda este punctul de legatura dintre AsistentTriaj si Raport.
     *
     * Fluxul complet al unui defect:
     *   1. Raportor.raporteazaDefect()      → defect creat, status NOU
     *   2. AsistentTriaj.triazaDefect()     → defect clasificat, intrari adaugate in istoric
     *   3. Inginer.preiaDefect()            → status IN_LUCRU, intrare in istoric
     *   4. Inginer.rezolvaDefect()          → status REZOLVAT, intrare in istoric
     *   5. AsistentTriaj.genereazaRaport()  → raport final care citeste tot istoricul
     *
     * @param defect defectul pentru care se genereaza raportul
     * @param idRaport ID-ul raportului nou
     * @return raportul generat, sau null daca defectul nu e rezolvat
     */
    public Raport genereazaRaportDefect(Defect defect, int idRaport) {
        if (defect == null) {
            return null;
        }
        if (!defect.esteRezolvat()) {
            System.out.println("Atentie: defectul #" + defect.getId()
                    + " nu este inca rezolvat. Raportul va fi marcat ca incomplet.");
        }

        // extrage justificarea din ultima intrare de triaj din istoric
        String justificare = extragJustificareTriaj(defect);

        // creeaza si populeaza raportul
        Raport raport = new Raport(idRaport, defect, justificare);
        raport.genereazaContinut();

        // adauga in istoricul defectului faptul ca s-a generat un raport
        IntrareIstoric intrare = new IntrareIstoric();
        intrare.setDataModificare(java.time.LocalDateTime.now());
        intrare.setActiune("Raport final generat (ID raport: " + idRaport + ")");
        intrare.setEfectuatDe(this.nume);
        defect.adaugaIntrareIstoric(intrare);

        // daca defectul era REZOLVAT, il inchidem acum
        if (defect.getStatus() == Status.REZOLVAT) {
            defect.actualizeazaStatus(Status.INCHIS, this.nume);
        }

        return raport;
    }

    /**
     * Cauta in istoricul defectului intrarea de triaj si extrage justificarea.
     * Metoda helper folosita de genereazaRaportDefect.
     *
     * @param defect defectul din care se extrage justificarea
     * @return textul justificarii, sau un mesaj default daca nu se gaseste
     */
    private String extragJustificareTriaj(Defect defect) {
        for (IntrareIstoric intrare : defect.getIstoric()) {
            if (intrare.getActiune() != null
                    && intrare.getActiune().startsWith("Triaj automat:")) {
                return intrare.getActiune().substring("Triaj automat: ".length());
            }
        }
        return "Justificare triaj indisponibila";
    }

    public void afisareDetalii() {
        System.out.println("=== Asistent Triaj #" + id + " ===");
        System.out.println("Nume: " + nume);
        System.out.println("Numar reguli active: " + reguli.size());
        System.out.println("Defecte procesate: " + defecteProcessate);
    }

    // Getters si setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public List<RegulaTriaj> getReguli() {
        return reguli;
    }

    public void setReguli(List<RegulaTriaj> reguli) {
        this.reguli = reguli;
    }

    public int getDefecteProcessate() {
        return defecteProcessate;
    }

    public void setDefecteProcessate(int defecteProcessate) {
        this.defecteProcessate = defecteProcessate;
    }
}