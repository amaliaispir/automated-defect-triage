package proiect_triaj_defecte;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Clasa Inginer reprezinta utilizatorul care rezolva defectele.
 * Mosteneste de la Utilizator (relatie de generalizare).
 */
public class Inginer extends Utilizator {

    private String specializare;       // ex: "Mecanic", "Electric", "Hidraulic", "Automatizari", "Robotica"
    private int nivelExperienta;       // de la 1 la 5
    private List<Defect> defecteAsignate;  // asociatie 1-to-many cu Defect
    private int incarcareCurenta;      // numar de defecte active

    public Inginer() {
        super();
        this.specializare = "General";
        this.nivelExperienta = 1;
        this.defecteAsignate = new ArrayList<>();
        this.incarcareCurenta = 0;
    }

    public Inginer(int id, String nume, String email, String parola,
                   String specializare, int nivelExperienta) {
        super(id, nume, email, parola);
        this.specializare = specializare;
        this.nivelExperienta = nivelExperienta;
        this.defecteAsignate = new ArrayList<>();
        this.incarcareCurenta = 0;
    }

    /**
     * Inginerul preia un defect care i-a fost asignat.
     * Pasii:
     *   1. Adauga defectul in lista defectelor asignate
     *   2. Incrementeaza incarcarea curenta
     *   3. Schimba statusul defectului in IN_LUCRU
     *   4. Inregistreaza tranzitia in istoricul defectului
     *
     * @param defect defectul preluat
     */
    public void preiaDefect(Defect defect) {
        if (!esteAutentificat) {
            System.out.println("Eroare: trebuie sa va autentificati pentru a prelua un defect.");
            return;
        }

        this.defecteAsignate.add(defect);
        this.incarcareCurenta++;

        Status statusVechi = defect.getStatus();
        defect.setStatus(Status.IN_LUCRU);

        // adauga intrare in istoric
        IntrareIstoric intrare = new IntrareIstoric();
        intrare.setDataModificare(LocalDateTime.now());
        intrare.setActiune("Defect preluat de inginer");
        intrare.setEfectuatDe(this.nume);
        intrare.setValoareVeche(statusVechi.toString());
        intrare.setValoareNoua(Status.IN_LUCRU.toString());
        defect.adaugaIntrareIstoric(intrare);
    }

    /**
     * Actualizeaza statusul unui defect si inregistreaza modificarea in istoric.
     *
     * @param defect defectul modificat
     * @param statusNou noul status
     */
    public void actualizeazaStatus(Defect defect, Status statusNou) {
        if (!esteAutentificat) {
            System.out.println("Eroare: trebuie sa va autentificati pentru a actualiza statusul.");
            return;
        }

        Status statusVechi = defect.getStatus();
        defect.setStatus(statusNou);

        IntrareIstoric intrare = new IntrareIstoric();
        intrare.setDataModificare(LocalDateTime.now());
        intrare.setActiune("Status actualizat");
        intrare.setEfectuatDe(this.nume);
        intrare.setValoareVeche(statusVechi.toString());
        intrare.setValoareNoua(statusNou.toString());
        defect.adaugaIntrareIstoric(intrare);
    }

    /**
     * Marcheaza un defect ca rezolvat.
     *
     * @param defect defectul rezolvat
     * @param solutie descrierea solutiei aplicate
     */
    public void rezolvaDefect(Defect defect, String solutie) {
        if (!esteAutentificat) {
            System.out.println("Eroare: trebuie sa va autentificati pentru a rezolva un defect.");
            return;
        }

        Status statusVechi = defect.getStatus();
        defect.setStatus(Status.REZOLVAT);
        defect.setDataRezolvarii(LocalDateTime.now());

        IntrareIstoric intrare = new IntrareIstoric();
        intrare.setDataModificare(LocalDateTime.now());
        intrare.setActiune("Defect rezolvat. Solutie: " + solutie);
        intrare.setEfectuatDe(this.nume);
        intrare.setValoareVeche(statusVechi.toString());
        intrare.setValoareNoua(Status.REZOLVAT.toString());
        defect.adaugaIntrareIstoric(intrare);

        if (this.incarcareCurenta > 0) {
            this.incarcareCurenta--;
        }
    }

    /**
     * Adauga un comentariu pe un defect.
     * Permite inginerului sa ofere informatii suplimentare in timpul lucrului
     * sau sa documenteze pasi intermediari de diagnoza.
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
        System.out.println("Rol: Inginer");
        System.out.println("Specializare: " + specializare);
        System.out.println("Nivel experienta: " + nivelExperienta + "/5");
        System.out.println("Incarcare curenta: " + incarcareCurenta + " defecte active");
    }

    // Getters si setters
    public String getSpecializare() {
        return specializare;
    }

    public void setSpecializare(String specializare) {
        this.specializare = specializare;
    }

    public int getNivelExperienta() {
        return nivelExperienta;
    }

    public void setNivelExperienta(int nivelExperienta) {
        this.nivelExperienta = nivelExperienta;
    }

    public List<Defect> getDefecteAsignate() {
        return defecteAsignate;
    }

    public void setDefecteAsignate(List<Defect> defecteAsignate) {
        this.defecteAsignate = defecteAsignate;
    }

    public int getIncarcareCurenta() {
        return incarcareCurenta;
    }

    public void setIncarcareCurenta(int incarcareCurenta) {
        this.incarcareCurenta = incarcareCurenta;
    }
}