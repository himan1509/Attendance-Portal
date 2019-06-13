package com.mmdu.erp.sms;

import android.Manifest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.media.ImageReader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ViewProfile extends AppCompatActivity {

    ImageView imageView;
    TextView rollNo;
    String rollNum;
    TextView course;
    TextView fatherName;
    TextView email;
    TextView name1;
    Bitmap bitmap;
    ProgressDialog progressDialog;
    private static final int REQUEST_READ_CODE = 99;

    AlertDialogManager alert = new AlertDialogManager();

    String UPLOAD_URL= "http://14.139.236.66:8001/CSEP/uploadImage.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile2);

        name1= (TextView) findViewById(R.id.user_profile_name);
        rollNo = (TextView) findViewById(R.id.rollnoprofile);
        course = (TextView) findViewById(R.id.courseprofile);
        fatherName = (TextView) findViewById(R.id.fatherprofile);
        email = (TextView) findViewById(R.id.emailprofile);
        imageView = (ImageView) findViewById(R.id.imageView);

        Boolean hasPermission = (ContextCompat.checkSelfPermission(ViewProfile.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(ViewProfile.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_READ_CODE);
        }else {

        }

        String info[] = PreferenceManager.getPreferenceManagerInstance(this).getDataFromSharedPreferences();
        rollNo.setText("Roll Number: "+info[0]);
        rollNum= info[0];
        String name[]= PreferenceManager.getPreferenceManagerInstance(this).getUserDataFromSharedPreferences();
        name1.setText(name[0]);

        String profileData[]= PreferenceManager.getPreferenceManagerInstance(this).getProfileDataFromSharedPreferences();
        if (PreferenceManager.getPreferenceManagerInstance(this).checkProfile())
        {
            course.setText("Course: "+profileData[0]);
            fatherName.setText("Father's Name: "+profileData[1]);
            email.setText("Email Id: "+profileData[2]);
        }

        viewProfileImage();

        //opening image chooser option while tapping Image
        imageView.setOnClickListener(new View.OnClickListener(){

            public void onClick(View view) {

                selectImage();
            }});

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case REQUEST_READ_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    //reload my activity with permission granted or use the features what required the permission
                } else
                {
                    Toast.makeText(ViewProfile.this, "You Must Give Access to Storage", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }

    }

    public void viewProfileImage(){

        String imageUploadUrl = "http://14.139.236.66:8001/CSEP/students_Photos/"+rollNum+".jpg";

        progressDialog = new ProgressDialog(ViewProfile.this);
        progressDialog.setMessage("Fetching Data, Please Wait...");
        progressDialog.show();

        new DownloadImageTask(imageView).execute(imageUploadUrl);

    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        ImageView bmImage;

        private DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {

            String urldisplay = urls[0];
            Bitmap image = BitmapFactory.decodeResource(ViewProfile.this.getResources(),
                    R.drawable.default_image);
            try {
                // Find Image on Server
                InputStream in = new URL(urldisplay).openStream();
                // Decode Base64 String into Image
                image = BitmapFactory.decodeStream(in);
            }
            catch (FileNotFoundException e)
            {
                progressDialog.dismiss();
                ViewProfile.this.runOnUiThread(new Runnable() {
                    public void run() {
                        alert.showAlertDialog(ViewProfile.this, "Important !!", "Please Upload your Photo", false);
                    }
                });
            }
            catch (Exception e){
                progressDialog.dismiss();
                Toast.makeText(ViewProfile.this, "Something Went Wrong ->> Fetching Image !!", Toast.LENGTH_LONG).show();
            }

            return image;
        }

        protected void onPostExecute(Bitmap result) {
            // Set Image from Server
            bmImage.setImageBitmap(result);
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 999 && resultCode== RESULT_OK && data !=null){
            Uri filepath = data.getData();
            try {
                //Get image from gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filepath);

                //Set image to ImageView
                imageView.setImageBitmap(bitmap);

                imageView.setTag("Image Selected");

                UploadImage();

            } catch (IOException e) {
                Toast.makeText(ViewProfile.this, "An Error Occurred while Selection !!", Toast.LENGTH_LONG).show();
            }
            catch (Exception e) {
                Toast.makeText(ViewProfile.this, "Something Went Wrong ->> Selecting Image !!", Toast.LENGTH_LONG).show();
            }

        }
    }
    //Converting image into base64 string
    public String getStringImage(Bitmap bm){

        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,20,ba);
        byte[] imagebyte = ba.toByteArray();
        String encode = Base64.encodeToString(imagebyte,Base64.DEFAULT);

        return encode;

    }
    //Select Image from Phone
    public void selectImage(){

        new AlertDialog.Builder(this)
                .setTitle("Please Note:")
                .setMessage("Upload Only A Photo Where The Characters Of Your Face Are Clearly Visible")
                .setPositiveButton("Proceed",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent,"Select Picture"),999);

                            }
                        }).create().show();
    }

    //Image Uploading function
    public void UploadImage(){

        progressDialog = new ProgressDialog(ViewProfile.this);
        progressDialog.setMessage("Uploading, Please Wait...");
        progressDialog.show();

        //sending image to server
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.dismiss();

                if(response != null){

                    alert.showAlertDialog(ViewProfile.this, "Successful !!", "Photo Uploaded", false);
                }
                else{
                    alert.showAlertDialog(ViewProfile.this, "Unsuccessful !!", "Photo Upload Failed ! Try Again", false);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                progressDialog.dismiss();
                Toast.makeText(ViewProfile.this, "An Error Occurred, Please Try Again !! ", Toast.LENGTH_LONG).show();

            }
        }){
            //adding parameters to send
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String name = rollNum;
                String image = getStringImage(bitmap);
                Map<String ,String> params = new HashMap<String,String>();

                params.put("name",name);
                params.put("image",image);

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}