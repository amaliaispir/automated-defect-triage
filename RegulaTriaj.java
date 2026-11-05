package proiect_triaj_defecte;

/**
 * Clasa RegulaTriaj reprezinta o regula simpla folosita de AsistentTriaj
 * pentru a clasifica automat defectele.
 *
 * Logica unei reguli:
 *   "Daca descrierea defectului contine 'cuvantCheie',
 *    atunci componenta este 'componentaSugerata'
 *    si severitatea este 'severitateSugerata'."
 *
 * Face parte dintr-o relatie de COMPOZITIE cu AsistentTriaj -
 * regulile nu exista in afara unui asistent.
 */
public class RegulaTriaj {

    private int id;
    private String cuvantCheie;
    private String componentaSugerata;
    private Severitate severitateSugerata;

    public RegulaTriaj() {
        this.id = 0;
        this.cuvantCheie = "";
        this.componentaSugerata = "Necategorizat";
        this.severitateSugerata = Severitate.MINORA;
    }

    public RegulaTriaj(int id, String cuvantCheie, String componentaSugerata,
                       Severitate severitateSugerata) {
        this.id = id;
        this.cuvantCheie = cuvantCheie;
        this.componentaSugerata = componentaSugerata;
        this.severitateSugerata = severitateSugerata;
    }

    /**
     * Verifica daca regula se aplica pentru un anumit defect.
     * Cauta cuvantul cheie al regulii in descrierea sau titlul defectului.
     * Comparatia este case-insensitive.
     *
     * @param defect defectul de verificat
     * @return true daca regula se aplica defectului, false altfel
     */
    public boolean seAplica(Defect defect) {
        if (defect == null || cuvantCheie == null || cuvantCheie.isEmpty()) {
            return false;
        }

        String cuvantNormalizat = cuvantCheie.toLowerCase();
        String descriere = defect.getDescriere() != null
                ? defect.getDescriere().toLowerCase()
                : "";
        String titlu = defect.getTitlu() != null
                ? defect.getTitlu().toLowerCase()
                : "";

        return descriere.contains(cuvantNormalizat) || titlu.contains(cuvantNormalizat);
    }

    public void afisareDetalii() {
        System.out.println("Regula #" + id + ": daca apare '" + cuvantCheie
                + "' -> componenta: " + componentaSugerata
                + ", severitate: " + severitateSugerata);
    }

    @Override
    public String toString() {
        return "RegulaTriaj{cuvantCheie='" + cuvantCheie + "', componenta='"
                + componentaSugerata + "', severitate=" + severitateSugerata + "}";
    }

    // Getters si setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCuvantCheie() {
        return cuvantCheie;
    }

    public void setCuvantCheie(String cuvantCheie) {
        this.cuvantCheie = cuvantCheie;
    }

    public String getComponentaSugerata() {
        return componentaSugerata;
    }

    public void setComponentaSugerata(String componentaSugerata) {
        this.componentaSugerata = componentaSugerata;
    }

    public Severitate getSeveritateSugerata() {
        return severitateSugerata;
    }

    public void setSeveritateSugerata(Severitate severitateSugerata) {
        this.severitateSugerata = severitateSugerata;
    }
}