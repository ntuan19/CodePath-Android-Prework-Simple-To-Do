package com.jamesvuong.todoapp.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.jamesvuong.todoapp.R;
import com.jamesvuong.todoapp.models.ToDoItem;
import com.jamesvuong.todoapp.data.ToDoItemDbHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by jvuonger on 9/19/16.
 */

public class EditToDoItemDiaglogFragment extends DialogFragment {

    ToDoItemDbHelper db;
    View rootView;
    ToDoItem itemToEdit;
    EditText etEditItemText;
    EditText etDueDate;
    RadioGroup rgPriority;

    int itemId;
    int itemPosition;

    EditToDoItemDiaglogListener listener;

    final Calendar myCalendar = Calendar.getInstance();


    // Defines the listener interface with a method passing back data result
    public interface EditToDoItemDiaglogListener {
        void onFinishEditDialog(int itemPosition, ToDoItem item);
    }

    // Create a new instance of the DialogFragment
    public static EditToDoItemDiaglogFragment newInstance(int position, ToDoItem item) {
        EditToDoItemDiaglogFragment f = new EditToDoItemDiaglogFragment();

        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putInt("id", item.getToDoId());
        args.putInt("dateTime", (int)item.getDueDateTime());
        args.putString("name", item.getToDoItem());
        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Initialize variables
        listener = (EditToDoItemDiaglogListener) getActivity();
        itemId = getArguments().getInt("id");
        itemPosition = getArguments().getInt("position");

        rootView = inflater.inflate(R.layout.fragment_edit_item, container, false);

        etEditItemText = (EditText) rootView.findViewById(R.id.etEditItemText);

        // Get Item from SQL Lite database
        db = ToDoItemDbHelper.getInstance(rootView.getContext());
        itemToEdit = db.getToDoItemById(itemId);

        // Set To Do Item Name
        etEditItemText = (EditText) rootView.findViewById(R.id.etEditItemText);
        etEditItemText.setText(itemToEdit.getToDoItem());
        etEditItemText.setSelection(etEditItemText.getText().length());

        // Set Due Date
        etDueDate = (EditText) rootView.findViewById(R.id.etDueDate);

        Calendar c = Calendar.getInstance();
        if (itemToEdit.getDueDateTime() > 0) {
            etDueDate.setText(itemToEdit.getDueDateForEditText());
        }

        // Set Due Date Calendar Dialog On Click
        etDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(rootView.getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // Set RadioGroup
        rgPriority = (RadioGroup) rootView.findViewById(R.id.rgPriorityOptions);
        int selectedRadioButton = getSelectedPriority(itemToEdit.getPriority());
        if (selectedRadioButton > 0) {
            ((RadioButton) rootView.findViewById(selectedRadioButton)).setChecked(true);
        }

        // Attach Events to buttons
        Button btnSaveButton = (Button) rootView.findViewById(R.id.btnSaveEditItem);
        btnSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToDoItem();
                listener.onFinishEditDialog(itemPosition, itemToEdit);
                dismiss();
            }
        });

        Button dismiss = (Button) rootView.findViewById(R.id.dismiss);
        dismiss.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return rootView;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onResume() {
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

        super.onResume();
    }

    private void saveToDoItem() {

        // To do item
        itemToEdit.setToDoItem(etEditItemText.getText().toString());
        Date dueDate = null;
        SimpleDateFormat f = new SimpleDateFormat("MM/dd/yy");
        try {
            dueDate = f.parse(etDueDate.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        itemToEdit.setDueDate(dueDate);

        // Priority
        if (rgPriority.getCheckedRadioButtonId() > 0) {
            itemToEdit.setPriority(
                    ((RadioButton) rootView.findViewById(rgPriority.getCheckedRadioButtonId())).getText().toString()
            );
        }

        int newToDoId = db.updateOrAddToDoItem(itemToEdit);

        if(itemToEdit.getToDoId() < 0){
            itemToEdit.setToDoId(newToDoId);
        }
    }

    private int getSelectedPriority(String priority) {
        switch (priority.toLowerCase()) {
            case "low":
                return R.id.rbLowPriority;
            case "medium":
                return R.id.rbMediumPriority;
            case "high":
                return R.id.rbHighPriority;
            default:
                return -1;
        }
    }

    final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDueDate();
        }
    };

    private void updateDueDate() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        etDueDate.setText(sdf.format(myCalendar.getTime()));
    }


}
