package com.mmdu.erp.sms;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

public class RecyclerViewForDetailedView extends AppCompatActivity {

    String JSON_String = null;
    JSONArray jsonArray;
    JSONObject jsonObject;

    android.support.v7.widget.RecyclerView recyclerView;
    AttendanceAdapter attendanceAdapter;
    List<AttendanceData> attendanceDataList;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view_for_detailed_view);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Subjectwise");
        setSupportActionBar(toolbar);

        attendanceDataList = new ArrayList<>();
        recyclerView = (android.support.v7.widget.RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        try
        {
            String name,present,absent;
            JSON_String = getIntent().getExtras().getString("json");
            jsonObject = new JSONObject(JSON_String);
            jsonArray = jsonObject.getJSONArray("details");

            attendanceDataList.add(new AttendanceData("Subject","PR","AB","%"));

            if (jsonArray.length() == 0)
            {
                Toast.makeText(getBaseContext(), "Attendance Is Being Updated, Please Contact Your Department", Toast.LENGTH_SHORT).show();
                finish();
            }
            for(int i = 0; i < jsonArray.length();i++)
            {
                JSONObject jos = jsonArray.getJSONObject(i);
                name = jos.getString("subjectname");
                present = jos.getString("present");
                absent = jos.getString("absent");

                Integer total = Integer.parseInt(present)+ parseInt(absent);
                Float percentage = Integer.parseInt(present)/Float.parseFloat(total.toString()) *100 ;

                attendanceDataList.add(new AttendanceData(name,present,absent,new DecimalFormat("##.#").format(percentage).toString()));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);
        attendanceAdapter = new AttendanceAdapter(this,attendanceDataList);
        recyclerView.setAdapter(attendanceAdapter);
    }
}
