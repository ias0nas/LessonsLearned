package com.lessonslearned.lessonslearned;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class CreateLessonActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lesson);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_lesson, menu);
        return true;
    }

    private void createLesson(boolean displayValidation){
        EditText lesson = (EditText)findViewById(R.id.editLessonName);
        EditText lessonDescription = (EditText)findViewById(R.id.editLessonDescription);
        if (!lesson.getText().toString().trim().isEmpty()) {
            LessonDataSource lessonDataSource = new LessonDataSource(CreateLessonActivity.this);
            lessonDataSource.open();
            lessonDataSource.createLesson(lesson.getText().toString(), lessonDescription.getText().toString());
            lessonDataSource.close();
            startActivity(new Intent(CreateLessonActivity.this, Home.class));
        }

        if (displayValidation && lesson.getText().toString().trim().isEmpty()){
            Context context = getApplicationContext();
            Toast.makeText(context, R.string.toast_no_lesson_name_entered, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_save:
                createLesson(true);
                return true;

            case android.R.id.home:
                createLesson(false);
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        createLesson(false);
        CreateLessonActivity.super.onBackPressed();
    }

}
