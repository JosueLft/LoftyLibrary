package br.com.reign.loftylibrary.model;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import br.com.reign.loftylibrary.R;
import br.com.reign.loftylibrary.activity.manga.MangaActivity;
import br.com.reign.loftylibrary.activity.notifications.Notification;

public class FCMService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        final Map<String, String> data = remoteMessage.getData();

        if(data == null) return;

        final Intent ii = new Intent(this, MangaActivity.class);

        FirebaseFirestore.getInstance().collection("/users")
                .document(data.get("sender"))
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, ii, 0);

                        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                        String notificationChannelId = "my_channel_id_01";

                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            NotificationChannel notificationChannel = new NotificationChannel(notificationChannelId, "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);

                            notificationChannel.setDescription("Channel description");
                            notificationChannel.enableLights(true);
                            notificationChannel.setLightColor(Color.RED);

                            notificationManager.createNotificationChannel(notificationChannel);
                        }

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), notificationChannelId);

                        builder.setAutoCancel(true)
                                .setSmallIcon(R.mipmap.lofty_logo)
                                .setContentTitle(data.get("title"))
                                .setContentText(data.get("body"))
                                .setContentIntent(pendingIntent);

                        notificationManager.notify(1, builder.build());
                    }
                });
    }
}
