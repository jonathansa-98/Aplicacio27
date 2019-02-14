package res.jonathansuarez.aplicacio0;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity implements View.OnClickListener, View.OnLongClickListener, GestureOverlayView.OnGesturePerformedListener {

    private TextView titol;
    private Button btnJoc;
    private Button btnConfig;
    private Button bAcercaDe;
    private Button bSortir;
    public static MagatzemPuntuacions magatzem;

    // Animacio
    private Animation animacio_gir;
    private Animation animacio_apareixer;
    private Animation animacio_desplaca_dreta;
    private GestureLibrary llibreria;
    // private MediaPlayer mp;

    // Preferencies
    private SharedPreferences pref;

    // puntuacions
    private String nom;
    private int puntuacio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        if (pref.getBoolean(getResources().getString(R.string.pa1_key), true)) {
            startService(new Intent(MainActivity.this, ServeiMusica.class));
            // mp = MediaPlayer.create(this, R.raw.cumbia);
        }
        titol= findViewById(R.id.titol);
        /*animacio_gir = AnimationUtils.loadAnimation(this, R.anim.gir_amb_zoom);
        titol.startAnimation(animacio_gir);*/

        btnJoc = findViewById(R.id.button_jugar);
        btnJoc.setOnClickListener(this);
        /*animacio_apareixer = AnimationUtils.loadAnimation(this, R.anim.apareixer);
        btnJoc.startAnimation(animacio_apareixer);*/

        btnConfig= findViewById(R.id.button_config);
        btnConfig.setOnClickListener(this);
        btnConfig.setOnLongClickListener(this);
        /*btnConfig.setOnLongClickListener(new View.OnLongClickListener() {
             @Override
             public boolean onLongClick(View v) {
                 mostrarPreferencies(null);
                 return true;
             }
         });*/
        /*animacio_desplaca_dreta = AnimationUtils.loadAnimation(this, R.anim.desplacament_dreta);
        btnConfig.startAnimation(animacio_desplaca_dreta);*/

        bAcercaDe= findViewById(R.id.button_acerca);
        bAcercaDe.setOnClickListener(this);
        /*bAcercaDe.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                bAcercaDe.startAnimation(animacio_gir);
                llancarAcercaDe(null);
            }
        });*/

        bSortir= findViewById(R.id.button_salir);
        bSortir.setOnClickListener(this);
        bSortir.setOnLongClickListener(this);

        llibreria = GestureLibraries.fromRawResource(this, R.raw.gestures);
        if (!llibreria.load()) finish();
        GestureOverlayView gesturesView = findViewById(R.id.gestures);
        gesturesView.addOnGesturePerformedListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // On es guardara.
        magatzem = getModeGuardat();
        //Toast.makeText(this, "onStart", Toast.LENGTH_SHORT).show();
    }

    private MagatzemPuntuacions getModeGuardat() {
        String val = pref.getString(getResources().getString(R.string.pa8_key), "0");
        int opcio = Integer.parseInt(val);
        switch (opcio) {
            case 0: return new MagatzemPuntuacionsArray();
            case 1: return new MagatzemPuntuacionsPreferencies(MainActivity.this);
            case 2: return new MagatzemPuntuacionsFitxerIntern(MainActivity.this);
            case 3: return new MagatzemPuntuacionsFitxerExtern(MainActivity.this);
            case 4: return new MagatzemPuntuacionsXML_SAX(MainActivity.this);
            case 5: return new MagatzemPuntuacionsGson();
            case 6: return new MagatzemPuntuacionsJson();
            case 7: return new MagatzemPuntuacionsSQLite(MainActivity.this);
            case 8: return new MagatzemPuntuacionsSQLiteRelacional(MainActivity.this);
            case 9: return new MagatzemPuntuacionsProvider(MainActivity.this);
            case 10: return new MagatzemPuntuacionsSocket();
        }
        return new MagatzemPuntuacionsArray();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (pref.getBoolean(getResources().getString(R.string.pa1_key), true)) {
            /*if (mp == null) {
                mp = MediaPlayer.create(this, R.raw.cumbia);
            }
            if (!mp.isPlaying()) {
                mp.start();
            }*/
        }
        //Toast.makeText(this, "onResume", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        // Toast.makeText(this, "onPause", Toast.LENGTH_SHORT).show();
        super.onPause();
    }

    @Override
    protected void onStop() {
        // Toast.makeText(this, "onStop", Toast.LENGTH_SHORT).show();
        if (pref.getBoolean(getResources().getString(R.string.pa1_key), true)) {
            // mp.pause();
        }
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // Toast.makeText(this, "onRestart", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        // Toast.makeText(this, "onDestroy", Toast.LENGTH_SHORT).show();
        if (pref.getBoolean(getResources().getString(R.string.pa1_key), true)) {
            stopService(new Intent(MainActivity.this, ServeiMusica.class));
        }
        super.onDestroy();
    }

    //----------------------Tracta els listeners dels botones----------

    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
        double major_prediccio = 0;
        String major_prediccio_nom = "";
        ArrayList<Prediction> predictions = llibreria.recognize(gesture);
        titol.setText("");
        for (Prediction prediction : predictions){
            if (major_prediccio<prediction.score){
                major_prediccio = prediction.score;
                major_prediccio_nom = prediction.name;
            }
        }
        titol.setText(major_prediccio_nom);
        switch (major_prediccio_nom) {
            case "jugar":
                llancarJoc(null);
                break;
            case "configurar":
                llancarConfig(null);
                break;
            case "verpreferencias":
                mostrarPreferencies(null);
                break;
            case "acercade":
                llancarAcercaDe(null);
                break;
            case "cancelar":
                finish();
                break;
            case "puntuaciones":
                llancarPuntuacions(null);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_jugar:
                llancarJoc(v);
                break;
            case R.id.button_config:
                llancarConfig(v);
                break;
            case R.id.button_acerca:
                //bAcercaDe.startAnimation(animacio_gir);
                llancarAcercaDe(v);
                break;
            case R.id.button_salir:
                finish();
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.button_config:
                mostrarPreferencies(v);
                break;
            case R.id.button_salir:
                llancarPuntuacions(v);
                break;
        }
        return true;
    }

    //-----------------------Tractament del menú-----------------------

    // Mostra el menú
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //Crea l'objecte en Java que representa el menú
        MenuInflater infl=getMenuInflater();
        //Associa el menú creat en XML a l'objecte Java
        infl.inflate(R.menu.menu_main, menu);
        //Indica que es vol visualitzar(activar) el menú
        return true;
    }

    // Cada vegada que es selecciona el menú es crida el següent mètode
    // Per que tracti els esdeveniments capturats.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if (id==R.id.menuconfiguracio){
            llancarConfig(null);
            //Indica que l'event ha sigut tractat i que no s'ha de propagar más.
            return true;
        }
        if (id==R.id.menuacercade) {
            llancarAcercaDe(null);
            //Indica que l'event ha sigut tractat i que no s'ha de propagar más.
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //------------------------------------------------------------------

    private void llancarJoc(View view) {
        Intent i = new Intent(this, Joc.class);
        startActivityForResult(i,1234);
    }

    // Metode que es crida de forma automatica quan finalitza l'activitat secundaria.
    // Permet llegir les dades retornades.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1234 && resultCode == RESULT_OK && data != null) {
            puntuacio = data.getExtras().getInt("puntuacio");
            // nom = "Jo";
            // Millor si ho llegim des d'un dialeg o una nova activitat
            // AlertDialog.Builder
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Usuario");
            builder.setMessage("Escriu el teu nom d'usuari.");
            final EditText entrada = new EditText(MainActivity.this);
            entrada.setText("Jo");
            entrada.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(entrada);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    nom = entrada.getText().toString();
                    if (nom.isEmpty()){
                        nom = "Jo";
                    }
                    // guardar puntuacio
                    magatzem.guardarPuntuacio(puntuacio, nom, System.currentTimeMillis());
                    llancarPuntuacions(null);
                }
            });
            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    nom = entrada.getText().toString();
                    if (nom.isEmpty()){
                        nom = "Jo";
                    }
                    // guardar puntuacio
                    magatzem.guardarPuntuacio(puntuacio, nom, System.currentTimeMillis());
                    llancarPuntuacions(null);
                }
            });
            builder.show();
        }
    }

    public void llancarConfig(View view) {
        Intent i= new Intent(this, Preferencies.class);
        startActivity(i);
    }

    public void llancarAcercaDe(View view) {
        Intent i=new Intent(this, AcercaDe.class);
        startActivity(i);
    }

    public void mostrarPreferencies(View view){
        // SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String s = "musica: "+ pref.getBoolean(getResources().getString(R.string.pa1_key), false) +
                    "\ntipus gràfics: "+ pref.getString(getResources().getString(R.string.pa2_key), "1") +
                    "\nnúmero fragmentos: "+ pref.getString(getResources().getString(R.string.pa3_key), "3") +
                    "\nactivar multijugador: "+ pref.getBoolean(getResources().getString(R.string.pa4_key), false) +
                    "\nmàxim jugadores: "+ pref.getString(getResources().getString(R.string.pa5_key), "1") +
                    "\ntipus connexió: "+ pref.getString(getResources().getString(R.string.pa6_key), "0") +
                    "\ntipus controls: "+ pref.getString(getResources().getString(R.string.pa7_key), "0");
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    // Métode que s'executa quan pitjem boto_sortir.
    // Llança una nova activitat per mostrar les puntuacions.
    public void llancarPuntuacions(View view) {
        Intent i = new Intent(this, Puntuacions.class);
        startActivity(i);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (pref.getBoolean(getResources().getString(R.string.pa1_key), true)) {
            // int pos = mp.getCurrentPosition();
            // outState.putInt("pos", pos);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (pref.getBoolean(getResources().getString(R.string.pa1_key), true)) {
            // int pos = savedInstanceState.getInt("pos");
            // mp.seekTo(pos);
        }
    }
}
