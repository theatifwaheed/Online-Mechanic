package com.onlinemachenic.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.onlinemachenic.R;


public class MyFirebaseMessagingService extends FirebaseMessagingService
{


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this);

        String click_action = remoteMessage.getNotification().getClickAction();

        if (remoteMessage.getData().size() > 0)
        {
            Log.d("TAG", "onMessageReceived: " + remoteMessage.getData());
        }


        String data = remoteMessage.getData().toString();

        Intent intent=new Intent(click_action);
        intent.putExtra("noti", remoteMessage.getData().toString());
        Log.d("MY_TAG", "onMessageReceived: " + data);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder=new NotificationCompat.Builder(this);
        notificationBuilder.setContentTitle("title");
        notificationBuilder.setContentText("message");
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setContentIntent(pendingIntent);
        NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,notificationBuilder.build());
    }
}
