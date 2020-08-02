package com.jamesvuong.todoapp.models;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.jamesvuong.todoapp.R.id.etDueDate;

/**
 * Created by jvuonger on 9/17/16.
 */
public class ToDoItem {
    private int mToDoId = -1;
    private String mToDoItem;
    private Date mDueDate;
    private String mPriority;
    private String mNotes;

    public ToDoItem() {
        mToDoItem = "";
    }

    public ToDoItem(String toDoItem) {
        mToDoItem = toDoItem;
    }

    public ToDoItem(String toDoItem, Date dueDate) {
        mToDoItem = toDoItem;
        mDueDate = dueDate;
    }

    public ToDoItem(String toDoItem, Date dueDate, String priority) {
        mToDoItem = toDoItem;
        mDueDate = dueDate;
        mPriority = priority;
    }

    // To Do Items with a Due Date
    public ToDoItem(String toDoItem, Date dueDate, String priority, String notes) {
        mToDoItem = toDoItem;
        mDueDate = dueDate;
        mPriority = priority;
        mNotes = notes;
    }

    public void setToDoId(int id) {
        mToDoId = id;
    }

    public void setToDoItem(String toDoItem) {
        mToDoItem = toDoItem;
    }

    public void setDueDate(Date dueDate) {
        mDueDate = dueDate;
    }

    public void setPriority(String priority) {
        mPriority = priority;
    }

    public int getToDoId() {
        return mToDoId;
    }

    public String getToDoItem() {
        return mToDoItem;
    }

    public String getDueDate() {
        if( mDueDate == null ) {
            return "";
        } else {
            return mDueDate.toString();
        }
    }

    public long getDueDateTime() {
        if( mDueDate == null ) {
            return -1;
        } else {
            return mDueDate.getTime();
        }
    }

    public String getDueDateForEditText() {
        Calendar cal = Calendar.getInstance();

        if( mDueDate != null ) {
            cal.setTime(mDueDate);
        }

        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        return sdf.format(cal.getTime());
    }

    public String getPriority() {
        if (mPriority == null) return "";
        return mPriority;
    }

    public void setNotes(String notes) { mNotes = notes; }
    public String getNotes() {
        if (mNotes == null) return "";
        return mNotes;
    }

    public boolean hasPriority() {
        return !mPriority.equals("");
    }

    @Override
    public String toString() {
        return "ToDoItem {" +
                    "\n\t ID: " + mToDoId +
                    "\n\t ID: " + mToDoItem +
                    "\n\t ID: " + mDueDate +
                    "\n\t ID: " + mPriority +
                    "\n\t ID: " + mNotes +
                "}";
    }
}
