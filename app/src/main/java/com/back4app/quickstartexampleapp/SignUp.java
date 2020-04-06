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
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.List;

public class SignUp extends AppCompatActivity {

    EditText username;
    EditText password;
    EditText repassword;
    EditText editTextBio;
    TextView error;
    boolean exist;

    public  void keyBoardDown(View view){
        if(view.getId()!=R.id.buttonSignUp&&view.getId()!=R.id.textViewAlreadytHaveAccount) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if(i==KeyEvent.KEYCODE_ENTER&& keyEvent.getAction()==KeyEvent.ACTION_DOWN){
            clickedSignUp(view);
        }
        return false;
    }

    public void clickedSignUp(View view){
        final String usernameString=username.getText().toString();
        final String passwordString=password.getText().toString();
        final String repasswordString=repassword.getText().toString();

        if(passwordString.equals(repasswordString)){
            exist=false;
            ParseQuery<ParseUser> query= ParseQuery.getQuery("username");
            query.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {
                    if(e==null){
                        if(objects.size()>0){
                            for (ParseUser object: objects){
                                if(object.getUsername()==usernameString){
                                    exist=true;
                                    break;
                                }
                            }
                        }
                        if(exist==true){
                            error.setText("* username not available");
                        }
                        else{
                            ParseUser user=new ParseUser();
                            user.setUsername(usernameString);
                            user.setPassword(passwordString);

                            user.signUpInBackground(new SignUpCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e==null){
                                        /*ParseUser.logInInBackground(usernameString, passwordString, new LogInCallback() {
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

                                         */

                                        String bio=editTextBio.getText().toString();
                                        try {
                                            ParseObject object = new ParseObject("Bio");
                                            object.put("Bio", bio);
                                            object.put("username", usernameString);
                                            object.saveInBackground(new com.parse.SaveCallback() {
                                                @Override
                                                public void done(com.parse.ParseException e) {
                                                    if (e == null) {
                                                        Log.i("Succeed", "Bio saved");
                                                    } else {
                                                        Log.i("Bio not saved", "unable to signup");
                                                    }
                                                }
                                            });
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                        }


                                        Intent intent=new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(intent);
                                        //intent add

                                        Log.i("Ok","SignedUp");
                                    }else{
                                        e.printStackTrace();
                                        //Toast.makeText(SignUp.this,"* username not available",Toast.LENGTH_SHORT).show();
                                        error.setText("* username not available");

                                    }
                                }
                            });
                        }
                    }
                }
            });

        }else{
            error.setText("* Re-enter Password");
        }

    }

    public  void clickedLogInText(View view){
        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        getSupportActionBar().hide(); //hide the title bar
        setContentView(R.layout.signup);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //show the activity in full screen

        username=findViewById(R.id.editTextUsername2);
        password=findViewById(R.id.editTextPassword2);
        repassword=findViewById(R.id.editTextRePassaword);
        editTextBio=findViewById(R.id.editTextBio);
        error=findViewById(R.id.textViewError2);

    }
}
