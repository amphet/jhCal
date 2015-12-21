package com.example.hjh.drawtest;


import android.content.Intent;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;



public class DayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);
    }

    public int nTimeSelectMode;
    public void scheduleTimeSelectEnd(int time)
    {
        Intent intent = getIntent();
        Bundle myBundle = intent.getExtras();
        String str;
        str = String.format("%02d",time/60)+":"+String.valueOf(time%60)+":00";
        myBundle.putString("Time", str);
        intent.putExtras(myBundle);
        setResult(nTimeSelectMode+10,intent);
        finish();
    }

    public void scheduleModify(ScheduleNode pickedNode){
        Intent intent = getIntent();
        Bundle myBundle = intent.getExtras();



        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        myBundle.putString("ScheduleName",pickedNode.strScheduleName);

        String strStartDate= sdf.format(pickedNode.startDate);
        String strEndDate = sdf.format(pickedNode.endDate);
        myBundle.putString("StartTime",strStartDate);
        myBundle.putString("EndTime",strEndDate);
        myBundle.putString("DATE", myBundle.getString("DATE"));
        myBundle.putBoolean("IsModify", true);
        myBundle.putInt("ScheduleId",pickedNode.nKey);
        intent.putExtras(myBundle);

        setResult(MainActivity.SCHEDULETIMESELECT_ENDTIME+20,intent);
        finish();
    }
    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();
        Bundle myBundle = intent.getExtras();
        String strTouchedDate = myBundle.getString("DATE");
        nTimeSelectMode = myBundle.getInt("TimeSelectMode");

        switch(nTimeSelectMode){
            case MainActivity.SCHEDULETIMESELECT_NONE:

                break;
            case MainActivity.SCHEDULETIMESELECT_STARTTIME:
                Toast.makeText(getBaseContext(),"Select Start Time",Toast.LENGTH_LONG ).show();

                break;
            case MainActivity.SCHEDULETIMESELECT_ENDTIME:
                Toast.makeText(getBaseContext(),"Select End Time",Toast.LENGTH_LONG ).show();
                break;
            default:
                break;
        }



        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy.MM.dd");
        Calendar cal = Calendar.getInstance();

        try {

            Date tempDate = dayFormat.parse(strTouchedDate);
            cal.setTime(tempDate);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }



       // Toast.makeText(getBaseContext(), strTouchedDate, Toast.LENGTH_LONG).show();

        MainActivity.myDBManager.get1DaySchedule(cal);
        ((YearMonthFragment) (getSupportFragmentManager().findFragmentById(R.id.yearmonthfragment))).setYearMonth(strTouchedDate.substring(0, 7));
        ((ScheduleFragment)(getSupportFragmentManager().findFragmentById(R.id.schedulefragment))).setScheduleListview(strTouchedDate);
        ((TodoFragment)(getSupportFragmentManager().findFragmentById(R.id.todofragment))).setTodoListview(cal);
        ((TodayFragment)(getSupportFragmentManager().findFragmentById(R.id.todayfragment))).setTodayText(cal);
        ((TimelineFragment)(getSupportFragmentManager().findFragmentById(R.id.timelinefragment))).setDateForTimeline(cal);
        ((TimelineFragment)(getSupportFragmentManager().findFragmentById(R.id.timelinefragment))).setTimeSelectionMode(nTimeSelectMode);





    }
}
