package res.jonathansuarez.aplicacio0;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class MagatzemPuntuacionsJson implements MagatzemPuntuacions {

    private String string; // Emmagatzema puntuacions en format JSON

    public MagatzemPuntuacionsJson() {
        string = "";
        guardarPuntuacio(45000, "Mi nombre", System.currentTimeMillis());
        guardarPuntuacio(31000, "Otro nombre", System.currentTimeMillis());
    }

    @Override
    public void guardarPuntuacio(int punts, String nom, long data) {
        // string = llegirString();
        List<Puntuacio> puntuacions = llegirJson(string);
        puntuacions.add(new Puntuacio(punts, nom, data));
        string = guardarJson(puntuacions);
        // guardarString(string);
    }

    @Override
    public Vector<String> llistaPuntuacions(int quantitat) {
        // string = llegirString();
        List<Puntuacio> puntuacions = llegirJson(string);
        Vector<String> salida = new Vector<>();
        for (Puntuacio puntuacio:puntuacions) {
            salida.add(puntuacio.getPunts()+" "+puntuacio.getNom());
        }
        return salida;
    }

    private String guardarJson(List<Puntuacio> puntuacions) {
        String string  = "";
        try {
            JSONArray jsonArray = new JSONArray();
            for (Puntuacio puntuacio:puntuacions) {
                JSONObject obj = new JSONObject();
                obj.put("punts",puntuacio.getPunts());
                obj.put("nom",puntuacio.getNom());
                obj.put("data",puntuacio.getData());
                jsonArray.put(obj);
            }
            string = jsonArray.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return string;
    }

    private List<Puntuacio> llegirJson(String string) {
        List<Puntuacio> puntuacions = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(string);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                puntuacions.add(new Puntuacio(obj.getInt("punts"),
                        obj.getString("nom"), obj.getLong("data")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return puntuacions;
    }
}
