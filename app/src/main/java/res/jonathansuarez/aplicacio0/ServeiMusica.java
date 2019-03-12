package res.jonathansuarez.aplicacio0;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

public class ServeiMusica extends Service {

    MediaPlayer reproductor;
    private static final int ID_NOTIFICACIO_CREAR = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "Servei creat", Toast.LENGTH_SHORT).show();
        reproductor = MediaPlayer.create(this, R.raw.cumbia);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int idArranc) {
        super.onStartCommand(intent, flags, idArranc);
        Toast.makeText(this, "Servei arrancat " + idArranc, Toast.LENGTH_SHORT).show();
        reproductor.start();

        // Creacio de la notificació
        Notification.Builder notificacio = new Notification.Builder(this)
                .setContentTitle("Creant Servei de Musica") // titol que descriu la notificacio
                .setSmallIcon(R.mipmap.ic_launcher_round) // icona a visualitzar
                .setContentText("Informació addicional") // informació mes detallada
                .setLargeIcon(BitmapFactory.decodeResource(getResources()
                        ,android.R.drawable.ic_media_play))
                .setWhen(System.currentTimeMillis()+1000*60*60)
                .setShowWhen(true)
                //.setContentInfo("més info")
                .setSubText("més info")
                .setTicker("Text en barra d'estat");
        // Referencia que permet manejar les notificacions del sistema
        NotificationManager nm;
        nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Asociar activitat a notificacio
        PendingIntent intencioPendent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);
        notificacio.setContentIntent(intencioPendent);
        // Llança la notificacio
        nm.notify(ID_NOTIFICACIO_CREAR, notificacio.build());
        // El primer parametre indica un id per identificar aquesta notificacio
        // en el futur, i el segon la notificació.

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Servei aturat", Toast.LENGTH_SHORT).show();
        reproductor.stop();

        // Elimina la notificacio si el servei deixa d'estar actiu.
        NotificationManager nm;
        nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(ID_NOTIFICACIO_CREAR);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
