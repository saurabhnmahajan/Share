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
    private static final String TABLE_USERS = "users";
    private static final String TABLE_LOGGED = "user_logged";
    private static final String TABLE_USER_CONTACTS = "user_contacts";

    // columns
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_ACC_TYPE = "acc_type";
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
                + KEY_EMAIL + " TEXT,"
                + KEY_LATITUDE + " NUMERIC,"
                + KEY_LONGITUDE + " NUMERIC" +")";
        db.execSQL(table_users);
        String table_logged = "CREATE TABLE " + TABLE_LOGGED + "("
                + KEY_EMAIL + " TEXT," + KEY_ACC_TYPE + " TEXT" +")";
        db.execSQL(table_logged);
        String table_user_contacts = "CREATE TABLE " + TABLE_USER_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_USER_ID + " INTEGER,"
                + KEY_CONTACT + " TEXT" + ")";
        db.execSQL(table_user_contacts);
        Log.d("table", "created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGGED);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_CONTACTS);
        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    void addUser(String name, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_EMAIL, email);
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
        values.put(KEY_USER_ID, id);
        values.put(KEY_CONTACT, contact);

        // Inserting Row
        db.insert(TABLE_USER_CONTACTS, null, values);
        db.close(); // Closing database connection
        Log.d("contact_table", "inserted");
    }

    int findUserId(String email) {
        String selectQuery = "SELECT id FROM " + TABLE_USERS + " where " + KEY_EMAIL + " ='" + email +"'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            return cursor.getInt(0);
        }
        return -1;
    }

    // Getting All Users
    public void getAllLoggedUsers() {
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_LOGGED;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Log.d("data", cursor.getString(0));
                Log.d("data", cursor.getString(1));
            } while (cursor.moveToNext());
        }
    }

    // Getting All Contacts
    public String[] getAllContacts(String email) {
        // Select All Query
        int id = findUserId(email);
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
    public void updateUserLocation(String email, double Latitude, double Longitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_LATITUDE, Latitude);
        values.put(KEY_LONGITUDE, Longitude);

        // updating row
        db.update(TABLE_USERS, values, KEY_EMAIL + " = ?",
                new String[]{email});
    }

    public void loggedUser(String email, String acc_type) {
        SQLiteDatabase db = this.getWritableDatabase();
        if(acc_type.equals("IN") || acc_type.equals("google_in")) {
            ContentValues values = new ContentValues();
            values.put(KEY_EMAIL, email); // User Name
            values.put(KEY_ACC_TYPE, acc_type); // User Name
            // Inserting Row
            db.insert(TABLE_LOGGED, null, values);
        }
        else if(acc_type.equals("OUT")) {
            db.delete(TABLE_LOGGED, KEY_EMAIL + " = ?",
                    new String[]{email});
        }
        else if(acc_type.equals("google_out")) {
            ContentValues values = new ContentValues();
            values.put(KEY_ACC_TYPE, "OUT");
            // updating row
            db.update(TABLE_LOGGED, values, KEY_EMAIL + " = ?",
                    new String[]{email});
        }
        db.close(); // Closing database connection
    }

    public void removeLoggedUser() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LOGGED, KEY_ACC_TYPE + " = ?",
                new String[]{"OUT"});
        db.close();
    }

    public String[] getLoggedUser() {
        String selectQuery = "SELECT * FROM " + TABLE_LOGGED;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            String loggedUser[] = {cursor.getString(0), cursor.getString(1)};
            return loggedUser;
        }
        else {
            return null;
        }
    }

    public int checkUser(String email) {
        String selectQuery = "SELECT  * FROM " + TABLE_USERS + " where " + KEY_EMAIL + " = '" + email + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            return 1;
        }
        return -1;
    }

    public String[][] getSelectedContactsLocation(String users) {
        String selectQuery = "SELECT email, latitude, longitude FROM " + TABLE_USERS + " where " + KEY_EMAIL + " IN (" + users + ")";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        String loc[][] = new String[cursor.getCount()][3];
        int contact = 0;
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                loc[contact][0] = cursor.getString(0);
                loc[contact][1] = cursor.getString(1);
                loc[contact][2] = cursor.getString(2);
                contact++;
            } while(cursor.moveToNext());
        }
        return loc;
    }

    public String[] search(String searchString) {
        String selectQuery = "SELECT email FROM " + TABLE_USERS + " where " + KEY_EMAIL + " LIKE '%" + searchString + "%'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        String contacts[] = new String[cursor.getCount()];
        int count = 0;
        if (cursor.moveToFirst()) {
            do {
                contacts[count] = cursor.getString(0);
                count++;
            } while(cursor.moveToNext());
        }
        return contacts;
    }

    // Deleting single contact
//    public void deleteContact(Contact contact) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.delete(TABLE_CONTACTS, KEY_ID + " = ?",
//                new String[] { String.valueOf(contact.getID()) });
//        db.close();
//    }
}