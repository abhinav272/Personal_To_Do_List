package com.abhinav.personalto_dolist;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.abhinav.personalto_dolist.CustomAdapter.CustomCursorAdapter;
import com.abhinav.personalto_dolist.CustomAdapter.CustomRVAdapter;
import com.abhinav.personalto_dolist.DatabaseUtils.ToDoDBHelper;
import com.abhinav.personalto_dolist.Model.ToDoItem;
import com.facebook.Profile;
import com.squareup.picasso.Picasso;

/**
 * Created by abhinavsharma on 25-02-2016.
 */
public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private FloatingActionButton fab;
    private RecyclerView lview;
    private CustomRVAdapter customRVAdapter;
    private ToDoItem.List toDoItems = new ToDoItem.List();
    private ToDoDBHelper mToDoDBHelper;
    private Cursor allItems;
    private Uri photoUrl;
    private ImageView userImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        initialize();
    }

    @Override
    protected void onStart() {
        allItems = ToDoDBHelper.getAllItems(mToDoDBHelper);
        toDoItems = buildToDoList(allItems);
        customRVAdapter = new CustomRVAdapter(this,toDoItems);
        lview.setAdapter(customRVAdapter);
        lview.setLayoutManager(new LinearLayoutManager(this));
        super.onStart();
    }

    private void initialize(){
        photoUrl = getIntent().getParcelableExtra("photoUrl");
        userImage = (ImageView) findViewById(R.id.user_image);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        lview = (RecyclerView) findViewById(R.id.lview);
        mToDoDBHelper = ToDoDBHelper.getInstance(this);
        if(photoUrl!=null){
            Picasso.with(this).load(photoUrl).fit().centerCrop().into(userImage);
        }
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

    private ToDoItem.List buildToDoList(Cursor allItems){
        while(allItems.moveToNext()){
            ToDoItem toDoItem = new ToDoItem();
            toDoItem.setItem_name(allItems.getString(allItems.getColumnIndex(ToDoDBHelper.TableEntries.COL_ITEM_NAME)));
            toDoItem.setItem_date(allItems.getString(allItems.getColumnIndex(ToDoDBHelper.TableEntries.COL_ITEM_DATE)));
            toDoItem.setItem_time(allItems.getString(allItems.getColumnIndex(ToDoDBHelper.TableEntries.COL_ITEM_TIME)));
            toDoItem.setItem_location(allItems.getString(allItems.getColumnIndex(ToDoDBHelper.TableEntries.COL_ITEM_LOCATION)));
            toDoItem.setItem_notification(allItems.getString(allItems.getColumnIndex(ToDoDBHelper.TableEntries.COL_ITEM_NOTIFICATION)));
            toDoItems.add(toDoItem);
        }
        return toDoItems;
    }
}
