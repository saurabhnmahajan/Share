package com.example.home.share;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class RegisterUser extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        final EditText userId = (EditText)findViewById(R.id.userName);
        Button register = (Button)findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                db.addUser(userId.getText().toString());
                db.loggedUser(userId.getText().toString(), "IN");
                Intent intent = new Intent(RegisterUser.this, Home.class);
                Bundle b =new Bundle();
                b.putString("user",userId.getText().toString());
                intent.putExtras(b);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }
}
