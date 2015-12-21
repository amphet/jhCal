package com.example.hjh.drawtest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TimelineFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TimelineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimelineFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public TimelineFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TimelineFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TimelineFragment newInstance(String param1, String param2) {
        TimelineFragment fragment = new TimelineFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    MyTimelineView m_TimelineView;
    CustomScrollView m_TimelineScrollView;
    GestureDetector gestureDetector;
    RectF m_RectForRenameBtn;
    RectF m_RectForIconBtn;
    RectF m_RectForDeleteBtn;
    RectF m_RectForPriorBtn;
    AlertDialog.Builder alertForScheduleRename;

    AlertDialog.Builder alertForIcon;
    ListAdapter m_Iconadapter;

    public static class Item{
        public final String text;
        public final int icon;
        public Item(String text, Integer icon) {
            this.text = text;
            this.icon = icon;
        }
        @Override
        public String toString() {
            return text;
        }
    }

    String m_strRenamedStr;

    public void showRenameDialog(Context context)
    {
        ///////Schedule Rename Dialog
        alertForScheduleRename = new AlertDialog.Builder(context);
        alertForScheduleRename.setTitle("Schedule Rename");

        final EditText input = new EditText(context);
        alertForScheduleRename.setView(input);

        alertForScheduleRename.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                value.toString();
                // Do something with value!ScheduleNode nodeTemp = m_p1DayScheduleList.get(m_TimelineView.nTouchedBox);
                ScheduleNode nodeTemp = m_p1DayScheduleList.get(m_TimelineView.nTouchedBox);
                nodeTemp.strScheduleName = value;
                MainActivity.myDBManager.updateSchedule(nodeTemp.nKey,
                        nodeTemp.startDate, nodeTemp.endDate,
                        value,nodeTemp.nPriority,nodeTemp.nScheduleType,nodeTemp.nScheduleId);

                MainActivity.myDBManager.getSchedule(MainActivity.rView.mainCalendar.calendar_FirstShowingDay,MainActivity.rView.mainCalendar.calendar_LastShowingDay);
                MainActivity.myDBManager.get1DaySchedule(m_SelectedDate);
                m_TimelineView.invalidate();

                SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy.MM.dd");
                String strTouchedDate = dayFormat.format(m_SelectedDate.getTime());
                ((ScheduleFragment)(getActivity().getSupportFragmentManager().findFragmentById(R.id.schedulefragment))).setScheduleListview(strTouchedDate);


            }

        });
        alertForScheduleRename.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }

                });
        alertForScheduleRename.show();
        ////////////////////////
    }
    public void showIconDialog(Context context){
        //////Icon Dialog
        final Item[] items = {
                new Item("Food", R.drawable.food),
                new Item("Gear", R.drawable.gear),
                new Item("Rain", R.drawable.rain),
                new Item("Study", R.drawable.seminar),
                new Item("Sleep", R.drawable.sleep),
                new Item("Travel", R.drawable.travel),
                new Item("...", 0),//no icon for this one
        };

        m_Iconadapter= new ArrayAdapter<Item>(
                context,
                android.R.layout.select_dialog_item,
                android.R.id.text1,
                items){
            public View getView(int position, View convertView, ViewGroup parent) {
                //Use super class to create the View
                View v = super.getView(position, convertView, parent);
                TextView tv = (TextView)v.findViewById(android.R.id.text1);

                //Put the image on the TextView
                tv.setCompoundDrawablesWithIntrinsicBounds(items[position].icon, 0, 0, 0);

                //Add margin between image and text (support various screen densities)
                int dp5 = (int) (5 * getResources().getDisplayMetrics().density + 0.5f);
                tv.setCompoundDrawablePadding(dp5);

                return v;
            }
        };
        alertForIcon = new AlertDialog.Builder(context);
        alertForIcon.setTitle("Schedule Icon");
        alertForIcon.setAdapter(m_Iconadapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                // Do something with value!ScheduleNode nodeTemp = m_p1DayScheduleList.get(m_TimelineView.nTouchedBox);
                ScheduleNode nodeTemp = m_p1DayScheduleList.get(m_TimelineView.nTouchedBox);
                nodeTemp.nScheduleId = items[item].icon;
                MainActivity.myDBManager.updateSchedule(nodeTemp.nKey,
                        nodeTemp.startDate, nodeTemp.endDate,
                        nodeTemp.strScheduleName, nodeTemp.nPriority, nodeTemp.nScheduleType, items[item].icon);

                MainActivity.myDBManager.getSchedule(MainActivity.rView.mainCalendar.calendar_FirstShowingDay, MainActivity.rView.mainCalendar.calendar_LastShowingDay);
                MainActivity.myDBManager.get1DaySchedule(m_SelectedDate);
                m_TimelineView.invalidate();

                SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy.MM.dd");
                String strTouchedDate = dayFormat.format(m_SelectedDate.getTime());
                ((ScheduleFragment) (getActivity().getSupportFragmentManager().findFragmentById(R.id.schedulefragment))).setScheduleListview(strTouchedDate);
            }
        });

        alertForIcon.show();

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View ret =inflater.inflate(R.layout.fragment_timeline, container, false);
        LinearLayout layout = (LinearLayout) ret.findViewById(R.id.timelinescrolllayout);
        m_TimelineScrollView = (CustomScrollView) ret.findViewById(R.id.timelinescroll);


        m_RectForRenameBtn = new RectF();
        m_RectForIconBtn = new RectF();
        m_RectForDeleteBtn = new RectF();
        m_RectForPriorBtn = new RectF();

        m_TimelineView = new MyTimelineView(ret.getContext());






        //HARD CODING!!!!!!!!!!!!!!!!!!!
        ///HARDCODING WARning
        //I don't know how to get a size of layout.
        Point size = new Point(); getActivity().getWindowManager().getDefaultDisplay().getSize(size);

        layout.addView(m_TimelineView, size.x * 6 / 10, size.y * 2);
        //String strlog = "width : " + layout.getWidth() + " height : " + layout.getHeight();
        //Log.e("hjh",strlog);
        gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
            Toast toast;


           @Override
            public void onLongPress(MotionEvent event) {
                if(m_TimelineView.nTimeSelectMode != MainActivity.SCHEDULETIMESELECT_NONE) {
                    //Toast.makeText(this.getContext(), "OK", Toast.LENGTH_SHORT).show();
                    //TimelineFragment tlFrag = (TimelineFragment) (getActivity().getSupportFragmentManager().findFragmentById(R.id.timelinefragment));
                    timeSelectionEnd(m_TimelineView.getTouchedTime(event.getX(), event.getY()));
                }
                else{//Schedule Box Pick;
                    m_TimelineView.nTouchedBox = m_TimelineView.getTouchedBox(m_TimelineView.getTouchedTime(event.getX(), event.getY()));
                    if(m_TimelineView.nTouchedBox!=-1)
                        m_TimelineScrollView.setEnableScrolling(false);
                    else{
                        //Create New Schedule At Touched Time
                        int nTime = m_TimelineView.getTouchedTime(event.getX(), event.getY());
                        Date startDate;
                        Date endDate;

                        startDate = new Date();
                        startDate.setTime(m_SelectedDate.getTimeInMillis());
                        startDate.setSeconds(0);
                        startDate.setHours(nTime / 60);
                        startDate.setMinutes(nTime % 60);
                        endDate = new Date();
                        endDate.setTime(m_SelectedDate.getTimeInMillis());
                        endDate.setSeconds(0);
                        if(nTime / 60 == 23) {
                            endDate.setHours(23);
                            endDate.setMinutes(60);
                        }
                        else
                        {
                            endDate.setHours(nTime / 60+1);
                            endDate.setMinutes(nTime % 60);
                        }


                        MainActivity.myDBManager.insertSchedule(startDate, endDate, "New Schedule", 0, 0, 0);

                        MainActivity.myDBManager.getSchedule(MainActivity.rView.mainCalendar.calendar_FirstShowingDay, MainActivity.rView.mainCalendar.calendar_LastShowingDay);
                        MainActivity.myDBManager.get1DaySchedule(m_SelectedDate);

                        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy.MM.dd");
                        String strTouchedDate = dayFormat.format(m_SelectedDate.getTime());
                        ((ScheduleFragment)(getActivity().getSupportFragmentManager().findFragmentById(R.id.schedulefragment))).setScheduleListview(strTouchedDate);


                    }
                    m_TimelineView.invalidate();

                }
            }

            @Override
            public boolean onDown(MotionEvent event) {
                if(m_TimelineView.nTimeSelectMode != MainActivity.SCHEDULETIMESELECT_NONE) {
                    int nTouchedMinutes = m_TimelineView.getTouchedTime(event.getX(), event.getY());
                    String str = String.valueOf(nTouchedMinutes / 60) + ":" + String.valueOf(nTouchedMinutes % 60);
                    toast = Toast.makeText(m_TimelineView.getContext(), str, Toast.LENGTH_SHORT);
                    toast.show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            toast.cancel();
                        }
                    }, 200);
                }
                else
                {
                    if(m_TimelineView.bIsBoxSelected){// Trace Rename/Icon/Delete btn
                        float fx = event.getX();
                        float fy = event.getY();
                        if(m_RectForRenameBtn.contains(fx,fy))
                        {


                          //  AlertDialog renameDialog = alertForScheduleRename.create();
                            showRenameDialog(ret.getContext());

                        }
                        else if(m_RectForDeleteBtn.contains(fx,fy))
                        {
                            ScheduleNode nodeTemp = m_p1DayScheduleList.get(m_TimelineView.nTouchedBox);

                            MainActivity.myDBManager.deleteSchedule(nodeTemp.nKey);
                            MainActivity.myDBManager.getSchedule(MainActivity.rView.mainCalendar.calendar_FirstShowingDay,MainActivity.rView.mainCalendar.calendar_LastShowingDay);
                            MainActivity.myDBManager.get1DaySchedule(m_SelectedDate);
                            m_TimelineView.invalidate();
                            SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy.MM.dd");
                            String strTouchedDate = dayFormat.format(m_SelectedDate.getTime());
                            ((ScheduleFragment)(getActivity().getSupportFragmentManager().findFragmentById(R.id.schedulefragment))).setScheduleListview(strTouchedDate);

                            m_TimelineView.nTouchedBox = -1;
                            m_TimelineView.bIsBoxSelected = false;
                            m_TimelineScrollView.setEnableScrolling(true);

                        }
                        else if(m_RectForIconBtn.contains(fx,fy))
                        {
                            showIconDialog(ret.getContext());
                        }
                        else if(m_RectForPriorBtn.contains(fx,fy)){
                            ScheduleNode nodeTemp = m_p1DayScheduleList.get(m_TimelineView.nTouchedBox);
                            if(nodeTemp.nPriority>0) nodeTemp.nPriority = 0;
                            else nodeTemp.nPriority = 1;


                            MainActivity.myDBManager.updateSchedule(nodeTemp.nKey,
                                    nodeTemp.startDate, nodeTemp.endDate,
                                    nodeTemp.strScheduleName,nodeTemp.nPriority,nodeTemp.nScheduleType,nodeTemp.nScheduleId);

                            MainActivity.myDBManager.getSchedule(MainActivity.rView.mainCalendar.calendar_FirstShowingDay,MainActivity.rView.mainCalendar.calendar_LastShowingDay);
                            MainActivity.myDBManager.get1DaySchedule(m_SelectedDate);
                            m_TimelineView.invalidate();

                            SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy.MM.dd");
                            String strTouchedDate = dayFormat.format(m_SelectedDate.getTime());
                            ((ScheduleFragment)(getActivity().getSupportFragmentManager().findFragmentById(R.id.schedulefragment))).setScheduleListview(strTouchedDate);
                        }
                    }


                }
                //return super.onSingleTapUp(e);
                return true;
            }



            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if(m_TimelineView.bIsBoxSelected) {
                    int nMoveStartAt = m_TimelineView.getTouchedTime(e1.getX(), e1.getY());
                    int nMoveStartTo = m_TimelineView.getTouchedTime(e2.getX(), e2.getY());

                    ScheduleNode nodeTemp = m_p1DayScheduleList.get(m_TimelineView.nTouchedBox);
                    int nScheduleStartAt = nodeTemp.startDate.getHours()*60+nodeTemp.startDate.getMinutes();
                    int nScheduleEndAt = nodeTemp.endDate.getHours()*60+nodeTemp.endDate.getMinutes();

                    int newHour = nMoveStartTo/60;
                    int newMin = nMoveStartTo%60;
                    if(Math.abs(nScheduleStartAt-nMoveStartAt) <= 10)//move Schedule's Start Time
                    {
                        nodeTemp.startDate.setHours(newHour);
                        nodeTemp.startDate.setMinutes(newMin);
                    }
                    else if(Math.abs(nMoveStartAt-nScheduleEndAt)<=10)//move Schedule's End Time
                    {
                        nodeTemp.endDate.setHours(newHour);
                        nodeTemp.endDate.setMinutes(newMin);
                    }
                    m_TimelineView.invalidate();
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if(m_TimelineView.bIsBoxSelected) {
                    ScheduleNode nodeTemp = m_p1DayScheduleList.get(m_TimelineView.nTouchedBox);

                    MainActivity.myDBManager.updateSchedule(nodeTemp.nKey,
                            nodeTemp.startDate, nodeTemp.endDate,
                            nodeTemp.strScheduleName, nodeTemp.nPriority, nodeTemp.nScheduleType, nodeTemp.nScheduleId);

                    m_TimelineView.bIsBoxSelected = false;
                    m_TimelineView.nTouchedBox = -1;
                    m_TimelineScrollView.setEnableScrolling(true);


                    m_TimelineView.invalidate();


                    MainActivity.myDBManager.get1DaySchedule(m_SelectedDate);
                    SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy.MM.dd");
                    String strTouchedDate = dayFormat.format(m_SelectedDate.getTime());
                    ((ScheduleFragment) (getActivity().getSupportFragmentManager().findFragmentById(R.id.schedulefragment))).setScheduleListview(strTouchedDate);
                }

                 return super.onDoubleTap(e);

            }
        });


        return ret;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }



    @Override
    public void onAttach(Activity a) {
        super.onAttach(a);
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private ArrayList<ScheduleNode> m_p1DayScheduleList;
    private Calendar m_SelectedDate;
    public void setDateForTimeline(Calendar cal){
        ScheduleDBManager pDB = MainActivity.myDBManager;

        m_SelectedDate = cal;
        m_p1DayScheduleList = pDB.results_1DaySchedule;
    }


    public void setScrollByTime(String strTime)
    {
        int hour = Integer.parseInt(strTime.substring(0, 2));
        Log.e("HJH","setscrollhour : "+hour);

        m_TimelineScrollView.scrollTo(0, m_TimelineView.getHeight() * hour / 24 - m_TimelineScrollView.getHeight() / 3);

    }
    public void setTimeSelectionMode(int mode)
    {
        m_TimelineView.setTimeSelectionMode(mode);

    }
    private void timeSelectionEnd(int time)
    {
        ((DayActivity)getActivity()).scheduleTimeSelectEnd(time);
    }
    private class MyTimelineView extends View{
        Paint paint;
        Typeface font;
        private int nTimeSelectMode;
        private int nWidth;
        private int nHeight;
        private int nTouchedBox;
        private float fTimeTextTopBottomPadding;
        private float fTimeTextLeftPadding;
        private float fTimeTextBetweenPad;
        private float fTimeTextVerticalAlignConst;
        private float fTimeTextAreaWidth;
        public boolean bIsBoxSelected;

        public MyTimelineView(Context context) {
            super(context);
            paint = new Paint();
            fTimeTextTopBottomPadding = 80;
            nTouchedBox = -1;
            bIsBoxSelected = false;

        }
        public int getTouchedBox(int min)
        {
            int nIndex=-1;
            bIsBoxSelected = false;


            SimpleDateFormat sdf_YYYYMMDD = new SimpleDateFormat("yyyy.MM.dd");
            SimpleDateFormat sdf_ymdhms = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

            String strToDateTime = sdf_YYYYMMDD.format(m_SelectedDate.getTime());

            strToDateTime = strToDateTime + " "+String.valueOf(min/60)+":"+String.valueOf(min%60)+":00";
            Log.e("HJH",strToDateTime);
            Date targetDate=null;
            try{
                targetDate = sdf_ymdhms.parse(strToDateTime);}
            catch (ParseException e)
            {
                e.printStackTrace();
            }



            for(int i=0;i<m_p1DayScheduleList.size();i++)
            {
                ScheduleNode nodeTemp = m_p1DayScheduleList.get(i);
                if(nodeTemp.nScheduleType == MainActivity.myDBManager.SCHEDULE_TYPE_CONT) continue;

                if(targetDate.after(nodeTemp.startDate) && targetDate.before(nodeTemp.endDate))
                {
                    nIndex = i;
                    bIsBoxSelected = true;
                    break;
                }
            }

            return nIndex;
        }
        private int getTouchedTime(float  x,float y)
        {
            int hour=0;
            for(int j=0;j<25;j++)
            {
                if(y>=fTimeTextTopBottomPadding+j*fTimeTextBetweenPad &&y <= fTimeTextTopBottomPadding+(j+1)*fTimeTextBetweenPad)
                {
                    hour = j;
                    break;
                }
            }

            x = (x-fTimeTextAreaWidth)/(nWidth-fTimeTextLeftPadding-fTimeTextAreaWidth)*60.0f;
            Log.e("HJH","TIME : "+hour+","+(int)x);
            if(hour <0) hour = 0;
            if(hour > 23) hour = 23;
            if(x<0) x = 0;
            if(x>59) x = 60;
            return hour*60+(int)x;
        }
        public void setTimeSelectionMode(int mode)
        {
            nTimeSelectMode = mode;
        }


        @Override
        public boolean onTouchEvent( MotionEvent event)
        {
            //Log.e("HJH", "TOUCHED AT : " + event.getX() + "," + event.getY());
            return gestureDetector.onTouchEvent(event);

        }
        protected void onDraw(Canvas canvas) {
            Log.e("HJH","Timeline Draw Start");
            super.onDraw(canvas);
            paint.setColor(Color.CYAN);
            paint.setAntiAlias(true);
            nWidth = canvas.getWidth();
            nHeight = canvas.getHeight();
            int nFontSize = 40;
            /**************TimeText start**************/
            paint.setTextSize(nFontSize);
            paint.setTextAlign(Paint.Align.LEFT);//수평정렬
            paint.setColor(Color.argb(255, 0, 0, 0));
            paint.setStrokeWidth(1);
            canvas.drawText("12",0,0,paint);

            fTimeTextTopBottomPadding = 80;
            fTimeTextLeftPadding = nWidth*0.01f;
            fTimeTextBetweenPad = (nHeight-fTimeTextTopBottomPadding*2)/24.0f;

            Rect textBound = new Rect();
            paint.getTextBounds("12",0,"12".length(),textBound);
            fTimeTextVerticalAlignConst = (textBound.bottom + textBound.top) / 2;
            fTimeTextAreaWidth = (textBound.right - textBound.left)*2f;

            //text write
            String str;
            canvas.drawText("12",fTimeTextLeftPadding,fTimeTextTopBottomPadding - fTimeTextVerticalAlignConst,paint);
            canvas.drawText("AM",fTimeTextLeftPadding,fTimeTextTopBottomPadding - fTimeTextVerticalAlignConst+nFontSize*1.3f,paint);

            for(int i=1;i<13;i++)
            {
                str = String.format("%02d",i);
                canvas.drawText(str,fTimeTextLeftPadding,fTimeTextTopBottomPadding + i*fTimeTextBetweenPad-fTimeTextVerticalAlignConst,paint);
            }
            canvas.drawText("PM",fTimeTextLeftPadding,fTimeTextTopBottomPadding+(13) * fTimeTextBetweenPad - fTimeTextVerticalAlignConst+nFontSize*1.3f,paint);
            for(int i=1;i<13;i++)
            {
                str = String.format("%02d",i);
                canvas.drawText(String.format("%02d", i), fTimeTextLeftPadding, fTimeTextTopBottomPadding + (12 + i) * fTimeTextBetweenPad - fTimeTextVerticalAlignConst, paint);
            }

            // Line
            // TimeTextArea Vertical Dividing Line
            canvas.drawLine(fTimeTextAreaWidth,fTimeTextTopBottomPadding,fTimeTextAreaWidth,nHeight-fTimeTextTopBottomPadding,paint);
            for(int i=0;i<25;i++)
            {
                canvas.drawLine(fTimeTextAreaWidth,fTimeTextTopBottomPadding + i*fTimeTextBetweenPad,nWidth-fTimeTextLeftPadding,fTimeTextTopBottomPadding + i*fTimeTextBetweenPad,paint);
            }
            /*************TimeText End***************/

            /*****Schedule Box Start**************/
            int nScheduleOverlapLevel = 0;
            ScheduleNode nodeTemp;
            SimpleDateFormat sdf_YYYYMMDD = new SimpleDateFormat("yyyy.MM.dd");

            String strSelectedDate = sdf_YYYYMMDD.format(m_SelectedDate.getTime());

            String strTemp;
            int nStartHour,nStartMin;
            int nEndHour,nEndMin;
            int nPrevTotalMin, nTotalMin;//for Overlap check


            int cr,cg,cb;

            for(int i=0;i<m_p1DayScheduleList.size();i++)
            {
                nodeTemp = m_p1DayScheduleList.get(i);

                cr = nodeTemp.nR;
                cg = nodeTemp.nG;
                cb = nodeTemp.nB;

                strTemp = sdf_YYYYMMDD.format(nodeTemp.startDate);
                if(strSelectedDate.equals(strTemp))
                {
                    nStartHour = nodeTemp.startDate.getHours();
                    nStartMin = nodeTemp.startDate.getMinutes();
                }
                else////is Schedule started yesterday?
                {
                    nStartHour = 0;
                    nStartMin = 0;
                }
                strTemp = sdf_YYYYMMDD.format(nodeTemp.endDate);
                if(strSelectedDate.equals(strTemp))
                {
                    nEndHour = nodeTemp.endDate.getHours();
                    nEndMin = nodeTemp.endDate.getMinutes();
                }
                else////is Schedule started yesterday?
                {
                    nEndHour = 23;
                    nEndMin = 60;
                }
                Log.e("HJH", nodeTemp.strScheduleName + "," + nStartHour + "," + nStartMin + "," + nEndHour + "," + nEndMin);
                //Box Draw

                paint.setColor(Color.argb(100, cr, cg, cb));

                boolean bIsDrawScheduleName = false;
                if(nodeTemp.nScheduleType==MainActivity.myDBManager.SCHEDULE_TYPE_CONT)
                {
                    if(sdf_YYYYMMDD.format(nodeTemp.startDate).equals(strSelectedDate)){

                        LinearGradient shader = new LinearGradient(fTimeTextAreaWidth + (nWidth - fTimeTextLeftPadding - fTimeTextAreaWidth) * nStartMin / 60.0f
                                , fTimeTextTopBottomPadding + nStartHour * fTimeTextBetweenPad,
                                nWidth
                                , fTimeTextTopBottomPadding + nStartHour * fTimeTextBetweenPad, Color.RED, Color.WHITE,Shader.TileMode.CLAMP);

                        paint.setShader(shader);

                        canvas.drawRect(fTimeTextAreaWidth + (nWidth - fTimeTextLeftPadding - fTimeTextAreaWidth) * nStartMin / 60.0f,
                                fTimeTextTopBottomPadding + nStartHour * fTimeTextBetweenPad,
                                nWidth,
                                fTimeTextTopBottomPadding + (nStartHour + 1) * fTimeTextBetweenPad, paint);


                        // ??? Shader ??? ????.

                        paint.setShader(null);
                        canvas.drawText("Start", fTimeTextAreaWidth + (nWidth - fTimeTextLeftPadding - fTimeTextAreaWidth) * nStartMin / 60.0f,
                                fTimeTextTopBottomPadding + nStartHour * fTimeTextBetweenPad + nFontSize + nFontSize * 1.2f, paint);
                        bIsDrawScheduleName = true;
                    }
                    else if(sdf_YYYYMMDD.format(nodeTemp.endDate).equals(strSelectedDate)){
                        LinearGradient shader = new LinearGradient(fTimeTextAreaWidth+fTimeTextLeftPadding
                                , fTimeTextTopBottomPadding + nEndHour * fTimeTextBetweenPad,
                                fTimeTextAreaWidth+ (nWidth - fTimeTextLeftPadding - fTimeTextAreaWidth) * nEndMin / 60.0f
                                , fTimeTextTopBottomPadding + nEndHour * fTimeTextBetweenPad, Color.RED, Color.WHITE,Shader.TileMode.CLAMP);

                        paint.setShader(shader);

                        canvas.drawRect(fTimeTextAreaWidth+fTimeTextLeftPadding,
                                fTimeTextTopBottomPadding + nEndHour * fTimeTextBetweenPad,
                                fTimeTextAreaWidth + (nWidth - fTimeTextLeftPadding - fTimeTextAreaWidth) * nEndMin / 60.0f,
                                fTimeTextTopBottomPadding + (nEndHour + 1) * fTimeTextBetweenPad, paint);


                        // ??? Shader ??? ????.

                        paint.setShader(null);
                        canvas.drawText("End", fTimeTextAreaWidth + (nWidth - fTimeTextLeftPadding - fTimeTextAreaWidth) * nStartMin / 60.0f,
                                fTimeTextTopBottomPadding + nEndHour * fTimeTextBetweenPad + nFontSize + nFontSize * 1.2f, paint);
                        bIsDrawScheduleName = true;
                    }


                }
                else{
                    bIsDrawScheduleName = true;
                    for(int j=nStartHour;j<=nEndHour;j++)
                    {

                        //canvas.drawLine(fTimeTextAreaWidth,fTimeTextTopBottomPadding + i*fTimeTextBetweenPad,nWidth-fTimeTextLeftPadding,fTimeTextTopBottomPadding + i*fTimeTextBetweenPad,paint);
                        if (nStartHour == nEndHour && nEndHour == j)
                        {
                            canvas.drawRect(fTimeTextAreaWidth + (nWidth - fTimeTextLeftPadding - fTimeTextAreaWidth) * nStartMin / 60.0f,
                                    fTimeTextTopBottomPadding + j * fTimeTextBetweenPad,
                                    fTimeTextAreaWidth + (nWidth - fTimeTextLeftPadding - fTimeTextAreaWidth) * nEndMin / 60.0f,
                                    fTimeTextTopBottomPadding + (j + 1) * fTimeTextBetweenPad, paint);
                            if (i == nTouchedBox && bIsBoxSelected) {
                                paint.setColor(Color.argb(255, 0, 0, 0));
                                canvas.drawCircle(fTimeTextAreaWidth + (nWidth - fTimeTextLeftPadding - fTimeTextAreaWidth) * nStartMin / 60.0f,
                                        fTimeTextTopBottomPadding + (j + 0.5f) * fTimeTextBetweenPad, fTimeTextAreaWidth / 4, paint);
                                canvas.drawCircle(fTimeTextAreaWidth + (nWidth - fTimeTextLeftPadding - fTimeTextAreaWidth) * nEndMin / 60.0f,
                                        fTimeTextTopBottomPadding + (j + 0.5f) * fTimeTextBetweenPad, fTimeTextAreaWidth / 4, paint);

                                paint.getTextBounds("RENAME", 0, "RENAME".length(), textBound);
                                canvas.drawText("RENAME", fTimeTextAreaWidth + fTimeTextLeftPadding,
                                        fTimeTextTopBottomPadding + j * fTimeTextBetweenPad, paint);
                                m_RectForRenameBtn.set(fTimeTextAreaWidth + fTimeTextLeftPadding, fTimeTextTopBottomPadding + j * fTimeTextBetweenPad - textBound.height(),
                                        fTimeTextAreaWidth + fTimeTextLeftPadding + textBound.width(), fTimeTextTopBottomPadding + j * fTimeTextBetweenPad);

                                paint.getTextBounds("ICON", 0, "ICON".length(), textBound);//수직정렬기능이없어서 추가
                                canvas.drawText("ICON", nWidth - textBound.width(), fTimeTextTopBottomPadding + j * fTimeTextBetweenPad, paint);
                                m_RectForIconBtn.set(nWidth - textBound.width(), fTimeTextTopBottomPadding + j * fTimeTextBetweenPad - textBound.height(),
                                        nWidth, fTimeTextTopBottomPadding + j * fTimeTextBetweenPad);

                                paint.getTextBounds("DELETE", 0, "DELETE".length(), textBound);//수직정렬기능이없어서 추가
                                canvas.drawText("DELETE", fTimeTextAreaWidth + fTimeTextLeftPadding, fTimeTextTopBottomPadding + (j + 1) * fTimeTextBetweenPad + textBound.height(), paint);
                                m_RectForDeleteBtn.set(fTimeTextAreaWidth + fTimeTextLeftPadding, fTimeTextTopBottomPadding + (j + 1) * fTimeTextBetweenPad,
                                        fTimeTextAreaWidth + fTimeTextLeftPadding + textBound.width(), fTimeTextTopBottomPadding + (j + 1) * fTimeTextBetweenPad + textBound.height());


                                paint.getTextBounds("PRIOR", 0, "PRIOR".length(), textBound);//수직정렬기능이없어서 추가
                                if(nodeTemp.nPriority>0)
                                {
                                    paint.setColor(Color.argb(255, 255, 0, 0));
                                }
                                canvas.drawText("PRIOR", nWidth - textBound.width(), fTimeTextTopBottomPadding + (j+1) * fTimeTextBetweenPad+textBound.height(), paint);
                                m_RectForPriorBtn.set(nWidth - textBound.width(),fTimeTextTopBottomPadding + (j+1) * fTimeTextBetweenPad
                                        ,nWidth,fTimeTextTopBottomPadding + (j+1) * fTimeTextBetweenPad+textBound.height());
                                paint.setColor(Color.argb(100, cr, cg, cb));

                            }
                        } else if (j == nStartHour) {
                            canvas.drawRect(fTimeTextAreaWidth + (nWidth - fTimeTextLeftPadding-fTimeTextAreaWidth)*nStartMin/60.0f,
                                    fTimeTextTopBottomPadding + j * fTimeTextBetweenPad,
                                    nWidth - fTimeTextLeftPadding,
                                    fTimeTextTopBottomPadding + (j + 1) * fTimeTextBetweenPad, paint);
                            if(i == nTouchedBox && bIsBoxSelected) {
                                paint.setColor(Color.argb(255, 0, 0, 0));
                                canvas.drawCircle(fTimeTextAreaWidth + (nWidth - fTimeTextLeftPadding - fTimeTextAreaWidth) * nStartMin / 60.0f,
                                        fTimeTextTopBottomPadding + (j + 0.5f) * fTimeTextBetweenPad, fTimeTextAreaWidth / 4, paint);
                                canvas.drawText("RENAME", fTimeTextAreaWidth + fTimeTextLeftPadding,
                                        fTimeTextTopBottomPadding + j * fTimeTextBetweenPad, paint);
                                paint.getTextBounds("RENAME", 0, "RENAME".length(), textBound);
                                m_RectForRenameBtn.set(fTimeTextAreaWidth + fTimeTextLeftPadding, fTimeTextTopBottomPadding + j * fTimeTextBetweenPad - textBound.height(),
                                        fTimeTextAreaWidth + fTimeTextLeftPadding + textBound.width(), fTimeTextTopBottomPadding + j * fTimeTextBetweenPad);


                                paint.getTextBounds("ICON", 0, "ICON".length(), textBound);//수직정렬기능이없어서 추가
                                canvas.drawText("ICON", nWidth - textBound.width(), fTimeTextTopBottomPadding + j * fTimeTextBetweenPad, paint);
                                m_RectForIconBtn.set(nWidth - textBound.width(), fTimeTextTopBottomPadding + j * fTimeTextBetweenPad - textBound.height(),
                                        nWidth, fTimeTextTopBottomPadding + j * fTimeTextBetweenPad);

                                paint.setColor(Color.argb(100, cr, cg, cb));
                            }

                        }
                        else if (j==nEndHour)
                        {
                            canvas.drawRect(fTimeTextAreaWidth,
                                    fTimeTextTopBottomPadding + j*fTimeTextBetweenPad,
                                    fTimeTextAreaWidth+(nWidth-fTimeTextLeftPadding-fTimeTextAreaWidth)*nEndMin/60.0f,
                                    fTimeTextTopBottomPadding + (j+1)*fTimeTextBetweenPad,paint);
                            if(i == nTouchedBox && bIsBoxSelected)
                            {
                                paint.setColor(Color.argb(255, 0, 0, 0));
                                canvas.drawCircle(fTimeTextAreaWidth + (nWidth - fTimeTextLeftPadding - fTimeTextAreaWidth) * nEndMin / 60.0f,
                                        fTimeTextTopBottomPadding + (j + 0.5f) * fTimeTextBetweenPad, fTimeTextAreaWidth / 4, paint);
                                paint.getTextBounds("DELETE", 0, "DELETE".length(), textBound);//수직정렬기능이없어서 추가
                                canvas.drawText("DELETE", fTimeTextAreaWidth + fTimeTextLeftPadding, fTimeTextTopBottomPadding + (j + 1) * fTimeTextBetweenPad + textBound.height(), paint);
                                m_RectForDeleteBtn.set(fTimeTextAreaWidth + fTimeTextLeftPadding, fTimeTextTopBottomPadding + (j + 1) * fTimeTextBetweenPad,
                                        fTimeTextAreaWidth + fTimeTextLeftPadding + textBound.width(), fTimeTextTopBottomPadding + (j + 1) * fTimeTextBetweenPad + textBound.height());


                                paint.getTextBounds("PRIOR", 0, "PRIOR".length(), textBound);//수직정렬기능이없어서 추가
                                if(nodeTemp.nPriority>0)
                                {
                                    paint.setColor(Color.argb(255, 255, 0, 0));
                                }
                                canvas.drawText("PRIOR", nWidth - textBound.width(), fTimeTextTopBottomPadding + (j + 1) * fTimeTextBetweenPad + textBound.height(), paint);
                                m_RectForPriorBtn.set(nWidth - textBound.width(), fTimeTextTopBottomPadding + (j + 1) * fTimeTextBetweenPad
                                        , nWidth, fTimeTextTopBottomPadding + (j + 1) * fTimeTextBetweenPad + textBound.height());
                                paint.setColor(Color.argb(100, cr, cg, cb));

                            }

                        }

                        else
                        {
                            canvas.drawRect(fTimeTextAreaWidth,
                                    fTimeTextTopBottomPadding + j*fTimeTextBetweenPad,
                                    nWidth-fTimeTextLeftPadding,
                                    fTimeTextTopBottomPadding + (j+1)*fTimeTextBetweenPad,paint);
                        }

                    }
                }

                if(bIsDrawScheduleName) {
                    paint.setColor(Color.argb(255, cr, cg, cb));
                    canvas.drawText(nodeTemp.strScheduleName, fTimeTextAreaWidth + (nWidth - fTimeTextLeftPadding - fTimeTextAreaWidth) * nStartMin / 60.0f,
                            fTimeTextTopBottomPadding + nStartHour * fTimeTextBetweenPad + nFontSize, paint);
                }

            }

            /*****Schedule Box End***************/


        }


    }
}
