package test;

import proiect_triaj_defecte.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste JUnit pentru metoda asigneazaInginer() din clasa AsistentTriaj.
 *
 * Aceste teste verifica scenariul descris in diagrama de activitati
 * pentru metoda asigneazaInginer(), acoperind:
 *   - cazurile de validare a intrarii (null/lista goala)
 *   - cele 4 incercari in cascada (de la criterii stricte la relaxate)
 *   - metodele helper apelate (determinaNivelMinim, determinaSpecializareNecesara)
 *
 * Testele acopera toate ramurile decizionale si cazurile limita.
 */
public class AsistentTriajTest {

    private AsistentTriaj asistent;
    private Defect defectCritic;
    private Defect defectMajor;
    private Defect defectMinor;

    /**
     * Pregatire date inainte de fiecare test.
     */
    @BeforeEach
    void pregatireDate() {
        asistent = new AsistentTriaj(1, "AsistentTest");

        // defect critic - va necesita inginer nivel 3+
        defectCritic = new Defect(101, "Scurgere ulei",
                "Scurgere ulei la presa hidraulica", "Continuu");
        defectCritic.setComponentaAfectata("SistemHidraulic");
        defectCritic.setSeveritate(Severitate.CRITICA);

        // defect major - va necesita nivel 2+
        defectMajor = new Defect(102, "Vibratii robot",
                "Vibratii la robot sudura", "Periodic");
        defectMajor.setComponentaAfectata("RobotSudura");
        defectMajor.setSeveritate(Severitate.MAJORA);

        // defect minor - orice nivel
        defectMinor = new Defect(103, "Zgomot banda",
                "Zgomot la banda transportoare", "Continuu");
        defectMinor.setComponentaAfectata("BandaTransportoare");
        defectMinor.setSeveritate(Severitate.MINORA);
    }

    /**
     * Test 1: lista de ingineri NULL - acopera Decizia 1 ramura DA.
     */
    @Test
    void testListaInginerInull() {
        Inginer rezultat = asistent.asigneazaInginer(defectCritic, null);
        assertNull(rezultat, "Cu lista null trebuie sa returneze null");
    }

    /**
     * Test 2: lista de ingineri GOALA - acopera Decizia 1 ramura DA.
     */
    @Test
    void testListaInginerGoala() {
        List<Inginer> ingineri = new ArrayList<>();
        Inginer rezultat = asistent.asigneazaInginer(defectCritic, ingineri);
        assertNull(rezultat, "Cu lista goala trebuie sa returneze null");
    }

    /**
     * Test 3: Incercarea 1 reuseste - exista inginer cu specializare
     * potrivita si nivel suficient.
     * Pentru defect CRITIC pe SistemHidraulic, asteptam un Hidraulic cu nivel >= 3.
     */
    @Test
    void testIncercarea1Reuseste() {
        List<Inginer> ingineri = new ArrayList<>();
        ingineri.add(new Inginer(1, "Ana", "ana@x.ro", "p", "Hidraulic", 5));
        ingineri.add(new Inginer(2, "Ion", "ion@x.ro", "p", "Mecanic", 4));

        Inginer ales = asistent.asigneazaInginer(defectCritic, ingineri);

        assertNotNull(ales);
        assertEquals("Ana", ales.getNume(),
                "Trebuie ales Ana - singurul Hidraulic cu nivel >= 3");
    }

    /**
     * Test 4: mai multi ingineri potriviti - se alege cel cu INCARCAREA MINIMA.
     */
    @Test
    void testIncarcareMinimaCriteriu() {
        Inginer ana = new Inginer(1, "Ana", "ana@x.ro", "p", "Hidraulic", 5);
        ana.setIncarcareCurenta(5); // ocupat

        Inginer maria = new Inginer(2, "Maria", "maria@x.ro", "p", "Hidraulic", 4);
        maria.setIncarcareCurenta(1); // libera

        Inginer radu = new Inginer(3, "Radu", "radu@x.ro", "p", "Hidraulic", 3);
        radu.setIncarcareCurenta(3); // mediu

        List<Inginer> ingineri = new ArrayList<>();
        ingineri.add(ana);
        ingineri.add(maria);
        ingineri.add(radu);

        Inginer ales = asistent.asigneazaInginer(defectCritic, ingineri);

        assertEquals("Maria", ales.getNume(),
                "Trebuie aleasa Maria - cu incarcarea minima dintre Hidraulici cu nivel suficient");
    }

    /**
     * Test 5: Incercarea 2 - exista doar Hidraulici cu nivel SUB minim.
     * Pentru defect CRITIC (necesita nivel 3+), avem doar Hidraulic nivel 2.
     * Trebuie sa-l accept pe el (incercarea 2: ignora nivelul).
     */
    @Test
    void testIncercarea2Reuseste() {
    	Inginer ing1 = new Inginer(1, "Ana", "ana@x.ro", "p", "Hidraulic", 2);
    	Inginer ing2 = new Inginer(2, "Maria", "maria@x.ro", "p", "Mecanic", 5);
    	
        List<Inginer> ingineri = new ArrayList<>();
        ingineri.add(ing1); // nivel 2
        ingineri.add(ing2); // nivel 5

        Inginer ales = asistent.asigneazaInginer(defectCritic, ingineri);

        assertNotNull(ales);
        assertEquals("Ana", ales.getNume(),
                "Trebuie ales Ana - singurul Hidraulic, chiar daca nivelul e mic");
    }

    /**
     * Test 6: Incercarea 3 - nu exista nimeni cu specializarea ceruta (Hidraulic),
     * dar exista cineva cu nivelul potrivit.
     * Daca sunt mai multi cu nivelul potrivit este ales primul gasit.
     */
    @Test
    void testIncercarea3Reuseste() {
        List<Inginer> ingineri = new ArrayList<>();
        ingineri.add(new Inginer(1, "Amalia", "amalia@x.ro", "p", "Mecanic", 2));
        ingineri.add(new Inginer(2, "Daniel", "daniel@x.ro", "p", "Electric", 3));
        ingineri.add(new Inginer(3, "Ana", "ana@x.ro", "p", "Mecanic", 5));
        

        // defect cere Hidraulic, nu avem niciunul
        Inginer ales = asistent.asigneazaInginer(defectCritic, ingineri);

        assertNotNull(ales);
        // doar Ana are nivel >= 3
        assertTrue(ales.getNume().equals("Daniel"),
                "Trebuie ales unul cu nivel suficient, indiferent de specializare");
    }

    /**
     * Test 7: Incercarea 4 - nimeni nu indeplineste niciun criteriu.
     * Defect CRITIC, dar avem doar specializari nepotrivite si nivele de experiente
     * insuficiente. Este ales cel cu incarcarea minima
     */
    @Test
    void testIncercarea4LastResort() {
        Inginer junior1 = new Inginer(1, "Andrei", "andrei@x.ro", "p", "Mecanic", 1);
        junior1.setIncarcareCurenta(2);

        Inginer junior2 = new Inginer(2, "Alex", "alex@x.ro", "p", "Electric", 2);
        junior2.setIncarcareCurenta(1);

        List<Inginer> ingineri = new ArrayList<>();
        ingineri.add(junior1);
        ingineri.add(junior2);
        
        // defect CRITIC pe Hidraulic, dar nu avem Hidraulic, nici nivel 3+
        Inginer ales = asistent.asigneazaInginer(defectCritic, ingineri);

        assertNotNull(ales);
        assertEquals("Alex", ales.getNume(),
                "Trebuie ales Alex - cu incarcarea minima");
    }

    /**
     * Test 8: determinaNivelMinim pentru CRITICA - acopera ramura case CRITICA.
     */
    @Test
    void testNivelMinimCritica() {
        int nivel = asistent.determinaNivelMinim(Severitate.CRITICA);
        assertEquals(3, nivel, "CRITICA trebuie sa necesite nivel minim 3");
    }

    /**
     * Test 9: determinaNivelMinim pentru MAJORA - acopera ramura case MAJORA.
     */
    @Test
    void testNivelMinimMajora() {
        int nivel = asistent.determinaNivelMinim(Severitate.MAJORA);
        assertEquals(2, nivel, "MAJORA trebuie sa necesite nivel minim 2");
    }

    /**
     * Test 10: determinaNivelMinim pentru MINORA - acopera ramura default.
     */
    @Test
    void testNivelMinimMinora() {
        int nivel = asistent.determinaNivelMinim(Severitate.MINORA);
        assertEquals(1, nivel, "MINORA trebuie sa necesite nivel minim 1");
    }

    /**
     * Test 11: determinaNivelMinim pentru COSMETICA - tot ramura default.
     */
    @Test
    void testNivelMinimCosmetica() {
        int nivel = asistent.determinaNivelMinim(Severitate.COSMETICA);
        assertEquals(1, nivel, "COSMETICA trebuie sa necesite nivel minim 1");
    }

    /**
     * Test 12: determinaSpecializareNecesara pentru componente cunoscute.
     */
    @Test
    void testMaparespecializareCunoscute() {
        assertEquals("Robotica",
                asistent.determinaSpecializareNecesara("RobotSudura"));
        assertEquals("Hidraulic",
                asistent.determinaSpecializareNecesara("SistemHidraulic"));
        assertEquals("Electric",
                asistent.determinaSpecializareNecesara("SistemElectric"));
        assertEquals("Mecanic",
                asistent.determinaSpecializareNecesara("BandaTransportoare"));
        assertEquals("Automatizari",
                asistent.determinaSpecializareNecesara("Automatizari"));
    }

    /**
     * Test 13: determinaSpecializareNecesara pentru componenta NECUNOSCUTA -
     * acopera ramura default.
     */
    @Test
    void testMaparespecializareDefault() {
        String spec = asistent.determinaSpecializareNecesara("ComponentaInexistenta");
        assertEquals("Mecanic", spec,
                "Pentru componenta necunoscuta trebuie sa returneze Mecanic (default)");
    }

    /**
     * Test 14: componenta NULL - test de robustete.
     */
    @Test
    void testMaparespecializareNull() {
        String spec = asistent.determinaSpecializareNecesara(null);
        assertEquals("Mecanic", spec,
                "Pentru componenta null trebuie sa returneze tot Mecanic");
    }

    /**
     * Test 15: defect MINOR, ales cel cu specializare potrivita.
     * Pentru defect minor pe BandaTransportoare, asteptam Mecanic.
     */
    @Test
    void testDefectMinorAlegereSpecializare() {
        Inginer ana = new Inginer(1, "Ana", "ana@x.ro", "p", "Mecanic", 1);
        Inginer ion = new Inginer(2, "Ion", "ion@x.ro", "p", "Hidraulic", 5);

        List<Inginer> ingineri = new ArrayList<>();
        ingineri.add(ana);
        ingineri.add(ion);

        Inginer ales = asistent.asigneazaInginer(defectMinor, ingineri);

        assertEquals("Ana", ales.getNume(),
                "Pentru BandaTransportoare se cere Mecanic - chiar si cu nivel 1 (defectul e MINOR)");
    }

    /**
     * Test 16: defect MAJOR, alegere intre Robotica - se prefera nivelul suficient.
     */
    @Test
    void testDefectMajorAlegereNivel() {
        // pentru MAJORA pe RobotSudura: necesita Robotica + nivel 2+
        Inginer junior = new Inginer(1, "Alex", "alex@x.ro", "p", "Robotica", 1);
        Inginer mediu = new Inginer(2, "Razvan", "razvan@x.ro", "p", "Robotica", 3);

        List<Inginer> ingineri = new ArrayList<>();
        ingineri.add(junior);
        ingineri.add(mediu);

        Inginer ales = asistent.asigneazaInginer(defectMajor, ingineri);

        assertEquals("Razvan", ales.getNume(),
                "Pentru defect MAJOR trebuie ales cel cu nivel >= 2 ");
    }
}
