package com.lessonslearned.lessonslearned;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class HelpLegal extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_legal);

        TextView privacyContent = (TextView) findViewById(R.id.PrivacyContent);
        privacyContent.setMovementMethod(LinkMovementMethod.getInstance());

        TextView termsOfUseContent = (TextView) findViewById(R.id.TCContent);
        termsOfUseContent.setMovementMethod(LinkMovementMethod.getInstance());
    }

}
