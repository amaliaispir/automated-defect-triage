package test;

import proiect_triaj_defecte.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste JUnit pentru metoda genereazaContinut() din clasa Raport.
 *
 * Aceste teste verifica scenariul descris in diagrama de activitati
 * pentru metoda genereazaContinut(), acoperind toate ramurile decizionale
 * si buclele.
 */
public class RaportTest {

    private Defect defectComplet;
    private Raportor raportor;
    private Inginer inginer;

    /**
     * Pregatire date inainte de fiecare test - se executa automat.
     * Cream un defect complet "standard" cu toate campurile populate
     * si raportor + inginer asignat.
     */
    @BeforeEach
    void pregatireDate() {
        // raportor autentificat
        raportor = new Raportor(1, "Vasile", "vasile@fabrica.ro",
                "parola", "Asamblare");
        raportor.autentificare("vasile@fabrica.ro", "parola");

        // inginer autentificat
        inginer = new Inginer(2, "Ana", "ana@fabrica.ro",
                "parola", "Hidraulic", 4);
        inginer.autentificare("ana@fabrica.ro", "parola");

        // defect complet, raportat si rezolvat
        defectComplet = raportor.raporteazaDefect(
                "Scurgere ulei la presa hidraulica",
                "Se observa scurgere ulei la baza presei",
                "Vizibil dupa fiecare ciclu",
                1, "PRS-HID-01");
        defectComplet.setId(101);
        defectComplet.setComponentaAfectata("SistemHidraulic");
        defectComplet.setSeveritate(Severitate.CRITICA);
        defectComplet.setPrioritate(Prioritate.URGENTA);
        defectComplet.setInginerAsignat(inginer);
        inginer.preiaDefect(defectComplet);
        inginer.rezolvaDefect(defectComplet, "Inlocuit garnitura cilindrului principal");
    }

    /**
     * Test 1: defect NULL - acopera ramura "defect == null" → mesaj de eroare.
     */
    @Test
    void testDefectNull() {
        Raport raport = new Raport();
        raport.setDefectulRaportat(null);

        String continut = raport.genereazaContinut();

        assertNotNull(continut, "Metoda nu trebuie sa returneze null");
        assertTrue(continut.toLowerCase().contains("eroare"),
                "Cand defectul e null, raportul trebuie sa contina mesaj de eroare");
    }

    /**
     * Test 2: defect complet - verifica ca toate sectiunile importante apar in raport.
     */
    @Test
    void testDefectComplet() {
        Raport raport = new Raport(1, defectComplet,
                "Defect critic - scurgere ulei");

        String continut = raport.genereazaContinut();

        assertNotNull(continut);
        assertTrue(continut.contains("RAPORT FINAL DEFECT"), "Trebuie sa contina antet");
        assertTrue(continut.contains("Scurgere ulei la presa hidraulica"),
                "Trebuie sa contina titlul defectului");
        assertTrue(continut.contains("SistemHidraulic"), "Trebuie sa contina componenta");
        assertTrue(continut.contains("CRITICA"), "Trebuie sa contina severitatea");
        assertTrue(continut.contains("URGENTA"), "Trebuie sa contina prioritatea");
        assertTrue(continut.contains("Vasile"), "Trebuie sa contina raportorul");
        assertTrue(continut.contains("Ana"), "Trebuie sa contina inginerul");
        assertTrue(continut.contains("SFARSIT RAPORT"), "Trebuie sa contina footerul");
    }

    /**
     * Test 3: defect fara raportor - acopera ramura "raportor == null".
     */
    @Test
    void testDefectFaraRaportor() {
        defectComplet.setRaportor(null);
        Raport raport = new Raport(2, defectComplet, "test");

        String continut = raport.genereazaContinut();

        assertTrue(continut.contains("Raportor: necunoscut"),
                "Cand nu exista raportor, trebuie sa scrie 'necunoscut'");
        assertFalse(continut.contains("Vasile"),
                "Nu trebuie sa apara numele raportorului inlocuit");
    }

    /**
     * Test 4: defect fara inginer asignat - acopera ramura "inginer == null".
     */
    @Test
    void testDefectFaraInginer() {
        defectComplet.setInginerAsignat(null);
        Raport raport = new Raport(3, defectComplet, "test");

        String continut = raport.genereazaContinut();

        assertTrue(continut.contains("Inginer asignat: niciunul"),
                "Cand nu exista inginer, trebuie sa scrie 'niciunul'");
    }

    /**
     * Test 5: defect nerezolvat (fara dataRezolvarii) - acopera
     * ramura "dataRezolvarii == null".
     */
    @Test
    void testDefectNerezolvat() {
        Defect defectNou = new Defect(50, "Test", "Test descriere", "Test mod");
        defectNou.setRaportor(raportor);
        // nu setam dataRezolvarii - ramane null

        Raport raport = new Raport(4, defectNou, "test");
        String continut = raport.genereazaContinut();

        assertTrue(continut.contains("nerezolvat"),
                "Cand defectul nu e rezolvat, trebuie sa scrie 'nerezolvat inca'");
    }

    /**
     * Test 6: raport fara justificare triaj - acopera ramura
     * "justificareTriaj null sau gol".
     */
    @Test
    void testRaportFaraJustificare() {
        Raport raport = new Raport(5, defectComplet, "");  // justificare goala

        String continut = raport.genereazaContinut();

        assertTrue(continut.contains("fara justificare"),
                "Cand justificarea e goala, trebuie sa scrie '(fara justificare disponibila)'");
    }

    /**
     * Test 7: defect cu istoric gol - acopera ramura "istoric.isEmpty()".
     * Construim manual un defect fara intrari in istoric.
     */
    @Test
    void testIstoricGol() {
        Defect defectGol = new Defect();
        defectGol.setId(99);
        defectGol.setTitlu("Test");
        defectGol.setDescriere("Test");
        defectGol.getIstoric().clear();  // golim istoricul

        Raport raport = new Raport(6, defectGol, "test");
        String continut = raport.genereazaContinut();

        assertTrue(continut.contains("nu exista intrari"),
                "Cand istoricul e gol, trebuie sa apara mesajul corespunzator");
    }

    /**
     * Test 8: defect cu istoric multi-intrari - acopera bucla 1 cu N>=2 iteratii.
     * Defectul standard are deja mai multe intrari in istoric (creat, status, etc.),
     * dar adaugam si mai multe pentru a testa bucla.
     */
    @Test
    void testIstoricCuMaiMulteIntrari() {
        // adaugam intrari in plus
        IntrareIstoric intrare1 = new IntrareIstoric(1, "Test actiune 1", "User1", "vechi", "nou");
        IntrareIstoric intrare2 = new IntrareIstoric(2, "Test actiune 2", "User2", "vechi", "nou");
        defectComplet.adaugaIntrareIstoric(intrare1);
        defectComplet.adaugaIntrareIstoric(intrare2);

        int numarIntrari = defectComplet.getIstoric().size();
        assertTrue(numarIntrari >= 2, "Trebuie sa avem minim 2 intrari pentru a testa bucla");

        Raport raport = new Raport(7, defectComplet, "test");
        String continut = raport.genereazaContinut();

        assertTrue(continut.contains("Test actiune 1"), "Prima intrare trebuie sa apara");
        assertTrue(continut.contains("Test actiune 2"), "A doua intrare trebuie sa apara");
        // verificam si numerotarea
        assertTrue(continut.contains("1.") && continut.contains("2."),
                "Intrarile trebuie sa fie numerotate");
    }

    /**
     * Test 9: defect fara comentarii - acopera ramura "comentarii.isEmpty()".
     */
    @Test
    void testFaraComentarii() {
        defectComplet.getComentarii().clear();

        Raport raport = new Raport(8, defectComplet, "test");
        String continut = raport.genereazaContinut();

        assertTrue(continut.contains("fara comentarii"),
                "Cand nu exista comentarii, trebuie sa apara mesajul corespunzator");
    }

    /**
     * Test 10: defect cu multiple comentarii - acopera bucla 2 cu N>=2 iteratii.
     */
    @Test
    void testCuMaiMulteComentarii() {
        raportor.adaugaComentariu(defectComplet, "Primul comentariu de test");
        inginer.adaugaComentariu(defectComplet, "Al doilea comentariu de test");
        raportor.adaugaComentariu(defectComplet, "Al treilea comentariu");

        Raport raport = new Raport(9, defectComplet, "test");
        String continut = raport.genereazaContinut();

        assertTrue(continut.contains("Primul comentariu de test"));
        assertTrue(continut.contains("Al doilea comentariu de test"));
        assertTrue(continut.contains("Al treilea comentariu"));
    }

    /**
     * Test 11: verificarea calculului timpului de rezolvare.
     * Acopera apelul calculeazaTimpRezolvare() din ramura "dataRezolvarii != null".
     */
    @Test
    void testTimpRezolvare() {
        // setam manual datele pentru a controla timpul
        LocalDateTime acum = LocalDateTime.now();
        defectComplet.setDataRaportarii(acum.minusHours(2));  // raportat acum 2 ore
        defectComplet.setDataRezolvarii(acum);                  // rezolvat acum

        Raport raport = new Raport(10, defectComplet, "test");
        String continut = raport.genereazaContinut();

        assertTrue(continut.contains("Timp total rezolvare"),
                "Trebuie sa apara timpul de rezolvare cand defectul e rezolvat");
        long timpRezolvare = defectComplet.calculeazaTimpRezolvare();
        assertEquals(120, timpRezolvare, 1,
                "Timpul calculat trebuie sa fie aproximativ 120 minute");
    }
}
