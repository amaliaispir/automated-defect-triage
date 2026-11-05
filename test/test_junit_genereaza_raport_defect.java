package test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import proiect_triaj_defecte.AsistentTriaj;
import proiect_triaj_defecte.Defect;
import proiect_triaj_defecte.Inginer;
import proiect_triaj_defecte.Raport;
import proiect_triaj_defecte.Raportor;
import proiect_triaj_defecte.Status;

public class test_junit_genereaza_raport_defect {

    @Test
    void testGenereazaRaportPentruDefectNull() {
        AsistentTriaj asistent = new AsistentTriaj(1, "Asistent test");

        Raport raport = asistent.genereazaRaportDefect(null, 100);

        assertNull(raport);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("dateTestGenerareRaport")
    void testGenereazaRaportDefectPentruMaiMulteSituatii(
            String numeCaz,
            boolean aplicaTriaj,
            boolean rezolvaDefect,
            Status statusAsteptat,
            boolean asteptJustificareTriaj) {

        AsistentTriaj asistent = new AsistentTriaj(1, "Asistent test");

        Raportor raportor = new Raportor(
                1,
                "Ana Raportor",
                "ana@test.com",
                "1234",
                "Sudura"
        );

        raportor.autentificare("ana@test.com", "1234");

        Inginer inginer = new Inginer(
                2,
                "Mihai Inginer",
                "mihai@test.com",
                "1234",
                "Robotica",
                4
        );

        inginer.autentificare("mihai@test.com", "1234");

        Defect defect = raportor.raporteazaDefect(
                "Vibratii robot",
                "Robotul de sudura are vibratii puternice la axa 3",
                "Se porneste robotul RBT-04-A si apar vibratii in timpul sudurii",
                3,
                "RBT-04-A"
        );

        defect.setId(10);

        if (aplicaTriaj) {
            List<Inginer> ingineri = new ArrayList<>();
            ingineri.add(inginer);
            asistent.triazaDefect(defect, ingineri);
        }

        if (rezolvaDefect) {
            inginer.preiaDefect(defect);
            inginer.rezolvaDefect(defect, "Inlocuit rulment uzat la axa 3");
        } else {
            if (!aplicaTriaj) {
                defect.actualizeazaStatus(Status.IN_LUCRU, "Test");
            }
        }

        Raport raport = asistent.genereazaRaportDefect(defect, 101);

        assertNotNull(raport);
        assertEquals(statusAsteptat, defect.getStatus());

        String continut = raport.exporta();

        assertTrue(continut.contains("RAPORT FINAL DEFECT"));
        assertTrue(continut.contains("Vibratii robot"));
        assertTrue(continut.contains("RBT-04-A"));

        boolean existaIntrareRaport = defect.getIstoric()
                .stream()
                .anyMatch(intrare -> intrare.getActiune() != null
                        && intrare.getActiune().contains("Raport final generat"));

        assertTrue(existaIntrareRaport);

        if (asteptJustificareTriaj) {
            assertTrue(continut.contains("Defect clasificat ca"));
            assertFalse(continut.contains("Justificare triaj indisponibila"));
        } else {
            assertTrue(continut.contains("Justificare triaj indisponibila"));
        }
    }

    static Stream<Arguments> dateTestGenerareRaport() {
        return Stream.of(
                Arguments.of(
                        "Defect triat si rezolvat - raport final, defect inchis",
                        true,
                        true,
                        Status.INCHIS,
                        true
                ),

                Arguments.of(
                        "Defect netriat dar rezolvat - raport cu justificare indisponibila",
                        false,
                        true,
                        Status.INCHIS,
                        false
                ),

                Arguments.of(
                        "Defect triat dar nerezolvat - raport incomplet, status ramane ASIGNAT",
                        true,
                        false,
                        Status.ASIGNAT,
                        true
                ),

                Arguments.of(
                        "Defect netriat si nerezolvat - raport incomplet, status ramane IN_LUCRU",
                        false,
                        false,
                        Status.IN_LUCRU,
                        false
                )
        );
    }
}