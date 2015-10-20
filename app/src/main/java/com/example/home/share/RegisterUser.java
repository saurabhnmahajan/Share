package com.example.home.share;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class RegisterUser extends Activity {
    DatabaseHandler db = new DatabaseHandler(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        ((EditText)findViewById(R.id.password)).setRawInputType(Configuration.KEYBOARD_12KEY);
        Button register = (Button)findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = ((EditText)findViewById(R.id.name)).getText().toString();
                String email = ((EditText)findViewById(R.id.email)).getText().toString();
                String pwd = ((EditText)findViewById(R.id.password)).getText().toString();
                db.addUser(name, email, pwd);
                db.loggedUser(email, "IN");
                Intent intent = new Intent(RegisterUser.this, Home.class);
                Bundle b =new Bundle();
                b.putString("email", email);
                b.putString("acc_type", "normal");
                intent.putExtras(b);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }
}
