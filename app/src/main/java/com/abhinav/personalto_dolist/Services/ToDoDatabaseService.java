package com.abhinav.personalto_dolist.Services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.abhinav.personalto_dolist.AddToDo;
import com.abhinav.personalto_dolist.DatabaseUtils.ToDoDBHelper;
import com.abhinav.personalto_dolist.Model.ToDoItem;
import com.abhinav.personalto_dolist.Receivers.AlarmReceiver;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by abhinavsharma on 27-02-2016.
 */
public class ToDoDatabaseService extends Service {

    private ToDoItem item;
    private ToDoDBHelper mToDoDBHelper;
    private static final String TAG = "ToDoDatabaseService";


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent.getStringExtra("query_type").equalsIgnoreCase("ADD")){
            mToDoDBHelper = ToDoDBHelper.getInstance(getApplicationContext());
            item = intent.getParcelableExtra("item");
            long rowid = ToDoDBHelper.addItem(mToDoDBHelper,item);
            Log.d(TAG,"Item Added at "+rowid);
            Intent sendRowId = new Intent(AddToDo.ITEM_ADDED_BROADCAST);
            sendRowId.putExtra("rowid",(int)rowid);
            sendBroadcast(sendRowId);
            Toast.makeText(getApplicationContext(),"ToDo item added successfully!!",Toast.LENGTH_SHORT).show();
        }
        else if(intent.getStringExtra("query_type").equalsIgnoreCase("DISMISS")){
            int id = intent.getIntExtra("id",-1);
            ToDoDBHelper.deleteItem(id);
        }
        else if(intent.getStringExtra("query_type").equalsIgnoreCase("SNOOZE")){
            ToDoItem toDoItem = intent.getParcelableExtra("item");
            int id = intent.getIntExtra("id",-1);
            Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            Date date = new Date();
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            try {
                date = df.parse(toDoItem.getItem_date() + " " + toDoItem.getItem_time());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            calendar.setTime(date);
            calendar.add(Calendar.MINUTE,-2);
            Intent alarmIntent = new Intent(getBaseContext(), AlarmReceiver.class);
            alarmIntent.putExtra("item", toDoItem);
            alarmIntent.putExtra("type", "alarm");
            PendingIntent pendingIntentAlarm = PendingIntent.getBroadcast(getBaseContext(), id, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntentAlarm);
            Log.d(TAG, "Alarm Set");
        }
        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }
}
