package com.lessonslearned.lessonslearned;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

/**
 * Created by ias0nas on 11/05/14.
 */
public class LessonAdapter extends ArrayAdapter {
    private final Context context;
    private final List<Lesson> values;

    public LessonAdapter(Context context, List<Lesson> values) {
        super(context, R.layout.adapter_lessons, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.adapter_lessons, parent, false);
        //set lesson name and description
        TextView lessonName = (TextView) rowView.findViewById(R.id.lessonName);
        lessonName.setText(values.get(position).getName());
        TextView lessonDescription = (TextView) rowView.findViewById(R.id.lessonDescription);
        if (values.get(position).getDescription() != null && !values.get(position).getDescription().isEmpty()){
            lessonDescription.setText(values.get(position).getDescription());
        }

        return rowView;
    }
}
