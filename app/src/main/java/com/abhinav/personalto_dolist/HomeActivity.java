package com.abhinav.personalto_dolist;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import com.github.clans.fab.FloatingActionButton;

/**
 * Created by abhinavsharma on 25-02-2016.
 */
public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private FloatingActionButton fab;
    private ListView lview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        initialize();
    }

    private void initialize(){
        fab = (FloatingActionButton) findViewById(R.id.fab);
        lview = (ListView) findViewById(R.id.lview);
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
