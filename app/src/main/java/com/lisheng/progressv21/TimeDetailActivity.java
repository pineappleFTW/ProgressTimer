package com.lisheng.progressv21;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeDetailActivity extends Activity {

    public static final String EXTRA_ID ="IDPos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_detail);

        int id=(int)getIntent().getExtras().get(EXTRA_ID);

        try{
            SQLiteOpenHelper timerDatabaseHelper=new TimerDatabaseHelper(this);
            SQLiteDatabase db=timerDatabaseHelper.getWritableDatabase();

            Cursor cursor=db.query("TIMER",
                    new String[] {"DATE" , "GOAL" ,"DESC" },
                    " _id = ? ",
                    new String[]{Integer.toString(id)},
                    null,null,null);

            if(cursor.moveToFirst()){
                String dateText=cursor.getString(0);
                int goalText=cursor.getInt(1);
                String descText=cursor.getString(2);

                TextView date=(TextView)findViewById(R.id.date);
                date.setText(dateText);

                TextView goal=(TextView)findViewById(R.id.goal);
                String goalT= Integer.toString(goalText);
                goal.setText(goalT+ " seconds");

                TextView desc=(TextView)findViewById(R.id.desc_detail);
                desc.setText(descText);



            }
            cursor.close();
            db.close();


        }catch(SQLiteException e){
            Toast toast=Toast.makeText(this,"Database not found",Toast.LENGTH_SHORT);
            toast.show();
        }


    }
}
