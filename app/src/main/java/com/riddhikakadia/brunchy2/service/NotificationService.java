package com.riddhikakadia.brunchy2.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.riddhikakadia.brunchy2.R;
import com.riddhikakadia.brunchy2.ui.MainActivity;
import com.riddhikakadia.brunchy2.ui.RecipesListActivity;
import com.riddhikakadia.brunchy2.util.Constants;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by RKs on 1/7/2017.
 */

public class NotificationService extends FirebaseMessagingService {

    private final String LOG_TAG = NotificationService.class.getSimpleName();
    final int notificationID = 101;

    Bitmap recipe_image = null;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        //Log.d(LOG_TAG, "*** In FirebaseMessagingService");

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.chef2)
                .setContentTitle(remoteMessage.getData().get("title"))
                .setContentText(remoteMessage.getData().get("message"))
                .setTicker(remoteMessage.getData().get("title"));

        try {
            URL url = new URL(remoteMessage.getData().get("picture"));
            //Log.d(LOG_TAG, "*** image URL: " + url.toString());

            recipe_image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (MalformedURLException mfu) {

        } catch (IOException ioe) {

        }
        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle().bigPicture(recipe_image);
        bigPictureStyle.setSummaryText(remoteMessage.getData().get("message").toString());
        mBuilder.setStyle(bigPictureStyle);

//        Notification notification = new NotificationCompat.Builder(this)
//                .setContentTitle(remoteMessage.getNotification().getTitle())
//                .setContentText(remoteMessage.getNotification().getBody())
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .build();

        Intent resultIntent = new Intent(this, RecipesListActivity.class);
        String searchString = remoteMessage.getData().get("search");
        resultIntent.putExtra(Constants.RECIPE_TO_SEARCH, searchString);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        mBuilder.setContentIntent(pendingIntent);

//        NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
//        manager.notify(123, notification);

        NotificationManager mNotificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(notificationID, mBuilder.build());
    }
}
