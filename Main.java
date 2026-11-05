package proiect_triaj_defecte;

import java.util.ArrayList;
import java.util.List;

/**
 * Clasa Main - demonstratie completa a functionalitatii aplicatiei
 * "Asistent pentru trierea defectelor" intr-o fabrica auto.
 *
 * Scenariul demonstreaza:
 *   1. Crearea sistemului (administrator, ingineri, raportori, asistent)
 *   2. Autentificarea utilizatorilor
 *   3. Adaugarea unui inginer nou de catre administrator
 *   4. Raportarea mai multor defecte cu severitati diferite
 *   5. Triajul automat (componenta + severitate + prioritate + asignare inginer)
 *   6. Procesarea defectelor de catre ingineri (preluare, comentarii, rezolvare)
 *   7. Generarea rapoartelor per defect (salvate in fisiere)
 *   8. Generarea raportului global (salvat in fisier)
 *   9. Testarea de cazuri limita 
 */
public class Main {

    public static void main(String[] args) {
        afiseazaTitlu("APLICATIE: ASISTENT TRIAJ DEFECTE - FABRICA AUTO");

        // 1 - INITIALIZARE SISTEM
        afiseazaTitlu("PASUL 1: INITIALIZARE SISTEM");

        // Asistentul de triaj - sistemul automat
        AsistentTriaj asistent = new AsistentTriaj(1, "AsistentVirtual");
        System.out.println("Asistent de triaj creat: " + asistent.getNume());
        System.out.println("Reguli implicite incarcate: " + asistent.getReguli().size());

        // Administrator
        Administrator admin = new Administrator(100, "Maria Director",
                "maria.director@fabrica.ro", "admin2026");
        System.out.println("\nAdministrator creat: " + admin.getNume());

        // Ingineri cu specializari si nivele de experienta diferite
        List<Inginer> ingineri = new ArrayList<>();

        Inginer ing1 = new Inginer(1, "Ion Popescu", "ion.popescu@fabrica.ro",
                "parola1", "Mecanic", 4);
        Inginer ing2 = new Inginer(2, "Ana Marinescu", "ana.marinescu@fabrica.ro",
                "parola2", "Electric", 3);
        Inginer ing3 = new Inginer(3, "Radu Stoica", "radu.stoica@fabrica.ro",
                "parola3", "Hidraulic", 5);
        Inginer ing4 = new Inginer(4, "Cristina Dobre", "cristina.dobre@fabrica.ro",
                "parola4", "Robotica", 2);
        Inginer ing5 = new Inginer(5, "Mihai Gheorghiu", "mihai.g@fabrica.ro",
                "parola5", "Automatizari", 4);

        ingineri.add(ing1);
        ingineri.add(ing2);
        ingineri.add(ing3);
        ingineri.add(ing4);
        ingineri.add(ing5);

        System.out.println("\nIngineri creati:");
        for (Inginer ing : ingineri) {
            System.out.println("  - " + ing.getNume() + " | "
                    + ing.getSpecializare() + " | nivel " + ing.getNivelExperienta() + "/5");
        }

        // Raportori - operatori de pe linia de productie
        Raportor op1 = new Raportor(10, "Vasile Radulescu", "vasile.r@fabrica.ro",
                "operator1", "Asamblare");
        Raportor op2 = new Raportor(11, "Elena Tudor", "elena.t@fabrica.ro",
                "operator2", "Sudura");

        System.out.println("\nRaportori creati:");
        System.out.println("  - " + op1.getNume() + " | departament " + op1.getDepartament());
        System.out.println("  - " + op2.getNume() + " | departament " + op2.getDepartament());

        // 2 - AUTENTIFICARE
        afiseazaTitlu("PASUL 2: AUTENTIFICARE UTILIZATORI");

        // Test 1: autentificare cu parola gresita
        System.out.println("Test 1 - autentificare cu parola GRESITA:");
        boolean ok = op1.autentificare("vasile.r@fabrica.ro", "parola_gresita");
        System.out.println("  Rezultat: " + (ok ? "REUSIT" : "ESUAT") + " (asteptat: ESUAT)");

        // Test 2: autentificare corecta pentru toti
        System.out.println("\nTest 2 - autentificare corecta pentru toti utilizatorii:");
        admin.autentificare("maria.director@fabrica.ro", "admin2026");
        op1.autentificare("vasile.r@fabrica.ro", "operator1");
        op2.autentificare("elena.t@fabrica.ro", "operator2");
        for (Inginer ing : ingineri) {
            ing.autentificare(ing.getEmail(), "parola" + ing.getId());
        }
        System.out.println("  Admin autentificat: " + admin.esteAutentificat());
        System.out.println("  Op1 autentificat: " + op1.esteAutentificat());
        System.out.println("  Op2 autentificat: " + op2.esteAutentificat());
        System.out.println("  Toti inginerii autentificati: " + ingineri.stream()
                .allMatch(Utilizator::esteAutentificat));

        // 3 - ADMINISTRATORUL ADAUGA UN INGINER NOU + O REGULA
        afiseazaTitlu("PASUL 3: ADMINISTRATORUL ADAUGA UN INGINER NOU");

        Inginer ingNou = new Inginer(6, "Stefan Lupu", "stefan.lupu@fabrica.ro",
                "parola6", "Mecanic", 1);
        admin.adaugaInginer(ingNou, ingineri);
        ingNou.autentificare(ingNou.getEmail(), "parola6");
        System.out.println("Total ingineri in sistem dupa adaugare: " + ingineri.size());

        // Configurare regula noua de catre administrator
        System.out.println("\nAdministratorul adauga o regula noua de triaj:");
        int reguliInainte = asistent.getReguli().size();
        admin.configureazaReguli(asistent, "fum",
                "SistemElectric", Severitate.CRITICA);
        System.out.println("  Reguli inainte: " + reguliInainte
                + " | Reguli dupa: " + asistent.getReguli().size());

        // 4 - RAPORTARE DEFECTE
        afiseazaTitlu("PASUL 4: RAPORTARE DEFECTE");

        // Lista globala pentru raportul administratorului
        List<Defect> toateDefectele = new ArrayList<>();

        // Defect 1 - CRITIC, scurgere ulei (pentru ing3 - Hidraulic, nivel 5)
        Defect defect1 = op1.raporteazaDefect(
                "Scurgere ulei la presa hidraulica",
                "Se observa scurgere ulei la baza presei hidraulice principale",
                "Vizibil dupa fiecare ciclu de presare",
                1, "PRS-HID-01");
        defect1.setId(101);
        toateDefectele.add(defect1);
        System.out.println("Defect 1 raportat: " + defect1.getTitlu());

        // Defect 2 - MAJOR, vibratii robot sudura (pentru ing4 - Robotica, nivel 2)
        Defect defect2 = op2.raporteazaDefect(
                "Vibratii puternice la robotul de sudura",
                "Robotul are vibratii anormale la sudarea pragului stang",
                "Apare la fiecare a 10-a caroserie procesata",
                3, "RBT-SUD-04");
        defect2.setId(102);
        toateDefectele.add(defect2);
        System.out.println("Defect 2 raportat: " + defect2.getTitlu());

        // Defect 3 - CRITIC, blocare banda transportoare
        Defect defect3 = op1.raporteazaDefect(
                "Blocare banda transportoare linia 2",
                "Banda transportoare s-a blocat complet, productia oprita",
                "Aparitie brusca la 14:30",
                2, "BND-02");
        defect3.setId(103);
        toateDefectele.add(defect3);
        System.out.println("Defect 3 raportat: " + defect3.getTitlu());

        // Defect 4 - MINOR, zgomot la banda
        Defect defect4 = op2.raporteazaDefect(
                "Zgomot anormal la banda transportoare",
                "Se aude un zgomot de uzura la rolele benzii",
                "Continuu, dar productia merge",
                3, "BND-03");
        defect4.setId(104);
        toateDefectele.add(defect4);
        System.out.println("Defect 4 raportat: " + defect4.getTitlu());

        // Defect 5 - COSMETICA, problema afisaj panou
        Defect defect5 = op1.raporteazaDefect(
                "Afisaj panou control intermitent",
                "Afisajul panoului de control linia 1 palpaie din cand in cand",
                "La fiecare 30 de minute aproximativ",
                1, "PNL-CTL-01");
        defect5.setId(105);
        toateDefectele.add(defect5);
        System.out.println("Defect 5 raportat: " + defect5.getTitlu());

        System.out.println("\nTotal defecte raportate: " + toateDefectele.size());

        // 5 - TRIAJ AUTOMAT
        afiseazaTitlu("PASUL 5: TRIAJ AUTOMAT");

        for (Defect d : toateDefectele) {
            System.out.println("\n>>> Triaj defect #" + d.getId() + " - " + d.getTitlu());
            asistent.triazaDefect(d, ingineri);
            System.out.println("  Componenta:    " + d.getComponentaAfectata());
            System.out.println("  Severitate:    " + d.getSeveritate());
            System.out.println("  Prioritate:    " + d.getPrioritate());
            System.out.println("  Status:        " + d.getStatus());
            if (d.getInginerAsignat() != null) {
                Inginer ing = d.getInginerAsignat();
                System.out.println("  Asignat lui:   " + ing.getNume()
                        + " (" + ing.getSpecializare() + ", nivel " + ing.getNivelExperienta() + ")");
            } else {
                System.out.println("  Asignat lui:   nimeni");
            }
        }

        System.out.println("\nIncarcarea inginerilor dupa triaj:");
        for (Inginer ing : ingineri) {
            System.out.println("  - " + ing.getNume() + " (" + ing.getSpecializare()
                    + "): " + ing.getIncarcareCurenta() + " defecte active");
        }

        // 6 - INGINERII PREIAU SI REZOLVA DEFECTELE
        afiseazaTitlu("PASUL 6: PROCESARE DEFECTE DE CATRE INGINERI");

        // Defect 1 - rezolvat de inginerul asignat (Hidraulic)
        if (defect1.getInginerAsignat() != null) {
            Inginer ing = defect1.getInginerAsignat();
            System.out.println(">>> " + ing.getNume() + " preia defectul #" + defect1.getId());
            ing.preiaDefect(defect1);
            ing.adaugaComentariu(defect1, "Am inceput diagnoza. Verific garnitura cilindrului principal.");
            ing.adaugaComentariu(defect1, "Garnitura uzata, comand piesa de schimb.");
            op1.adaugaComentariu(defect1, "Productia poate continua pe linia 2 pana se rezolva?");
            ing.adaugaComentariu(defect1, "Da, am izolat zona afectata. Inlocuire in 2 ore.");
            ing.rezolvaDefect(defect1, "Inlocuita garnitura cilindrului principal cu una noua de calitate superioara.");
            System.out.println("  Defect rezolvat. Status: " + defect1.getStatus());
        }

        // Defect 2 - rezolvat de inginerul asignat (Robotica)
        if (defect2.getInginerAsignat() != null) {
            Inginer ing = defect2.getInginerAsignat();
            System.out.println("\n>>> " + ing.getNume() + " preia defectul #" + defect2.getId());
            ing.preiaDefect(defect2);
            ing.adaugaComentariu(defect2, "Inspectez axele si rulmentii bratului robotic.");
            ing.adaugaComentariu(defect2, "Detectata uzura la rulmentul axei 3.");
            ing.rezolvaDefect(defect2, "Inlocuit rulmentul uzat la axa 3 si recalibrat robotul.");
            System.out.println("  Defect rezolvat. Status: " + defect2.getStatus());
        }

        // Defect 3 - rezolvat
        if (defect3.getInginerAsignat() != null) {
            Inginer ing = defect3.getInginerAsignat();
            System.out.println("\n>>> " + ing.getNume() + " preia defectul #" + defect3.getId());
            ing.preiaDefect(defect3);
            ing.adaugaComentariu(defect3, "Banda blocata din cauza unui obiect strain.");
            ing.rezolvaDefect(defect3, "Indepartat obiect strain (piesa cazuta de pe linie). Banda functionala.");
            System.out.println("  Defect rezolvat. Status: " + defect3.getStatus());
        }

        // Defect 4 - doar status actualizat (in lucru, neterminat)
        if (defect4.getInginerAsignat() != null) {
            Inginer ing = defect4.getInginerAsignat();
            System.out.println("\n>>> " + ing.getNume() + " preia defectul #" + defect4.getId());
            ing.preiaDefect(defect4);
            ing.adaugaComentariu(defect4, "Verific rolele benzii. Va dura - planificare mentenanta.");
            System.out.println("  Status actual: " + defect4.getStatus() + " (nerezolvat inca)");
        }

        // Defect 5 - rezolvat rapid
        if (defect5.getInginerAsignat() != null) {
            Inginer ing = defect5.getInginerAsignat();
            System.out.println("\n>>> " + ing.getNume() + " preia defectul #" + defect5.getId());
            ing.preiaDefect(defect5);
            ing.rezolvaDefect(defect5, "Resetat panou control. Cablu de date fixat.");
            System.out.println("  Defect rezolvat. Status: " + defect5.getStatus());
        }

        // 7 - GENERAREA RAPOARTELOR PER DEFECT
        afiseazaTitlu("PASUL 7: GENERARE RAPOARTE PER DEFECT");

        int idRaport = 1;
        for (Defect d : toateDefectele) {
            if (d.esteRezolvat()) {
                System.out.println("\n>>> Generez raport pentru defectul #" + d.getId());
                Raport raport = asistent.genereazaRaportDefect(d, idRaport++);
                if (raport != null) {
                    String calefisier = raport.salveazaInDirector("rapoarte");
                    if (calefisier != null) {
                        System.out.println("  Salvat in: " + calefisier);
                    }
                }
            } else {
                System.out.println("\n>>> Defectul #" + d.getId() + " inca nu e rezolvat - "
                        + "fara raport");
            }
        }

        // Exemplu raport
        System.out.println("\n--- EXEMPLU CONTINUT RAPORT (defectul #" + defect1.getId() + ") ---");
        Raport exemplu = new Raport(99, defect1,
                "Defect critic - scurgere ulei la presa hidraulica");
        System.out.println(exemplu.exporta());

        // 8 - RAPORT GLOBAL DE LA ADMINISTRATOR
        afiseazaTitlu("PASUL 8: RAPORT GLOBAL ADMINISTRATOR");

        System.out.println(admin.genereazaRaportGlobal(toateDefectele));
        String caleGlobal = admin.salveazaRaportGlobalInDirector(toateDefectele, "rapoarte");
        if (caleGlobal != null) {
            System.out.println("Raport global salvat in: " + caleGlobal);
        }

        // 9 - TESTE DE CAZURI LIMITA
        afiseazaTitlu("PASUL 9: TESTE DE CAZURI LIMITA");

        // Test: utilizator nelogat nu poate face nimic
        System.out.println("Test: raportor delogat incearca sa raporteze defect:");
        op1.delogare();
        Defect defectFail = op1.raporteazaDefect("test", "test", "test", 1, "X");
        System.out.println("  Rezultat: " + (defectFail == null ? "BLOCAT corect" : "PROBLEMA!"));

        // Test: triaj cu defect null
        System.out.println("\nTest: triaj cu defect NULL:");
        boolean rez = asistent.triazaDefect(null, ingineri);
        System.out.println("  Rezultat: " + (rez ? "REUSIT (PROBLEMA!)" : "RESPINS corect"));

        // Test: triaj cu lista de ingineri goala
        System.out.println("\nTest: triaj cu lista ingineri GOALA:");
        op2.autentificare(op2.getEmail(), "operator2");
        Defect d_test = op2.raporteazaDefect("Test", "test scurgere ulei", "test", 1, "T-1");
        d_test.setId(999);
        boolean rezTriaj = asistent.triazaDefect(d_test, new ArrayList<>());
        System.out.println("  Triaj reusit: " + rezTriaj);
        System.out.println("  Inginer asignat: "
                + (d_test.getInginerAsignat() == null ? "NIMENI (corect)" : "PROBLEMA!"));

        // Test: autentificare cu email null
        System.out.println("\nTest: autentificare cu email NULL:");
        Raportor opTest = new Raportor(99, "Test", "test@x.ro", "p", "Test");
        boolean rezAut = opTest.autentificare(null, "p");
        System.out.println("  Rezultat: " + (rezAut ? "REUSIT (PROBLEMA!)" : "ESUAT corect"));

        afiseazaTitlu("DEMO FINALIZAT CU SUCCES");

        System.out.println("Statistici finale:");
        System.out.println("  Total defecte raportate:  " + toateDefectele.size());
        System.out.println("  Total defecte rezolvate:  "
                + toateDefectele.stream().filter(Defect::esteRezolvat).count());
        System.out.println("  Total defecte procesate de asistent: " + asistent.getDefecteProcessate());
        System.out.println("  Total ingineri in sistem: " + ingineri.size());
        System.out.println("  Total reguli triaj:       " + asistent.getReguli().size());
    }

    private static void afiseazaTitlu(String titlu) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  " + titlu);
        System.out.println("=".repeat(60));
    }
}