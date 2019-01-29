package res.jonathansuarez.aplicacio0;

import android.app.Activity;
import android.os.Bundle;

public class Preferencies extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PreferenciesFragment()).commit();
    }
}
