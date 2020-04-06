package com.back4app.quickstartexampleapp;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.app.Application;
import android.util.Log;

public class App extends Application {




    @Override
    public void onCreate() {
        super.onCreate();


        Parse.enableLocalDatastore(this);


        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId(getString(R.string.app_id))
                // if defined
                .clientKey(getString(R.string.client_key))
                .server(getString(R.string.server_url))
                .build()
        );






        /*

        ParseObject object=new ParseObject("ExampleObject");
        object.put("myNumber", "123");
        object.put("myString", "rob");


        object.saveInBackground(new SaveCallback () {
            @Override
            public void done(ParseException ex) {
                if (ex == null) {
                    Log.i("Parse Result", "Successful!");
                } else {
                    Log.i("Parse Result", "Failed" + ex.toString());
                }
            }
        });

         */


        ParseUser.enableAutomaticUser();

        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);

    }
}