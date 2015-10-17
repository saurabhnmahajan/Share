package com.example.home.share;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class AddContact extends Activity {
    DatabaseHandler db = new DatabaseHandler(this);
    String user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        final Bundle b = new Bundle();
        user = b.getString("user");
        Button search = (Button)findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText contact = (EditText)findViewById(R.id.search_contacts);
                db.addContact(user, contact.getText().toString());
                Intent intent = new Intent(AddContact.this, Home.class);
                b.putString("user", user);
                intent.putExtras(b);
                startActivity(intent);
            }
        });
    }
}
