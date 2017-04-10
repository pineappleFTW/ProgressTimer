package com.lisheng.progressv21;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.hanks.htextview.HTextView;
import com.hanks.htextview.HTextViewType;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class TimerFragment extends Fragment implements View.OnClickListener{
    private int seconds;
    private boolean running;
    private double goal=60;
    private long startTimeInSecond;
    private boolean goalAchieved;
    private boolean saved=false;
    private int spinnerPost;
    private int count;

    public TimerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout=inflater.inflate(R.layout.fragment_timer,container,false);

        count=0;
        if(savedInstanceState!=null){
            seconds=savedInstanceState.getInt("seconds");
            running=savedInstanceState.getBoolean("running");
            goal=savedInstanceState.getDouble("goal");
        }
        if(savedInstanceState==null){
            SharedPreferences sharedPref= this.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
            int secInSaved=sharedPref.getInt("seconds",0);
            long goalInSeconds=sharedPref.getLong("goalInSeconds",0);
            long pastStarttTime=sharedPref.getLong("currentTimeInSecond",0);
            boolean isRunning=sharedPref.getBoolean("isRunning",false);
            double goalR=sharedPref.getLong("goal",0);
            spinnerPost=sharedPref.getInt("spinnerPos",0);
            Spinner spinner=(Spinner)layout.findViewById(R.id.goal_spinner);
            spinner.setSelection(spinnerPost);


            if(isRunning){
                Button button=(Button)layout.findViewById(R.id.button);
                button.setText("Reset");
                goal=goalR;
                running=true;
                seconds=(int)(new Date().getTime() /1000)-(int)pastStarttTime;
                if(seconds>goalR){
                    running=false;
                    seconds=(int)goalR;
                }
                startTimeInSecond=pastStarttTime;

                //TextView progressView=(TextView)layout.findViewById(R.id.progress);
                CircularProgressBar progressBar=(CircularProgressBar)layout.findViewById(R.id.progressBar);

                double progress= (seconds/goal)*100;
                String prog= String.format("%.1f %%",progress);
                progressBar.setProgressWithAnimation((float)progress,2000);
               // progressView.setText(prog);


            }else{
                seconds=secInSaved;
                goal=goalR;
                if(seconds==goalR){
                    Button button=(Button)layout.findViewById(R.id.button);
                    button.setText("Reset");
                    spinner.setEnabled(false);
                    goalAchieved=true;
                }
            }
        }
        Button button=(Button)layout.findViewById(R.id.button);
        button.setOnClickListener(this);
        Button saveButton=(Button)layout.findViewById(R.id.save_button);
        saveButton.setOnClickListener(this);
        runTimer(layout);
        final Spinner spinner=(Spinner)layout.findViewById(R.id.goal_spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view,
            int pos, long id) {
                count++;
                if(count>=2)
                {
                    switch(pos){
                        case 0:goal=60;
                            break;
                        case 1:goal=120;
                            break;
                        case 2:goal=180;
                            break;
                        case 3:goal=240;
                            break;
                        case 4:goal=300;
                            break;
                        case 5:goal=360;
                            break;
                        case 6: goal=420;
                            break;
                        case 7:goal=480;
                            break;
                        case 8:goal=540;
                            break;
                        case 9:goal=600;
                            break;
                        case 10:goal=660;
                            break;
                        case 11:goal=720;
                            break;
                        case 12:goal=780;
                            break;
                        case 13:goal=840;
                            break;
                        case 14:goal=900;
                            break;

                        default:goal=60;

                    }
                    spinnerPost=pos;
                    Toast toast=Toast.makeText(getActivity(),goal/60+" minute "+count +" "+spinnerPost,Toast.LENGTH_SHORT);
                    toast.show();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return layout;
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.button:onClickButton(v);
                break;
            case R.id.save_button:onClickSaveButton(v);
                break;

        }
    }

    public void onClickSaveButton(View v){
        if(goalAchieved && !saved){
            AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
            LayoutInflater inflater=getActivity().getLayoutInflater();
            builder.setView(inflater.inflate(R.layout.save_dialog,null));
            builder.setPositiveButton("Confirm",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try{
                        SQLiteOpenHelper timerDatabaseHelper=new TimerDatabaseHelper(getActivity());
                        SQLiteDatabase db=timerDatabaseHelper.getWritableDatabase();
                        Dialog d=(Dialog)dialog;
                        EditText descInput=(EditText)d.findViewById(R.id.description_timer);
                        Date date=new Date();
                        SimpleDateFormat df=new SimpleDateFormat("E dd.MM.yyyy ':' hh.mm.ss a");
                        String dateInString= df.format(date);
                        String desc=descInput.getText().toString();
                        TimerDatabaseHelper.insertTime(db,dateInString,(int)goal,desc);
                        saved=true;

                    }catch(SQLiteException e){
                        Toast toast=Toast.makeText(getActivity(),"Database not found",Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            });
            builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog dialog=builder.create();
            dialog.show();
        }else{
            Toast toast=Toast.makeText(getActivity(),"Goal is not achieved cant save",Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    public void onClickButton(View view){
        final Button button=(Button)getView().findViewById(R.id.button);
        //final TextView progView=(TextView)getView().findViewById(R.id.progress);
        final CircularProgressBar progressBar=(CircularProgressBar)getView().findViewById(R.id.progressBar);

        if(button.getText().equals("Start"))
        {
            running=true;
            startTimeInSecond= new Date().getTime()/1000;
            button.setText("Reset");
            goalAchieved=false;
        }else{

            AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
            builder.setCancelable(true);
            builder.setMessage("Confirm to reset?");
            builder.setPositiveButton("Confirm",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final Spinner spinner=(Spinner)getView().findViewById(R.id.goal_spinner);
                    spinner.setEnabled(true);
                    running=false;
                    button.setText("Start");
                   // progView.setText("0.0");
                    progressBar.setProgressWithAnimation(0,2500);
                    seconds=0;
                    goalAchieved=false;
                    saved=false;
                }
            });
            builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog dialog=builder.create();
            dialog.show();

        }

    }


    @Override
    public void onStop(){
        super.onStop();
        boolean isRunning=false;
        long goalInSeconds= startTimeInSecond + (long)goal;
        if(running){
            isRunning=true;
        }
        final SharedPreferences sharedPref= this.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPref.edit();
        editor.putInt("seconds",seconds);
        editor.putLong("goalInSeconds",goalInSeconds);
        editor.putLong("currentTimeInSecond",startTimeInSecond);
        editor.putLong("goal",(long)goal);
        editor.putBoolean("isRunning",isRunning);
        editor.putInt("spinnerPos",spinnerPost);
        editor.commit();
    }

    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void runTimer(View view){
        //final TextView secView=(TextView)view.findViewById(R.id.secondView);
        final HTextView HTextSecView=(HTextView)view.findViewById(R.id.secView);
        //final TextView minView=(TextView)view.findViewById(R.id.minView);
        final HTextView HTextMinView=(HTextView)view.findViewById(R.id.minView);
        final Spinner spinner=(Spinner)view.findViewById(R.id.goal_spinner);
        //final TextView progressView=(TextView)view.findViewById(R.id.progress);
        final CircularProgressBar circularProgressBar=(CircularProgressBar)view.findViewById(R.id.progressBar);
        final Handler handler=new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {

                double progress= (seconds/goal)*100;
                int hours= seconds/3600;
                int minutes= (seconds%3600)/60;
                String prog= String.format("%.1f %%",progress);
                String sec= String.format("%2d second",seconds);
                String min=String.format("%2d minutes",minutes);

                if(seconds>0 && !goalAchieved)
                {
                    HTextSecView.setAnimateType(HTextViewType.LINE);
                    HTextSecView.animateText(Integer.toString(seconds) + " "+"seconds");
                }else{
                    HTextSecView.setAnimateType(HTextViewType.EVAPORATE);
                    HTextSecView.animateText(Integer.toString(seconds) + " "+"seconds");
                }



                    HTextMinView.setAnimateType(HTextViewType.EVAPORATE);
                    HTextMinView.animateText(min);

                //secView.setText(sec);
                //minView.setText(min);
                if(Double.isNaN(progress)) {
                    //progressView.setText("0.0 %");
                    circularProgressBar.setProgressWithAnimation(0,2000);
                }else{
                    //progressView.setText(prog);
                    circularProgressBar.setProgressWithAnimation((float)progress,2500);
                }
                if(running){
                    seconds++;


                }

                if(seconds>=goal){
                    //Toast toast=Toast.makeText(getActivity(),"yay u got it",Toast.LENGTH_SHORT);
                   // toast.show();
                    //seconds=(int)goal;
                    spinner.setEnabled(false);
                    goalAchieved=true;
                    running=false;
                }

                handler.postDelayed(this,1000);
            }
        } );
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){

        savedInstanceState.putInt("seconds",seconds);
        savedInstanceState.putBoolean("running",running);
        savedInstanceState.putDouble("goal",goal);

    }

}
