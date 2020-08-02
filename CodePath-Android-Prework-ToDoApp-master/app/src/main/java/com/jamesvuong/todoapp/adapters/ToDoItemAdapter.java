package com.jamesvuong.todoapp.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jamesvuong.todoapp.R;
import com.jamesvuong.todoapp.models.ToDoItem;
import com.jamesvuong.todoapp.utils.TimeUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;
import static com.jamesvuong.todoapp.R.id.tvPriority;


/**
 * Created by jvuonger on 9/17/16.
 */
public class ToDoItemAdapter extends RecyclerView.Adapter<ToDoItemAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClicked(int position, ToDoItem item);
        void onItemLongClick(int itemPosition, ToDoItem item) ;
    }

    private static OnItemClickListener listener;

    // ViewHolder Pattern
    public static class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener{

        private ToDoItem mToDoItem;
        public TextView tvToDoItem;
        public TextView tvDueDate;
        public TextView tvPriority;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            tvToDoItem = (TextView) itemView.findViewById(R.id.tvToDoItemText);
            tvDueDate = (TextView) itemView.findViewById(R.id.tvDueDate);
            tvPriority = (TextView) itemView.findViewById(R.id.tvPriority);
        }

        @Override
        public void onClick(View v) {
            listener.onItemClicked(getAdapterPosition(),mToDoItem);
        }

        @Override
        public boolean onLongClick(View v) {
            listener.onItemLongClick(getAdapterPosition(),mToDoItem);
            return true;
        }

        public void setToDoItem(ToDoItem toDoItem) {
            mToDoItem = toDoItem;
        }
    }

    // Store a member variable for the contacts
    private List<ToDoItem> mToDoItems;
    // Store the context for easy access
    private Context mContext;

    // Pass in the to do items array into the constructor
    public ToDoItemAdapter(OnItemClickListener listener, Context context, List<ToDoItem> toDoItems) {
        this.listener = listener;
        mToDoItems = toDoItems;
        mContext = context;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public ToDoItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View toDoItemView = inflater.inflate(R.layout.to_do_item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(toDoItemView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(ToDoItemAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        ToDoItem toDoItem = mToDoItems.get(position);

        viewHolder.setToDoItem(toDoItem);

        // Set item views based on your views and data model
        TextView tvToDoItem = viewHolder.tvToDoItem;
        tvToDoItem.setText(toDoItem.getToDoItem());

        TextView tvDueDate = viewHolder.tvDueDate;
        tvDueDate.setText(getDateForView(toDoItem.getDueDateTime()));

        TextView tvPriority = viewHolder.tvPriority;
        if( toDoItem.hasPriority() ) {
            tvPriority.setText(toDoItem.getPriority());
            stylePriorityText(viewHolder.tvPriority.getContext(), tvPriority);
            tvPriority.setVisibility(View.VISIBLE);
        } else {
            tvPriority.setVisibility(View.GONE);
        }
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mToDoItems.size();
    }

    private String getDateForView(long time) {
        if (time == -1 ) return "";

        String date = TimeUtils.formatHumanFriendlyShortDate(getContext(), time);

        return "Due " + date;
    }

    private void stylePriorityText(Context context, TextView tvPriority) {
        int textColor = R.color.lowPriority;
        switch(tvPriority.getText().toString().toLowerCase()) {
            case "medium":
                textColor = R.color.mediumPriority;
                break;
            case "high":
                textColor = R.color.highPriority;
                break;
            default:
                break;
        }

        tvPriority.setBackgroundColor(ContextCompat.getColor(context, textColor));
    }

}
