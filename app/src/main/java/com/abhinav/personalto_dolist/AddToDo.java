package com.abhinav.personalto_dolist;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.abhinav.personalto_dolist.Model.ToDoItem;
import com.abhinav.personalto_dolist.Receivers.AlarmReceiver;
import com.abhinav.personalto_dolist.Services.LatLongService;
import com.abhinav.personalto_dolist.Services.ToDoDatabaseService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by abhinavsharma on 25-02-2016.
 */
public class AddToDo extends AppCompatActivity {

    private EditText itemName;
    private TextView itemNotificationSound, itemDate, itemTime, itemLocation;
    private Switch isItemSoundEnabled;
    private LocationManager mlocationManager;
    private static final int PICK_AUDIO_REQ_CODE = 1;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 122;
    private Uri audioURI;
    private static final String TAG = "AddToDo";
    private Toolbar toolbar;
    private String address;
    private AddressResultReceiver addressResultReceiver;
    private ToDoItem toDoItem;
    private PendingIntent pendingIntentAlarm, pendingIntentNotificationActivity, pendingIntentNotificationBroadcast;
    private AlarmManager alarmManager;
    public final static String ITEM_ADDED_BROADCAST = "ItemAdded";
    private BroadcastReceiver broadcastReceiver;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_todo_activity);
        initialize();
    }

    @Override
    protected void onStart() {
        IntentFilter intentFilter = new IntentFilter(ITEM_ADDED_BROADCAST);
        registerReceiver(broadcastReceiver, intentFilter);
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.todo_save_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (validations()) {
            toDoItem = new ToDoItem();
            toDoItem.setItem_name(itemName.getText().toString());
            toDoItem.setItem_date(itemDate.getText().toString());
            toDoItem.setItem_time(itemTime.getText().toString());
            if (address != null)
                toDoItem.setItem_location(address);
            if (itemNotificationSound.getTag() != null)
                toDoItem.setItem_notification(itemNotificationSound.getTag().toString());

            Intent saveItemIntent = new Intent(AddToDo.this, ToDoDatabaseService.class);
            saveItemIntent.putExtra("item", toDoItem);
            saveItemIntent.putExtra("query_type", "ADD");
            startService(saveItemIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void initialize() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        itemName = (EditText) findViewById(R.id.item_name);
        itemTime = (TextView) findViewById(R.id.item_time);
        itemDate = (TextView) findViewById(R.id.item_date);
        itemLocation = (TextView) findViewById(R.id.item_location);
        itemNotificationSound = (TextView) findViewById(R.id.item_notification_sound);
        isItemSoundEnabled = (Switch) findViewById(R.id.item_notification);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add Reminder");

        itemDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddToDo.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        monthOfYear++;
                        itemDate.setText(dayOfMonth + "/" + monthOfYear + "/" + year);
                    }
                },
                        Calendar.getInstance().get(Calendar.YEAR),
                        Calendar.getInstance().get(Calendar.MONTH),
                        Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        itemTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddToDo.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        itemTime.setText(hourOfDay + ":" + minute);
                    }
                },
                        Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                        Calendar.getInstance().get(Calendar.MINUTE), true);
                timePickerDialog.show();
            }
        });

        isItemSoundEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    itemNotificationSound.setTag("Alarm Notification Enabled");
                    Toast.makeText(AddToDo.this, "Alarm Notification enabled", Toast.LENGTH_SHORT).show();
//                    setAlarmPicker();
                } else {
                    itemNotificationSound.setText("Notification");
                    itemNotificationSound.setOnClickListener(null);
                    Toast.makeText(AddToDo.this, "Alarm Notification disabled", Toast.LENGTH_SHORT).show();
                }
            }
        });


        itemLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                    if (ContextCompat.checkSelfPermission(AddToDo.this,
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(AddToDo.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                    } else {
                        if (checkPlayServices()) {
                            mlocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                            if (!mlocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                showGPSDisabledAlertToUser();

                            } else {
                                showLocationChooserDialog();
                            }
                        } else
                            Toast.makeText(AddToDo.this, "Location feature not available on device", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int rowId = intent.getIntExtra("rowid", -1);
                if (rowId != -1) {
                    Log.d("Broadcast received with", "" + rowId);
                    setUpAlarm(toDoItem, rowId);
                    setUpNotification(toDoItem, rowId);
                    finish();
                }
            }
        };


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (checkPlayServices()) {
                    mlocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    if (!mlocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        showGPSDisabledAlertToUser();

                    } else {
                        showLocationChooserDialog();
                    }
                } else
                    Toast.makeText(AddToDo.this, "Location feature not available on device", Toast.LENGTH_SHORT).show();

            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void updateAddressOnUI(String address) {
        itemLocation.setText(address);
    }

    private boolean validations() {
        if (itemName.getText().toString().isEmpty()) {
            Snackbar.make(itemName, "Reminder description is mandatory", Snackbar.LENGTH_LONG).show();
            return false;
        } else if (itemTime.getText().toString().isEmpty()) {
            Snackbar.make(itemName, "Reminder time is mandatory", Snackbar.LENGTH_LONG).show();
            return false;
        } else if (itemLocation.getText().toString().isEmpty()) {
            Snackbar.make(itemName, "Location is mandatory", Snackbar.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private void setAlarmPicker() {
        itemNotificationSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent audioPickerIntent = new Intent();
                audioPickerIntent.setType("audio/*");
                audioPickerIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(audioPickerIntent, "Select audio:"), PICK_AUDIO_REQ_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PICK_AUDIO_REQ_CODE && resultCode == RESULT_OK) {
            audioURI = data.getData();
            itemNotificationSound.setText(getFileName(audioURI));
            itemNotificationSound.setTag(audioURI);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(this, "This device does not support google play services.", Toast.LENGTH_LONG).show();
            }
            return false;
        }
        return true;
    }

    public void showGPSDisabledAlertToUser() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Location Services Not Active");
        builder.setMessage("Please enable Location Services and GPS");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        Dialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

    }

    @SuppressLint("ParcelCreator")
    public class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultCode == LatLongService.STATUS_PASS) {
                address = resultData.getString("address");
                Log.d("Test", address);
                updateAddressOnUI(address);
            }
            super.onReceiveResult(resultCode, resultData);
        }
    }

    private void showLocationChooserDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Choose location");
        dialogBuilder.setMessage("What location you want to use?");
        dialogBuilder.setPositiveButton("Current Location", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addressResultReceiver = new AddressResultReceiver(new Handler());
                Intent intent = new Intent(AddToDo.this, LatLongService.class);
                intent.putExtra("receiver", addressResultReceiver);
                startService(intent);
            }
        });
        dialogBuilder.setNegativeButton("Other", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                userInputAddress();
            }
        });
        Dialog dialog = dialogBuilder.create();
        dialog.show();
    }

    private void userInputAddress() {
        final Dialog dialog = new Dialog(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_dialog_layout, null);
        dialog.setContentView(view);
        Button submit = (Button) view.findViewById(R.id.submit);
        final EditText address = (EditText) view.findViewById(R.id.user_address);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAddressOnUI(address.getText().toString());
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void setUpAlarm(ToDoItem toDoItem, int id) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Date date = new Date();
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
            date = df.parse(toDoItem.getItem_date() + " " + toDoItem.getItem_time());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setTime(date);
        Intent alarmIntent = new Intent(AddToDo.this, AlarmReceiver.class);
        alarmIntent.putExtra("item", toDoItem);
        alarmIntent.putExtra("type", "alarm");
        pendingIntentAlarm = PendingIntent.getBroadcast(AddToDo.this, toDoItem.hashCode(), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntentAlarm);
        Log.d(TAG, "Alarm Set for " + date);

    }

    public void setUpNotification(ToDoItem toDoItem, int id) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Date date_prior = new Date();
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
            date_prior = df.parse(toDoItem.getItem_date() + " " + toDoItem.getItem_time());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setTime(date_prior);
        calendar.add(Calendar.MINUTE, -4);
        Intent notifyUser = new Intent(this, AlarmReceiver.class);
        Intent notificationIntent = new Intent(this, HomeActivity.class);
        Intent dismissIntent = new Intent(AddToDo.this, ToDoDatabaseService.class);
        Intent snoozeIntent = new Intent(AddToDo.this, ToDoDatabaseService.class);
        dismissIntent.putExtra("id", id);
        snoozeIntent.putExtra("id", id);
        snoozeIntent.putExtra("item", toDoItem);
        dismissIntent.putExtra("query_type", "dismiss");
        snoozeIntent.putExtra("query_type", "snooze");
        snoozeIntent.putExtra("hash", toDoItem.hashCode());
        PendingIntent pendingDismiss = PendingIntent.getService(this, 1, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingSnooze = PendingIntent.getService(this, 0, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notifyUser.putExtra("type", "notification");
        pendingIntentNotificationActivity = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setContentIntent(pendingIntentNotificationActivity)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setStyle(
                        new android.support.v4.app.NotificationCompat.BigTextStyle()
                                .bigText(toDoItem.getItem_name() + " at " + toDoItem.getItem_location()));

        NotificationCompat.Action dAction = new android.support.v4.app.NotificationCompat.Action(R.drawable.icon_dismiss, "Dismiss", pendingDismiss);
        NotificationCompat.Action sAction = new android.support.v4.app.NotificationCompat.Action(R.drawable.icon_snooze, "Snooze", pendingSnooze);
        notificationBuilder.addAction(dAction);
        notificationBuilder.addAction(sAction);

        notifyUser.putExtra("Notification", notificationBuilder.build());
        pendingIntentNotificationBroadcast = PendingIntent.getBroadcast(this, toDoItem.hashCode() + 1, notifyUser, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntentNotificationBroadcast);
    }

    @Override
    protected void onStop() {
        stopService(new Intent(AddToDo.this, LatLongService.class));
        unregisterReceiver(broadcastReceiver);
        super.onStop();
    }
}
