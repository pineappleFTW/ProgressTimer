package com.lisheng.progressv21;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Date;

/**
 * Created by lisheng on 3/18/2017.
 */

public class TimerDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME="timerdata";
    private static final int DB_VERSION=1;

    TimerDatabaseHelper(Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        updateMyDatabase(db,0,DB_VERSION);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
        updateMyDatabase(db,oldVersion,newVersion);
    }

    protected static void insertTime(SQLiteDatabase db, String date, int goal,String desc){
        ContentValues timerValues=new ContentValues();
        timerValues.put("DATE",date);
        timerValues.put("GOAL",goal);
        timerValues.put("DESC",desc);

        db.insert("TIMER",null,timerValues);
    }

    private void updateMyDatabase(SQLiteDatabase db,int oldVersion,int newVersion){
        if(oldVersion<1){
            db.execSQL("CREATE TABLE TIMER ("
                    + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "DATE TEXT,"
                    + "GOAL INTEGER,"
                    + "DESC TEXT); ");
            insertTime(db, "Fri 12.03.2017 : 12:43:12 PM", 120,"Studying");
            insertTime(db, "Sat 13.03.2017 : 09:43:12 PM", 180,"Nap");
            insertTime(db, "Mon 15.03.2017 : 05:43:12 AM", 60,"Driving");

        }

    }

}
