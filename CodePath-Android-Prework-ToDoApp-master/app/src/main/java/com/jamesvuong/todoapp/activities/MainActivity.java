package com.tuannguyen.todoapp.activities;

import android.app.FragmentManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.jamesvuong.todoapp.fragments.EditToDoItemDiaglogFragment;
import com.jamesvuong.todoapp.R;
import com.jamesvuong.todoapp.models.ToDoItem;
import com.jamesvuong.todoapp.adapters.ToDoItemAdapter;
import com.jamesvuong.todoapp.data.ToDoItemDbHelper;
import com.jamesvuong.todoapp.ui.SimpleDividerItemDecoration;

import java.util.ArrayList;


public class MainActivity
        extends AppCompatActivity
        implements  EditToDoItemDiaglogFragment.EditToDoItemDiaglogListener,
                    ToDoItemAdapter.OnItemClickListener {

    final static int NEW_TODO_ITEM = -1;
    final String TAG = "MainActivity";
    ArrayList<ToDoItem> toDoItems = new ArrayList<ToDoItem>();
    ToDoItemAdapter aToDoAdapter;
    ToDoItemDbHelper db;

    FloatingActionButton fabAddTodoItem;
    RecyclerView rvItems;

    // Edit DialogFragment Listener
    @Override
    public void onFinishEditDialog(int itemPosition, ToDoItem item) {
        // if itemPosition not -1, then update that item otherwise it's a new to do item
        if (itemPosition == NEW_TODO_ITEM) {
            toDoItems.add(item);
            aToDoAdapter.notifyItemInserted(toDoItems.size() - 1);
        } else {
            toDoItems.set(itemPosition, db.getToDoItemById(item.getToDoId()));
            aToDoAdapter.notifyItemChanged(itemPosition);
        }
    }

    // Recycler View Listeners
    @Override
    public void onItemClicked(int itemPosition, ToDoItem item) {
        launchEditItemView(itemPosition, item);
    }

    @Override
    public void onItemLongClick(int itemPosition, ToDoItem item) {
        db.deleteToDoItem(item);
        toDoItems.remove(itemPosition);
        aToDoAdapter.notifyItemRemoved(itemPosition);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_action_name);

        db = ToDoItemDbHelper.getInstance(this);

        toDoItems = db.getAllToDoItems();
        aToDoAdapter = new ToDoItemAdapter(this,this,toDoItems);

        rvItems = (RecyclerView) findViewById(R.id.rvItems);
        rvItems.setAdapter(aToDoAdapter);
        rvItems.setLayoutManager(new LinearLayoutManager(this));
        rvItems.addItemDecoration(new SimpleDividerItemDecoration(this));

        fabAddTodoItem = (FloatingActionButton) findViewById(R.id.fabAddTodoItem);
        fabAddTodoItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchEditItemView(NEW_TODO_ITEM, new ToDoItem(""));
            }
        });
    }

    public void launchEditItemView(int itemPosition, ToDoItem item) {
        FragmentManager fm = getFragmentManager();
        EditToDoItemDiaglogFragment dialogFragment = EditToDoItemDiaglogFragment.newInstance(itemPosition, item);
        dialogFragment.show(fm, "EditToDoItemDialogFragment");
    }
}
