package com.lessonslearned.lessonslearned;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class DeletedLessons extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deleted_lessons);

        LessonsFragment lessonsFragment = (LessonsFragment)
                getFragmentManager().findFragmentById(R.id.deletedLessons);

        if (lessonsFragment != null){
            lessonsFragment.showOnlyDeleted(1);
        }

        LessonDataSource lessonDatasource;
        lessonDatasource = new LessonDataSource(this);
        lessonDatasource.open();
        if (!lessonDatasource.deletedLessonExists()) {
            findViewById(R.id.NoDeletedLessonsHeader).setVisibility(View.VISIBLE);
            findViewById(R.id.NoDeletedLessonsContent).setVisibility(View.VISIBLE);
        }
        lessonDatasource.close();
    }
}
