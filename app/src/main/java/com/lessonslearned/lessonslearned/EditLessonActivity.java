package com.lessonslearned.lessonslearned;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.splunk.mint.Mint;

public class EditLessonActivity extends Activity {

    private void loadData() {

        //load data
        Intent intent = getIntent();
        LessonDataSource lessonDataSource = new LessonDataSource(EditLessonActivity.this);
        lessonDataSource.open();
        Lesson lesson = lessonDataSource.getLesson(intent.getExtras().getLong(Extra.Lesson.LESSONID.toString()));
        lessonDataSource.close();

        EditText editLessonName = (EditText) findViewById(R.id.editLessonName);
        EditText editLessonDescription = (EditText)  findViewById(R.id.editLessonDescription);
        editLessonName.setText(lesson.getName());
        editLessonDescription.setText(lesson.getDescription());
    }

    private void saveChanges(){
        try{
            EditText lessonName = (EditText) findViewById(R.id.editLessonName);
            EditText lessonDescription = (EditText) findViewById(R.id.editLessonDescription);
            Intent intent = getIntent();
            long lessonId = intent.getExtras().getLong(Extra.Lesson.LESSONID.toString());
            LessonDataSource lessonDataSource = new LessonDataSource(EditLessonActivity.this);
            lessonDataSource.open();
            lessonDataSource.updateLesson(lessonId, lessonName.getText().toString(), lessonDescription.getText().toString());
            lessonDataSource.close();
        }
        catch (Exception e){
            Toast toast = Toast.makeText(this, "Something went wrong there, couldn't save the changes", Toast.LENGTH_LONG);
            toast.show();
            if (!AppSettings.RELEASE_MODE){
                Log.e("Lessons Learned", "Could not save lesson " + e.getMessage() + " " + e.getStackTrace());
            }
            else{
                Mint.logException(e);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_lesson);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            getActionBar().setHomeButtonEnabled(true);
        }

        loadData();
    }

    @Override
    public void onBackPressed(){
        saveChanges();
        EditLessonActivity.super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        getMenuInflater().inflate(R.menu.edit_lesson, menu);
        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()) {
            case R.id.action_discard:
                EditLessonActivity.super.onBackPressed();
                return true;

            case android.R.id.home: case R.id.action_save:
                saveChanges();
                EditLessonActivity.super.onBackPressed();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
}
