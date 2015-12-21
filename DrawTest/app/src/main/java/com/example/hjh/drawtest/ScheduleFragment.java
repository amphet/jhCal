package com.example.hjh.drawtest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ScheduleFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ScheduleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScheduleFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private ListView mListView = null;
    private ListViewAdapter mAdapter = null;





    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ScheduleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ScheduleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ScheduleFragment newInstance(String param1, String param2) {
        ScheduleFragment fragment = new ScheduleFragment();
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

    //implement start
    private class ScheduleListviewHolder {
        public ImageView mIcon;

        public TextView mText;

        public TextView mDate;
    }
    private class ListViewAdapter extends BaseAdapter {
        private Context mContext = null;
        private ArrayList<ScheduleListviewData> mListData = new ArrayList<ScheduleListviewData>();

        public ListViewAdapter(Context mContext) {
            super();
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            return mListData.size();
        }

        @Override
        public Object getItem(int position) {
            return mListData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ScheduleListviewHolder holder;
            if (convertView == null) {
                holder = new ScheduleListviewHolder();

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.schedule_listview_item, null);

                holder.mIcon = (ImageView) convertView.findViewById(R.id.mImage);
                holder.mText = (TextView) convertView.findViewById(R.id.mText);
                holder.mDate = (TextView) convertView.findViewById(R.id.mDate);

                convertView.setTag(holder);
            }else{
                holder = (ScheduleListviewHolder) convertView.getTag();
            }

            ScheduleListviewData mData = mListData.get(position);

            if (mData.mIcon != null) {
                holder.mIcon.setVisibility(View.VISIBLE);
                holder.mIcon.setImageDrawable(mData.mIcon);
            }else{
                holder.mIcon.setVisibility(View.GONE);
            }

            holder.mText.setTextColor(mData.mColor);
            holder.mText.setText(mData.mTitle);
            holder.mDate.setText(mData.mDate);

            return convertView;
        }
        public void addItem(Drawable icon, String mTitle, String mDate, int color,int id){
            ScheduleListviewData addInfo = null;
            addInfo = new ScheduleListviewData();
            addInfo.mIcon = icon;
            addInfo.mTitle = mTitle;
            addInfo.mDate = mDate;
            addInfo.mColor = color;
            addInfo.nId = id;
            mListData.add(addInfo);
        }

        public void remove(int position){
            mListData.remove(position);
            dataChange();
        }

        public void sort(){
            Collections.sort(mListData, ScheduleListviewData.ALPHA_COMPARATOR);
            dataChange();
        }

        public void dataChange(){
            this.notifyDataSetChanged();
        }

    }

    //implement end
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_schedule, container, false);
/*

        arrayList = new ArrayList<String>();
        arrayList.add("14:40 Seminar 1");
        arrayList.add("22:00 Sleep 1");
        arrayList.add("12:10 Lunch 0");
        arrayList.add("18:30 Dinner 0");

        listAdaptor = new ArrayAdapter<String>(rootView.getContext(),android.R.layout.simple_list_item_1,arrayList);
        mListView = (ListView) rootView.findViewById(R.id.listView);
        mListView.setAdapter(listAdaptor);
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);*/
        mListView = (ListView) rootView.findViewById(R.id.listView);

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                //Toast.makeText(getBaseContext(), "long~~~~~~~~~", Toast.LENGTH_SHORT).show();
                ScheduleListviewData selectedSchedule = (ScheduleListviewData) mListView.getItemAtPosition(arg2);

                if( ((DayActivity)(getActivity())).nTimeSelectMode == MainActivity.SCHEDULETIMESELECT_NONE)
                {
                    ScheduleNode nodeTemp;
                    for(int i=0;i<MainActivity.myDBManager.results_1DaySchedule.size();i++)
                    {
                        nodeTemp = MainActivity.myDBManager.results_1DaySchedule.get(i);
                        if(nodeTemp.nKey == selectedSchedule.nId)
                        {
                            //should create NewSchedule By
                            ((DayActivity)getActivity()).scheduleModify(nodeTemp);
                            break;
                        }
                    }
                }



                return true;
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ScheduleListviewData selectedSchedule = (ScheduleListviewData) mListView.getItemAtPosition(i);

                //not oop sorry
                TimelineFragment tlFrag = (TimelineFragment)(getActivity().getSupportFragmentManager().findFragmentById(R.id.timelinefragment));
                tlFrag.setScrollByTime(selectedSchedule.mDate);


            }
        });





        return rootView;

        //return inflater.inflate(R.layout.fragment_schedule, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
 /*       if (context instanceof OnFragmentInteractionListener) {
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

    public void setScheduleListview(String strTouchedDay)
    {


        mAdapter = new ListViewAdapter(this.getView().getContext());
        mListView.setAdapter(mAdapter);

        ScheduleDBManager pDB = MainActivity.myDBManager;

        Collections.sort(pDB.results_Schedule, pDB.scheduleSort);//priority & datetime sort
        SimpleDateFormat sdf_Time = new SimpleDateFormat("HH:mm");
        SimpleDateFormat sdf_Date = new SimpleDateFormat("yyyy.MM.dd");

        //String strDate = sdf_Date.format(tgtDay);

        for(int i=0;i<pDB.results_Schedule.size();i++)
        {
            ScheduleNode scheduleNodeTemp =pDB.results_Schedule.get(i);

            String strNodeStartDate = sdf_Date.format(scheduleNodeTemp.startDate.getTime());


            if((strNodeStartDate.substring(0,10)).equals(strTouchedDay.substring(0,10)))
            {
                String schedName =pDB.results_Schedule.get(i).strScheduleName;
                String schedTime =sdf_Time.format(pDB.results_Schedule.get(i).startDate);
                int nId = pDB.results_Schedule.get(i).nKey;
                int nPr = pDB.results_Schedule.get(i).nPriority;
                int nIcon = pDB.results_Schedule.get(i).nScheduleId;

                if(nIcon == 0)
                {
                    nIcon = R.drawable.gear;
                }
                if(nPr > 0)
                    mAdapter.addItem(getResources().getDrawable(nIcon), schedName, schedTime, 0xFFFF0000,nId);
                else
                    mAdapter.addItem(getResources().getDrawable(nIcon), schedName, schedTime, 0xFF000000,nId);
            }


        }
        /*
        mAdapter.addItem(getResources().getDrawable(R.drawable.seminar), "Seminar", "14:40", 0xFFFF0000);
        mAdapter.addItem(getResources().getDrawable(R.drawable.sleep), "Sleep", "22:00", 0xFFFF0000);
        mAdapter.addItem(getResources().getDrawable(R.drawable.food), "Lunch", "12:10", 0xFF000000);
        mAdapter.addItem(null, "Dinner", "18:30", 0xFF000000);*/



    }
}
