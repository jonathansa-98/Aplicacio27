package res.jonathansuarez.aplicacio0;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.util.Map;
import java.util.Vector;

public class MagatzemPuntuacionsPreferencies implements MagatzemPuntuacions {

    // Aquest valor servirà per doar nom al fitxer xml
    private static String PREFERENCIES = "puntuacions";
    private Context context;

    public MagatzemPuntuacionsPreferencies(Context context) {
        this.context = context;
    }

    @Override
    public void guardarPuntuacio(int punts, String nom, long data) {
        SharedPreferences preferencies = context.getSharedPreferences(PREFERENCIES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencies.edit();
        for (int n = 9; n >= 1; n--) {
            editor.putString("puntuacio" + n,
                preferencies.getString("puntuacio" + (n - 1), ""));
        }
        editor.putString("puntuacio0", punts+ " " + nom);
        editor.commit();
    }

    @Override
    public Vector<String> llistaPuntuacions(int quantitat) {
        Vector<String> result = new Vector<String>();
        SharedPreferences preferencies = context.getSharedPreferences(PREFERENCIES, Context.MODE_PRIVATE);
        for (int n = 0; n <= 9; n++) {
            String s = preferencies.getString("puntuacio" + n, "");
            if (!s.isEmpty()) {
                result.add(s);
            }
        }
        return result;
    }
}
