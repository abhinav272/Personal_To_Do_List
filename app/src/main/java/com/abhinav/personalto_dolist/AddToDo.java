package com.abhinav.personalto_dolist;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by abhinavsharma on 25-02-2016.
 */
public class AddToDo extends AppCompatActivity {

    private EditText itemName;
    private TextView itemNotificationSound, itemDate,itemTime, itemLocation;
    private Switch isItemSoundEnabled;
    private static final int PICK_AUDIO_REQ_CODE = 1;
    private Uri audioURI;
    private static final String TAG = "AddToDo";
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_todo_activity);
        initialize();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.todo_save_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(validations()){
            //todo item to be saved in DB
        }
        return super.onOptionsItemSelected(item);
    }

    private void initialize(){
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
                        itemDate.setText(dayOfMonth+"/"+monthOfYear+"/"+year);
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
                        itemTime.setText(hourOfDay+":"+minute);
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
                if(isChecked){
                    itemNotificationSound.setText("Pick Alarm Sound");
                    setAlarmPicker();
                }
                else{
                    itemNotificationSound.setText("Notification");
                    itemNotificationSound.setOnClickListener(null);
                    Toast.makeText(AddToDo.this, "Alarm Notification disabled", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private boolean validations(){
        if(itemName.getText().toString().isEmpty()){
            Snackbar.make(itemName,"Reminder description is mandatory",Snackbar.LENGTH_LONG).show();
            return false;
        }
        else if(itemTime.getText().toString().isEmpty()){
            Snackbar.make(itemName,"Reminder time is mandatory",Snackbar.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private void setAlarmPicker(){
        itemNotificationSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent audioPickerIntent = new Intent();
                audioPickerIntent.setType("audio/*");
                audioPickerIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(audioPickerIntent,"Select audio:"),PICK_AUDIO_REQ_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode==PICK_AUDIO_REQ_CODE && resultCode==RESULT_OK){
            audioURI = data.getData();
            itemNotificationSound.setText(getFileName(audioURI));
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

}
