package com.back4app.quickstartexampleapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ChatActivity2 extends AppCompatActivity {

    ListView chatsListView;
    int posi;
    String activeUser="";
     ArrayList<ChatBox> Chats1 = new ArrayList<>();
     CustomAdapter chatArrayAdapter;
    String msg;

    public  void keyBoardDown(View view){
        try {
            if (view.getId() != R.id.hl) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void scrollMyListViewToBottom() {
        chatsListView.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                chatsListView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });
    }
    

    public  class ChatBox {

        private String messageText;
        private String time;
        private String senderName ;
        private String receiverName ;
        //constructor
        public ChatBox(String messageText, String time, String senderName,String receiverName){

            this.messageText = messageText;
            this.time = time;
            this.senderName = senderName;
            this.receiverName = receiverName;
        }

        //getters
        public String getMessageText() {return messageText; }
        public String getTime() {return time; }
        public String getSenderName() {return senderName; }
        public String getReceiverName() {return receiverName; }
    }


    public class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return Chats1.size();
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
            TextView text_message_body;
            TextView text_message_time;
            if(Chats1.get(position).getSenderName().equals(ParseUser.getCurrentUser().getUsername())&&Chats1.get(position).getReceiverName().equals(activeUser)){
                convertView=getLayoutInflater().inflate(R.layout.item_message_sent,null);

            }else {
                if (Chats1.get(position).getSenderName().equals(activeUser) && Chats1.get(position).getReceiverName().equals(ParseUser.getCurrentUser().getUsername())) {
                        convertView = getLayoutInflater().inflate(R.layout.item_message_received, null);
                    }
                }

                    //display trimmed excerpt for msg body
           /* int msgLength = Chats1.get(position).getMessageText().length();
            if(msgLength >= 1000){
                String msgTrim = Chats1.get(position).getMessageText().substring(0, 1000) + "...";
                text_message_body.setText(msgTrim);
            }else{
                text_message_body.setText(Chats1.get(position).getMessageText());
            }

            */
                    text_message_body =(TextView)convertView.findViewById(R.id.text_message_body);
                    text_message_time=(TextView)convertView.findViewById(R.id.text_message_time);
                    text_message_body.setText(Chats1.get(position).getMessageText());
                    text_message_time.setText(Chats1.get(position).getTime());

                    return convertView;
            }
    }

    String time;
    public  void send(View view){
        final EditText typeChat=findViewById(R.id.typeChat);
        ParseObject message=new ParseObject("Message");
        message.put("sender", ParseUser.getCurrentUser().getUsername());
        time=new SimpleDateFormat("HH:mm", Locale.getDefault()).format(Calendar.getInstance().getTime());
        message.put("senttime",time);
        message.put("recipient",activeUser);
        msg=typeChat.getText().toString();
        message.put("message",msg);

        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e==null){
                    Chats1.add(new ChatBox(msg,time,ParseUser.getCurrentUser().getUsername(),activeUser));
                    chatArrayAdapter.notifyDataSetChanged();
                    typeChat.setText("");
                    //Toast.makeText(ChatActivity2.this,msg,Toast.LENGTH_SHORT).show();
                    scrollMyListViewToBottom();
                }
            }
        });

    }
    Thread thread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent=getIntent();
        activeUser=intent.getStringExtra("username");
        posi=intent.getIntExtra("userno",-1);
        setTitle(activeUser);
        Log.i("Info: active user",activeUser);

        chatsListView = findViewById(R.id.chatHistory);
        chatArrayAdapter = new CustomAdapter();
        chatsListView.setAdapter(chatArrayAdapter);

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


                                ParseQuery< ParseObject > query1 = new ParseQuery<ParseObject>("Message");
                                query1.whereEqualTo("sender",ParseUser.getCurrentUser().getUsername());
                                query1.whereEqualTo("recipient",activeUser);

                                ParseQuery< ParseObject > query2 = new ParseQuery<ParseObject>("Message");
                                query2.whereEqualTo("recipient",ParseUser.getCurrentUser().getUsername());
                                query2.whereEqualTo("sender",activeUser);

                                List<ParseQuery<ParseObject>> queries=new ArrayList<ParseQuery<ParseObject>>();
                                queries.add(query1);
                                queries.add(query2);

                                ParseQuery<ParseObject> query=ParseQuery.or(queries);
                                query.orderByAscending("createdAt");
                                query.findInBackground(new FindCallback<ParseObject>() {
                                    @Override
                                    public void done(List<ParseObject> objects, ParseException e) {
                                        if(e==null){
                                            if(objects.size()>0){
                                                Chats1.clear();
                                                for (ParseObject message:objects){
                                                    String messageContent=message.getString("message");
                                                    Chats1.add(new ChatBox(messageContent, message.getString("senttime"),message.getString("sender"),message.getString("recipient")));
                                                }
                                                chatArrayAdapter.notifyDataSetChanged();
                                            }
                                        }else {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                scrollMyListViewToBottom();

                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        thread.start();

/*
        chatsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(ChatActivity2.this)
                        .setIcon(R.drawable.delete).setTitle("Delete this message")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Chats1.remove(position);
                                chatArrayAdapter.notifyDataSetChanged();
                            }


                        })
                        .setNegativeButton("No",null)
                        .show();
                return true;
            }
        });
        
 */

    }




    //MENU

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.chatactivitymenu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId()==R.id.photos){

            Intent intent=new Intent(getApplicationContext(), UserImages.class);
            intent.putExtra("username",activeUser);
            startActivity(intent);
            return true;
        }else if(item.getItemId()==R.id.delete_chat){
            ParseQuery<ParseObject> q1= new ParseQuery<ParseObject>("Message");
            q1.whereEqualTo("sender",ParseUser.getCurrentUser().getUsername());
            q1.whereEqualTo("recipient",activeUser);
            q1.findInBackground(new FindCallback<ParseObject>() {
                                    @Override
                                    public void done(final List<ParseObject> user, ParseException e) {
                                        if (e == null) {
                                            for (ParseObject object : user) {
                                                object.deleteInBackground();
                                                object.saveInBackground();
                                            }
                                        }
                                    }
                                });
            ParseQuery<ParseObject> q2= new ParseQuery<ParseObject>("Message");
            q2.whereEqualTo("sender",activeUser);
            q2.whereEqualTo("recipient",ParseUser.getCurrentUser().getUsername());
            q2.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(final List<ParseObject> user, ParseException e) {
                    if (e == null) {
                        for (ParseObject object : user) {
                            object.deleteInBackground();
                            object.saveInBackground();
                        }
                    }
                }
            });
            Chats1.clear();
            chatArrayAdapter.notifyDataSetChanged();

            return true;
        }else if(item.getItemId()==R.id.back){
            finish();
        }
        return false;
    }

}
