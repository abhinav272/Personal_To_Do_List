package com.abhinav.personalto_dolist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by abhinavsharma on 25-02-2016.
 */
public class AddToDo extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_todo_activity);
        initialize();
    }

    private void initialize(){

    }
}