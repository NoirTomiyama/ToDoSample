package com.lifeistech.android.todosample;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class SharedAdapter extends ArrayAdapter<Task>{

    List<Task> mTasks;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    public static class ViewHolder{

        public LinearLayout linearLayout;
        public TextView titleTextView;
        public CheckBox checkBox;

        public ViewHolder(View view){
            linearLayout = view.findViewById(R.id.linearLayout);
            titleTextView = view.findViewById(R.id.titleText);
            checkBox = view.findViewById(R.id.checkBox);
        }
    }

    public SharedAdapter(@NonNull Context context, int resource, @NonNull List<Task> objects) {
        super(context, resource, objects);

        mTasks = objects;
    }

    @Override
    public int getCount() {
        return mTasks.size();
    }

    @Nullable
    @Override
    public Task getItem(int position) {
        return mTasks.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ViewHolder viewHolder;

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_task_memo,null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Task task = getItem(position);

        if(task != null){
            viewHolder.titleTextView.setText(task.title);
            viewHolder.checkBox.setChecked(task.isChecked);

            Log.d("taskTitle", task.title);

        }

        return convertView;

    }
}
