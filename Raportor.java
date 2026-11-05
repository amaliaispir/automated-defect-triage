package proiect_triaj_defecte;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Clasa Raportor reprezinta utilizatorul care raporteaza defecte.
 * Mosteneste de la Utilizator (relatie de generalizare).
 */
public class Raportor extends Utilizator {

    private String departament;  // ex: "Asamblare", "Vopsitorie", "Sudura", "Montaj final", "Caroserie"
    private List<Defect> defecteRaportate;  // asociatie one-to-many cu Defect

    public Raportor() {
        super(); 
        this.departament = "Necunoscut";
        this.defecteRaportate = new ArrayList<>();
    }

    public Raportor(int id, String nume, String email, String parola, String departament) {
        super(id, nume, email, parola);  // apeleaza constructorul cu parametri al parintelui
        this.departament = departament;
        this.defecteRaportate = new ArrayList<>();
    }

    /**
     * Raporteaza un defect nou.
     * 
     * @param titlu titlul defectului
     * @param descriere descrierea detaliata a problemei
     * @param modReproducere pasii pentru reproducerea problemei
     * @param numarLinie numarul liniei de productie unde s-a observat problema
     * @param codUtilaj codul utilajului afectat
     * @return defectul nou creat
     */
    public Defect raporteazaDefect(String titlu, String descriere, String modReproducere,
                                   int numarLinie, String codUtilaj) {
        if (!esteAutentificat) {
            System.out.println("Eroare: trebuie sa va autentificati pentru a raporta un defect.");
            return null;
        }

        Defect defect = new Defect();
        defect.setTitlu(titlu);
        defect.setDescriere(descriere);
        defect.setModReproducere(modReproducere);
        defect.setNumarLinie(numarLinie);
        defect.setCodUtilaj(codUtilaj);
        defect.setRaportor(this);
        defect.setStatus(Status.NOU);
        defect.setDataRaportarii(LocalDateTime.now());

        this.defecteRaportate.add(defect);
        return defect;
    }

    public List<Defect> vizualizeazaDefecteleMele() {
        return defecteRaportate;
    }

    /**
     * Adauga un comentariu pe un defect.
     * Permite raportorului sa ofere informatii suplimentare.
     *
     * @param defect defectul pe care se adauga comentariul
     * @param textComentariu textul comentariului
     */
    public void adaugaComentariu(Defect defect, String textComentariu) {
        if (!esteAutentificat) {
            System.out.println("Eroare: trebuie sa va autentificati pentru a adauga un comentariu.");
            return;
        }

        Comentariu comentariu = new Comentariu();
        comentariu.setText(textComentariu);
        comentariu.setAutor(this.nume);
        comentariu.setDataPostarii(LocalDateTime.now());
        defect.getComentarii().add(comentariu);
    }

    @Override
    public void afisareDetalii() {
        super.afisareDetalii();
        System.out.println("Rol: Raportor");
        System.out.println("Departament: " + departament);
        System.out.println("Numar defecte raportate: " + defecteRaportate.size());
    }

    // Getters si setters
    public String getDepartament() {
        return departament;
    }

    public void setDepartament(String departament) {
        this.departament = departament;
    }

    public List<Defect> getDefecteRaportate() {
        return defecteRaportate;
    }

    public void setDefecteRaportate(List<Defect> defecteRaportate) {
        this.defecteRaportate = defecteRaportate;
    }
}
