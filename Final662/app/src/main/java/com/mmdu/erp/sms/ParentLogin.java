package com.mmdu.erp.sms;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import butterknife.ButterKnife;

public class ParentLogin extends AppCompatActivity {

    private static final int REQUEST_SIGNUP = 0;
    Button startingDate;
    EditText _usernameText;
    EditText _passwordText;
    Button _loginButton,parentLogin;
    ProgressDialog pd;

    private DatePickerDialog.OnDateSetListener dateSetListener;

    PreferenceManager preferenceManager;

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    // Session Manager Class

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_login);
        ButterKnife.bind(this);

        _usernameText = (EditText)findViewById(R.id.input_username);
        _passwordText = (EditText)findViewById(R.id.input_password);
        _loginButton = (Button) findViewById(R.id.btn_login);

        preferenceManager = PreferenceManager.getPreferenceManagerInstance(this);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    public void login() {
        if (validate()) {
            _loginButton.setEnabled(false);
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        // Staring MainActivity

    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login Failed !!", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;


        // Get username, password from EditText
        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();

        // Check if username, password is filled
        if(username.trim().length() > 0 && password.trim().length() > 0){
            new BackgroundTaskForLogin().execute(username);
        }
        else
        {
            // user didn't entered username or password
            // Show alert asking him to enter the details
            alert.showAlertDialog(ParentLogin.this, "Login Failed  !!", "Please enter Username and Password", false);
        }
        if (username.isEmpty()) {
            _usernameText.setError("Enter Username");
            valid = false;
        } else {
            _usernameText.setError(null);
        }

        if (password.isEmpty()) {
            _passwordText.setError("Enter Password");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }


    class BackgroundTaskForLogin extends AsyncTask<String, Void, String> {
        BackgroundTaskForLogin() {
            super();
            pd= ProgressDialog.show(ParentLogin.this,"","Please Wait",false);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String Reg_url = ""; //adddress of the server
            try {
                String rollNumber = params[0];
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
                _loginButton.setEnabled(true);
                alert.showAlertDialog(ParentLogin.this, "Server Down !!", "Something Went Wrong", false);
            }
            else if(result.equals("404")){
                _loginButton.setEnabled(true);
                alert.showAlertDialog(ParentLogin.this, "Server Down !!", "Something Went Wrong", false);
            }
            else if((result.equals("Server Error !!"))){
                alert.showAlertDialog(ParentLogin.this, "SERVER DOWN", "Please Try Again Later", false);
                _loginButton.setEnabled(true);
            }
            else
            {
                String logindata[] = result.split("&");
                if (logindata[3].equals("NA")) {
                    _loginButton.setEnabled(true);
                    alert.showAlertDialog(ParentLogin.this, "Login Failed  !!", "This Roll Number is not Valid please enter valid Roll Number", false);
                }
                else if (logindata[3].equals(_passwordText.getText().toString())) {
                    preferenceManager.writeLoginData(_usernameText.getText().toString(), logindata[4]);
                    preferenceManager.WriteUserData(logindata[0], logindata[2]);

                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                    finish();
                }
                else
                {
                    _loginButton.setEnabled(true);
                    alert.showAlertDialog(ParentLogin.this, "Login Failed  !!", "Password For the Username didn't Match Try Again!!!", false);
                }
            }

        }

    }

    public void backToStudentLogin(View view)
    {
        finish();
    }
}
