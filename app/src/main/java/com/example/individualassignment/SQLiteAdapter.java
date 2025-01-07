package com.example.individualassignment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import java.sql.Date;

public class SQLiteAdapter {

    public static final String db_Name = "eZ_DATABASE";
    public static final int db_ver = 1;
    public static final String db_Summary = "Summary";
    public static final String sum_Name = "sum_Name";
    public static final String total_Amount = "total_Amount";
    public static final String total_ppl = "total_ppl";
    public static final String split_choice = "split_choice";
    public static final String date = "date_time";


    //SQL command to create table with 4 columns
    private static final String SCRIPT_CREATE_DATABASE = "create table " + db_Summary + "(id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + sum_Name + " text not null, "
            + total_Amount + " Integer not null, "
            + total_ppl + " Integer not null, "
            + split_choice + " text not null, "
            + date + " text not null);";


    //variables
    private Context context;

    private SQLiteHelper sqLiteHelper;
    private SQLiteDatabase sqLiteDatabase;
    //constructor for SQLiteAdapter
    public SQLiteAdapter(Context c)
    {
        context = c;
    }

    //open the database to write something
    public SQLiteAdapter openToWrite() throws android.database.SQLException
    {
        sqLiteHelper = new SQLiteHelper(context, db_Name, null, db_ver);
        sqLiteDatabase = sqLiteHelper.getWritableDatabase();
        return this;
    }

    //open the database to read something
    public SQLiteAdapter openToRead() throws android.database.SQLException
    {
        sqLiteHelper = new SQLiteHelper(context, db_Name, null, db_ver);
        sqLiteDatabase = sqLiteHelper.getReadableDatabase();
        return this;
    }
//
//
    public long insert(String name, String amount, String ppl, String choice, String date_time)
    {
        ContentValues contentValues = new ContentValues();

        //to write the content to the column of KEY_CONTENT;
        contentValues.put(sum_Name,name);
        contentValues.put(total_Amount,amount);
        contentValues.put(total_ppl,ppl);
        contentValues.put(split_choice, choice);
        contentValues.put(date, date_time);

        return sqLiteDatabase.insert(db_Summary, null, contentValues);
    }


    public Cursor getCursorAll() //return the cursor
    {
        String[] columns = new String[] { sum_Name, total_Amount, total_ppl, split_choice, date};
        Cursor cursor = sqLiteDatabase.query(db_Summary, columns, null,null,null,null,null);
        return cursor;
    }



    public void close()
    {
        sqLiteHelper.close();
    }

    public int deleteAll()
    {
        return sqLiteDatabase.delete(db_Summary, null, null);
    }

    public class SQLiteHelper extends SQLiteOpenHelper
    {

        //constructor for SQLiteHelper
        public SQLiteHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        //create a table with columns
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SCRIPT_CREATE_DATABASE);
        }


        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i1) {
            db.execSQL(SCRIPT_CREATE_DATABASE); // to inform it has upgraded
        }
    }
}
