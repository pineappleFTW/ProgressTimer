package com.lisheng.progressv21;


import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;


import com.lisheng.progressv21.TimerDatabaseHelper;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimerDataFragment extends ListFragment implements AdapterView.OnItemLongClickListener  {
    private SQLiteDatabase db;
    private Cursor cursor;
    public static long currentLongId;

    public TimerDataFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Context context=getActivity();
        try{
            SQLiteOpenHelper timerDatabaseHelper=new TimerDatabaseHelper(context);
            db=timerDatabaseHelper.getReadableDatabase();

            cursor=db.query("TIMER",new String[] {"_id","DATE"},null,null,null,null,null);

            CursorAdapter listAdapter=new SimpleCursorAdapter(getActivity(),
                    android.R.layout.simple_list_item_1,
                    cursor,new String[] {"DATE"},
                    new int[] {android.R.id.text1},
                    0);

            setListAdapter(listAdapter);
        }catch(SQLiteException e){
            Toast toast=Toast.makeText(getActivity(),"Database not found",Toast.LENGTH_SHORT);
            toast.show();
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
        ListView lv=getListView();
        lv.setLongClickable(true);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lv.setOnItemLongClickListener(this) ;


    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Toast toast=Toast.makeText(getActivity(),"hello" +position,Toast.LENGTH_SHORT);
        toast.show();
        getListView().setSelection(position);
        getListView().setSelector(android.R.color.darker_gray);
        currentLongId=id;
        return true;
    }

    public static void deleteEntry(SQLiteDatabase db,long id){
        db.delete("TIMER"," _id = ? ",new String[] {Long.toString(id)});
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        cursor.close();
        db.close();


    }


    @Override
    public void onListItemClick(ListView listView, View itemView, int position, long id){
        getListView().setSelector(android.R.color.transparent);
        Intent intent=  new Intent(getActivity(), TimeDetailActivity.class);
        intent.putExtra(TimeDetailActivity.EXTRA_ID,(int)id);
        startActivity(intent);
    }


}
