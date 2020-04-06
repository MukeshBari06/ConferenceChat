package com.back4app.quickstartexampleapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class EditProfile extends AppCompatActivity {

    EditText editBio;
    ImageView imageProfile;


    public void keyBoardDown(View view) {
        if (view.getId() != R.id.editDP && view.getId() != R.id.buttonSave) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            clickedSave(view);
        }
        return false;
    }
    public void clickedSaveDP(View view){
        getPhoto();
    }

    public void clickedSave(View view){
        String bio=editBio.getText().toString();
        try {
            ParseObject object = new ParseObject("Bio");
            object.put("Bio", bio);
            object.put("username", ParseUser.getCurrentUser().getUsername());
            object.saveInBackground(new com.parse.SaveCallback() {
                @Override
                public void done(com.parse.ParseException e) {
                    if (e == null) {
                        Toast.makeText(EditProfile.this, "Saved Successfully :)", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(EditProfile.this, "Unable to save", Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }catch (Exception e) {
            e.printStackTrace();
        }
        finish();


    }



    private void pickFromGallery() {
        //Create an Intent with action as ACTION_PICK
        Intent intent = new Intent(Intent.ACTION_PICK);
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.setType("image/*");
        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        // Launching the Intent
        startActivityForResult(intent, 1);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Result code is RESULT_OK only if the user selects an Image
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case 1:
                    //data.getData returns the content URI for the selected Image
                    Uri selectedImage = data.getData();
                    imageProfile.setImageURI(selectedImage);
                    //
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteArray = stream.toByteArray();
                        ParseFile file = new ParseFile("image.png", byteArray);

                        ParseObject object = new ParseObject("Myimage");
                        object.put("image", file);
                        object.put("username", ParseUser.getCurrentUser().getUsername());
                        object.saveInBackground(new com.parse.SaveCallback() {
                            @Override
                            public void done(com.parse.ParseException e) {
                                if (e == null) {
                                    Toast.makeText(EditProfile.this, "image saved", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(EditProfile.this, "Unable to save to server", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });

                    } catch (Exception e) {
                        Log.i("Error ", "Unable to fetch image");
                    }

                    //
                    break;
            }
    }

    public void getPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        getSupportActionBar().hide(); //hide the title bar
        setContentView(R.layout.activity_edit_profile);

        imageProfile = findViewById(R.id.imageProfile);
        editBio=findViewById(R.id.editBio);
        TextView profileName=findViewById(R.id.profileName);

        ParseQuery<ParseObject> query = new ParseQuery("Bio");
        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        for (ParseObject object : objects) {
                            editBio.setText(object.getString("Bio"));
                            break;
                        }
                    }
                }
            }
        });

        profileName.setText(ParseUser.getCurrentUser().getUsername());
        //textViewBio.setText(intent.getStringExtra("Bio"));

        ParseQuery<ParseObject> query2 = new ParseQuery("Myimage");
        query2.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
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
                                        ImageView dp = findViewById(R.id.imageProfile);
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

        imageProfile.setOnHoverListener(new View.OnHoverListener() {
            @Override
            public boolean onHover(View view, MotionEvent motionEvent) {
                imageProfile.setAlpha((float).9);
                return false;
            }
        });

    }



}
    
    
    
    
    /*

    private void pickFromGallery() {
        //Create an Intent with action as ACTION_PICK
        Intent intent = new Intent(Intent.ACTION_PICK);
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.setType("image/*");
        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        // Launching the Intent
        startActivityForResult(intent, 1);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Result code is RESULT_OK only if the user selects an Image
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case 1:
                    //data.getData returns the content URI for the selected Image
                    Uri selectedImage = data.getData();
                    imageProfile.setImageURI(selectedImage);
                    //
                    try {

                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                        ByteArrayOutputStream stream=new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
                        byte[] byteArray=stream.toByteArray();
                        ParseFile file=new ParseFile("image.png",byteArray);

                        ParseObject object=new ParseObject("Displaypicture");
                        object.put("image",file);
                        object.put("username", ParseUser.getCurrentUser().getUsername());
                        object.saveInBackground(new com.parse.SaveCallback() {
                            @Override
                            public void done(com.parse.ParseException e) {
                                if(e==null){
                                    Toast.makeText(EditProfile.this, "Saved Successfully :)", Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(EditProfile.this,"Unable to save",Toast.LENGTH_SHORT).show();

                                }
                            }
                        });

                    }catch (Exception e){
                        Log.i("Error ","Unable to fetch image");
                    }

                    //
                    break;
            }
    }

    public void getPhoto(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
        imageProfile = findViewById(R.id.image);
    }



    public void clickedSave(View view){
        ParseObject object=new ParseObject("Bio");
        String bio=editBio.getText().toString();
        object.put("Bio",bio);
        object.put("username", ParseUser.getCurrentUser().getUsername());
        object.saveInBackground(new com.parse.SaveCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                if(e==null){
                    //Toast.makeText(EditProfile.this, "Saved Successfully :)", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(EditProfile.this,"Unable to save",Toast.LENGTH_SHORT).show();

                }
            }
        });
        finish();


    }
    public void clickedSaveDP(View view){
        getPhoto();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        editBio=findViewById(R.id.editBio);
        imageProfile=findViewById(R.id.imageProfile);





        getPhoto();


    }

}


     */