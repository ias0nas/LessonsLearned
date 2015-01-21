package com.lessonslearned.lessonslearned;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import com.splunk.mint.Mint;

public class Home extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        Mint.initAndStartSession(this, "0d217c1d");
        setContentView(R.layout.activity_home);
        AlarmSetter.startAlarm(this);

        LessonDataSource lessonDatasource;
        lessonDatasource = new LessonDataSource(this);
        lessonDatasource.open();
        if (!lessonDatasource.lessonExists()){
            findViewById(R.id.add_lesson_tip_header).setVisibility(View.VISIBLE);
            findViewById(R.id.add_lesson_tip_details).setVisibility(View.VISIBLE);
            RelativeLayout container = (RelativeLayout) findViewById(R.id.container);
            container.setBackgroundColor(getResources().getColor(R.color.transparent_gray));
        }
        lessonDatasource.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_create_lesson){
            Intent intent = new Intent(this, CreateLessonActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_deleted_lessons){
            Intent intent = new Intent(this, DeletedLessons.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_help_legal){
            Intent intent = new Intent(this, HelpLegal.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
