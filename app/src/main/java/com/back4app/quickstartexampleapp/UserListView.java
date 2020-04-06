package com.back4app.quickstartexampleapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class UserListView extends AppCompatActivity {

     ArrayList<String> Users = new ArrayList<>();
     ArrayAdapter arrayAdapter;
/*    SQLiteDatabase  DB;

    public static class card {

        private String title;
        private String description;

        //constructor
        public card(String title,String description){

            this.title = title;
            this.description = description;
        }

        //getters
        public String getTitle() {return title; }
        public String getDescription() {return description; }
    }


    public class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return Users.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.user_list_item, null);
            TextView title = (TextView) convertView.findViewById(R.id.textViewProfilename);
            TextView description = (TextView) convertView.findViewById(R.id.textViewCaption);


            title.setText(Users.get(position).getTitle());
            int descriptionLength = Users.get(position).getDescription().length();
            if (descriptionLength >= 100) {
                String descriptionTrim = Users.get(position).getDescription().substring(0, 100) + "...";
                description.setText(descriptionTrim);
            } else {
                description.setText(Users.get(position).getDescription());
            }
                return convertView;
            }
        }


    public void Download(){
        ParseQuery<ParseUser> query= ParseUser.getQuery();
        query.whereNotEqualTo("username",ParseUser.getCurrentUser().getUsername());
        query.addAscendingOrder("username");
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e==null){
                    DB.execSQL("DELETE FROM DB");
                    if(objects.size()>0){
                        for (ParseUser user:objects){
                            //Users.add(user.getUsername());

                            String sqlString="INSERT INTO DB (profiletitle , profiledescription) VALUES (?,?)";
                            SQLiteStatement statement=  DB.compileStatement(sqlString);
                            statement.bindString(1,user.getUsername());
                            statement.bindString(2,user.getUsername());

                            statement.execute();

                        }
                    }
                }else {
                    e.printStackTrace();
                }
            }
        });

    }


    public void updateListView() {
        Cursor c =  DB.rawQuery("SELECT * FROM DB", null);

        int titleIndex = c.getColumnIndex("profiletitle");
        int descriptionIndex = c.getColumnIndex("profiledescription");

        if (c.moveToFirst()){
            Users.clear();
            do{
                Users.add(new card(c.getString(titleIndex),c.getString(descriptionIndex)));
            } while (c.moveToNext());
            arrayAdapter.notifyDataSetChanged();
        }
    }


 */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list_view);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar_layout);
        View view =getSupportActionBar().getCustomView();

        ImageButton imageButton= (ImageButton)view.findViewById(R.id.action_bar_back);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), sharePhotos.class);
                startActivity(intent);
            }
        });

        ImageButton imageButton2= (ImageButton) view.findViewById(R.id.action_bar_forward);
        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ParseUser.logOut();
                startActivity(intent);
            }
        });

        ImageButton imageButton3= (ImageButton) view.findViewById(R.id.editProfile);
        imageButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), UserImages.class);
                intent.putExtra("username",ParseUser.getCurrentUser().getUsername());
                startActivity(intent);
            }
        });


/*
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.logo);
 */
        final ListView listView = findViewById(R.id.listView);
        Users=new ArrayList();

       /* DB=this.openOrCreateDatabase("DB",MODE_PRIVATE,null);
        DB.execSQL("CREATE TABLE IF NOT EXISTS DB(id INTEGER PRIMARY KEY, profiletitle VARCHAR, profiledescription VARCHAR)");




        Intent intent=getIntent();
        String usernameString=intent.getStringExtra("username");
        String passwordString=intent.getStringExtra("password");
        ParseUser.logInInBackground(usernameString, passwordString, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if (e == null) {
                            Download();
                        }else {
                            Log.i("Login error","unable to connect");
                        }
                    }
                });

         arrayAdapter=new CustomAdapter();
        */

        Intent intent=getIntent();
        try {
            ParseUser.logIn(intent.getStringExtra("username"), intent.getStringExtra("password"));
            ParseQuery < ParseUser > query = ParseUser.getQuery();
            query.whereNotEqualTo("username",ParseUser.getCurrentUser().getUsername());
            query.addAscendingOrder("username");
            query.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {
                    if(e==null){
                        if(objects.size()>0){
                            for (ParseUser user:objects){
                                Users.add(user.getUsername());
                            }
                            Log.i("users after for",Integer.toString(Users.size()));

                            arrayAdapter = new ArrayAdapter(UserListView.this, android.R.layout.simple_list_item_1, Users);
                            listView.setAdapter(arrayAdapter);

                        }

                    }else {
                        e.printStackTrace();
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }



        Log.i("users arrayadapter",Integer.toString(Users.size()));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent=new Intent(getApplicationContext(), ChatActivity2.class);
                intent.putExtra("username",Users.get(position));
                intent.putExtra("userno",position);
                startActivity(intent);

            }
        });//updateListView();
    }


/*

    //MENU

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId()==R.id.Add){
            Intent intent=new Intent(getApplicationContext(), ChatActivity2.class);
            startActivity(intent);
            return true;
        }else if(item.getItemId()==R.id.Logout){
            Intent intent=new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            ParseUser.logOut();
            startActivity(intent);
            return true;
        }
        return false;
    }

 */

}
