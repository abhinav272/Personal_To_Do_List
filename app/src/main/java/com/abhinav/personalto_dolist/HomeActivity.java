package com.abhinav.personalto_dolist;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.abhinav.personalto_dolist.CustomAdapter.CustomCursorAdapter;
import com.abhinav.personalto_dolist.DatabaseUtils.ToDoDBHelper;
import com.github.clans.fab.FloatingActionButton;

/**
 * Created by abhinavsharma on 25-02-2016.
 */
public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private FloatingActionButton fab;
    private ListView lview;
    private CustomCursorAdapter mCustomCursorAdapter;
    private ToDoDBHelper mToDoDBHelper;
    private Cursor allItems;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        initialize();
    }

    @Override
    protected void onStart() {
        allItems = ToDoDBHelper.getAllItems(mToDoDBHelper);
        mCustomCursorAdapter = new CustomCursorAdapter(this,allItems, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        lview.setAdapter(mCustomCursorAdapter);
        super.onStart();
    }

    private void initialize(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Current Reminders");
        fab = (FloatingActionButton) findViewById(R.id.fab);
        lview = (ListView) findViewById(R.id.lview);
        mToDoDBHelper = ToDoDBHelper.getInstance(this);
        fab.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab:
                startActivity(new Intent(HomeActivity.this,AddToDo.class));
                break;
        }
    }
}
