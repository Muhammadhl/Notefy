package com.project.notefy;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.GridLayout;
import android.widget.Toast;

import com.joshschriever.livenotes.activity.LiveNotesActivity;
import com.todobom.opennotescanner.GalleryGridActivity;

import java.util.ArrayList;
import java.util.List;

import java8.util.J8Arrays;
import uk.co.dolphin_com.seescoreandroid.PlayerActivity;

import static java8.util.stream.StreamSupport.stream;

public class MainActivity extends AppCompatActivity {

    GridLayout mainGrid;
    private static final List<String> REQUIRED_PERMISSIONS = new ArrayList<>();
    private static final int PERMISSION_REQUEST_ALL_REQUIRED = 1;

    static {
        REQUIRED_PERMISSIONS.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        REQUIRED_PERMISSIONS.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        REQUIRED_PERMISSIONS.add(Manifest.permission.CAMERA);
        REQUIRED_PERMISSIONS.add(Manifest.permission.INTERNET);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            checkPermissions();
        }

        mainGrid = (GridLayout) findViewById(R.id.mainGrid);

        //Set Event
        setSingleEvent(mainGrid);
        //setToggleEvent(mainGrid);
    }

    private void checkPermissions() {
        String[] permissionsToRequest = stream(REQUIRED_PERMISSIONS)
                .filter(s -> checkSelfPermission(s) == PackageManager.PERMISSION_DENIED)
                .toArray(String[]::new);
        if (permissionsToRequest.length > 0) {
            requestPermissions(permissionsToRequest, PERMISSION_REQUEST_ALL_REQUIRED);
        }
    }

    @Override
    public void onRequestPermissionsResult(int code,
                                           @NonNull String permissions[],
                                           @NonNull int[] results) {
        if (results.length > 0
                && J8Arrays.stream(results).allMatch(r -> r == PackageManager.PERMISSION_GRANTED)) {
        } else {
            checkPermissions();
        }
    }

    private void setToggleEvent(GridLayout mainGrid) {
        //Loop all child item of Main Grid
        for (int i = 0; i < mainGrid.getChildCount(); i++) {
            //You can see , all child item is CardView , so we just cast object to CardView
            final CardView cardView = (CardView) mainGrid.getChildAt(i);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (cardView.getCardBackgroundColor().getDefaultColor() == -1) {
                        //Change background color
                        cardView.setCardBackgroundColor(Color.parseColor("#FF6F00"));
                        Toast.makeText(MainActivity.this, "State : True", Toast.LENGTH_SHORT).show();

                    } else {
                        //Change background color
                        cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                        Toast.makeText(MainActivity.this, "State : False", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void setSingleEvent(GridLayout mainGrid)
    {
        //Loop all child item of Main Grid

        CardView cardView = (CardView) mainGrid.getChildAt(0);
        cardView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this , CameraActivity.class);
                startActivity(intent);
            }
        });

        cardView = (CardView) mainGrid.getChildAt(1);
        cardView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this , GalleryGridActivity.class);
                startActivity(intent);
            }
        });

        cardView = (CardView) mainGrid.getChildAt(2);
        cardView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this , PlayerActivity.class);
                startActivity(intent);
            }
        });

        cardView = (CardView) mainGrid.getChildAt(3);
        cardView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this , LiveNotesActivity.class);
                startActivity(intent);
            }
        });

    }
}
