package res.jonathansuarez.aplicacio0;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

public class Joc extends Activity {

    private VistaJoc vistaJoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.joc);
        vistaJoc = (VistaJoc) findViewById(R.id.VistaJoc);
        vistaJoc.setVistaVictoria(findViewById(R.id.Victoria));
        vistaJoc.setPare(this);
    }

    @Override
    protected void onPause() {
        vistaJoc.getFil().pausar();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        vistaJoc.getFil().reanudar();
    }

    @Override
    protected void onStop() {
        vistaJoc.getMSensorManager().unregisterListener(vistaJoc);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        vistaJoc.getFil().aturar();
        super.onDestroy();
    }
}
