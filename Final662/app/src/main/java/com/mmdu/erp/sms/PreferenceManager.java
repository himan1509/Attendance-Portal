package com.mmdu.erp.sms;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * <<<<<  Saving the Onboarding screen , So that Next Time It Doesn't Appear  >>>>>>
 */

public class PreferenceManager {

    private Context context;
    private SharedPreferences sharedPreferences;
    private static PreferenceManager preferenceManager;

    private PreferenceManager(Context context)
    {
        this.context = context;
        getSharedPreferences();
    }

    public static PreferenceManager getPreferenceManagerInstance(Context context)
    {
            if(preferenceManager==null)
                 preferenceManager = new PreferenceManager(context);

            return preferenceManager;
    }

    private void getSharedPreferences()
    {
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.my_preference),Context.MODE_PRIVATE);
    }

    public void writePreference()
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.my_preference_key),"INIT_OK");
        editor.commit();
    }

    public boolean checkPreference()
    {
        boolean status = false;
        if(sharedPreferences.getString("rollNumber","null").equals("null"))
        {
            status = false;
        }
        else
        {
            status = true;
        }
        return status;
    }

    public boolean checkProfile()
    {
        boolean status = false;
        if(sharedPreferences.getString("course","null").equals("null"))
        {
            status = false;
        }
        else
        {
            status = true;
        }
        return status;
    }

    public void clearPreference()
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }



    public void writeLoginData(String rollNumber,String date)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("rollNumber",rollNumber);
        editor.commit();

        editor.putString("starting_date",date);
        editor.commit();
    }
     public void WriteUserData(String name, String sem)
     {
         SharedPreferences.Editor editor = sharedPreferences.edit();

         editor.putString("name",name);
         editor.commit();

         editor.putString("sem",sem);
         editor.commit();
     }

     public void writeProfileData(String course, String fatherName, String email)
     {
         SharedPreferences.Editor editor= sharedPreferences.edit();

         editor.putString("course", course);
         editor.commit();
         editor.putString("fatherName", fatherName);
         editor.commit();
         editor.putString("email", email);
         editor.commit();
     }

    public String[] getDataFromSharedPreferences()
    {
        String data[] = new String[2];

        data[0] = sharedPreferences.getString("rollNumber","null");
        data[1] = sharedPreferences.getString("starting_date","null");

        return data;
    }

    public String[] getUserDataFromSharedPreferences()
    {
        String data[] = new String[2];

        data[0] = sharedPreferences.getString("name","null");
        data[1] = sharedPreferences.getString("sem","null");

        return data;
    }

    public String[] getProfileDataFromSharedPreferences()
    {
        String data[]= new String[3];
        data[0]= sharedPreferences.getString("course", "null");
        data[1]= sharedPreferences.getString("fatherName","null");
        data[2]= sharedPreferences.getString("email", "null");

        return data;
    }
}

