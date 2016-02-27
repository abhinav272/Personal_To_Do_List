package com.abhinav.personalto_dolist.CustomAdapter;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.abhinav.personalto_dolist.AddToDo;
import com.abhinav.personalto_dolist.DatabaseUtils.ToDoDBHelper;
import com.abhinav.personalto_dolist.R;

/**
 * Created by abhinavsharma on 27-02-2016.
 */
public class CustomCursorAdapter extends CursorAdapter {

    private LayoutInflater inflater;
    private AddToDo addToDo = new AddToDo();

    public static class ViewHolder{
        TextView itemName, itemDate, itemTime, itemLocation, itemNotificationSound;
    }


    public CustomCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = inflater.inflate(R.layout.single_list_item,null);
        ViewHolder holder = new ViewHolder();
        holder.itemName = (TextView) view.findViewById(R.id.item_name);
        holder.itemDate = (TextView) view.findViewById(R.id.item_date);
        holder.itemTime = (TextView) view.findViewById(R.id.item_time);
        holder.itemLocation = (TextView) view.findViewById(R.id.item_location);
        holder.itemNotificationSound = (TextView) view.findViewById(R.id.item_notification_sound);
        view.setTag(holder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.itemName.setText(cursor.getString(cursor.getColumnIndex(ToDoDBHelper.TableEntries.COL_ITEM_NAME)));
        holder.itemDate.setText(cursor.getString(cursor.getColumnIndex(ToDoDBHelper.TableEntries.COL_ITEM_DATE)));
        holder.itemTime.setText(cursor.getString(cursor.getColumnIndex(ToDoDBHelper.TableEntries.COL_ITEM_TIME)));
        holder.itemLocation.setText(cursor.getString(cursor.getColumnIndex(ToDoDBHelper.TableEntries.COL_ITEM_LOCATION)));
        if(cursor.getString(cursor.getColumnIndex(ToDoDBHelper.TableEntries.COL_ITEM_NOTIFICATION))!=null){
            holder.itemNotificationSound.setText(cursor.getString(cursor.getColumnIndex(ToDoDBHelper.TableEntries.COL_ITEM_NOTIFICATION)));
        }

        else holder.itemNotificationSound.setText("Alarm Notification disabled");
    }
}
