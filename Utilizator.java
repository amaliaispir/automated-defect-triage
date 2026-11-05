package proiect_triaj_defecte;

import java.time.LocalDate;

/**
 * Clasa abstracta de baza pentru toti utilizatorii sistemului.
 */
public abstract class Utilizator {

    protected int id;
    protected String nume;
    protected String email;
    protected String parola;
    protected LocalDate dataInregistrarii;
    protected boolean esteAutentificat;  // flag de sesiune - true doar dupa autentificare cu succes

    public Utilizator() {
        this.id = 0;
        this.nume = "Necunoscut";
        this.email = "";
        this.parola = "";
        this.dataInregistrarii = LocalDate.now();
        this.esteAutentificat = false;
    }

    public Utilizator(int id, String nume, String email, String parola) {
        this.id = id;
        this.nume = nume;
        this.email = email;
        this.parola = parola;
        this.dataInregistrarii = LocalDate.now();
        this.esteAutentificat = false;
    }

    /**
     * Verifica daca datele primite corespund cu cele ale utilizatorului.
     * Daca da, marcheaza utilizatorul ca autentificat (deschide sesiunea).
     *
     * @param emailIntrodus emailul introdus la autentificare
     * @param parolaIntrodusa parola introdusa la autentificare
     * @return true daca datele sunt corecte, false altfel
     */
    public boolean autentificare(String emailIntrodus, String parolaIntrodusa) {
        if (emailIntrodus == null || parolaIntrodusa == null) {
            this.esteAutentificat = false;
            return false;
        }
        boolean valid = this.email.equals(emailIntrodus) && this.parola.equals(parolaIntrodusa);
        this.esteAutentificat = valid;
        return valid;
    }

    public void delogare() {
        this.esteAutentificat = false;
    }

    /**
     * Verifica daca utilizatorul este autentificat in prezent.
     *
     * @return true daca sesiunea este activa
     */
    public boolean esteAutentificat() {
        return esteAutentificat;
    }

    public void afisareDetalii() {
        System.out.println("ID: " + id);
        System.out.println("Nume: " + nume);
        System.out.println("Email: " + email);
        System.out.println("Data inregistrarii: " + dataInregistrarii);
        System.out.println("Autentificat: " + (esteAutentificat ? "Da" : "Nu"));
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getParola() {
        return parola;
    }

    public void setParola(String parola) {
        this.parola = parola;
    }

    public LocalDate getDataInregistrarii() {
        return dataInregistrarii;
    }

    public void setDataInregistrarii(LocalDate dataInregistrarii) {
        this.dataInregistrarii = dataInregistrarii;
    }
}