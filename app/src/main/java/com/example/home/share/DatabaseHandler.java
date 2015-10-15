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

    // columns
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_STATUS = "status";

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
        Log.d("table", "inserted");
    }

    // Getting All Users
    public void getAllContacts() {
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_USERS;
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

    // Updating single contact
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
            db.close(); // Closing database connection
            Log.d("table", "inserted");
        }
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

//    // Getting contacts Count
//    public int getContactsCount() {
//        String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.rawQuery(countQuery, null);
//        cursor.close();
//
//        // return count
//        return cursor.getCount();
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