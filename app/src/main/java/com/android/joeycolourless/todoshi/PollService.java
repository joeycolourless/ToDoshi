package com.android.joeycolourless.todoshi;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.joeycolourless.todoshi.datebase.ToDODbSchema;

import java.util.Date;
import java.util.List;


public class PollService extends IntentService {
    private static final String TAG = "PollService";
    private static final String TAG1 = "PollService1";
    private static final int NOTIFY_ID = 101;
    private static final int POLL_INTERVAL = 1000 * 60; //60 seconds
    private static final int TIME_DIFFERENCE = 15000 * 60; // 15 minutes
    public static final String ACTION_SHOW_NOTIFICATION = "com.android.joeycolourless.todoshi.SHOW_NOTIFICATION";

    public static Intent newIntent(Context context){
        return new Intent(context, PollService.class);
    }

    public static void setServiceAlarm(Context context, boolean isOn){
        Intent intent = PollService.newIntent(context);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (isOn){
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), POLL_INTERVAL, pendingIntent);
        } else {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
        QueryPreferences.setAlarmOn(context, isOn);
    }


    public PollService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.i(TAG, "Received in intent: " + intent);
        Context context = getApplicationContext();
        ToDoLab todoLab = ToDoLab.get(context);
        List<ToDo> toDos = todoLab.getToDos(ToDODbSchema.ToDoTable.NAME);



        for (ToDo toDo : toDos) {
            if (toDo.getNotificationDate().getTime() > toDo.getDate().getTime()) {
                long time = toDo.getNotificationDate().getTime() - new Date().getTime();
                if (time < TIME_DIFFERENCE && time > 0 && toDo.isNotification()) {

                    Intent notificationIntent = ToDoPagerActivity.newIntent(this, toDo.getId(), ToDODbSchema.ToDoTable.NAME);
                    PendingIntent contentIntent = PendingIntent.getActivity(context,
                            0, notificationIntent,
                            PendingIntent.FLAG_CANCEL_CURRENT);

                    Resources res = context.getResources();
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

                    builder.setContentIntent(contentIntent)
                            .setSmallIcon(R.drawable.ic_add_white_18dp) //small picture
                            .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.ic_view_carousel_black_32dp)) //big picture
                            .setWhen(System.currentTimeMillis())
                            .setAutoCancel(true);

                    NotificationManager notificationManager = (NotificationManager) context
                            .getSystemService(Context.NOTIFICATION_SERVICE);

                    builder.setContentTitle(toDo.getTitle())
                            .setContentText(toDo.getDetails());
                    Notification notification = builder.build();
                    notification.defaults = Notification.DEFAULT_ALL;
                    notificationManager.notify((int) toDo.getId().getLeastSignificantBits(), notification);
                    toDo.setNotification(false);
                    ToDoLab.get(context).updateToDo(toDo, ToDODbSchema.ToDoTable.NAME, ToDODbSchema.ToDoTable.Cols.UUID);
                }

            }
        }

        sendBroadcast(new Intent(ACTION_SHOW_NOTIFICATION));
    }
}
