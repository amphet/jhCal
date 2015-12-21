package com.example.hjh.drawtest;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TodoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TodoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TodoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    private ListView mListView = null;
    private TodoListViewAdapter mAdapter = null;


    public TodoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TodoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TodoFragment newInstance(String param1, String param2) {
        TodoFragment fragment = new TodoFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_todo, container, false);



        return rootView;


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

    public void setTodoListview(Calendar tgtDate)
    {
        mListView = (ListView) this.getView().findViewById(R.id.listView_Todo);
        mAdapter = new TodoListViewAdapter(this.getView().getContext());
        mListView.setAdapter(mAdapter);

        ScheduleDBManager pDB = MainActivity.myDBManager;

        pDB.getTodo(tgtDate);

        SimpleDateFormat dformat_Time = new SimpleDateFormat("HH:mm");
        SimpleDateFormat dformat_Date = new SimpleDateFormat("yyyy.MM.dd");

        for(int i=0;i<pDB.results_Todo.size();i++)
        {
            TodoNode todonodetemp = pDB.results_Todo.get(i);


            //CountDown Calc
            Calendar cal = Calendar.getInstance();
            cal.setTime(todonodetemp.endDate);

            long day = cal.getTimeInMillis()/86400000;//end
            long tday = tgtDate.getTimeInMillis()/86400000;//today
            long count = tday - day;
            String strDday = "D-"+String.valueOf(count+1);

            //elapsed dayte Calc

            day = tgtDate.getTimeInMillis()/86400000;
            cal.setTime(todonodetemp.startDate);
            tday = cal.getTimeInMillis()/86400000;
            count = day - tday;
            String strEday = ordinal((int)count+1);
            if(todonodetemp.nTodoType==ScheduleDBManager.TODO_TYPE_COUNTDOWN)
                mAdapter.addItem("",strDday,todonodetemp.strTodoName);
            else if(todonodetemp.nTodoType == ScheduleDBManager.TODO_TYPE_COUNTBOTH)
                mAdapter.addItem(strEday,strDday,todonodetemp.strTodoName);

        }

            //mAdapter.addItem(getResources().getDrawable(R.drawable.seminar), schedName, schedTime, 0xFFFF0000);





    }
    //implement start
    private class TodoListviewHolder {
        public TextView m_strToCount;
        public TextView m_strFromCount;
        public TextView m_strName;
        public TextView m_strOnlyToCount;
    }

    private class TodoListViewAdapter extends BaseAdapter {
        private Context mContext = null;
        private ArrayList<TodoListviewData> mListData = new ArrayList<TodoListviewData>();

        public TodoListViewAdapter(Context mContext) {
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
            TodoListviewHolder holder;
            if (convertView == null) {
                holder = new TodoListviewHolder();

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.todo_listview_item, null);

                holder.m_strFromCount = (TextView) convertView.findViewById(R.id.mFromCount);
                holder.m_strToCount = (TextView) convertView.findViewById(R.id.mToCount);
                holder.m_strName = (TextView) convertView.findViewById(R.id.mTodoText);
                holder.m_strOnlyToCount = (TextView) convertView.findViewById(R.id.mOnlyToCount);
                convertView.setTag(holder);
            }else{
                holder = (TodoListviewHolder) convertView.getTag();
            }

            TodoListviewData mData = mListData.get(position);

            holder.m_strName.setText(mData.m_strName);
            if(mData.m_strFromCount.isEmpty())//D day count
            {

                holder.m_strOnlyToCount.setText(mData.m_strToCount);
                holder.m_strFromCount.setText("");
                holder.m_strToCount.setText("");
            }
            else {
                holder.m_strFromCount.setText(mData.m_strFromCount);
                holder.m_strToCount.setText(mData.m_strToCount);
                holder.m_strOnlyToCount.setText("");
            }

            return convertView;
        }
        public void addItem(String from,String to, String name){
            TodoListviewData addInfo = null;
            addInfo = new TodoListviewData();
            addInfo.m_strFromCount = from;
            addInfo.m_strToCount = to;
            addInfo.m_strName = name;



            mListData.add(addInfo);
        }

        public void remove(int position){
            mListData.remove(position);
            dataChange();
        }


        public void dataChange(){
            this.notifyDataSetChanged();
        }

    }

    public static String ordinal(int i) {
        String[] sufixes = new String[] { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };
        switch (i % 100) {
            case 11:
            case 12:
            case 13:
                return i + "th";
            default:
                return i + sufixes[i % 10];

        }
    }

    //implement end
}
