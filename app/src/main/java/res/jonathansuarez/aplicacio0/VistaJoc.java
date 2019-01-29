package res.jonathansuarez.aplicacio0;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.graphics.drawable.shapes.RectShape;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class VistaJoc extends View implements SensorEventListener {

    private Context context;
    private SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());

    // FILS I TEMPS
    // Fil encarregat de processar el joc
    private ThreadJoc fil=new ThreadJoc();
    // Cada quan colem processar canvis (ms)
    private static int PERIODE_PROCES=50;
    // Quan es va realitzar el darrer procés
    private long darrerProces=0;
    private Vector<Grafic> asteroides;
    private int numAsteroides = 5;
    private int numFragments = Integer.parseInt(pref.getString(getResources().getString(R.string.pa3_key), "3"));
    private Grafic nau;
    private int girNau;
    private double acceleracioNau;
    private static final int MAX_VELOCITAT_NAU=20;
    private static final int PAS_GIR_NAU=5;
    private static final float PAS_ACCELERACIO_NAU=0.5f;
    private float mX = 0, mY = 0;
    private boolean dispar = false;
    private boolean hihaValorInicial=false;
    private float valorInicial;
    //variable pel Missil
    private Grafic missil;
    private static int PAS_VELOCITAT_MISSIL=12;
    //private boolean missilActiu=false;
    //private int tempsMissil;
    private List<Integer> tempsMissils = new ArrayList<Integer>();
    private List<Boolean> missilsActiu = new ArrayList<Boolean>();
    private List<Grafic> missils = new ArrayList<Grafic>();
    private int numMissils = 20;
    private static int missilActual = 0;
    private Drawable[] drawableAsteroide = new Drawable[3];

    // variables pel so
    private SoundPool soundPool;
    private int idDispar, idExplosio;

    private SensorManager mSensorManager;

    public VistaJoc(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        //Inicialitzar so
        if (pref.getBoolean(getResources().getString(R.string.pa0_key), true)) {
            soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
            idDispar = soundPool.load(context, R.raw.dispar, 0);
            idExplosio = soundPool.load(context, R.raw.explosio, 0);
        }

        Drawable drawableNau, drawableMissil;
        //drawableAsteroide = context.getResources().getDrawable(R.drawable.asteroide1);
        //if (pref.getString("graficos", "1").equals("0")) {
        if (pref.getString(getResources().getString(R.string.pa2_key), "1").equals("0")) {
            setLayerType(View.LAYER_TYPE_SOFTWARE,null);
            //Gràfic vectorial nau
            Path pathNau = new Path();
            pathNau.moveTo((float)0,(float)0);
            pathNau.lineTo((float)1,(float)0.5);
            pathNau.lineTo((float)0,(float)1);
            pathNau.lineTo((float)0,(float)0);
            ShapeDrawable dNau = new ShapeDrawable(new PathShape(pathNau,1,1));
            dNau.getPaint().setColor(Color.GREEN);
            dNau.getPaint().setStyle(Paint.Style.FILL);
            dNau.setIntrinsicWidth(20);
            dNau.setIntrinsicHeight(15);
            drawableNau = dNau;
            //Gràfic vectorial asteroides
            Path pathAsteroide = new Path();
            pathAsteroide.moveTo((float)0.3, (float)0.0);
            pathAsteroide.lineTo((float)0.6, (float)0.0);
            pathAsteroide.lineTo((float)0.6, (float)0.3);
            pathAsteroide.lineTo((float)0.8, (float)0.2);
            pathAsteroide.lineTo((float)1.0, (float)0.4);
            pathAsteroide.lineTo((float)0.8, (float)0.6);
            pathAsteroide.lineTo((float)0.9, (float)0.9);
            pathAsteroide.lineTo((float)0.8, (float)1.0);
            pathAsteroide.lineTo((float)0.4, (float)1.0);
            pathAsteroide.lineTo((float)0.0, (float)0.6);
            pathAsteroide.lineTo((float)0.0, (float)0.2);
            pathAsteroide.lineTo((float)0.3, (float)0.0);
            for (int i = 0; i < 3; i++){
                ShapeDrawable dAsteroide = new ShapeDrawable(new PathShape(pathAsteroide,1,1));
                dAsteroide.getPaint().setColor(Color.WHITE);
                dAsteroide.getPaint().setStyle(Paint.Style.STROKE);
                dAsteroide.setIntrinsicWidth(50);
                dAsteroide.setIntrinsicHeight(50);
                setBackgroundColor(Color.BLACK);
                drawableAsteroide[i] = dAsteroide;
            }
            //Gràfic vectorial Missil
            /*ShapeDrawable dMissil = new ShapeDrawable(new RectShape());
            dMissil.getPaint().setColor(Color.WHITE);
            dMissil.getPaint().setStyle(Paint.Style.STROKE);
            dMissil.setIntrinsicWidth(15);
            dMissil.setIntrinsicHeight(3);
            drawableMissil=dMissil;*/
        } else{
            drawableAsteroide[0] = context.getResources().getDrawable(R.drawable.asteroide1);
            drawableAsteroide[1] = context.getResources().getDrawable(R.drawable.asteroide2);
            drawableAsteroide[2] = context.getResources().getDrawable(R.drawable.asteroide3);
            drawableNau = context.getResources().getDrawable(R.drawable.nau);
            drawableMissil = context.getResources().getDrawable(R.drawable.missil1);
        }
        nau = new Grafic(this, drawableNau);
        // missil = new Grafic(this, drawableMissil);
        asteroides=new Vector<Grafic>();
        for (int i=0; i<numAsteroides; i++) {
            Grafic asteroide = new Grafic(this, drawableAsteroide[0/*(int)(Math.random()*drawableAsteroide.length)*/]);
            asteroide.setIncY(Math.random()*4-2);
            asteroide.setIncX(Math.random()*4-2);
            asteroide.setAngle((int)(Math.random()*360));
            asteroide.setRotacio((int)(Math.random()*8-4));
            asteroides.add(asteroide);
        }
        // Registre el sensor d'orientació i indica gestió d'events.
        mSensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> llistaSensors = mSensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
        if (!llistaSensors.isEmpty()) {
            Sensor orientacioSensor = llistaSensors.get(0);
            mSensorManager.registerListener(this, orientacioSensor, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    // ACTUALITZA ELS VALORS DELS ELEMENTS, GESTIONA ELS MOVIMENTS
    synchronized protected void actualitzaFisica() {
        // Hora actual en milisegons
        long ara=System.currentTimeMillis();
        // No fer res si el periode de proces NO s'ha complert
        if (darrerProces+PERIODE_PROCES > ara) {
            return;
        }
        // Per una execució en temps real calculem retard
        double retard=(ara-darrerProces)/PERIODE_PROCES;
        darrerProces=ara; // Per la propera vegada
        // Actualitzem valocitat i direcció de la nau a partir de
        // girNau i acceleracioNau segons l'entrada del jugador
        nau.setAngle((int) (nau.getAngle()+girNau*retard));
        double nIncX = nau.getIncX()+acceleracioNau*Math.cos(
            Math.toRadians(nau.getAngle()))*retard;
        double nIncY = nau.getIncY()+acceleracioNau*Math.sin(
            Math.toRadians(nau.getAngle()))*retard;
        // Actualizem si el mòdul de la velocitat no passa el màxim
        if (Math.hypot(nIncX, nIncY) <= MAX_VELOCITAT_NAU) {
            nau.setIncX(nIncX);
            nau.setIncY(nIncY);
        }
        //Actualitzem les posicions X i Y
        nau.incrementaPos(retard);
        for (int i=0; i<asteroides.size(); i++) {
            asteroides.get(i).incrementaPos(retard);
        }
        // Actualitzem posició del missil
        for(int i = 0; i<missils.size(); i++){
            missils.get(i).incrementaPos(retard);
            double aux = tempsMissils.get(i) - retard;
            tempsMissils.set(i, (int)aux);
            if(aux < 1){
                missils.remove(i);
                tempsMissils.remove(i);
            }
            if(!missils.isEmpty()){
                for (int u = 0; u < asteroides.size(); u++){
                    if(missils.get(i).verificaColisio(asteroides.get(u))){
                        destrueixAsteroide(u);
                        missils.remove(i);
                        break;
                    }
                }
            }
        }
    }

    //CLASSE QUE CREA UN NOU FIL
    class ThreadJoc extends Thread {
        private boolean pausa, corrent;

        public synchronized void pausar() {
            pausa = true;
        }

        public synchronized void reanudar() {
            pausa = false;
            notify();
        }
        public synchronized void aturar() {
            corrent = false;
            if (pausa) reanudar();
        }

        @Override
        public void run() {
            corrent = true;
            while (corrent) {
                actualitzaFisica();
                synchronized (this) {
                    while (pausa){
                        try {
                            wait();
                        }catch (Exception e){

                        }
                    }
                }
            }
        }
    }

    @Override
    protected void onSizeChanged(int ample, int alt, int ample_ant, int alt_ant) {
        super.onSizeChanged(ample, alt, ample_ant, alt_ant);
        nau.setCenX(ample/2);
        nau.setCenY(alt/2);
        for (Grafic asteroide:asteroides){
            do {
                asteroide.setCenX((int) (Math.random()*ample));
                asteroide.setCenY((int) (Math.random()*alt));
            } while (asteroide.distancia(nau) < (ample+alt)/5);
        }
        //Llança un nou fil
        darrerProces = System.currentTimeMillis();
        fil.start();
    }

    @Override
    synchronized protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        nau.dibuixaGrafic(canvas);
        /*if (missilActiu){
            missil.dibuixaGrafic(canvas);
        }*/
        for(Grafic missil: missils){
            missil.dibuixaGrafic(canvas);
        }
        for (Grafic asteroide:asteroides){
            asteroide.dibuixaGrafic(canvas);
        }
    }

    //GESTIO D'EVENTS DE LA NAU AMB PANTALLA TACTIL

    @Override
    public boolean onTouchEvent(MotionEvent mevent) {
        super.onTouchEvent(mevent);
        float x = mevent.getX();
        float y = mevent.getY();
        switch (mevent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                dispar=true;
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = Math.abs(x-mX);
                float dy = Math.abs(y-mY);
                if (dy<6 && dx>6) {
                    //s'activa el gir si esta la opcio tactil esta activat en la configuració.
                    if (pref.getString(getResources().getString(R.string.pa7_key), "0").equals("0")) {
                        girNau = Math.round((x - mX) / 2);
                        dispar = false;
                    }
                } else if (dx<6 && dy>6) {
                    acceleracioNau = Math.round((mY-y)/25);
                    dispar = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                girNau = 0;
                acceleracioNau = 0;
                if (dispar) {
                    ActivaMissil();
                }
                break;
        }
        mX=x; mY=y;
        return true;
    }

    //METODES AUXILIARS
    private void destrueixAsteroide(int i){
        int tam;
        if (asteroides.get(i).getDrawable()!=drawableAsteroide[2]){
            if (asteroides.get(i).getDrawable()==drawableAsteroide[1]) {
                tam = 2; // [1]
            } else {
                tam = 1; // [0]
            }
            for (int n = 0; n < numFragments; n++) {
                Grafic asteroide = new Grafic(this, drawableAsteroide[tam]);
                asteroide.setCenX(asteroides.get(i).getCenX());
                asteroide.setCenY(asteroides.get(i).getCenY());
                asteroide.setIncX(Math.random()*7-2-tam);
                asteroide.setIncY(Math.random()*7-2-tam);
                asteroide.setAngle((int)(Math.random()*360));
                asteroide.setRotacio((int)(Math.random()*8-4));
                asteroides.add(asteroide);
            }
        }
        asteroides.remove(i);
        //missilActiu=false;
        if (pref.getBoolean(getResources().getString(R.string.pa0_key), true)) {
            soundPool.play(idExplosio, 1, 1, 0, 0, 1);
        }
    }

    private void ActivaMissil() {
        Drawable drawableMissil;
        if (pref.getString(getResources().getString(R.string.pa2_key), "1").equals("0")) {
            //Gràfic vectorial Missil
            ShapeDrawable dMissil = new ShapeDrawable(new RectShape());
            dMissil.getPaint().setColor(Color.WHITE);
            dMissil.getPaint().setStyle(Paint.Style.STROKE);
            dMissil.setIntrinsicWidth(15);
            dMissil.setIntrinsicHeight(3);
            drawableMissil=dMissil;
        } else {
            drawableMissil = this.context.getResources().getDrawable(R.drawable.missil1);
        }
        missil = new Grafic(this, drawableMissil);
        missil.setCenX(nau.getCenX());
        missil.setCenY(nau.getCenY());
        missil.setAngle(nau.getAngle());
        missil.setIncX(Math.cos(Math.toRadians(missil.getAngle()))*PAS_VELOCITAT_MISSIL);
        missil.setIncY(Math.sin(Math.toRadians(missil.getAngle()))*PAS_VELOCITAT_MISSIL);
        double tempsMissil = (int)Math.min(
                this.getWidth()/Math.abs(missil.getIncX()),
                this.getHeight()/Math.abs(missil.getIncY()))-2;
        missils.add(missil);
        tempsMissils.add((int)tempsMissil);
        //missilActiu=true;
        if (pref.getBoolean(getResources().getString(R.string.pa0_key), true)) {
            soundPool.play(idDispar, 1, 1, 1, 0, 1);
        }
    }

    // GESTIO D'EVENTS DE SENSORS PER LA NAU
    @Override
    public void onSensorChanged(SensorEvent event) {
        //s'activa el gir si esta activat la opcio de gir a la configuració.
        if (pref.getString(getResources().getString(R.string.pa7_key), "1").equals("1")) {
            float valor = event.values[1]; // eix Y
            if (!hihaValorInicial) {
                valorInicial = valor;
                hihaValorInicial = true;
            }
            girNau = (int) (valor - valorInicial) / 3;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    public ThreadJoc getFil() {
        return fil;
    }

    protected SensorManager getMSensorManager(){
        return mSensorManager;
    }
}
