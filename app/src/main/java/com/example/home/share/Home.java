package com.example.home.share;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class Home extends ListActivity {
    DatabaseHandler db = new DatabaseHandler(this);
    String user, contacts[], selectedContacts = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Bundle b = getIntent().getExtras();
        user = b.getString("user");
        contacts = db.getAllContacts(user);
        final ArrayAdapter<String> myAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,contacts);
        setListAdapter(myAdapter);
        final ListView listView = getListView();
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                if (checked) {
                    listView.getChildAt(position).setBackgroundColor(Color.LTGRAY);
                    selectedContacts += "'" + contacts[position] + "', ";
                } else {
                    listView.getChildAt(position).setBackgroundColor(Color.TRANSPARENT);
                    String selected = "'" + contacts[position] + "', ";
                    int pos = selectedContacts.indexOf(selected);
                    selectedContacts = selectedContacts.substring(0, pos) + selectedContacts.substring(pos + selected.length());
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.menu_home_selected, menu);
                mode.setTitle("Select Contacts");
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                listView.clearChoices();
                for (int i = 0; i < listView.getCount(); i++)
                    listView.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
                selectedContacts = selectedContacts.substring(0, selectedContacts.lastIndexOf(','));
                Intent intent = new Intent(Home.this, MapsActivity.class);
                Bundle b = new Bundle();
                b.putString("user", user);
                b.putString("selectedContacts", selectedContacts);
                intent.putExtras(b);
                startActivity(intent);
                selectedContacts = "";
            }
        });
    }

    @Override
    protected void onListItemClick(ListView list, View view, int position, long id) {
        super.onListItemClick(list, view, position, id);
        String selectedItem = (String) getListView().getItemAtPosition(position);
        String selectedContact = "'" + contacts[position] + "'";
        Intent intent = new Intent(Home.this, MapsActivity.class);
        Bundle b = new Bundle();
        b.putString("user", user);
        b.putString("selectedContacts", selectedContact);
        intent.putExtras(b);
        startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id == R.id.action_contact) {
            //add user to your list
            Intent intent = new Intent(Home.this, AddContact.class);
            Bundle b = new Bundle();
            b.putString("user", user);
            intent.putExtras(b);
            startActivity(intent);
        }
        else if (id == R.id.action_settings) {
            return true;
        }
        else if (id == R.id.action_logout) {
            db.loggedUser(user, "OUT");
            Intent intent = new Intent(Home.this,Launcher.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
