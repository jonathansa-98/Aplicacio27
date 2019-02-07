package res.jonathansuarez.aplicacio0;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.Date;
import java.util.Vector;

public class MagatzemPuntuacionsSQLiteRelacional extends SQLiteOpenHelper implements MagatzemPuntuacions {

    public MagatzemPuntuacionsSQLiteRelacional(Context context) {
        super(context,"puntuacions",null,2);
    }

    @Override
    // Aquest metode nomes es cridata una vegada, quan el sistema detecti que la db encara no esta creada
    public void onCreate(SQLiteDatabase db) {
        // Aqui s'han de crear totes les taules de la BD, i inicialitzar les dades si es necessari.
        // CREATE TABLE nom_taula(nom_columna tipus [atributs], ... )
        db.execSQL("CREATE TABLE usuaris ( " +
                "usu_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nom TEXT, " +
                "correu TEXT)");
        db.execSQL("CREATE TABLE vpuntuacions2 ( " +
                "punts_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "punts INTEGER, " +
                "data LONG, " +
                "usuari INTEGER, " +
                "FOREIGN KEY(usuari) REFERENCES usuaris(usu_id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // En el cas d'una nova versió hauriem d'actualitzar les taules
        db.execSQL("CREATE TABLE usuaris ( " +
                "usu_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nom TEXT, " +
                "correu TEXT)");
        db.execSQL("CREATE TABLE vpuntuacions2 ( " +
                "punts_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "punts INTEGER, " +
                "data LONG, " +
                "usuari INTEGER, " +
                "FOREIGN KEY(usuari) REFERENCES usuaris(usu_id))");
        // Creem els usuaris
        Cursor cursor = db.rawQuery("SELECT _id, nom from vpuntuacions group by _id", null);
        while(cursor.moveToNext()){
            String nom = cursor.getString(1).toLowerCase();
            String correu = nom.charAt(0)+"@iesjoanramis.org";
            db.execSQL("INSERT INTO usuaris VALUES ("+cursor.getInt(0)+",'"+cursor.getString(1)+"','"+correu+"')");
        }
        // Afegim les puntuacions
        cursor = db.rawQuery("SELECT _id, punts, data from vpuntuacions", null);
        while(cursor.moveToNext()){
            Date date = new Date(cursor.getLong(2));
            String data_str = date.getDay()+"/"+date.getMonth()+"/"+date.getYear();
            db.execSQL("INSERT INTO vpuntuacions2 VALUES (null,"+cursor.getInt(1)+",'"+data_str +"',"+cursor.getInt(0)+")");
        }
    }

    @Override
    public void guardarPuntuacio(int punts, String nom, long data) {
        // Obté una referencia a la nostra BD en mode L/E.
        SQLiteDatabase db = getWritableDatabase();
        // Sentencia SQL que afegeix una fila a la taula.
        // INSERT INTO nom_taula VALUES (valor1, valor2, ... )
        // Els valors cadena van entre cometes;
        String correu = nom.toLowerCase().charAt(0)+"@iesjoanramis.org";
        Date date = new Date(data);
        String data_str = date.getDay()+"/"+date.getMonth()+"/"+date.getYear();

        db.execSQL("INSERT INTO usuaris VALUES (null, '"+nom+"', '"+correu+"')");

        Cursor cursor = db.rawQuery("SELECT usu_id FROM usuaris WHERE nom='"+nom+"'", null);
        cursor.moveToNext();
        int id = cursor.getInt(0);
        cursor.close();

        db.execSQL("INSERT INTO vpuntuacions2 VALUES (null, "+punts+", '"+data_str+"', "+id+")");
        db.close();
    }

    @Override
    public Vector<String> llistaPuntuacions(int quantitat) {
        Vector<String> result = new Vector<>();
        // Obte una referencia a la nostra BD en mode lectura
        SQLiteDatabase db = getReadableDatabase();
        // Obté un cursor que conté totes les files de la consulta.
        // SELECT columna1, ... FROM nom_taula ORDER BY columna [mode] LIMIT numero
        Cursor cursor = db.rawQuery("SELECT punts, data, nom FROM " +
                "vpuntuacions2, usuaris WHERE vpuntuacions2.usuari = usuaris.usu_id " +
                "ORDER BY punts DESC LIMIT " + quantitat, null);
        while (cursor.moveToNext()) {
            result.add(cursor.getInt(0) + " " + cursor.getString(1) +
                    " " + cursor.getString(2));
        }
        cursor.close();
        db.close();
        return result;
    }
}
