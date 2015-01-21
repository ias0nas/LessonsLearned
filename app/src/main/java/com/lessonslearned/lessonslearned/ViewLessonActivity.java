package com.lessonslearned.lessonslearned;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;


public class ViewLessonActivity extends Activity {

    private void loadData()
    {
        //load data
        Intent intent = getIntent();
        LessonDataSource lessonDataSource = new LessonDataSource(ViewLessonActivity.this);
        lessonDataSource.open();
        Lesson lesson = lessonDataSource.getLesson(intent.getExtras().getLong(Extra.Lesson.LESSONID.toString()));
        lessonDataSource.close();

        TextView viewLessonName = (TextView)findViewById(R.id.viewLessonName);
        TextView viewLessonDescription = (TextView)findViewById(R.id.viewLessonDescription);
        TextView viewLessonTimestamp = (TextView) findViewById(R.id.viewLessonTimestamp);
        SimpleDateFormat dateFormat = new SimpleDateFormat(AppSettings.DATE_FORMAT_DATE_ONLY);
        viewLessonTimestamp.setText(dateFormat.format(lesson.getTimestamp()));
        viewLessonName.setText(lesson.getName());
        viewLessonDescription.setText(lesson.getDescription());
        Linkify.addLinks(viewLessonDescription, Linkify.ALL);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_lesson);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            getActionBar().setHomeButtonEnabled(true);
        }

        loadData();
    }

    @Override
    public void onResume(){
        loadData();
        super.onResume();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_lesson, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        if (getIntent().getExtras().getBoolean(Extra.ViewLessonMode.DELETED.toString(), false) == true){
            menu.findItem(R.id.action_edit).setVisible(false);
            menu.findItem(R.id.action_delete).setVisible(false);
        }
        else{
            menu.findItem(R.id.action_undelete).setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.action_edit: {
                if (getIntent().getExtras().getBoolean(Extra.ViewLessonMode.DELETED.toString(), false)) {
                    item.setVisible(false);
                }
                Intent editIntent = new Intent(this, EditLessonActivity.class);
                editIntent.putExtra(Extra.Lesson.LESSONID.toString(), getIntent().getExtras().getLong(Extra.Lesson.LESSONID.toString()));
                startActivity(editIntent);
                return true;
            }
            case R.id.action_delete: {
                LessonDataSource lessonDataSource = new LessonDataSource(ViewLessonActivity.this);
                lessonDataSource.open();
                lessonDataSource.deleteLesson(lessonDataSource.getLesson(getIntent().getExtras().getLong(Extra.Lesson.LESSONID.toString())));
                lessonDataSource.close();
                Intent homeIntent = new Intent(this, Home.class);
                startActivity(homeIntent);
                Toast.makeText(this, getString(R.string.toast_lesson_deleted), Toast.LENGTH_LONG).show();
                return true;
            }
            case R.id.action_undelete: {
                LessonDataSource lessonDataSource = new LessonDataSource(ViewLessonActivity.this);
                lessonDataSource.open();
                lessonDataSource.undeleteLesson(lessonDataSource.getLesson(getIntent().getExtras().getLong(Extra.Lesson.LESSONID.toString())));
                lessonDataSource.close();
                Intent homeIntent = new Intent(this, Home.class);
                startActivity(homeIntent);
                Toast.makeText(this, getString(R.string.toast_lesson_undeleted), Toast.LENGTH_LONG).show();
                return true;
            }
            case android.R.id.home: {

                if (getIntent().getExtras().getBoolean(Extra.ViewLessonBackNewHome.NewHome.toString(), false)) {
                    Intent home = new Intent(this, Home.class);
                    startActivity(home);
                    return true;
                }
                else{
                    finish();
                    return true;
                }

            }
        }
        return super.onOptionsItemSelected(item);
    }
}
