package com.mmdu.erp.sms;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private ActionBar actionBar;
    private NavigationView navigationView;
    public AlertDialogManager alert = new AlertDialogManager();

    private TextView name, rollNo, semester,last_updated_on;

    private Calendar dateCurrent;
    private int year, month, day;

    String detailedViewJSON = null;
    String predictionJSON = null;
    String rollNumber = null;
    String startingDate = null;
    ProgressDialog pd;
    Dialog aboutUs, contactUs;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        aboutUs = new Dialog(this);
        contactUs = new Dialog(this);

        //navigation view concerns
        navigationView= findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);

        //for header in navigation bar, display user details
        name= (TextView) headerView.findViewById(R.id.name);
        rollNo= (TextView) headerView.findViewById(R.id.rollno);
        semester= (TextView) headerView.findViewById(R.id.semester);
        last_updated_on= (TextView) headerView.findViewById(R.id.last_updated_on);

        if(haveNetworkConnection()){
            new BackgroundTaskForLastUpdateDate().execute();
        }

        String info[] = PreferenceManager.getPreferenceManagerInstance(this).getUserDataFromSharedPreferences();
        name.setText(info[0]);


        switch (info[1])
        {
            case "1":
                semester.setText(info[1]+"st Semester");
                break;
            case "2":
                semester.setText(info[1]+"nd Semester");
                break;
            case "3":
                semester.setText(info[1]+"rd Semester");
                break;
            default:
                semester.setText(info[1]+"th Semester");
                break;
        }
        info = PreferenceManager.getPreferenceManagerInstance(this).getDataFromSharedPreferences();
        rollNo.setText(info[0]);
        //toolbar
        drawerLayout= findViewById(R.id.drawer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);

    }

    public void ShowPopup() {
        TextView txtclose;
        aboutUs.setContentView(R.layout.about_us);
        txtclose =(TextView) aboutUs.findViewById(R.id.txtclose);
        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aboutUs.dismiss();
            }
        });
        aboutUs.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        aboutUs.show();
    }

    public void showPopUpForContact() {
        TextView txtclose;
        contactUs.setContentView(R.layout.contact_us);
        txtclose =(TextView) contactUs.findViewById(R.id.txtclose);
        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactUs.dismiss();
            }
        });
        contactUs.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        contactUs.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //is executed when the back button is pressed
    @Override
    public void onBackPressed()
    {
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }

    public void feedback()
    {
        Intent gmailintent = new Intent (Intent.ACTION_SEND);
        gmailintent.setType("message/rfc822");
        gmailintent.putExtra(Intent.EXTRA_EMAIL, new String[]{"mmduattendanceportalfeedback@gmail.com"});
        gmailintent.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
        gmailintent.setPackage("com.google.android.gm");

        if (gmailintent.resolveActivity(getPackageManager())!= null)
            startActivity(gmailintent);
        else
        {

            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto: //address email"));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
            try {
                startActivity(intent);
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(getApplicationContext(), "Mail account not configured", Toast.LENGTH_SHORT).show();
            }

        }
    }



    //is called when an item in navigation drawer is pressed
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        int id= item.getItemId();
        Intent i;
        switch (id)
        {
            case R.id.starting_date:
                //showing the calendar at the current date
                dateCurrent= Calendar.getInstance();
                day= dateCurrent.get(Calendar.DAY_OF_MONTH);
                month= dateCurrent.get(Calendar.MONTH);
                year= dateCurrent.get(Calendar.YEAR);
                new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener()
                {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dateOfMonth)
                    {
                        monthOfYear+=1;
                        String date = year + "-" + monthOfYear + "-" + dateOfMonth;

                        PreferenceManager preferenceManager = PreferenceManager.getPreferenceManagerInstance(MainActivity.this);
                        String[] data = preferenceManager.getDataFromSharedPreferences();
                        rollNumber = data[0];
                        preferenceManager.writeLoginData(rollNumber,date);

                    }
                }, year, month, day).show();
                break;

            case R.id.logout:
                PreferenceManager preferenceManager = PreferenceManager.getPreferenceManagerInstance(MainActivity.this);
                preferenceManager.clearPreference();
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
                finish();
                break;
            case R.id.about_us:
                ShowPopup();
                break;
            case R.id.feedback:
                feedback();
                break;
            case R.id.contact:
                showPopUpForContact();
                break;
            case R.id.view_profile:
                if (haveNetworkConnection()) {
                    startActivity(new Intent(MainActivity.this, ViewProfile.class));
                }
                else
                {
                    Toast.makeText(getBaseContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
        }
        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    public boolean haveNetworkConnection() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if(info != null && info.isConnectedOrConnecting()){
            return true;
        }
        else{
            return false;
        }
    }


    public void getDataForDetailedView(View view)
    {
        if(haveNetworkConnection()) {
            new BackgroundTaskForDetailedView().execute();
        }
        else{
            alert.showAlertDialog(MainActivity.this, "No Internet Connection!!", "Please Check Your Internet", false);
        }
    }

    public void getDataForPrediction(View view)
    {
        if(haveNetworkConnection()) {
            new BackgroundTaskForPrediction().execute();
        }
        else{
            alert.showAlertDialog(MainActivity.this, "No Internet Connection!!", "Please Check Your Internet", false);
        }
    }

    public void getDataForCalendarView(View view)
    {
        String updated_date = "";
        if(haveNetworkConnection()) {
            Intent i = new Intent(MainActivity.this,MainActivityForCalendarView.class);
            startActivity(i);
        }
        else{

            alert.showAlertDialog(MainActivity.this, "No Internet Connection!!", "Please Check Your Internet", false);
        }

    }


    class BackgroundTaskForDetailedView extends AsyncTask<String,Void,String>
    {
        BackgroundTaskForDetailedView()
        {
            super();
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd=ProgressDialog.show(MainActivity.this,"","Please Wait",false);

        }

        @Override
        protected String doInBackground(String... params) {

            String Reg_url =""; //address of the server
            try {
                PreferenceManager preferenceManager = PreferenceManager.getPreferenceManagerInstance(MainActivity.this);
                String[] data = preferenceManager.getDataFromSharedPreferences();
                rollNumber = data[0];
                startingDate = data[1];
                String line;

                URL url = new URL(Reg_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String post_data = URLEncoder.encode("rollNumber", "UTF-8") + "=" + URLEncoder.encode(rollNumber, "UTF-8") + "&" +
                        URLEncoder.encode("date", "UTF-8") + "=" + URLEncoder.encode(startingDate, "UTF-8");

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
                        while ((line = br.readLine()) != null) {
                            sb.append(line + "\n");
                        }
                        br.close();
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
            if(result == null){
                Toast.makeText(getBaseContext(), "Something Went Wrong!!", Toast.LENGTH_SHORT).show();
            }
            else if(result.equals("404")){
                Toast.makeText(getBaseContext(), "Server is Down!! Come back later", Toast.LENGTH_SHORT).show();
            }
            else if(result.equals("Server Error !!")){
                Toast.makeText(getBaseContext(), "Server is Down!! Come back later", Toast.LENGTH_SHORT).show();
            }
            else {
                detailedViewJSON = result;
                Intent i = new Intent(MainActivity.this, RecyclerViewForDetailedView.class);
                i.putExtra("json", detailedViewJSON);
                startActivity(i);

            }
        }

    }

    class BackgroundTaskForPrediction extends AsyncTask<String, Void, String> {
        BackgroundTaskForPrediction() {
            super();
            pd=ProgressDialog.show(MainActivity.this,"","Please Wait",false);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String Reg_url = ""; //address of the server
            try {
                PreferenceManager preferenceManager = PreferenceManager.getPreferenceManagerInstance(MainActivity.this);
                String[] data = preferenceManager.getDataFromSharedPreferences();
                rollNumber = data[0];
                startingDate = data[1];

                String line_prediction = null;
                String date = "2018-07-01";
                URL url = new URL(Reg_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String post_data = URLEncoder.encode("rollNumber", "UTF-8") + "=" + URLEncoder.encode(rollNumber, "UTF-8") + "&" +
                        URLEncoder.encode("date", "UTF-8") + "=" + URLEncoder.encode(startingDate, "UTF-8");

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
                        while ((line_prediction = br.readLine()) != null) {
                            sb.append(line_prediction + "\n");
                        }
                        br.close();
                        return sb.toString().trim();
                    case 404:
                        return "404";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            pd.dismiss();
            return null;
        }
        @Override
        protected void onPostExecute(String result) {

            pd.dismiss();
            if(result == null){
                Toast.makeText(getBaseContext(), "Something Went Wrong!!", Toast.LENGTH_SHORT).show();
            }
            else if(result.equals("404")){
                Toast.makeText(getBaseContext(), "Server is Down!! Come back later", Toast.LENGTH_SHORT).show();
            }
            else if(result.equals("Server Error !!")){
                Toast.makeText(getBaseContext(), "Server is Down!! Come back later", Toast.LENGTH_SHORT).show();
            }
            else {
                predictionJSON = result;
                Intent i = new Intent(MainActivity.this, RecyclerViewForPrediction.class);
                i.putExtra("json", predictionJSON);
                startActivity(i);
            }
        }

    }

    class BackgroundTaskForLastUpdateDate extends AsyncTask<String, Void, String> {
        BackgroundTaskForLastUpdateDate() {
            super();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String Reg_url = ""; //address of the server
            try {
                PreferenceManager preferenceManager = PreferenceManager.getPreferenceManagerInstance(MainActivity.this);
                String[] data = preferenceManager.getDataFromSharedPreferences();
                rollNumber = data[0];

                String line_prediction = null;
                URL url = new URL(Reg_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String post_data = URLEncoder.encode("rollNumber", "UTF-8") + "=" + URLEncoder.encode(rollNumber, "UTF-8");

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
                        while ((line_prediction = br.readLine()) != null) {
                            sb.append(line_prediction + "\n");
                        }
                        br.close();
                        return sb.toString().trim();
                    default:
                        return null;
                }
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if(result == null){

            }
            else if(result.equals("NA")){

            }
            else{
                try{
                    String profileData[]= result.split("&");
                    last_updated_on.setText("Updated on "+profileData[0]);

                    PreferenceManager preferenceManager = PreferenceManager.getPreferenceManagerInstance(MainActivity.this);
                    preferenceManager.writeProfileData(profileData[1], profileData[2], profileData[3]);
                }
                catch (Exception e)
                {

                }

            }
        }

    }

}
