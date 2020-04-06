package com.back4app.quickstartexampleapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class UserImages extends AppCompatActivity {

    LinearLayout linearLayout;
    TextView profileName;
    TextView textViewBio;
    String bioInfo="";
    Thread thread;
    String username;
    HorizontalScrollView hl;
    TextView notification;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        getSupportActionBar().hide(); //hide the title bar
        setContentView(R.layout.activity_user_images);


        linearLayout=findViewById(R.id.linearLayout);
        profileName=findViewById(R.id.profileName);
        textViewBio=findViewById(R.id.bio);
        hl=findViewById(R.id.hsv);
        notification=findViewById(R.id.notification);

        thread = new Thread() {

            @Override
            public void run() {
                try {
                    while (!thread.isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // update TextView here!


                                Intent intent = getIntent();
                                ImageButton imageButton = findViewById(R.id.imageButton);
                                final String username = intent.getStringExtra("username");
                                if (username.equals(ParseUser.getCurrentUser().getUsername())) {
                                    imageButton.setVisibility(View.VISIBLE);
                                    imageButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intent = new Intent(getApplicationContext(), EditProfile.class);
                                            startActivity(intent);

                                        }
                                    });

                                } else {
                                    imageButton.setVisibility(View.INVISIBLE);
                                    imageButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                        }
                                    });
                                }
                                ParseQuery<ParseObject> query = new ParseQuery("Bio");
                                query.whereEqualTo("username", username);
                                query.orderByDescending("createdAt");
                                query.findInBackground(new FindCallback<ParseObject>() {
                                    @Override
                                    public void done(List<ParseObject> objects, ParseException e) {
                                        if (e == null) {
                                            if (objects.size() > 0) {
                                                for (ParseObject object : objects) {
                                                    bioInfo = object.getString("Bio");
                                                    textViewBio.setText(bioInfo);
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                });


                                profileName.setText(username);
                                //textViewBio.setText(intent.getStringExtra("Bio"));

                                ParseQuery<ParseObject> query2 = new ParseQuery("Myimage");
                                query2.whereEqualTo("username", username);
                                query2.orderByDescending("createdAt");
                                query2.findInBackground(new FindCallback<ParseObject>() {
                                    @Override
                                    public void done(List<ParseObject> objects, ParseException e) {
                                        if (e == null) {
                                            if (objects.size() > 0) {
                                                for (ParseObject object : objects) {
                                                    ParseFile file = (ParseFile) object.get("image");
                                                    file.getDataInBackground(new GetDataCallback() {
                                                        @Override
                                                        public void done(byte[] data, ParseException e) {
                                                            if (e == null && data != null) {
                                                                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                                                ImageView dp = findViewById(R.id.dp);
                                                                dp.setImageBitmap(bitmap);
                                                            }

                                                        }

                                                    });
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                });


                                ParseQuery<ParseObject> query1=new ParseQuery("Image");
                                query1.whereEqualTo("username",username);
                                query1.orderByDescending("createdAt");
                                query1.findInBackground(new FindCallback<ParseObject>() {
                                    @Override
                                    public void done(List<ParseObject> objects, ParseException e) {
                                        if (e == null) {
                                            if (objects.size() > 0) {
                                                hl.setVisibility(View.VISIBLE);
                                                notification.setVisibility(View.INVISIBLE);
                                                for (ParseObject object : objects) {
                                                    ParseFile file = (ParseFile) object.get("image");
                                                    file.getDataInBackground(new GetDataCallback() {
                                                        @Override
                                                        public void done(byte[] data, ParseException e) {
                                                            if(e==null&&data!=null) {
                                                                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                                                                ImageView imageView=new ImageView(getApplicationContext());
                                                                imageView.setLayoutParams(new ViewGroup.LayoutParams(
                                                                        ViewGroup.LayoutParams.WRAP_CONTENT,
                                                                        ViewGroup.LayoutParams.WRAP_CONTENT
                                                                ));
                                                                imageView.setPadding(10,10,10,10);
                                                                imageView.setImageBitmap(bitmap);
                                                                linearLayout.addView(imageView);
                                                            }

                                                        }

                                                    });
                                                }
                                            }else{
                                                hl.setVisibility(View.INVISIBLE);
                                                notification.setVisibility(View.VISIBLE);
                                                if (username.equals(ParseUser.getCurrentUser().getUsername())) {
                                                    notification.setText("Share Your Moments to show here");
                                                }else{
                                                    notification.setText("When "+username+" will share something,\nyou will see here");
                                                }
                                            }
                                        }
                                    }
                                });

                            }
                        });
                    }
                }
                catch (InterruptedException e) {
                }
            }
        };
        thread.start();

    }
}
