package com.abhinav.personalto_dolist.Receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.abhinav.personalto_dolist.AddToDo;
import com.abhinav.personalto_dolist.HomeActivity;
import com.abhinav.personalto_dolist.Model.ToDoItem;
import com.abhinav.personalto_dolist.R;

/**
 * Created by abhinavsharma on 27-02-2016.
 */
public class AlarmReceiver extends WakefulBroadcastReceiver{

    private ToDoItem toDoItem;
    private Uri soundUri;
    private Ringtone ringtone;
    private static final String TAG = "AlarmReceiver";
    private NotificationManager nMgr;
    private Notification notification;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getStringExtra("type").equalsIgnoreCase("alarm")){
            toDoItem = intent.getParcelableExtra("item");
            Log.d(TAG,"Broadcast Received for "+toDoItem.getItem_name());
            if(toDoItem.getItem_notification()!=null){
//            soundUri = Uri.parse(toDoItem.getItem_notification());
                soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

            } else {
                soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }
            Intent openApp = new Intent(context,HomeActivity.class);
            openApp.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(openApp);
            ringtone = RingtoneManager.getRingtone(context,soundUri);
            ringtone.play();
        }
        else{
            Log.d(TAG,"Broadcast received for notification");
            nMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notification = intent.getParcelableExtra("Notification");
            nMgr.notify(3,notification);
        }


    }
}
