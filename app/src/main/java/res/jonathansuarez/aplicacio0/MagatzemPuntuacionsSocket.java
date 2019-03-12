package res.jonathansuarez.aplicacio0;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Vector;

public class MagatzemPuntuacionsSocket implements MagatzemPuntuacions {

    String ip = "192.168.1.40"; // ip mi pc
    int port = 4321;
    Vector<String> result = new Vector<String>();

    MagatzemPuntuacionsSocket() {
        // StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
    }

    @Override
    public void guardarPuntuacio(int punts, String nom, long data) {
        new GuardaSocketAsynTask().execute(punts,nom,data);
    }

    @Override
    public Vector<String> llistaPuntuacions(int quantitat) {
        LlistaSocketAsynTask llista = new LlistaSocketAsynTask();
        llista.execute(result,quantitat);
        return result;
    }

    private class GuardaSocketAsynTask extends AsyncTask<Object, Void, Void> {

        @Override
        protected Void doInBackground(Object... objects) {
            int punts = (int)objects[0];
            String nom = (String)objects[1];
            // La connexió sempre s'ha de realitzar dins try-catch ja que conectar pot donar error
            try {
                // Realitza connexió
                Socket sk = new Socket(ip, port);
                // Objecte que representa el flux de dades d'entrada per xarxa
                BufferedReader entrada = new BufferedReader(new InputStreamReader(sk.getInputStream()));
                // Objecte que representa el flux de dades sortida per xarxa
                PrintWriter sortida = new PrintWriter(new OutputStreamWriter(sk.getOutputStream()),true);
                // Envia per xarxa una cadena
                sortida.println(punts+" "+nom);
                // Llegeix una cadena des de la xarxa
                String resposta = entrada.readLine();
                if (!resposta.equals("OK")) {
                    Log.e("Asteroides", "Error: resposta del servidor incorrecte");
                }
                // Tanca la connexió
                sk.close();
            } catch (IOException e) {
                Log.e("Asteroides", e.toString(), e);
            }
            return null;
        }

    }

    private class LlistaSocketAsynTask extends AsyncTask<Object, Void, Vector<String>> {

        @Override
        protected Vector<String> doInBackground(Object... objects) {
            Vector<String> puntuacions = (Vector<String>) objects[0];
            int quantitat = (int)objects[1];
            try {
                // Realitza connexió
                Socket sk = new Socket(ip, port);
                // Objecte que representa el flux de dades d'entrada per xarxa
                BufferedReader entrada = new BufferedReader(new InputStreamReader(sk.getInputStream()));
                // Objecte que representa el flux de dades sortida per xarxa
                PrintWriter sortida = new PrintWriter(new OutputStreamWriter(sk.getOutputStream()),true);
                // Envia per xarxa una cadena
                sortida.println("PUNTS");
                int n = 0;
                String resposta;
                do {
                    resposta = entrada.readLine();
                    if (resposta!=null) {
                        puntuacions.add(resposta);
                        n++;
                    }
                } while (n < quantitat && resposta!=null);
                // Tanca la connexió
                sk.close();
            } catch (IOException e) {
                Log.e("Asteroides", e.toString(), e);
            }
            return puntuacions;
        }

        @Override
        protected void onPostExecute(Vector<String> strings) {
            super.onPostExecute(strings);
            result = new Vector<String>(strings);
        }
    }
}
