package com.example.home.share;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;


public class AddContact extends ListActivity {
    DatabaseHandler db = new DatabaseHandler(this);
    private String user, searchList[];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        final Bundle b = getIntent().getExtras();
        user = b.getString("user");
        Button search = (Button)findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText contact = (EditText)findViewById(R.id.search_contacts);
                String searchContacts = contact.getText().toString();
                searchList = db.search(searchContacts);
                ArrayAdapter<String> myAdapter=new ArrayAdapter<String>(AddContact.this, android.R.layout.simple_list_item_1,searchList);
                setListAdapter(myAdapter);
            }
        });
    }
    @Override
    protected void onListItemClick(ListView list, View view, int position, long id) {
        super.onListItemClick(list, view, position, id);
        db.addContact(user, searchList[position]);
        Intent intent = new Intent(AddContact.this, Home.class);
        Bundle b = new Bundle();
        b.putString("user", user);
        intent.putExtras(b);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
