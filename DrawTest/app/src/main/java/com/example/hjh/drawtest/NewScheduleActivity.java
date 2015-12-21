package com.example.hjh.drawtest;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;

public class NewScheduleActivity extends AppCompatActivity {

    public static int [] iconImage={R.drawable.food,R.drawable.gear,R.drawable.rain,R.drawable.seminar,R.drawable.sleep,R.drawable.travel};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_schedule);

        Button okBtn = (Button) findViewById(R.id.button);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = getIntent();
                Bundle myBundle = intent.getExtras();

                myBundle.clear();
                EditText eText = (EditText) findViewById(R.id.editText_ScheduleName);
                String str = eText.getText().toString();
                myBundle.putString("ScheduleName", str);

                TextView tView = (TextView) findViewById(R.id.textView_To);
                str = tView.getText().toString();
                myBundle.putString("EndTime", str);

                TextView fView = (TextView) findViewById(R.id.textView_From);
                str = fView.getText().toString();
                myBundle.putString("StartTime", str);

                intent.putExtras(myBundle);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
        Button deleteBtn = (Button) findViewById(R.id.button_Delete);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = getIntent();
                setResult(327, intent);
                finish();
            }
        });
        TextView textViewFrom = (TextView) findViewById(R.id.textView_From);
        textViewFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //From time select
                Intent intent = getIntent();
                Bundle myBundle = intent.getExtras();

                EditText eText = (EditText) findViewById(R.id.editText_ScheduleName);
                String str = eText.getText().toString();
                myBundle.putString("ScheduleName",str);

                TextView tView = (TextView) findViewById(R.id.textView_To);
                str = tView.getText().toString();
                myBundle.putString("EndTime", str);

                TextView fView = (TextView) findViewById(R.id.textView_From);
                str = fView.getText().toString();
                myBundle.putString("StartTime", str);
                intent.putExtras(myBundle);
                setResult(10,intent);
                finish();
            }
        });

        TextView textViewTo = (TextView) findViewById(R.id.textView_To);
        textViewTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //From time select
                Intent intent = getIntent();
                Bundle myBundle = intent.getExtras();

                EditText eText = (EditText) findViewById(R.id.editText_ScheduleName);
                String str = eText.getText().toString();
                myBundle.putString("ScheduleName",str);

                TextView tView = (TextView) findViewById(R.id.textView_To);
                str = tView.getText().toString();
                myBundle.putString("EndTime", str);

                TextView fView = (TextView) findViewById(R.id.textView_From);
                str = fView.getText().toString();
                myBundle.putString("StartTime", str);
                intent.putExtras(myBundle);
                setResult(11,intent);
                finish();
            }
        });

        Intent intent = getIntent();
        Bundle myBundle = intent.getExtras();
        String str = myBundle.getString("ScheduleName");
        EditText eText = (EditText) findViewById(R.id.editText_ScheduleName);
        eText.setText(str);
        str = myBundle.getString("StartTime");
        textViewFrom.setText(str);
        str = myBundle.getString("EndTime");
        textViewTo.setText(str);

        boolean bIsModify = myBundle.getBoolean("IsModify",false);
        if(bIsModify)
        {
            //enable DeleteButton
            deleteBtn.setAlpha(1);
            deleteBtn.setEnabled(true);
        }
        else
        {
            deleteBtn.setAlpha(0);
            deleteBtn.setEnabled(false);
        }


        ////grid icon
        GridView gv =  gv=(GridView) findViewById(R.id.gridView);
        gv.setAdapter(new CustomIconGridAdapter(this,iconImage ));




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_schedule, menu);
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
}
