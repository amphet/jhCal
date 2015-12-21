package com.example.hjh.drawtest;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Region;
import android.graphics.Typeface;
import android.util.Log;

import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.zip.DataFormatException;

/**
 * Created by HJH on 2015-08-11.
 */
public class DayDrawing {

    Paint pPaint;
    MainCalendarDrawing pMainCalendar;
    ScheduleDBManager pDBManager;
    Typeface pFont;



    public DayDrawing()
    {

    }
    public void setPaintPtr(Paint pP) {
        pPaint = pP;

    }

    public void setMainCalendarPtr(MainCalendarDrawing pMCD) {
        pMainCalendar = pMCD;

    }
    public void setDBManagerPtr(ScheduleDBManager pDB)
    {
        pDBManager = pDB;

    }

    public void setTypeFaceFont(Typeface pF)
    {
        pFont = pF;
    }






    public void drawDay(Canvas canvas,float x,float y,float width,float height,float fontsize,int colorset,Calendar cal)
    {

        Calendar calendarToday = Calendar.getInstance();

        SimpleDateFormat sdf_DBFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf_OnlyTime;
      //  SimpleDateFormat dFormat = new SimpleDateFormat("yyyy/MM/dd");

        String strToday = sdf_DBFormat.format(calendarToday.getTime());
        String strDrawingDay =  sdf_DBFormat.format(cal.getTime());

        //String strVerbose = String.valueOf("2".compareTo("1"));

       // Log.v("HELLO","START DRAWING TO :"+strDrawingDay);

        pPaint.setAntiAlias(true);

        if(strToday.substring(0,10).compareTo(strDrawingDay.substring(0, 10)) == 0) {
            pPaint.setStyle(Paint.Style.FILL);
            pPaint.setColor(Color.argb(100, 0, 255, 255));
        }
        else {
            pPaint.setStyle(Paint.Style.STROKE);
            pPaint.setColor(Color.BLACK);
        }

        strDrawingDay =  sdf_DBFormat.format(cal.getTime());
        pPaint.setStrokeWidth(3);
        canvas.drawRect(x, y, x + width, y + height, pPaint);
        //canvas.drawRect(x, y, x + width, y + height / 4, paint);

        pPaint.setTypeface(pFont);

        pPaint.setTextAlign(Paint.Align.LEFT);
        pPaint.setStyle(Paint.Style.FILL);



        pPaint.setStrokeWidth(20);
        pDBManager.get1DaySchedule(cal);
        Collections.sort(pDBManager.results_1DaySchedule, pDBManager.scheduleSort);
        float fTimeTextSize = fontsize/2;//
        float fTextPad = fontsize/10;
        float fTextPosY = y + height/8 + fTextPad;
        for(int i=0;i<pDBManager.results_1DaySchedule.size();i++) {
            ScheduleNode nodeTemp = pDBManager.results_1DaySchedule.get(i);
            if (nodeTemp.nPriority > 0)
                pPaint.setColor(Color.RED);
            else
                pPaint.setColor(Color.BLACK);


            switch (nodeTemp.nScheduleType) {
                case ScheduleDBManager.SCHEDULE_TYPE_1DAY:
                    if ((sdf_DBFormat.format(nodeTemp.startDate)).substring(0, 10).equals(strDrawingDay.substring(0, 10))) {//draw only for start time
                        //  Log.v("HELLO","1day");
                        pPaint.setTextSize(fTimeTextSize);
                        sdf_OnlyTime = new SimpleDateFormat("HH:mm");
                        fTextPosY += fTimeTextSize;
                        pPaint.setTextAlign(Paint.Align.LEFT);
                        canvas.drawText(sdf_OnlyTime.format(nodeTemp.startDate), x, fTextPosY, pPaint);
                        pPaint.setTextSize(fontsize);
                        fTextPosY += fontsize;
                        canvas.drawText(nodeTemp.strScheduleName, x, fTextPosY, pPaint);
                        fTextPosY += fTextPad;
                    }
                    break;
                case ScheduleDBManager.SCHEDULE_TYPE_CONT:
                    //   Log.v("HELLO","CONT!");
                    pPaint.setTextSize(fontsize * 2 / 3);
                    if ((sdf_DBFormat.format(nodeTemp.startDate)).substring(0, 10).equals(strDrawingDay.substring(0, 10))) {
                        pPaint.setTextAlign(Paint.Align.LEFT);
                        pPaint.setColor(Color.argb(80, 0, 0, 255));
                        canvas.drawLine(x, y, x + width, y, pPaint);
                        canvas.drawLine(x, y, x, y + height / 5, pPaint);
                        pPaint.setColor(Color.argb(255, 0, 0, 255));
                        canvas.drawText(nodeTemp.strScheduleName, x, y, pPaint);

                    } else if ((sdf_DBFormat.format(nodeTemp.endDate)).substring(0, 10).equals(strDrawingDay.substring(0, 10))) {
                        pPaint.setColor(Color.argb(80, 0, 0, 255));
                        canvas.drawLine(x, y, x + width, y, pPaint);
                        canvas.drawLine(x + width, y, x + width, y + height / 5, pPaint);
                        pPaint.setColor(Color.argb(255, 0, 0, 255));
                        pPaint.setTextAlign(Paint.Align.RIGHT);
                        canvas.drawText(nodeTemp.strScheduleName, x + width, y, pPaint);
                    } else {
                        pPaint.setColor(Color.argb(80, 0, 0, 255));
                        canvas.drawLine(x, y, x + width, y, pPaint);


                    }
                    break;
                case ScheduleDBManager.SCHEDULE_TYPE_RECURRING:
                    break;
                default:
                    Log.v("hjh", "err at scheduletype parsing(DayDrawing)");
                    break;
            }
        }




           // if(fTextPosY > y+height)
//                break;


        /*
        if( nSchedCnt!=0){//if schedules exist......
            Collections.sort(daySchedule, pDBManager.scheduleSort);//priority & datetime sort


            float fTimeTextSize = fontsize/2;//
            float fTextPad = fontsize/10;
            float fTextPosY = y + height/8 + fTextPad;
//            pPaint.setTextSize(fontsize);
            for (int i = 0; i < nSchedCnt; i++) {
                scheduleNodeTemp = daySchedule.get(i);
                if(scheduleNodeTemp.nPriority == 1)
                    pPaint.setColor(Color.RED);
                else
                    pPaint.setColor(Color.BLACK);

                pPaint.setTextSize(fTimeTextSize);
                dFormat = new SimpleDateFormat("HH:mm");
                fTextPosY += fTimeTextSize;
                switch(scheduleNodeTemp.nScheduleType){
                    case ScheduleDBManager.SCHEDULE_TYPE_1DAY:
                        pPaint.setTextAlign(Paint.Align.LEFT);
                        canvas.drawText(dFormat.format(scheduleNodeTemp.startDate), x, fTextPosY, pPaint);
                        pPaint.setTextSize(fontsize);
                        fTextPosY += fontsize;
                        canvas.drawText(scheduleNodeTemp.strScheduleName, x, fTextPosY, pPaint);
                        fTextPosY += fTextPad;
                        break;
                    case ScheduleDBManager.SCHEDULE_TYPE_CONT:
                        pPaint.setTextSize(fontsize*2/3);
                        if( (dayFormat.format(scheduleNodeTemp.startDate)).substring(0,10).equals(strDrawingDay.substring(0,10)))
                        {
                            pPaint.setTextAlign(Paint.Align.LEFT);
                            pPaint.setColor(Color.argb(80, 0, 0, 255));
                            canvas.drawLine(x, y, x + width, y, pPaint);
                            canvas.drawLine(x, y, x,y+height/6,pPaint);
                            pPaint.setColor(Color.argb(255, 0, 0, 255));
                            canvas.drawText(scheduleNodeTemp.strScheduleName, x, y, pPaint);

                        }
                        else if( (dayFormat.format(scheduleNodeTemp.endDate)).substring(0,10).equals(strDrawingDay.substring(0,10)))
                        {
                            pPaint.setColor(Color.argb(80, 0, 0, 255));
                            canvas.drawLine(x, y, x + width, y, pPaint);
                            canvas.drawLine(x+width, y, x+width, y + height / 6, pPaint);
                            pPaint.setColor(Color.argb(255, 0, 0, 255));
                            pPaint.setTextAlign(Paint.Align.RIGHT);
                            canvas.drawText(scheduleNodeTemp.strScheduleName, x+width, y, pPaint);
                        }
                        else
                        {
                            pPaint.setColor(Color.argb(80, 0, 0, 255));
                            canvas.drawLine(x, y, x + width, y, pPaint);


                        }
                        break;
                    case ScheduleDBManager.SCHEDULE_TYPE_RECURRING:
                        break;
                    default:
                        Log.v("hjh","err at scheduletype parsing(DayDrawing)");
                        break;
                }




                if(fTextPosY > y+height)
                    break;

            }
        }
*/




        pPaint.setTextSize(height/8);
        //요일 색상처리....
        if(colorset == 0)
            pPaint.setColor(Color.RED);
        else if (colorset == 6)
            pPaint.setColor(Color.BLUE);
        else
            pPaint.setColor(Color.BLACK);

        //Date Text
        if(strDrawingDay.substring(8,10).compareTo("01")!=0)
            strDrawingDay = strDrawingDay.substring(8,10);
        else {
            pPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            strDrawingDay = strDrawingDay.substring(5, 10);
        }
        canvas.drawText(strDrawingDay, x+3, y + height / 8 + 5, pPaint);
    }
}
