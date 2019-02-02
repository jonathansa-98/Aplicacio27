package res.jonathansuarez.aplicacio0;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.Vector;

public class MagatzemPuntuacionsFitxerExtern implements MagatzemPuntuacions{

    private static String FITXER = Environment.getExternalStorageDirectory() + "/puntuacions.txt";
    private Context context;

    public MagatzemPuntuacionsFitxerExtern(Context context) {
        this.context = context;
    }

    @Override
    public void guardarPuntuacio(int punts, String nom, long data) {
        try {
            String estatSD = Environment.getExternalStorageState();
            if(estatSD.equals(Environment.MEDIA_MOUNTED)){
                // Podem llegir i escriure
                FileOutputStream f = new FileOutputStream(FITXER, true);
                String text = punts + " " + nom + "\n";
                f.write(text.getBytes());
                f.close();
            } else {
                Toast.makeText(context, "No pots guardar puntuacions", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("Asteroides", e.getMessage(), e);
        }
    }

    @Override
    public Vector<String> llistaPuntuacions(int quantitat) {
        Vector<String> result = new Vector<String>();
        try {
            String estatSD = Environment.getExternalStorageState();
            if(estatSD.equals(Environment.MEDIA_MOUNTED) || estatSD.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
                // Podem llegir i escriure
                FileInputStream f = new FileInputStream(FITXER);
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
            } else {
                // NO podem llegir ni escriure
                Toast.makeText(context, "No pots llegir puntuacions", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("Asteroides", e.getMessage(), e);
        }
        return result;
    }
}
