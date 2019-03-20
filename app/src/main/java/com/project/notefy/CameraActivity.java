package com.project.notefy;



import android.app.Activity;
import android.content.ActivityNotFoundException;

import android.content.Intent;

import android.net.Uri;

import android.os.Bundle;
import android.os.Environment;

import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;


import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;



import java.io.File;

import java.io.IOException;


public class CameraActivity extends AppCompatActivity {




    private static final int TAKE_PICTURE = 1;
    private static final int PIC_CROP = 2;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(Environment.getExternalStorageDirectory(),  "Notefy/Images/Pic_"+ String.valueOf(System.currentTimeMillis()) + ".png");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(photo));
        imageUri = Uri.fromFile(photo);
        startActivityForResult(intent, TAKE_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PICTURE: {
                if (resultCode == Activity.RESULT_OK) {

                    try {
                        //call the standard crop action intent (the user device may not support it)
                        Intent cropIntent = new Intent("com.android.camera.action.CROP");
                        //indicate image type and Uri
                        cropIntent.setDataAndType(imageUri, "image/*");
                        //set crop properties
                        cropIntent.putExtra("crop", "true");
                        cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

                        startActivityForResult(cropIntent, PIC_CROP);

                    } //respond to users whose devices do not support the crop action
                    catch (ActivityNotFoundException anfe) {
                        //display an error message
                        String errorMessage = "Your device doesn't support the crop action!";
                        Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            }
            case PIC_CROP: {

                finish();
            }
        }
    }
}
