package com.rhythm003.help;

import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.microedition.khronos.opengles.GL;

/**
 * Created by Rhythm003 on 9/6/2016.
 * Use Dbhelper to perform CRUD operations on the database in sqlite.
 * Currently there are four tables in the sqlite database.
 */
public class DbHelper extends SQLiteOpenHelper {
    private final static String DB_NAME = "type1_db";
    private final static String GLU_TABLE = "glu_table";
    private final static String CAL_TABLE = "cal_table";
    private final static String HR_TABLE = "hr_table";
    private final static int DB_VER = 1;
    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    // Create tables.
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_GLU = "CREATE TABLE " + GLU_TABLE + "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "level FLOAT NOT NULL, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, devicetime TEXT NOT NULL)";
        String CREATE_CAL = "CREATE TABLE " + CAL_TABLE + "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "level INT NOT NULL, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, intime TEXT NOT NULL)";
        String CREATE_HR = "CREATE TABLE " + HR_TABLE + "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "level INT NOT NULL, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, intime TEXT NOT NULL UNIQUE)";
        sqLiteDatabase.execSQL(CREATE_GLU);
        sqLiteDatabase.execSQL(CREATE_CAL);
        sqLiteDatabase.execSQL(CREATE_HR);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String DROP_TABLE = "DROP TABLE IF EXISTS" + GLU_TABLE;
        sqLiteDatabase.execSQL(DROP_TABLE);
        DROP_TABLE = "DROP TABLE IF EXISTS" + CAL_TABLE;
        sqLiteDatabase.execSQL(DROP_TABLE);
        onCreate(sqLiteDatabase);
    }

    // Insert glucose level into glu_table.
    public void insertGlu(float level, long devicetime) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("level", level);
        values.put("devicetime", devicetime);
        db.insert(GLU_TABLE, null, values);
        db.close();
        Log.d("DbHelper GLU", "inserted" + devicetime);
    }

    // Get most recent 10 glucose level record.
    public List<Pair<Float, Long>> getGlu() {
        SQLiteDatabase db = getReadableDatabase();
        String GET_GLU = "select t.* from (select * from " + GLU_TABLE + " order by created_at desc limit 10) t order by created_at asc";
        Cursor cursor = db.rawQuery(GET_GLU, null);
        String res = "";
        List<Pair<Float, Long>> values = new ArrayList<>();
        cursor.moveToFirst();
        if(cursor.getCount() > 0) {
            do {
                values.add(new Pair<Float, Long>(cursor.getFloat(1), cursor.getLong(3)));
                res += Long.toString(cursor.getLong(3)) + " ";
            } while (cursor.moveToNext());
        }
        //Log.d("DbHelper", res);
        db.close();
        cursor.close();
        return values;
    }

    // Get most recent 10 calorie level record.
    public List<Pair<Integer, Long>> getCal() {
        SQLiteDatabase db = getReadableDatabase();
        String GET_CAL = "select t.* from (select * from " + CAL_TABLE + " order by intime desc limit 10) t order by intime asc";
        Cursor cursor = db.rawQuery(GET_CAL, null);
        String res = "";
        List<Pair<Integer, Long>> values = new ArrayList<>();
        cursor.moveToFirst();
        if(cursor.getCount() > 0) {
            do {
                values.add(new Pair<Integer, Long>(cursor.getInt(1), cursor.getLong(3)));
                res += Long.toString(cursor.getLong(3)) + " ";
            } while (cursor.moveToNext());
        }
        //Log.d("DbHelper", res);
        db.close();
        cursor.close();
        return values;
    }

    // Get most recent 10 heart rate level record.
    public List<Pair<Integer, Long>> getHR() {
        SQLiteDatabase db = getReadableDatabase();
        String GET_HR = "select t.* from (select * from " + HR_TABLE + " order by intime desc limit 10) t order by intime asc";
        Cursor cursor = db.rawQuery(GET_HR, null);
        String res = "";
        List<Pair<Integer, Long>> values = new ArrayList<>();
        cursor.moveToFirst();
        if(cursor.getCount() > 0) {
            do {
                values.add(new Pair<Integer, Long>(cursor.getInt(1), cursor.getLong(3)));
                res += Long.toString(cursor.getLong(3)) + " ";
            } while (cursor.moveToNext());
        }
        //Log.d("DbHelper", res);
        db.close();
        cursor.close();
        return values;
    }

    // Insert calorie level into glu_table.
    public void insertCal(int level, String intime) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("level", level);
        values.put("intime", intime);
        db.insert(CAL_TABLE, null, values);
        db.close();
        Log.d("DbHelper CAL", "inserted" + level + intime);
    }

    // Insert heart rate level into glu_table.
    public void insertHR(int level, String intime) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("level", level);
        values.put("intime", intime);
        db.insert(HR_TABLE, null, values);
        db.close();
        Log.d("DbHelper HR", "inserted" + level + intime);
    }
}
