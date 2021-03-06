package com.example.home.share;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddContact extends ListActivity {
    DatabaseHandler db = new DatabaseHandler(this);
    Bundle b;
    private String email, searchList[];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        b = getIntent().getExtras();
        email = b.getString("email");
        Button search = (Button)findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText contact = (EditText)findViewById(R.id.search_contacts);
                String searchContacts = contact.getText().toString();
                searchList = db.search(searchContacts);
                List<String> list = new ArrayList<String>(Arrays.asList(searchList));
                list.remove(email);
                searchList = list.toArray(new String[0]);
                ArrayAdapter<String> myAdapter=new ArrayAdapter<String>(AddContact.this, android.R.layout.simple_list_item_1,searchList);
                setListAdapter(myAdapter);
            }
        });
    }
    @Override
    protected void onListItemClick(ListView list, View view, int position, long id) {
        super.onListItemClick(list, view, position, id);
        db.addContact(email, searchList[position]);
        Intent intent = new Intent(AddContact.this, Home.class);
        intent.putExtras(b);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
