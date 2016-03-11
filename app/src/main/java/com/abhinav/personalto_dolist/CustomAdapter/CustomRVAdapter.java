package com.abhinav.personalto_dolist.CustomAdapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.abhinav.personalto_dolist.Model.ToDoItem;
import com.abhinav.personalto_dolist.R;

/**
 * Created by abhinavsharma on 02-03-2016.
 */
public class CustomRVAdapter extends RecyclerView.Adapter<CustomRVAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private ToDoItem.List toDoItems;

    public CustomRVAdapter(Context context, ToDoItem.List toDoItems){
        inflater = LayoutInflater.from(context);
        this.toDoItems = toDoItems;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.single_list_item,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.itemName.setText(toDoItems.get(position).getItem_name());
        holder.itemDate.setText(toDoItems.get(position).getItem_date());
        holder.itemTime.setText(toDoItems.get(position).getItem_time());
        holder.itemLocation.setText(toDoItems.get(position).getItem_location());
        holder.itemNotificationSound.setText(toDoItems.get(position).getItem_notification());
    }

    @Override
    public int getItemCount() {
        return toDoItems.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemDate, itemTime, itemLocation, itemNotificationSound;
        public MyViewHolder(View itemView) {
            super(itemView);
            itemName = (TextView) itemView.findViewById(R.id.item_name);
            itemDate = (TextView) itemView.findViewById(R.id.item_date);
            itemTime = (TextView) itemView.findViewById(R.id.item_time);
            itemLocation = (TextView) itemView.findViewById(R.id.item_location);
            itemNotificationSound = (TextView) itemView.findViewById(R.id.item_notification_sound);
        }
    }
}
