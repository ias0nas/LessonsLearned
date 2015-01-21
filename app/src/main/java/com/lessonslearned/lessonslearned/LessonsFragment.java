package com.lessonslearned.lessonslearned;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.splunk.mint.Mint;

import java.util.List;

public class LessonsFragment extends Fragment {

    private LessonDataSource lessonDatasource;
    private ListView lessonsList;
    private int showOnlyDeleted = 0;

    public void showOnlyDeleted(int deleted){
        showOnlyDeleted = deleted;
    }

    public void bindLessons(){
        try{
            //bind lessons list view
            lessonDatasource = new LessonDataSource(getActivity());
            lessonDatasource.open();
            List<Lesson> lessons = lessonDatasource.getLessons(showOnlyDeleted);
            LessonAdapter lessonAdapter = new LessonAdapter(getActivity(), lessons);
            lessonsList.setAdapter(lessonAdapter);
            lessonDatasource.close();

            //list view click handlers
            lessonsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    Lesson selectedLesson = (Lesson) lessonsList.getItemAtPosition(position);
                    Intent intent = new Intent(getActivity(), ViewLessonActivity.class);
                    intent.putExtra(Extra.Lesson.LESSONID.toString(), selectedLesson.getId());
                    if (showOnlyDeleted == 1){
                        intent.putExtra(Extra.ViewLessonMode.DELETED.toString(), true);
                    }
                    startActivity(intent);
                }
            });
        }
        catch (Exception e){
            Toast toast = Toast.makeText(getActivity(), "Sorry, something went wrong and can't seem to load the lessons.", Toast.LENGTH_LONG);
            toast.show();
            if (!AppSettings.RELEASE_MODE){
                Log.e("Lessons Learned", "Could not bind lessons " + e.getMessage() + " " + e.getStackTrace());
            }
            else{
                Mint.logException(e);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_lessons, container, false);
        lessonsList = (ListView)view.findViewById(R.id.lessonsList);
        bindLessons();
        return view;
    }

    @Override
    public void onResume(){
        bindLessons();
        super.onResume();
    }

}
