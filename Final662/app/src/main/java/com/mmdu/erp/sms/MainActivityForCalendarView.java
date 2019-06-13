package com.mmdu.erp.sms;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import static android.widget.Toast.makeText;

public class MainActivityForCalendarView extends AppCompatActivity {

    String calendar_data = null;
    HashSet<CalendarDay> presentDate = new HashSet<CalendarDay>();
    HashSet<CalendarDay> absentDate = new HashSet<CalendarDay>();
    HashSet<CalendarDay> partialPresent = new HashSet<CalendarDay>();
    public ArrayList<Date> arrayListForDates = new ArrayList<>();
    ArrayList<LinkedHashMap<String,String>> listOfData = new ArrayList<LinkedHashMap<String, String>>();
    MaterialCalendarView materialCalendarView;
    String rollNumber = null;
    String startingdate = null;
    PreferenceManager preferenceManager ;

    ProgressDialog pd;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_for_calendar_view);

        materialCalendarView = (MaterialCalendarView)findViewById(R.id.calendarView);
        PreferenceManager preferenceManager = PreferenceManager.getPreferenceManagerInstance(this);

        preferenceManager = PreferenceManager.getPreferenceManagerInstance(this);
        String data[] = preferenceManager.getDataFromSharedPreferences();
        rollNumber = data[0];
        startingdate = data[1];

        new BackgroundTask().execute(rollNumber,startingdate);

        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                Intent i = new Intent(MainActivityForCalendarView.this,RecyclerViewForParticularDate.class);

                if(arrayListForDates.contains(date.getDate())) {
                    LinkedHashMap<String,String> local = listOfData.get(arrayListForDates.indexOf(date.getDate()));
                    Set<String> keys = local.keySet();
                    ArrayList<String> forIntent = new ArrayList<String>();
                    for (String key : keys) {
                        forIntent.add(key + "\n" + local.get(key));
                    }
                    i.putStringArrayListExtra("main", forIntent);
                    startActivity(i);
                    pd.dismiss();
                }
                else
                {
                    makeText(getApplicationContext(),
                            "It was Holiday",Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    class BackgroundTask extends AsyncTask<String,Void,String>
    {
        BackgroundTask()
        {
            super();
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd=ProgressDialog.show(MainActivityForCalendarView.this,"","Please Wait",false);
        }

        @Override
        protected String doInBackground(String... params) {
            String Reg_url ="http://14.139.236.66:8001/CSEP/CalendarView.php";
            try {
                String rollNumber = params[0];
                String date = params[1];
                URL url = new URL(Reg_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String post_data = URLEncoder.encode("rollNumber", "UTF-8") + "=" + URLEncoder.encode(rollNumber, "UTF-8") + "&" +
                        URLEncoder.encode("date", "UTF-8") + "=" + URLEncoder.encode(date, "UTF-8");

                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                int status = httpURLConnection.getResponseCode();

                switch (status) {
                    case 200:
                    case 201:
                        BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        while ((calendar_data = br.readLine()) != null) {
                            sb.append(calendar_data + "\n");
                        }
                        br.close();
                        calendar_data = null;
                        return sb.toString().trim();
                    case 404:
                        return "404";

                }
            }catch (Exception e) {
                e.printStackTrace();
            }
            pd.dismiss();
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            pd.dismiss();

            if (result == null) {
                Toast.makeText(getBaseContext(), "Something Went Wrong!!", Toast.LENGTH_SHORT).show();
                finish();
            } else if ((result.equals("404"))) {
                Toast.makeText(getBaseContext(), "Server is Down!! Come back later", Toast.LENGTH_SHORT).show();
                finish();
            } else if ((result.equals("Server Error !!"))) {
                Toast.makeText(getBaseContext(), "Server is Down!! Come back later", Toast.LENGTH_SHORT).show();
                finish();
            }
             else {

                calendar_data = result;
                MainActivityForCalendarView.this.addData();
            }
        }

    }

    public void addData()
    {
        try
        {
            JSONObject jsonObject = new JSONObject(calendar_data); //has the data of from Database
            JSONArray jsonArray = jsonObject.getJSONArray("calendar"); //Has the array that contains all data
            String subject = null;
            String status = null;
            LinkedHashMap<String,String> localmap = new LinkedHashMap<String, String>(); // Stores subject and status in Key value format

            if (jsonArray.length() == 0)
            {
                Toast.makeText(getBaseContext(), "Attendance Is Being Updated, Please Contact Your Department", Toast.LENGTH_SHORT).show();
                finish();
            }

            for(int i = 0; i < jsonArray.length();i++)
            {
                JSONObject jos = jsonArray.getJSONObject(i);
                String date1 = jos.getString("date");
                date1 = date1.substring(9,19);

                Date date=new SimpleDateFormat("yyyy-MM-dd").parse(date1); // converting data in our usable format.

                int noOfLectures = Integer.parseInt(jos.getString("noOfLecture")); // Getting no of lectures so to iterate for that much time
                localmap = new LinkedHashMap<String, String>();
                for(int j = 0; j < noOfLectures; j++)
                {
                    subject = jos.getString(new Integer(j).toString()); //Getting subject name
                    status = jos.getString(new Integer(j+noOfLectures).toString()); // Getting status
                    if(status.equals("1")) //if true it is absent
                        localmap.put(subject,"AB");
                    else if(status.equals("3"))
                    {
                        localmap.put(subject,"PR");
                    }
                    else
                    {
                            //do nothing
                    }
                }
                listOfData.add(localmap); //listofData is a ArrayList of LinkedHashMap. localMap has the data i.e. key value pairs of subject and status of all subjects of particular date
                arrayListForDates.add(date);//simple arraylist of dates to store the information of dates
                if(listOfData.get(listOfData.size() - 1).containsValue("AB") && listOfData.get(listOfData.size() - 1).containsValue("PR")) // getting the status of the last entered LinkedHaspMap
                {
                    partialPresent.add(CalendarDay.from(date));
                }
                else if(listOfData.get(listOfData.size()-1).containsValue("PR"))
                {
                    presentDate.add(CalendarDay.from(date));
                }
                else
                {
                    absentDate.add(CalendarDay.from(date));
                }
            }

            Date startingDate = arrayListForDates.get(0);
            Date endingDate = arrayListForDates.get(arrayListForDates.size() - 1);

            materialCalendarView.state().edit()
                    .setFirstDayOfWeek(Calendar.MONDAY)
                    .setMinimumDate(CalendarDay.from(startingDate))
                    .setMaximumDate(CalendarDay.from(endingDate))
                    .setCalendarDisplayMode(CalendarMode.MONTHS)
                    .commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        //Different decorates for decorating differently in the calendar view
        materialCalendarView.addDecorator(new AbsentDecorator(absentDate,getBaseContext()));
        materialCalendarView.addDecorator(new PresentDecorator(presentDate,getBaseContext()));
        materialCalendarView.addDecorator(new PartialPresentDecorator(partialPresent,getBaseContext()));

    }
}

class AbsentDecorator implements DayViewDecorator
{
    private final HashSet<CalendarDay> dates;
    Context ctx;
    public AbsentDecorator(Collection<CalendarDay> dates, Context ctx) {
        this.dates = new HashSet<>(dates);
        this.ctx = ctx;
    }
    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.red));
    }
}

class PresentDecorator implements DayViewDecorator
{
    private final HashSet<CalendarDay> dates;
    Context ctx;
    public PresentDecorator(Collection<CalendarDay> dates,Context ctx) {
        this.dates = new HashSet<>(dates);
        this.ctx = ctx;
    }
    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.green));
    }
}

class PartialPresentDecorator implements DayViewDecorator
{
    private final HashSet<CalendarDay> dates;
    Context ctx;
    public PartialPresentDecorator(Collection<CalendarDay> dates,Context ctx) {
        this.dates = new HashSet<>(dates);
        this.ctx = ctx;
    }
    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.blue));
    }
}
