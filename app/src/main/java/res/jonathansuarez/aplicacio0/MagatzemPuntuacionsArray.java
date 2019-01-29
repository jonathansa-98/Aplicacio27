package res.jonathansuarez.aplicacio0;

import java.util.Vector;

public class MagatzemPuntuacionsArray implements MagatzemPuntuacions {
    private Vector<String> puntuacions;

    public MagatzemPuntuacionsArray() {
        puntuacions = new Vector<String>();
        puntuacions.add("120874 Cipri Muñoz");
        puntuacions.add("841273 Marc López");
        puntuacions.add("128412 Biel López");
        puntuacions.add("128371 Pep Malle");
        puntuacions.add("124891 Guiem Lluch");
        puntuacions.add("219481 Manel Pons");
        puntuacions.add("712122 Mariano Sanchez");
        puntuacions.add("102941 Christian Diaz");
        puntuacions.add("298414 Carlos Esteban");
        puntuacions.add("123000 Pepito Dominguez");
        puntuacions.add("111000 Pedro Martinez");
        puntuacions.add("219831 Paco Pèrez");
        puntuacions.add("123793 Pepito Pèrez");
        puntuacions.add("124812 Marco Guix");
        puntuacions.add("597122 Esteban Pèrez");
        puntuacions.add("571293 Uxias Kalim");
        puntuacions.add("581231 Mohamed Ali");
        puntuacions.add("182312 Flint Gary");
        puntuacions.add("591212 Carla Nuñez");
        puntuacions.add("591200 Sara Melar");
    }

    @Override
    public void guardarPuntuacio(int punts, String nom, long data) {
        puntuacions.add(0,punts+" "+nom);
    }

    @Override
    public Vector<String> llistaPuntuacions(int quantitat) {
        return puntuacions;
    }
}
