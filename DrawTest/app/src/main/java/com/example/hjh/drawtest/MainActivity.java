package com.example.hjh.drawtest;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Region;
import android.provider.CalendarContract;
//import android.support.v4.app.DialogFragment;
import android.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.FloatMath;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.app.Activity;

import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.DatePicker;
import android.widget.Toast;

import junit.framework.Assert;


//한글




public class MainActivity extends Activity implements View.OnTouchListener{// ActionBarActivity {



   static ScheduleDBManager myDBManager;

    String strSavedScheduleName;
    String strSavedScheduleStartTime;
    String strSavedScheduleEndTime;
    int nScheduleTimeSelectMode;
    public static final int SCHEDULETIMESELECT_NONE = 0;
    public static final int SCHEDULETIMESELECT_STARTTIME = 1;
    public static final int SCHEDULETIMESELECT_ENDTIME = 2;

    Intent intentNewScheduleActivity;
    Bundle bundleNewScheduleActivity;
    Intent intentDayActivity;
    Bundle bundleDayActivity;

    //for YEAR/MONTH change dialog?????????
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker


            int year = rView.mainCalendar.calendar_YearMonth.get(Calendar.YEAR);
            int month = rView.mainCalendar.calendar_YearMonth.get(Calendar.MONTH);
            int day = rView.mainCalendar.calendar_YearMonth.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(),android.R.style.Theme_Holo_Light_Dialog_NoActionBar, this, year, month, 1);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {

            String strTemp = String.valueOf(year) + "/" + String.valueOf(month) +"/"+String.valueOf(day);
            //Toast tmpToast = Toast.makeText(getActivity(),strTemp,Toast.LENGTH_SHORT);
            //tmpToast.show();
                        //reset all values
            rView.mainCalendar.resetCalendar();

            Calendar targetDate = Calendar.getInstance();
            targetDate.set(year,month,1,0,0,0);//should jump to this


            SimpleDateFormat dayFormat = new SimpleDateFormat("c", Locale.UK);
            String weekdayCheck =  dayFormat.format(targetDate.getTime());
            int nDifferenceFromThisWeek;
            if(weekdayCheck.equals("Mon"))
            {nDifferenceFromThisWeek = 1;}
            else if(weekdayCheck.equals("Tue"))
            {nDifferenceFromThisWeek = 2;}
            else if(weekdayCheck.equals("Wed"))
            {nDifferenceFromThisWeek = 3;}
            else if(weekdayCheck.equals("Thu"))
            {nDifferenceFromThisWeek = 4;}
            else if(weekdayCheck.equals("Fri"))
            {nDifferenceFromThisWeek = 5;}
            else if(weekdayCheck.equals("Sat"))
            {nDifferenceFromThisWeek = 6;}
            else
            {
                nDifferenceFromThisWeek = 0;
            }
            targetDate.add(Calendar.DATE,-nDifferenceFromThisWeek);
            rView.mainCalendar.calendar_FirstShowingDay.setTime(targetDate.getTime());
            rView.mainCalendar.pRenderView.invalidate();

        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        //rView.mainCalendar.touchEventWrapper(v, event);

        int checker = 0;



        boolean bCh = false;

        float x,y;

        y = rView.mainCalendar.fCalendarY;
        for(int j=0;j<8;j++) {
            x = rView.mainCalendar.xPad + rView.mainCalendar.fCalendarX;
            for (int i = 0; i < 7; i++) {
                if(x <= event.getX() && event.getX() <= x+rView.mainCalendar.dW)
                {
                    if(y <= event.getY() && event.getY() <= y+rView.mainCalendar.dH)
                    {
                        bCh = true;
                        break;

                    }
                }

                checker++;
                x = x + rView.mainCalendar.dW;
            }
            y = y + rView.mainCalendar.dH;
            if(bCh) break;
        }



        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy.MM.dd");
        Calendar calTemp = Calendar.getInstance();
        calTemp.setTime(rView.mainCalendar.calendar_FirstShowingDay.getTime());
        calTemp.add(Calendar.DATE, checker);
        String strTouchedDate =  dayFormat.format(calTemp.getTime());





        float fOrigCalendarX=0;
        float fOrigCalendarY=0;
        float fDistX,fDistY;
        switch(event.getAction() & MotionEvent.ACTION_MASK){
            //Pinch Zoom Implementation
            case MotionEvent.ACTION_POINTER_DOWN:
                rView.mainCalendar.fZoomMidX = (event.getX(0) + event.getX(1))/2;
                rView.mainCalendar.fZoomMidY = (event.getY(0) + event.getY(1))/2;

                fDistX = event.getX(0) - event.getX(1);
                fDistY =event.getY(0) - event.getY(1);

                rView.mainCalendar.fZoomOldDist = FloatMath.sqrt(fDistX * fDistX + fDistY * fDistY);
                rView.mainCalendar.fZoomNewDist = rView.mainCalendar.fZoomOldDist;
                rView.mainCalendar.bIsZoom = 1;//zoomStart
                break;
            case MotionEvent.ACTION_POINTER_UP:
                rView.mainCalendar.bIsZoom = 2;//for async ACTION_UP
                break;

            //mmmmmmmmmmmmmmmmmmmm
            case MotionEvent.ACTION_UP:

                rView.mainCalendar.bIsZoom = 0;
                long clickDuration = Calendar.getInstance().getTimeInMillis() - rView.mainCalendar.startClickTime;

                if((event.getX()-rView.mainCalendar.fTodayBtnCoordX)*(event.getX()-rView.mainCalendar.fTodayBtnCoordX)
                        +(event.getY()-rView.mainCalendar.fTodayBtnCoordY)*(event.getY()-rView.mainCalendar.fTodayBtnCoordY)<=6400)//today button check
                {
                    rView.mainCalendar.resetCalendar();

                    rView.mainCalendar.pRenderView.invalidate();
                }
                else if((event.getX()-rView.mainCalendar.fNewScheduleBtnCoordX)*(event.getX()-rView.mainCalendar.fNewScheduleBtnCoordX)
                        +(event.getY()-rView.mainCalendar.fNewScheduleBtnCoordY)*(event.getY()-rView.mainCalendar.fNewScheduleBtnCoordY)<=6400)//NewScheduleBtnCheck
                {
                    nScheduleTimeSelectMode = SCHEDULETIMESELECT_NONE;


                    bundleNewScheduleActivity.putString("DATE", strTouchedDate);
                    bundleNewScheduleActivity.putString("StartTime", "Touch Here!");
                    bundleNewScheduleActivity.putString("EndTime", "Touch Here!");
                    bundleNewScheduleActivity.putString("ScheduleName", "??");
                    bundleNewScheduleActivity.putBoolean("IsModify", false);
                    bundleNewScheduleActivity.putInt("ScheduleId", -1);

                    intentNewScheduleActivity.putExtras(bundleNewScheduleActivity);


                    ((Activity)(v.getContext())).startActivityForResult(intentNewScheduleActivity,1);

//                    rView.mainCalendar.fScale += 0.2;
  //                  rView.invalidate();

                }
                else {
                    //Day touch
                    //show new Schedule diaglog
                    if (clickDuration < rView.mainCalendar.MAX_CLICK_DURATION) {

                        float fdx = event.getX() - rView.mainCalendar.fTouchStartX;
                        float fdy = event.getY() - rView.mainCalendar.fTouchStartY;
                        float fmoveddistance = FloatMath.sqrt(fdx*fdx + fdy*fdy);

                        if(event.getY()>=rView.mainCalendar.fMonthBannerSize && fmoveddistance < rView.mainCalendar.fRealCanvasWidth*0.01f)//disable on clipping area touch
                        {
                            //   dialog.show();
                            //Intent i = new Intent("net.learn2develop.NewScheduleActivity");
                            //Bundle myData = new Bundle();
                            //myData.putString("DATE", strTouchedDate);
                            //i.putExtras(myData);
                            //v.getContext().startActivity(new Intent("net.learn2develop.NewScheduleActivity"));
                            //((Activity)(v.getContext())).startActivityForResult(i,1);

                            bundleDayActivity.putString("DATE", strTouchedDate);

                            bundleDayActivity.putInt("TimeSelectMode", nScheduleTimeSelectMode);

                            intentDayActivity.putExtras(bundleDayActivity);
                            ((Activity)(v.getContext())).startActivityForResult(intentDayActivity,2);
                        //    overridePendingTransition(R.anim.abc_fade_in,R.anim.abc_fade_out);
                        }
                        else if(event.getY()<=rView.mainCalendar.fMonthBannerSize)// year,month touch
                        {
                            DialogFragment newFragment = new DatePickerFragment();


                            newFragment.show(getFragmentManager(), "datePicker");


                        }

                    }
                }
                break;
            case MotionEvent.ACTION_DOWN:
                //       dialog.show();    // 알림창 띄우기

                //Save touched Point's coord.
                rView.mainCalendar.fSlideStartY = event.getY();
                rView.mainCalendar.fSlideStartX = event.getX();

                //Save original coord of calendar
                fOrigCalendarY = rView.mainCalendar.fCalendarY;
                fOrigCalendarX = rView.mainCalendar.fCalendarX;
                rView.mainCalendar.startClickTime = Calendar.getInstance().getTimeInMillis();//click 구별

                rView.mainCalendar.fTouchStartX = event.getX();
                rView.mainCalendar.fTouchStartY = event.getY();

                break;
            case MotionEvent.ACTION_MOVE:
                if(rView.mainCalendar.bIsZoom==1) {//PINCH ZOOM EVENT

                    fDistX = event.getX(0) - event.getX(1);
                    fDistY =event.getY(0) - event.getY(1);

                    rView.mainCalendar.fZoomNewDist = FloatMath.sqrt(fDistX*fDistX + fDistY*fDistY);
                    float fNewScale = rView.mainCalendar.fScale * rView.mainCalendar.fZoomNewDist / rView.mainCalendar.fZoomOldDist;
                    if(fNewScale >=1 && fNewScale <5) {
                        float fScaleChange = fNewScale - rView.mainCalendar.fScale;
                        float offsetX = -(rView.mainCalendar.fZoomMidX * fScaleChange);
                        float offsetY = -(rView.mainCalendar.fZoomMidY * fScaleChange);
                        rView.mainCalendar.fCalendarX += offsetX;
                        rView.mainCalendar.fCalendarY += offsetY;

                        rView.mainCalendar.fZoomMidX = (event.getX(0) + event.getX(1)) / 2;
                        rView.mainCalendar.fZoomMidY = (event.getY(0) + event.getY(1)) / 2;

                        rView.mainCalendar.fZoomOldDist = rView.mainCalendar.fZoomNewDist;
                        rView.mainCalendar.fScale = fNewScale;
                    }
                    else if(fNewScale >=5)
                    {
                        //jump to DayActivity
                        //blur animation implement?
                       /* rView.mainCalendar.bIsZoom = 0;
                        Intent i = new Intent("hjh.learn2develop.DayActivity");
                        startActivity(i);
                        overridePendingTransition(R.anim.abc_fade_in,R.anim.abc_fade_out);*/



                    }


                }
                else if(rView.mainCalendar.bIsZoom == 0) {//DRAG EVENT
                    rView.mainCalendar.fCalendarY += event.getY() - rView.mainCalendar.fSlideStartY;//Change Calendar's draw-starting point.
                    rView.mainCalendar.fSlideStartY = event.getY();

                    rView.mainCalendar.fCalendarX += event.getX() - rView.mainCalendar.fSlideStartX;
                    rView.mainCalendar.fSlideStartX = event.getX();

                    //out of screen check
                    if(rView.mainCalendar.fCalendarX >0) rView.mainCalendar.fCalendarX =0;
                    else if(rView.mainCalendar.fCalendarX+rView.mainCalendar.dW*7 < rView.mainCalendar.fRealCanvasWidth) rView.mainCalendar.fCalendarX = rView.mainCalendar.fRealCanvasWidth- rView.mainCalendar.dW*7;



                    while(rView.mainCalendar.fCalendarY>rView.mainCalendar.fMonthBannerSize)
                    {
                        //down slide

                        rView.mainCalendar.calendar_FirstShowingDay.add(Calendar.DATE,-7);
                        rView.mainCalendar.fCalendarY -= rView.mainCalendar.dH;
                        myDBManager.getSchedule(rView.mainCalendar.calendar_FirstShowingDay,rView.mainCalendar.calendar_LastShowingDay);


                    }

                    while(rView.mainCalendar.fCalendarY+rView.mainCalendar.dH<=rView.mainCalendar.fMonthBannerSize)
                    {
                        //up slide
                        rView.mainCalendar.calendar_FirstShowingDay.add(Calendar.DATE,7);
                        rView.mainCalendar.fCalendarY += rView.mainCalendar.dH;
                        myDBManager.getSchedule(rView.mainCalendar.calendar_FirstShowingDay,rView.mainCalendar.calendar_LastShowingDay);
                    }
                    /*
                    float fSlidedDistY = rView.mainCalendar.fCalendarY - fOrigCalendarY;

                    while (fSlidedDistY >= rView.mainCalendar.dH) {
                        rView.mainCalendar.fCalendarY -= rView.mainCalendar.dH;
                        fOrigCalendarY = rView.mainCalendar.fCalendarY;
                        fSlidedDistY -= rView.mainCalendar.dH;
                        rView.mainCalendar.weekCount++;

                    }
                    while (fSlidedDistY <= -rView.mainCalendar.dH) {
                        rView.mainCalendar.fCalendarY += rView.mainCalendar.dH;
                        fOrigCalendarY = rView.mainCalendar.fCalendarY;
                        fSlidedDistY += rView.mainCalendar.dH;
                        rView.mainCalendar.weekCount--;

                    }*/

                }

                rView.invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                break;



            default:
                break;
        }



        return true;

    }

    static RenderView rView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
      //  requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        rView = new RenderView((this));

        rView.setOnTouchListener(this);


        setContentView(rView);

        nScheduleTimeSelectMode = 0;

        intentNewScheduleActivity = new Intent("hjh.learn2develop.NewScheduleActivity");
        bundleNewScheduleActivity = new Bundle();
        intentDayActivity = new Intent("hjh.learn2develop.DayActivity");
        bundleDayActivity = new Bundle();

        rView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        String str1;
        String str2;
        Boolean bIsModify;
        try{
            Bundle returnData = data.getExtras();
            switch(requestCode){
                case 1://New Schedule Activity

                    switch(resultCode){
                        case Activity.RESULT_OK://New Schedule Activity End

                            String returnSchedule = returnData.getString("ScheduleName");
                            String returnStartTime = returnData.getString("StartTime");
                            String returnEndTime = returnData.getString("EndTime");
                            if(!returnStartTime.equals("Touch Here!")) {
                                nScheduleTimeSelectMode = SCHEDULETIMESELECT_NONE;
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");


                                Date startDate = sdf.parse(returnStartTime);
                                Date endDate = sdf.parse(returnEndTime);


                                bIsModify = returnData.getBoolean("IsModify", false);
                                if (bIsModify) {
                                    int nkey = returnData.getInt("ScheduleId", -1);
                                    if (nkey == -1)
                                        throw new AssertionError("Schedule Modify Error At onActivityResult()-1");
                                    myDBManager.deleteSchedule(nkey);
                                }

                                myDBManager.insertSchedule(startDate, endDate, returnSchedule, 0, 0, 0);
                                //Toast.makeText(getBaseContext(),returnSchedule,Toast.LENGTH_LONG ).show();

                                //rView.myDBManager.insertSchedule(rView.myDBManager, returnDate, returnSchedule);

                            }
                            break;
                        case 327://ScheduleDelete on Modify Mode
                            bIsModify = returnData.getBoolean("IsModify",false);
                            if(bIsModify)
                            {
                                int nkey = returnData.getInt("ScheduleId",-1);
                                if (nkey==-1) throw new AssertionError("Schedule Modify Error At onActivityResult()-1");
                                myDBManager.deleteSchedule(nkey);
                            }

                            break;
                        case 10://Schedule Start Time Select

                            strSavedScheduleName = returnData.getString("ScheduleName");
                            strSavedScheduleEndTime = returnData.getString("EndTime");
                            strSavedScheduleStartTime = returnData.getString("StartTime");
                            //Toast.makeText(getBaseContext(),returnSchedule,Toast.LENGTH_LONG ).show();
                            Toast.makeText(getBaseContext(),"Select Start Date",Toast.LENGTH_SHORT ).show();
                            nScheduleTimeSelectMode = SCHEDULETIMESELECT_STARTTIME;
                            if(!strSavedScheduleStartTime.equals("Touch Here!"))
                            {
                                bundleDayActivity.putString("DATE", strSavedScheduleStartTime.substring(0,10));
                                bundleDayActivity.putInt("TimeSelectMode", nScheduleTimeSelectMode);
                                intentDayActivity.putExtras(bundleDayActivity);
                                this.startActivityForResult(intentDayActivity,2);
                            }

                            //rView.myDBManager.insertSchedule(rView.myDBManager, returnDate, returnSchedule);
                            break;
                        case 11://Schedule End Time Select
                            strSavedScheduleName = returnData.getString("ScheduleName");
                            strSavedScheduleStartTime = returnData.getString("StartTime");
                            strSavedScheduleEndTime = returnData.getString("EndTime");
                            Toast.makeText(getBaseContext(),"Select End Date",Toast.LENGTH_SHORT ).show();
                            nScheduleTimeSelectMode = SCHEDULETIMESELECT_ENDTIME;
                            if(!strSavedScheduleEndTime.equals("Touch Here!"))
                            {
                                bundleDayActivity.putString("DATE", strSavedScheduleEndTime.substring(0,10));
                                bundleDayActivity.putInt("TimeSelectMode", nScheduleTimeSelectMode);
                                intentDayActivity.putExtras(bundleDayActivity);
                                this.startActivityForResult(intentDayActivity,2);
                            }



                        default:
                            break;
                    }


                    break;
                case 2://Day Activity End
                    switch(resultCode){
                        case SCHEDULETIMESELECT_STARTTIME+10://start time selected
                            str1 = returnData.getString("DATE");
                            str2 = returnData.getString("Time");
                            bundleNewScheduleActivity.putString("ScheduleName",strSavedScheduleName);
                            bundleNewScheduleActivity.putString("StartTime",str1 +" "+str2);

                            if(strSavedScheduleEndTime.equals("Touch Here!")){

                                if(str2.substring(0,2).equals("23"))
                                {
                                    str2 = "23:59:00";
                                }
                                else
                                {
                                    int a = Integer.parseInt(str2.substring(0,2));
                                    str2 = String.valueOf(a+1)+str2.substring(2,8);
                                }
                                strSavedScheduleEndTime = str1 +" "+str2;
                            }
                            bundleNewScheduleActivity.putString("EndTime",strSavedScheduleEndTime);
                            intentNewScheduleActivity.putExtras(bundleNewScheduleActivity);
                            this.startActivityForResult(intentNewScheduleActivity, 1);
                            break;
                        case SCHEDULETIMESELECT_ENDTIME+10://end time selected
                            str1 = returnData.getString("DATE");
                            str2 = returnData.getString("Time");
                            bundleNewScheduleActivity.putString("ScheduleName",strSavedScheduleName);
                            bundleNewScheduleActivity.putString("StartTime",strSavedScheduleStartTime);
                            bundleNewScheduleActivity.putString("EndTime",str1 +" "+str2);

                            intentNewScheduleActivity.putExtras(bundleNewScheduleActivity);
                            this.startActivityForResult(intentNewScheduleActivity, 1);
                            break;
                        case SCHEDULETIMESELECT_ENDTIME+20://From ScheduleListview LongPress
                            strSavedScheduleName = returnData.getString("ScheduleName");
                            strSavedScheduleEndTime = returnData.getString("EndTime");
                            strSavedScheduleStartTime = returnData.getString("StartTime");
                            int nPressedScheduleId = returnData.getInt("ScheduleId");
                            bundleNewScheduleActivity.putString("ScheduleName",strSavedScheduleName);
                            bundleNewScheduleActivity.putString("StartTime",strSavedScheduleStartTime);
                            bundleNewScheduleActivity.putString("EndTime",strSavedScheduleEndTime);
                            bundleNewScheduleActivity.putString("DATE",returnData.getString("DATE"));
                            bundleNewScheduleActivity.putInt("ScheduleId",nPressedScheduleId);
                            bundleNewScheduleActivity.putBoolean("IsModify",true);
                            intentNewScheduleActivity.putExtras(bundleNewScheduleActivity);
                            this.startActivityForResult(intentNewScheduleActivity, 1);

                            break;
                        default:

                            break;
                    }


                default:
                    break;
            }

            myDBManager.getSchedule(rView.mainCalendar.calendar_FirstShowingDay, rView.mainCalendar.calendar_LastShowingDay);
            rView.invalidate();
        }
        catch (Exception e){}
    }


    class RenderView extends View{
        Paint paint;
        MainCalendarDrawing mainCalendar;



        public RenderView(Context context){
            super(context);
            paint = new Paint();

            mainCalendar = new MainCalendarDrawing();
            myDBManager = new ScheduleDBManager(context);
            //myDBManager.getReadableDatabase();

            //myDBManager.close();
            mainCalendar.initPtr(paint,this,myDBManager);

        }

        protected void onDraw(Canvas canvas)
        {
            mainCalendar.drawMainCalendar(canvas);

        }


    }
}
