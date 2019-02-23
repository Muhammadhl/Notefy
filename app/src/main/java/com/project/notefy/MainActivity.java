package com.project.notefy;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.GridLayout;
import android.widget.Toast;

import com.joshschriever.livenotes.activity.LiveNotesActivity;
import com.todobom.opennotescanner.GalleryGridActivity;

import uk.co.dolphin_com.seescoreandroid.PlayerActivity;

public class MainActivity extends AppCompatActivity {

    GridLayout mainGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainGrid = (GridLayout) findViewById(R.id.mainGrid);

        //Set Event
        setSingleEvent(mainGrid);
        //setToggleEvent(mainGrid);
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

                //Intent intent = new Intent(MainActivity.this, OpenNoteScannerActivity.class);
                //startActivity(intent);
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
