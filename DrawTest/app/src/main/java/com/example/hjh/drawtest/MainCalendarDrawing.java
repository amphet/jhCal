package com.example.hjh.drawtest;

import android.app.AlertDialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.RectF;
import android.support.v4.app.DialogFragment;
import android.content.Context;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.util.FloatMath;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


import android.view.View;
import android.app.Activity;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.Toast;

/**
 * Created by HJH on 2015-08-11.
 */
public class MainCalendarDrawing {

    static final int MAX_CLICK_DURATION = 200;
    public long startClickTime;

    Calendar calendar_Today;
    Calendar calendar_YearMonth;
    Calendar calendar_FirstShowingDay;
    Calendar calendar_LastShowingDay;

    ///float fMovement;//Vertical Scroll distance(pixel?)
    float fSlideStartY;
    float fSlideStartX;

    int diffDay;


    float xPad,fMonthBannerSize,xCanvas,yCanvas,dW,dH;
    float fCalendarX;//starting coordinate x of calendar
    float fCalendarY;//staring coordinate y of calendar
    float fScale;//scale const
    float fTodayBtnCoordX;
    float fTodayBtnCoordY;
    float fNewScheduleBtnCoordX;
    float fNewScheduleBtnCoordY;

    float fZoomMidX;
    float fZoomMidY;
    float fZoomOldDist;
    float fZoomNewDist;

    float fTouchStartX;
    float fTouchStartY;


    float fRealCanvasWidth;

    char bIsZoom;
    boolean bIsDrawingInit;

    DayDrawing dayDrawing;
    Paint pPaint;
    View pRenderView;
    ScheduleDBManager pDB;

    public MainCalendarDrawing(){
        fSlideStartY=0;
        fSlideStartX=0;

        fTouchStartX = fTouchStartY =0;

        fZoomMidX = fZoomMidY = 0;
        fZoomOldDist = 0;
        fZoomNewDist = 0;
        fRealCanvasWidth=0;

        bIsZoom = 0;
        fScale = 1;
        diffDay = 0;

        resetCalendar();

        dayDrawing = new DayDrawing();
    }

    public void initPtr(Paint pP, View pV, ScheduleDBManager pdb){
        pPaint = pP;
        pRenderView = pV;
        pDB = pdb;
        dayDrawing.setMainCalendarPtr(this);
        dayDrawing.setPaintPtr(pP);

        dayDrawing.setDBManagerPtr(pdb);



    }
    public void resetCalendar()
    {
        SimpleDateFormat dayFormat = new SimpleDateFormat("MM.dd");

        calendar_Today = Calendar.getInstance();
        calendar_Today.set(Calendar.HOUR_OF_DAY,0);
        calendar_Today.set(Calendar.MINUTE,0);


        calendar_YearMonth = Calendar.getInstance();
        calendar_FirstShowingDay = Calendar.getInstance();
        calendar_LastShowingDay = Calendar.getInstance();
        calendar_LastShowingDay.setTime(calendar_FirstShowingDay.getTime());
        calendar_LastShowingDay.add(Calendar.DATE,49);


        dayFormat = new SimpleDateFormat("c",Locale.US);
        String weekCheck =  dayFormat.format(calendar_Today.getTime());
        int nDifferenceFromThisWeek;
        if(weekCheck.equals("Mon"))
        {nDifferenceFromThisWeek = 1;}
        else if(weekCheck.equals("Tue"))

        {nDifferenceFromThisWeek = 2;}
        else if(weekCheck.equals("Wed"))
        {nDifferenceFromThisWeek = 3;}
        else if(weekCheck.equals("Thu"))
        {nDifferenceFromThisWeek = 4;}
        else if(weekCheck.equals("Fri"))
        {nDifferenceFromThisWeek = 5;}
        else if(weekCheck.equals("Sat"))
        {nDifferenceFromThisWeek = 6;}
        else
        {
            nDifferenceFromThisWeek = 0;
        }
        int nTotalDateDuration = nDifferenceFromThisWeek +14;

        calendar_FirstShowingDay.setTime(calendar_Today.getTime());
        calendar_FirstShowingDay.set(Calendar.HOUR_OF_DAY, 0);
        calendar_FirstShowingDay.set(Calendar.MINUTE,0);
        calendar_FirstShowingDay.add(Calendar.DATE, -nTotalDateDuration);

        fScale =1;
        fCalendarX=0;
        fCalendarY=fMonthBannerSize;
        bIsDrawingInit = false;
//        pDB.getSchedule(pDB, calendar_FirstShowingDay);
        //String strT = String.valueOf(fScale);
        //Log.v("HELLO",strT);

    }
    public void drawMainCalendar(Canvas canvas){
        canvas.drawRGB(255, 255, 255);

        xCanvas = canvas.getWidth()*fScale;
        yCanvas = canvas.getHeight()*fScale;
        fRealCanvasWidth = canvas.getWidth();


        //float xPad,fMonthBannerSize;
        if(!bIsDrawingInit) {
            xPad = 1;//canvas.getWidth()/10;

            fMonthBannerSize = canvas.getHeight() / 7;
            fCalendarY = fMonthBannerSize;
            bIsDrawingInit = true;
            pDB.getSchedule( calendar_FirstShowingDay,calendar_LastShowingDay);

        }


        calendar_LastShowingDay.setTime(calendar_FirstShowingDay.getTime());
        calendar_LastShowingDay.add(Calendar.DATE,49);


        // float dW,dH;
        dW = (xCanvas-2*xPad)/7;
        dH = yCanvas/7;
        int differ=0;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(calendar_FirstShowingDay.getTime());



        SimpleDateFormat dayFormat = new SimpleDateFormat("MM/dd");

        int colorset = 0;

        ///day Draw
        //TODO : OUT OF SCREEN CHECK
        //Rect.intersect???
        canvas.clipRect(xPad, fMonthBannerSize, canvas.getWidth() - xPad, canvas.getHeight());//global clipping for calendar area
        float y = fCalendarY;
        for(int j=0;j<7;j++) {
            float x = xPad + fCalendarX;
            for (int i = 0; i < 7; i++) {
                if(RectF.intersects(new RectF(x, y, x + dH, y + dW), new RectF(xPad,fMonthBannerSize,canvas.getWidth()-xPad,canvas.getHeight())))//
                {
                    canvas.clipRect(x, y-dH, x + dW, y + dH, Region.Op.INTERSECT);//local clipping for 1 day area
                    dayDrawing.drawDay(canvas, x, y, dW, dH, canvas.getHeight() * 0.03f, colorset, calendar);
                }

                calendar.add(Calendar.DATE,1);

                canvas.clipRect(xPad, fMonthBannerSize, canvas.getWidth() - xPad, canvas.getHeight(), Region.Op.REPLACE);//recover Calendar area clipping

                colorset++;
                if (colorset - 7 == 0) colorset = 0;
                x = x + dW;
            }
            y = y+ dH;

        }




        //Today Button
        pPaint.setStyle(Paint.Style.FILL);
        pPaint.setColor(Color.argb(100, 255, 0, 0));
        pPaint.setAntiAlias(true);
        canvas.drawCircle(canvas.getWidth() * 6 / 7, canvas.getHeight() * 6 / 7, 80, pPaint);
        fTodayBtnCoordX = canvas.getWidth() * 6 / 7;
        fTodayBtnCoordY = canvas.getHeight() * 6 / 7;


        pPaint.setTextSize(40);
        pPaint.setTextAlign(Paint.Align.CENTER);//수평정렬
        pPaint.setColor(Color.argb(150, 255, 0, 0));

        Rect textBound = new Rect();
        pPaint.getTextBounds("TODAY", 0, "TODAY".length(), textBound);//수직정렬기능이없어서 추가
        canvas.drawText("TODAY", fTodayBtnCoordX, fTodayBtnCoordY - (textBound.bottom + textBound.top) / 2, pPaint);

        /////New Schedule Button
        pPaint.setStyle(Paint.Style.FILL);
        pPaint.setColor(Color.argb(100, 0, 255, 0));

        canvas.drawCircle(canvas.getWidth() * 1 / 7, canvas.getHeight() * 6 / 7, 80, pPaint);
        fNewScheduleBtnCoordX = canvas.getWidth() * 1 / 7;
        fNewScheduleBtnCoordY = canvas.getHeight() * 6 / 7;


        pPaint.setTextSize(40);
        pPaint.setTextAlign(Paint.Align.CENTER);//수평정렬
        pPaint.setColor(Color.argb(150, 255, 0, 0));

        textBound = new Rect();
        pPaint.getTextBounds("NEW",0,"NEW".length(),textBound);//수직정렬기능이없어서 추가
        canvas.drawText("NEW", fNewScheduleBtnCoordX, fNewScheduleBtnCoordY - (textBound.bottom + textBound.top) / 2, pPaint);



        //상태바 year/month
        //보이는 달력의 2번째 주 토요일의 월 기준 표시??

        canvas.clipRect(0,0,canvas.getWidth(),canvas.getHeight(), Region.Op.REPLACE);//clip해제

        calendar.setTime(calendar_FirstShowingDay.getTime());
        calendar.add(Calendar.DATE, 20);

        calendar_YearMonth.setTime(calendar.getTime());
        dayFormat = new SimpleDateFormat("yyyy.MM");
        pPaint.setTextSize(fMonthBannerSize * 5 / 10);
        pPaint.setTextAlign(Paint.Align.CENTER);//수평정렬
        pPaint.setColor(Color.BLACK);
        String strYearMonth = dayFormat.format(calendar.getTime());
        pPaint.getTextBounds(strYearMonth, 0, strYearMonth.length(), textBound);//수직정렬기능이없어서 추가
        canvas.drawText(strYearMonth, canvas.getWidth() / 2, fMonthBannerSize / 2 - (textBound.bottom + textBound.top) / 2, pPaint);

        //요일표시
        pPaint.setTextSize(fMonthBannerSize * 1 / 10 * fScale);
        pPaint.setColor(Color.RED);
        pPaint.setTextAlign(Paint.Align.CENTER);//수평정렬
        canvas.drawText("SUN", fCalendarX+(xPad +  dW) / 2, fMonthBannerSize, pPaint);

        pPaint.setColor(Color.BLACK);
        canvas.drawText("MON", fCalendarX+(xPad*2 + dW*3) / 2, fMonthBannerSize, pPaint);
        canvas.drawText("WED", fCalendarX+(xPad*2 +dW*5) / 2, fMonthBannerSize, pPaint);
        canvas.drawText("TUE", fCalendarX+(xPad*2 + dW*7) / 2, fMonthBannerSize, pPaint);
        canvas.drawText("THU", fCalendarX+(xPad*2 + dW*9) / 2, fMonthBannerSize, pPaint);
        canvas.drawText("FRI", fCalendarX+(xPad*2 + dW*11) / 2, fMonthBannerSize, pPaint);

        pPaint.setColor(Color.BLUE);
        canvas.drawText("SAT", fCalendarX+(xPad*2 + dW*13) / 2, fMonthBannerSize, pPaint);

        // invalidate();

    }



    public void touchEventWrapper(View v, MotionEvent event) {
    }
}
