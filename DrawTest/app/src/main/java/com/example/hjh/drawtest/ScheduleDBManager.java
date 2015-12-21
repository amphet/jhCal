package com.example.hjh.drawtest;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.util.Log;

import java.text.Collator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Random;

/**
 * Created by HJH on 2015-09-01.
 */


public class ScheduleDBManager extends SQLiteOpenHelper {

    private static final String MY_DATABASE_NAME = "ScheduleList.db";
    private static final String MY_DATABASE_TABLE_SCHEDULE = "Schedule_Table ";
    private static final String MY_DATABASE_TABLE_TODO = "Todo_Table ";
    private static final int MY_DATABASE_VERSION = 35;

    public static final int SCHEDULE_TYPE_1DAY = 0;
    public static final int SCHEDULE_TYPE_CONT = 1;
    public static final int SCHEDULE_TYPE_RECURRING = 2;

    public static final int TODO_TYPE_COUNTDOWN =0;
    public static final int TODO_TYPE_COUNTBOTH = 1;




    private SQLiteDatabase mDB;
    private String str;
    private int temp;

    public ArrayList <ScheduleNode> results_Schedule;
    public ArrayList <ScheduleNode> results_1DaySchedule;
    public ArrayList <TodoNode> results_Todo;

    public ScheduleDBManager(Context context) {
        super(context, MY_DATABASE_NAME, null, MY_DATABASE_VERSION);
        results_Schedule = new ArrayList<ScheduleNode>();
        results_1DaySchedule = new ArrayList<ScheduleNode>();
        results_Todo = new ArrayList<TodoNode> ();
        str = "str_";
        temp = 0;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + MY_DATABASE_TABLE_SCHEDULE + "(" +
                "_id INTEGER PRIMARY KEY," +
                "startDate DATE," +
                "endDate DATE,"+
                "scheduleName VARCHAR,"+
                "priority NUMBER,"+
                "scheduleType NUMBER,"+
                "scheduleID NUMBER);");

        db.execSQL("delete from " + MY_DATABASE_TABLE_SCHEDULE);

        //1998-12-31 23:59:59
        String insertMy = "INSERT INTO " + MY_DATABASE_TABLE_SCHEDULE + " (startDate,endDate,scheduleName,priority,scheduleType,scheduleID) "+
                "VALUES ";


        db.execSQL(insertMy + "('2015-12-07 12:50:00','2015-12-07 14:20:00','Shopping',0,0,0);");


        db.execSQL(insertMy + "('2015-12-17 08:50:00','2015-12-17 09:20:00','exercise',0,0,0);");
        db.execSQL(insertMy + "('2015-12-17 10:00:00','2015-12-17 11:50:00','Study',0,0,0);");
        db.execSQL(insertMy + "('2015-12-16 12:50:00','2015-12-16 14:20:00','Lunch',0,0,0);");

        db.execSQL(insertMy + "('2015-12-17 13:10:00','2015-12-17 15:50:00','Family meeting',1,0,0);");
        db.execSQL(insertMy + "('2015-12-17 17:40:00','2015-12-17 18:00:00','Appointment',0,0,0);");
        db.execSQL(insertMy + "('2015-12-17 18:30:00','2015-12-17 20:40:00','Dinner',1,0,0);");



        db.execSQL(insertMy + "('2015-12-18 14:40:00','2015-12-18 15:50:00','Dentist',1,0,0);");
        db.execSQL(insertMy + "('2015-12-18 16:10:00','2015-12-18 16:40:00','Meet at Glass',0,0,0);");
        db.execSQL(insertMy + "('2015-12-18 19:20:00','2015-12-18 22:40:00','Review Design',0,0,0);");


        db.execSQL(insertMy + "('2015-12-24 08:40:00','2015-12-24 10:00:00','Team Meeting',0,0,0);");
        db.execSQL(insertMy + "('2015-12-24 17:40:00','2015-12-24 15:50:00','Meet with Marry',0,0,0);");


        db.execSQL(insertMy + "('2015-12-27 13:40:00','2015-12-27 15:50:00','Seminar',1,0,0);");

        db.execSQL(insertMy + "('2015-12-29 13:10:00','2015-12-29 15:50:00','Book bus',1,0,0);");
        db.execSQL(insertMy + "('2015-12-29 17:40:00','2015-12-29 20:50:00','Buy present',0,0,0);");

        db.execSQL(insertMy + "('2015-12-30 13:10:00','2015-12-30 15:50:00','Family meeting',0,0,0);");
        db.execSQL(insertMy + "('2015-12-30 17:40:00','2015-12-30 20:50:00','Appointment',0,0,0);");

        db.execSQL(insertMy + "('2016-01-05 13:10:00','2015-01-05 15:50:00','Weekly meeting',0,0,0);");
        db.execSQL(insertMy + "('2016-01-05 17:40:00','2015-01-05 20:50:00','Conference',0,0,0);");

        db.execSQL(insertMy + "('2016-01-08 13:10:00','2015-01-08 15:50:00','Deliver Document',1,0,0);");
        db.execSQL(insertMy + "('2016-01-08 17:40:00','2015-01-08 20:50:00','Submit repository',1,0,0);");


        db.execSQL(insertMy + "('2015-12-17 16:30:00','2015-12-28 15:50:00','Travel1',0,1,0);");
        db.execSQL(insertMy + "('2016-01-02 16:30:00','2016-01-13 15:50:00','Vacation',0,1,0);");





//        db.execSQL(insertMy + "('2015-12-19 00:00:00','2015-12-27 00:00:01','Travel',0,1,0);");



        /////////////////////////////////////////////
        // TODO TABLE
        db.execSQL("CREATE TABLE " + MY_DATABASE_TABLE_TODO + "(" +
                "_id INTEGER PRIMARY KEY," +
                "startDate DATE," +
                "endDate DATE,"+
                "todoName VARCHAR,"+
                "todoType NUMBER);");

        db.execSQL("delete from " + MY_DATABASE_TABLE_TODO);

        //1998-12-31 23:59:59
        insertMy = "INSERT INTO " + MY_DATABASE_TABLE_TODO + " (startDate,endDate,todoName,todoType) "+
                "VALUES ";

        db.execSQL(insertMy + "('2015-12-09 00:00:00','2015-12-23 00:00:00','Book Return',0);");
        db.execSQL(insertMy + "('2015-12-04 00:00:00','2015-12-24 00:00:00','Exam',0);");
        db.execSQL(insertMy + "('2015-12-07 00:00:00','2015-12-25 00:00:00','Running',1);");






    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion<newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + MY_DATABASE_TABLE_SCHEDULE);
            db.execSQL("DROP TABLE IF EXISTS " + MY_DATABASE_TABLE_TODO);
        }
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        mDB = db;
    }
    public void getSchedule(Calendar visibleDayFrom,Calendar visibleDayTo){
        results_Schedule.clear();
        this.getReadableDatabase();
        String[] selectionArgs = new String[]{"_id,startDate", "endDate","scheduleName", "priority","scheduleType","scheduleID"};

        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String strFrom = dayFormat.format(visibleDayFrom.getTime());
        String strTo = dayFormat.format(visibleDayTo.getTime());

        String[] whereArgs = new String[]{strFrom,strTo,strFrom,strTo};//Search in visible days


        Cursor c = mDB.query(MY_DATABASE_TABLE_SCHEDULE, selectionArgs, "(startDate between ? And ?) OR (endDate between ? And ?)", whereArgs, null, null, null);
        try{
            int pKeyCol = c.getColumnIndex("_id");
            int startDateCol = c.getColumnIndex("startDate");
            int endDateCol = c.getColumnIndex("endDate");
            int scheduleNameCol = c.getColumnIndex("scheduleName");
            int priorityCol = c.getColumnIndex("priority");
            int scheduleTypeCol = c.getColumnIndex("scheduleType");
            int scheduleIDCol = c.getColumnIndex("scheduleID");

            int i=0;
            Random r = new Random();
            if(c.moveToFirst()){
                do{
                    i++;
                    ScheduleNode nodeTemp = new ScheduleNode();
                    try {
                        String strTemp;
                        strTemp = c.getString(startDateCol);
                        nodeTemp.startDate = dayFormat.parse(strTemp);

                        strTemp = c.getString(endDateCol);
                        if(strTemp == null)
                            nodeTemp.endDate = null;
                        else
                            nodeTemp.endDate = dayFormat.parse(strTemp);

                        strTemp = c.getString(scheduleNameCol);
                        nodeTemp.strScheduleName = strTemp.substring(0);

                        int nTemp = c.getInt(priorityCol);
                        nodeTemp.nPriority = nTemp;

                        nTemp = c.getInt(scheduleTypeCol);
                        nodeTemp.nScheduleType = nTemp;
                        nTemp = c.getInt(scheduleIDCol);
                        nodeTemp.nScheduleId = nTemp;

                        nodeTemp.nKey = c.getInt(pKeyCol);
                        nodeTemp.nR = r.nextInt(255);
                        nodeTemp.nG = r.nextInt(255);
                        nodeTemp.nB = r.nextInt(255);


                    } catch (ParseException e) {
                        //handle exception
                    }




                    results_Schedule.add(nodeTemp);

                }while(c.moveToNext());
            }
        }
        finally{
            if (c!=null)c.close();
        }
    }
    public void get1DaySchedule(Calendar day){
        //MUST BE EXCECUTED AFTER getSchedule!!!!!!!!!!!!


        SimpleDateFormat sdf_DBDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf_YMD = new SimpleDateFormat("yyyy-MM-dd");

        String strSearchingDay = sdf_YMD.format(day.getTime());
        String strTemp;
        results_1DaySchedule.clear();
        for(int i=0;i<results_Schedule.size();i++)
        {
            ScheduleNode nodeTemp = results_Schedule.get(i);
            switch(nodeTemp.nScheduleType){
                case SCHEDULE_TYPE_CONT:
                case SCHEDULE_TYPE_RECURRING:
                case SCHEDULE_TYPE_1DAY:
                    strTemp = sdf_YMD.format(nodeTemp.startDate);
                    if(strTemp.equals(strSearchingDay))
                        results_1DaySchedule.add(nodeTemp);
                    else{//prevent double adding???
                        strTemp = sdf_YMD.format(nodeTemp.endDate);
                        if(strTemp.equals(strSearchingDay))
                            results_1DaySchedule.add(nodeTemp);
                        else{
                            //mayby continuous Schedule mid day?
                            if (nodeTemp.startDate.before(day.getTime()) && nodeTemp.endDate.after(day.getTime()))
                            {
                                results_1DaySchedule.add(nodeTemp);
                            }
                        }

                    }
                    break;
                default:
                    break;

            }

        }
        //sort by StarTime
        Collections.sort(results_1DaySchedule, new Comparator<ScheduleNode>() {
            public int compare(ScheduleNode obj1, ScheduleNode obj2) {
                // TODO Auto-generated method stub
                return obj1.startDate.compareTo(obj2.startDate);

            }
        });

    }

    public void getTodo(Calendar tgtDay){
        results_Todo.clear();
        this.getReadableDatabase();
        String[] selectionArgs = new String[]{"startDate", "endDate","todoName", "todoType"};

        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strTgtDay = dayFormat.format(tgtDay.getTime());


        String[] whereArgs = new String[]{strTgtDay,strTgtDay};

        Cursor c = mDB.query(MY_DATABASE_TABLE_TODO, selectionArgs, "startDate<=? AND ?<=endDate", whereArgs, null, null, null);
        try{
            int startDateCol = c.getColumnIndex("startDate");
            int endDateCol = c.getColumnIndex("endDate");
            int todoNameCol = c.getColumnIndex("todoName");
            int todoTypeCol = c.getColumnIndex("todoType");


            int i=0;
            if(c.moveToFirst()){
                do{
                    i++;
                    TodoNode nodeTemp = new TodoNode();
                    try {
                        String strTemp;
                        strTemp = c.getString(startDateCol);
                        nodeTemp.startDate = dayFormat.parse(strTemp);

                        strTemp = c.getString(endDateCol);
                        nodeTemp.endDate = dayFormat.parse(strTemp);


                        strTemp = c.getString(todoNameCol);
                        nodeTemp.strTodoName = strTemp.substring(0);

                        int nTemp = c.getInt(todoTypeCol);
                        nodeTemp.nTodoType = nTemp;



                    } catch (ParseException e) {
                        //handle exception
                    }




                    results_Todo.add(nodeTemp);

                }while(c.moveToNext());
            }
        }
        finally{
            if (c!=null)c.close();
        }
    }
    public void deleteSchedule(int nKey)
    {
        this.getReadableDatabase();
        String str = String.valueOf(nKey);
        mDB.delete(MY_DATABASE_TABLE_SCHEDULE,"_id="+nKey,null);

    }
    public void updateSchedule(int nkey,Date startdate,Date enddate,String schedulename,int priority,int scheduletype,int scheduleid){
        this.getWritableDatabase();
        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strStartDate;
        if(startdate == null)
        {
            strStartDate = "NULL";
        }
        else
            strStartDate = dayFormat.format(startdate.getTime());

        String strEndDate;
        if(enddate == null)
            strEndDate= "NULL";
        else
            strEndDate= dayFormat.format(enddate.getTime());

        String strPriority = String.valueOf(priority);
        String strScheduleType = String.valueOf(scheduletype);

        String strScheduleId;
        if(scheduleid != 0)
            strScheduleId = String.valueOf(scheduleid);
        else
            strScheduleId = "NULL";
        ContentValues cv= new ContentValues();
        cv.put("startDate",strStartDate);
        cv.put("endDate",strEndDate);
        cv.put("scheduleName",schedulename);
        cv.put("priority",strPriority);
        cv.put("scheduleType",strScheduleType);
        cv.put("scheduleID",strScheduleId);

        mDB.update(MY_DATABASE_TABLE_SCHEDULE, cv, "_id="+nkey, null);

    }
    public void insertSchedule(Date startdate,Date enddate,String schedulename,int priority,int scheduletype,int scheduleid) {
        this.getWritableDatabase();
        String insertMy = "INSERT INTO " + MY_DATABASE_TABLE_SCHEDULE + " (startDate,endDate,scheduleName,priority,scheduleType,scheduleID) "+
                "VALUES ";
        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String strStartDate;
        if(startdate == null)
        {
            strStartDate = "NULL";
        }
        else
            strStartDate = dayFormat.format(startdate.getTime());

        String strEndDate;
        if(enddate == null)
            strEndDate= "NULL";
        else
            strEndDate= dayFormat.format(enddate.getTime());

        String strPriority = String.valueOf(priority);
        String strScheduleType = String.valueOf(scheduletype);
        //Is Long Schedule?( >24h)
        long fDuration = (enddate.getTime()-startdate.getTime())/(1000*60*60);//duration by Hour
        if(fDuration>24)
        {
            strScheduleType = String.valueOf(SCHEDULE_TYPE_CONT);
        }
        else
        {
            if(String.valueOf(scheduletype).equals(String.valueOf(SCHEDULE_TYPE_1DAY)))
                strScheduleType = String.valueOf(SCHEDULE_TYPE_1DAY);
            else
                strScheduleType = String.valueOf(SCHEDULE_TYPE_RECURRING);
        }

        String strScheduleId;
        if(scheduleid != 0)
            strScheduleId = String.valueOf(scheduleid);
        else
            strScheduleId = "NULL";
        String strQuery =insertMy + "('" + strStartDate + "','" + strEndDate +"','"+schedulename  +"'," + strPriority+","+strScheduleType+","+strScheduleId+ ");";
        Log.e("HJH",strQuery);


        mDB.execSQL(strQuery);


        str += temp;
        temp++;
       // Log.v(null,"Insert DB : "+temp);
    }

    public void delete(ScheduleDBManager mDBManager) {
        mDBManager.getWritableDatabase();
      //  mDB.execSQL("DELETE FROM Android WHERE price = "+ (temp-1) +";");
        mDBManager.close();

        temp--;
       // Log.v(null,"Delete DB : "+temp);
    }


    //Schedule Sorting...............
    public final static Comparator<ScheduleNode> scheduleSort= new Comparator<ScheduleNode>() {
        private final Collator collator = Collator.getInstance();
        @Override
        public int compare(ScheduleNode object1,ScheduleNode object2) {

            String strAp = String.valueOf(object1.nPriority);
            String strBp = String.valueOf(object2.nPriority);

            int ret = strBp.compareTo(strAp);
            if(ret == 0)
            {
                ret =  object1.startDate.compareTo(object2.startDate);
            }
            return ret;
        }
    };
}
/*

public class ScheduleDBManager{




    private static class DatabaseHelper extends SQLiteOpenHelper {
        public SQLiteDatabase mDB;

        public DatabaseHelper(Context context) {
            super(context, MY_DATABASE_NAME, null, MY_DATABASE_VERSION);

        }

        @Override
        public void onCreate(SQLiteDatabase db) {




        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int currentVersion) {

        }
        @Override
        public void onOpen(SQLiteDatabase db) {
            super.onOpen(db);

            mDB = db;

        }
    }





    private DatabaseHelper mOpenHelper;

    public ScheduleDBManager(Context context){
        mOpenHelper = new DatabaseHelper(context);




    }

    public void InsertSchedule(){

        mOpenHelper.close();
    }


}
*/