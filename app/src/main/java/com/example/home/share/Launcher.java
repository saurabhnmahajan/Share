package com.example.home.share;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


public class Launcher extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        DatabaseHandler db = new DatabaseHandler(this);
        String email = db.getLoggedUser();
        if(email.length() > 0) {
            Intent intent = new Intent(Launcher.this, Home.class);
            Bundle b = new Bundle();
            b.putString("email", email);
            intent.putExtras(b);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(Launcher.this, MainActivity.class);
            startActivity(intent);
        }
    }
}
