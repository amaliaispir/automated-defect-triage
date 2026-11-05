package proiect_triaj_defecte;

public enum Status {
    NOU,        
    IN_TRIAJ,   
    ASIGNAT,  
    IN_LUCRU,  
    REZOLVAT,
    INCHIS,     // raportul a fost generat, defectul e inchis
    RESPINS     // duplicat sau invalid
}