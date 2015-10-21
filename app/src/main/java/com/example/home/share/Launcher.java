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
        String user[] = db.getLoggedUser();
        if(user != null) {
            Intent intent = new Intent(Launcher.this, Home.class);
            Bundle b = new Bundle();
            b.putString("email", user[0]);
            if(user[1].equalsIgnoreCase("google_in")) {
                b.putString("acc_type", "google");
            }
            else {
                b.putString("acc_type", "normal | facebook");
            }
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
