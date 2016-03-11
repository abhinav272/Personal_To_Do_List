package com.abhinav.personalto_dolist.DatabaseUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.abhinav.personalto_dolist.Model.ToDoItem;

/**
 * Created by abhinavsharma on 27-02-2016.
 */
public class ToDoDBHelper extends SQLiteOpenHelper {

    public static final class TableEntries implements BaseColumns {

        public static final String DATABASE_NAME = "TODODATABASE";
        public static final String TABLE_NAME = "PERSON";
        public static final String COL_ITEM_NAME = "ITEM_NAME";
        public static final String COL_ITEM_DATE = "ITEM_DATE";
        public static final String COL_ITEM_TIME = "ITEM_TIME";
        public static final String COL_ITEM_LOCATION = "ITEM_LOCATION";
        public static final String COL_ITEM_NOTIFICATION = "ITEM_NOTIFICATION";
        public static final int DB_VERSION = 1;
    }


    private static ToDoDBHelper mToDoDBHelper = null;
    private static SQLiteDatabase toDoDatabase;
    private static final String TAG = "ToDoDBHelper";
    private static final String QUERY_CREATE_TABLE = "CREATE TABLE " + TableEntries.TABLE_NAME +
            "("
            + TableEntries._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TableEntries.COL_ITEM_NAME + " TEXT, "
            + TableEntries.COL_ITEM_DATE + " TEXT, "
            + TableEntries.COL_ITEM_TIME + " TEXT, "
            + TableEntries.COL_ITEM_LOCATION + " TEXT, "
            + TableEntries.COL_ITEM_NOTIFICATION + " TEXT"
            + " )";
    private static final String DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TableEntries.TABLE_NAME;
    private static final String GET_ALL_ITEMS = "SELECT * FROM " + TableEntries.TABLE_NAME;

    public static ToDoDBHelper getInstance(Context context) {
        if (mToDoDBHelper == null) {
            mToDoDBHelper = new ToDoDBHelper(context, TableEntries.DATABASE_NAME, null, TableEntries.DB_VERSION);
            Log.d(TAG, "DatabaseHelper object created");
        }
        return mToDoDBHelper;
    }

    private ToDoDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        Log.d(TAG, "Database created");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(QUERY_CREATE_TABLE);
        Log.d(TAG, "Table created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static Cursor getAllItems(ToDoDBHelper mToDoDBHelper) {
        toDoDatabase = mToDoDBHelper.getReadableDatabase();
        Cursor cursor = toDoDatabase.rawQuery(GET_ALL_ITEMS, null);
        Log.d(TAG, "Items list returned");
        return cursor;
    }

    public static long addItem(ToDoDBHelper mToDoDBHelper, ToDoItem item) {
        toDoDatabase = mToDoDBHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TableEntries.COL_ITEM_NAME, item.getItem_name());
        cv.put(TableEntries.COL_ITEM_DATE, item.getItem_date());
        cv.put(TableEntries.COL_ITEM_TIME, item.getItem_time());
        cv.put(TableEntries.COL_ITEM_LOCATION, item.getItem_location());
        cv.put(TableEntries.COL_ITEM_NOTIFICATION, item.getItem_notification());

        long newRowId = toDoDatabase.insert(TableEntries.TABLE_NAME, null, cv);
        toDoDatabase.close();
        Log.d(TAG, "Item added successfully");
        return newRowId;
    }

    public static int deleteItem(int id){
        int rowsAffected = -1;
        if(id!=-1){
            toDoDatabase = mToDoDBHelper.getWritableDatabase();
            String sID = String.valueOf(id);
            rowsAffected = toDoDatabase.delete(TableEntries.TABLE_NAME, TableEntries._ID + " = ?", new String[]{sID});
            Log.d(TAG, "Record Deleted from AccountDetails at ID: "
                    + id
                    + "Number of rows affected: "
                    + rowsAffected);
        }
        return rowsAffected;
    }


}
