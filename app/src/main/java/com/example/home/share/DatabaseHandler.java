package com.example.home.share;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "users";

    // table names
    private static final String TABLE_USERS = "user_locations";
    private static final String TABLE_LOGGED = "user_logged";
    private static final String TABLE_USER_CONTACTS = "user_contacts";

    // columns
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_STATUS = "status";
    private static final String KEY_CONTACT = "contact";
    private static final String KEY_USER_ID = "user_id";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d("db", "created");
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String table_users = "CREATE TABLE " + TABLE_USERS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_LATITUDE + " NUMERIC,"
                + KEY_LONGITUDE + " NUMERIC" +")";
        db.execSQL(table_users);
        String table_logged = "CREATE TABLE " + TABLE_LOGGED + "("
                + KEY_NAME + " TEXT," + KEY_STATUS + " TEXT" +")";
        db.execSQL(table_logged);
        String table_user_contacts = "CREATE TABLE " + TABLE_USER_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_USER_ID + " INTEGER,"
                + KEY_CONTACT + " TEXT,"
                + KEY_LATITUDE + " NUMERIC,"
                + KEY_LONGITUDE + " NUMERIC" +")";
        db.execSQL(table_user_contacts);
        Log.d("table", "created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGGED);
        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    void addUser(String user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, user); // User Name
        values.put(KEY_LATITUDE, 0);
        values.put(KEY_LONGITUDE, 0);

        // Inserting Row
        db.insert(TABLE_USERS, null, values);
        db.close(); // Closing database connection
        Log.d("user_table", "inserted");
    }

    void addContact(String user, String contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        int id = findUserId(user);
        values.put(KEY_USER_ID, id); // User Name
        values.put(KEY_CONTACT, contact); // User Name
        values.put(KEY_LATITUDE, 0);
        values.put(KEY_LONGITUDE, 0);

        // Inserting Row
        db.insert(TABLE_USER_CONTACTS, null, values);
        db.close(); // Closing database connection
        Log.d("contact_table", "inserted");
    }

    int findUserId(String name) {
        String selectQuery = "SELECT id FROM " + TABLE_USERS + " where " + KEY_NAME + " ='" + name +"'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            return cursor.getInt(0);
        }
        return -1;
    }

    // Getting All Users
    public void getAllUsers() {
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_USERS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Log.d("data",cursor.getString(0));
                Log.d("data",cursor.getString(1));
                Log.d("data", cursor.getString(2));
                Log.d("data", cursor.getString(3));
            } while (cursor.moveToNext());
        }
    }

    // Getting All Contacts
    public String[] getAllContacts(String user) {
        // Select All Query
        int id = findUserId(user);
        String selectQuery = "SELECT contact FROM " + TABLE_USER_CONTACTS + " where " + KEY_USER_ID + " ='" + id + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        String str[] = new String[cursor.getCount()];
        int index = 0;
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Log.d("data",cursor.getString(0));
                str[index] = cursor.getString(0);
                index++;
            } while (cursor.moveToNext());
        }
        return str;
    }
    
    // Updating single user
    public int updateUserLocation(String user, double Latitude, double Longitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_LATITUDE, Latitude);
        values.put(KEY_LONGITUDE, Longitude);

        // updating row
        return db.update(TABLE_USERS, values, KEY_NAME + " = ?",
                new String[]{user});
    }

    public void loggedUser(String user, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        if(status.equals("IN")) {
            ContentValues values = new ContentValues();
            values.put(KEY_NAME, user); // User Name
            values.put(KEY_STATUS, status); // User Name
            // Inserting Row
            db.insert(TABLE_LOGGED, null, values);
            Log.d("table", "inserted");
        }
        else if(status.equals("OUT")){
            db.delete(TABLE_LOGGED, KEY_NAME + " = ?",
                new String[] {user});
            Log.d("table", "deleted");
        }
        db.close(); // Closing database connection
    }

    public String getLoggedUser() {
        String selectQuery = "SELECT  * FROM " + TABLE_LOGGED;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            return cursor.getString(0);
        }
        else
            return "";
    }

    // Deleting single contact
//    public void deleteContact(Contact contact) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.delete(TABLE_CONTACTS, KEY_ID + " = ?",
//                new String[] { String.valueOf(contact.getID()) });
//        db.close();
//    }



    // Getting single contact
//    Contact getContact(int id) {
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.query(TABLE_CONTACTS, new String[] { KEY_ID,
//                        KEY_NAME, KEY_PH_NO }, KEY_ID + "=?",
//                new String[] { String.valueOf(id) }, null, null, null, null);
//        if (cursor != null)
//            cursor.moveToFirst();
//        Contact contact = new Contact(Integer.parseInt(cursor.getString(0)),
//                cursor.getString(1), cursor.getString(2));
//        // return contact
//        return contact;
//    }

}