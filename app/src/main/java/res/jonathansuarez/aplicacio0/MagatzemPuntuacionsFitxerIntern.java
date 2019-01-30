package res.jonathansuarez.aplicacio0;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

public class MagatzemPuntuacionsFitxerIntern implements MagatzemPuntuacions{

    private static String FITXER = "puntuacions.txt";
    private Context context;

    public MagatzemPuntuacionsFitxerIntern(Context context) {
        this.context = context;
    }

    @Override
    public void guardarPuntuacio(int punts, String nom, long data) {
        try {
            FileOutputStream f = context.openFileOutput(FITXER, Context.MODE_APPEND);
            String text = punts + " " + nom + "\n";
            f.write(text.getBytes());
            f.close();
        } catch (Exception e) {
            Log.e("Asteroides", e.getMessage(), e);
        }
    }

    @Override
    public Vector<String> llistaPuntuacions(int quantitat) {
        Vector<String> result = new Vector<String>();
        try {
            FileInputStream f = context.openFileInput(FITXER);
            BufferedReader entrada = new BufferedReader(new InputStreamReader(f));
            int n = 0;
            String linia;
            do {
                linia = entrada.readLine();
                if (linia != null) {
                    result.add(linia);
                    n++;
                }
            } while (n < quantitat && linia != null);
            f.close();
        } catch (Exception e) {
            Log.e("Asteroides", e.getMessage(), e);
        }
        return result;
    }
}
