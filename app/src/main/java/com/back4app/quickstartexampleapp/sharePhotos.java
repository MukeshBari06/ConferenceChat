package com.back4app.quickstartexampleapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ParseException;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.service.autofill.SaveCallback;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;

public class sharePhotos extends AppCompatActivity {


    ImageView imageView;

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
                    imageView.setImageURI(selectedImage);
                    //
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                        ByteArrayOutputStream stream=new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
                        byte[] byteArray=stream.toByteArray();
                        ParseFile file=new ParseFile("image.png",byteArray);

                        ParseObject object=new ParseObject("Image");
                        object.put("image",file);
                        object.put("username", ParseUser.getCurrentUser().getUsername());
                        object.saveInBackground(new com.parse.SaveCallback() {
                            @Override
                            public void done(com.parse.ParseException e) {
                                if(e==null){
                                    Toast.makeText(sharePhotos.this, "image shared", Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(sharePhotos.this,"Unable to share to server",Toast.LENGTH_SHORT).show();

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
        imageView = findViewById(R.id.image);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        getSupportActionBar().hide(); //hide the title bar
        setContentView(R.layout.activity_share_photos);


        getPhoto();
    }
}
