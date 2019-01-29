package res.jonathansuarez.aplicacio0;

import java.util.Vector;

public interface MagatzemPuntuacions {
    void guardarPuntuacio(int punts, String nom, long data);
    Vector<String> llistaPuntuacions(int quantitat);
}
