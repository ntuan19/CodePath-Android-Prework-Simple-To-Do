package com.jamesvuong.todoapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.jamesvuong.todoapp.models.ToDoItem;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by jvuonger on 9/19/16.
 */
public class ToDoItemDbHelper extends SQLiteOpenHelper {
    private static final String TAG = "ToDoItemDatabase";

    // Singleton
    private static ToDoItemDbHelper sInstance;

    // Database Info
    private static final String DATABASE_NAME = "toDoDatabase";
    private static final int DATABASE_VERSION = 6;

    // Table Names
    private static final String TABLE_TODOITEMS = "todoItems";

    // To Do Item Table Columns
    private static final String KEY_TODOITEM_ID = "id";
    private static final String KEY_TODOITEM_NAME = "itemName";
    private static final String KEY_TODOITEM_DUEDATE = "dueDate";
    private static final String KEY_TODOITEM_PRIORITY = "priority";
    private static final String KEY_TODOITEM_NOTES = "notes";

    // Singleton Method
    public static synchronized ToDoItemDbHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new ToDoItemDbHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    public ToDoItemDbHelper (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called when the database connection is being configured.
    // Configure database settings for things like foreign key support, write-ahead logging, etc.
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TODO_TABLE = "CREATE TABLE " + TABLE_TODOITEMS +
                "(" +
                    KEY_TODOITEM_ID + " INTEGER PRIMARY KEY," + // Define Primary Key
                    KEY_TODOITEM_NAME + " TEXT," +
                    KEY_TODOITEM_DUEDATE + " TEXT," +
                    KEY_TODOITEM_PRIORITY + " TEXT," +
                    KEY_TODOITEM_NOTES + " TEXT" +
                ")";

        db.execSQL(CREATE_TODO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODOITEMS);
            onCreate(db);
        }
    }

    // Insert a to do item into the database
    public long addToDoItem(ToDoItem item) {
        long id = -1;
        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_TODOITEM_NAME, item.getToDoItem());
            values.put(KEY_TODOITEM_DUEDATE, item.getDueDateTime());
            values.put(KEY_TODOITEM_PRIORITY, item.getPriority());
            values.put(KEY_TODOITEM_NOTES, item.getNotes());

            id = db.insertOrThrow(TABLE_TODOITEMS, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add todo item to database.");
        } finally {
            db.endTransaction();
        }

        return id;
    }

    // Get all items in the database
    public ArrayList<ToDoItem> getAllToDoItems() {
        ArrayList<ToDoItem> toDoItems = new ArrayList<ToDoItem>();

        String TODOITEM_SELECT_QUERY = String.format("SELECT * FROM %s", TABLE_TODOITEMS);

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(TODOITEM_SELECT_QUERY, null);

        try{
            if(cursor.moveToFirst()) {
                do {
                    ToDoItem newToDoItem = new ToDoItem();
                    newToDoItem.setToDoId(cursor.getInt(cursor.getColumnIndex(KEY_TODOITEM_ID)));
                    newToDoItem.setToDoItem(cursor.getString(cursor.getColumnIndex(KEY_TODOITEM_NAME)));
                    newToDoItem.setDueDate(new Date(cursor.getLong(cursor.getColumnIndex(KEY_TODOITEM_DUEDATE))));
                    newToDoItem.setPriority(cursor.getString(cursor.getColumnIndex(KEY_TODOITEM_PRIORITY)));
                    newToDoItem.setNotes(cursor.getString(cursor.getColumnIndex(KEY_TODOITEM_NOTES)));

                    toDoItems.add(newToDoItem);
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get all to do items from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return toDoItems;
    }

    public ToDoItem getToDoItemById(int id) {
        ToDoItem item = new ToDoItem();

        String TODOITEM_SELECT_QUERY = String.format("SELECT * FROM %s WHERE id = %s", TABLE_TODOITEMS, id);

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(TODOITEM_SELECT_QUERY, null);

        try{
            if(cursor.moveToFirst()) {
                item.setToDoId(cursor.getInt(cursor.getColumnIndex(KEY_TODOITEM_ID)));
                item.setToDoItem(cursor.getString(cursor.getColumnIndex(KEY_TODOITEM_NAME)));
                item.setDueDate(new Date(cursor.getLong(cursor.getColumnIndex(KEY_TODOITEM_DUEDATE))));
                item.setPriority(cursor.getString(cursor.getColumnIndex(KEY_TODOITEM_PRIORITY)));
                item.setNotes(cursor.getString(cursor.getColumnIndex(KEY_TODOITEM_NOTES)));
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get item: " + id);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return item;
    }

    public int updateOrAddToDoItem(ToDoItem item) {
        if (item.getToDoId() < 0 ) {
            return (int) this.addToDoItem(item);
        } else {
            return this.updateToDoItem(item);
        }
    }

    public int updateToDoItem(ToDoItem item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TODOITEM_NAME, item.getToDoItem());
        values.put(KEY_TODOITEM_DUEDATE, item.getDueDateTime());
        values.put(KEY_TODOITEM_PRIORITY, item.getPriority());
        values.put(KEY_TODOITEM_NOTES, item.getNotes());

        return db.update(TABLE_TODOITEMS, values, KEY_TODOITEM_ID + " = ?",
                new String[] { String.valueOf(item.getToDoId()) });
    }

    public void deleteToDoItem(ToDoItem item) {
        int result;
        SQLiteDatabase db = this.getWritableDatabase();

        db.beginTransaction();
        try {
            result = db.delete(TABLE_TODOITEMS, KEY_TODOITEM_ID + " = ? ", new String[] { String.valueOf(item.getToDoId() )});
            if(result > 0) {
                db.setTransactionSuccessful();
            }
        } catch ( Exception e ) {
            Log.d(TAG, "Unable to delete item: " + item.getToDoId());
        } finally {
            db.endTransaction();
        }

    }
}
