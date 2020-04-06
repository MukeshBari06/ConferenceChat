package com.back4app.quickstartexampleapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnKeyListener{

    public  void keyBoardDown(View view){
        if(view.getId()!=R.id.buttonLogIn&&view.getId()!=R.id.textViewDontHaveAccount) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if(i==KeyEvent.KEYCODE_ENTER&& keyEvent.getAction()==KeyEvent.ACTION_DOWN){
            clickedLogIn(view);
        }
        return false;
    }

    EditText username;
    EditText password;
    TextView error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        getSupportActionBar().hide(); //hide the title bar
        setContentView(R.layout.activity_main);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //show the activity in full screen

        username=findViewById(R.id.editTextUsername);
        password=findViewById(R.id.editTextPassword);
        error=findViewById(R.id.textViewerror);

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
        
    }


    public  void clickedSignUpText(View view){
        Intent intent=new Intent(getApplicationContext(),SignUp.class);
        startActivity(intent);
    }

    public void clickedLogIn(View view){
        final String usernameString=username.getText().toString().trim();
        final String passwordString=password.getText().toString();
         ParseUser.logInInBackground(usernameString, passwordString, new LogInCallback() {
             @Override
             public void done(ParseUser user, ParseException e) {
                 if(e==null){
                     Intent intent=new Intent(getApplicationContext(),UserListView.class);
                     intent.putExtra("username",usernameString);
                     intent.putExtra("password",passwordString);
                     ParseUser.logOut();

                     startActivity(intent);
                 }else{
                     e.printStackTrace();
                     error.setText("*authentication error, check username and password");
                 }
             }
         });

    }

}
