package com.mmdu.erp.sms;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewForPrediction extends AppCompatActivity {

    String JSON_String_prediction = null;
    JSONArray jsonArray_prediction;
    JSONObject jsonObject_prediction;

    android.support.v7.widget.RecyclerView recyclerView;
    AttendanceAdapterForPrediction attendanceAdapterForPrediction;
    List<AttendanceDataForPredicttion> attendanceDataForPredicttionList;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view_for_prediction);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Prediction");
        setSupportActionBar(toolbar);

        attendanceDataForPredicttionList = new ArrayList<>();
        recyclerView = (android.support.v7.widget.RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        try
        {
            String name,total,present,leaves;
            JSON_String_prediction = getIntent().getExtras().getString("json");
            jsonObject_prediction = new JSONObject(JSON_String_prediction);
            jsonArray_prediction = jsonObject_prediction.getJSONArray("details");

            attendanceDataForPredicttionList.add(new AttendanceDataForPredicttion("Subject", "RQ" ,"LL"));

            if(jsonArray_prediction.length() == 0)
            {
                Toast.makeText(getBaseContext(), "Attendance Is Being Updated, Please Contact Your Department", Toast.LENGTH_SHORT).show();
                finish();
            }
            for(int i = 0; i < jsonArray_prediction.length();i++)
            {
                JSONObject jos = jsonArray_prediction.getJSONObject(i);
                name = jos.getString("subjectname");
                total = jos.getString("total");
                present = jos.getString("present");
                leaves = jos.getString("leaves");

                attendanceDataForPredicttionList.add(new AttendanceDataForPredicttion(name,total,present,leaves));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        attendanceAdapterForPrediction = new AttendanceAdapterForPrediction(this, attendanceDataForPredicttionList);
        recyclerView.setAdapter(attendanceAdapterForPrediction);


    }
}
