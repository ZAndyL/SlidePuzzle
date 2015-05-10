package com.zandyl.slidepuzzle;

import android.graphics.Point;
import android.graphics.PointF;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;


public class MainActivity extends ActionBarActivity {
    DisplayMetrics displaymetrics;
    static int height;
    static int width;

    static final int numSquares = 2;

    static ImageView[] squares = new ImageView[numSquares];

    static float squareWidth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }


        displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        height = displaymetrics.heightPixels;
        width = displaymetrics.widthPixels;
        squareWidth = (float)width / (numSquares + 1);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements View.OnTouchListener {

        Button butt;
        RelativeLayout idk;
        ImageView greenishSquare;
        PointF[] positions = new PointF[numSquares];

        PointF oldSquarePosition;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            idk = (RelativeLayout) rootView.findViewById(R.id.root);
            greenishSquare = (ImageView) rootView.findViewById(R.id.greenishSquare);
            //if (butt!=null)
//            idk.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    greenishSquare.setX(event.getX());
//                    greenishSquare.setY(event.getY());
//                    Log.d("you touch", "me at: " + event.getX() + " " + event.getY());
//                    return true;
//                }
//            });


            squares[0] = greenishSquare;
            squares[1] = (ImageView) rootView.findViewById(R.id.purpleishSquare);

            for(int i = 0; i < numSquares; i++){

                positions[i] = new PointF(i * squareWidth, (height - squareWidth)/2);

                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) squares[i].getLayoutParams();
                params.height = (int)squareWidth;
                params.width = (int)squareWidth;
                squares[i].setX(positions[i].x);
                squares[i].setLayoutParams(params);
                squares[i].setY(positions[i].y);

                //change later
                squares[i].setId(i);
                squares[i].setOnTouchListener(this);
            }

            return rootView;
        }

//        private int getPositionNumForXY(PointF position){
//
//        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {


            Log.d("you touch", "me at: " + event.getX() + " " + event.getY());

            switch(event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    v.bringToFront();
                    oldSquarePosition = positions[v.getId()];
                case MotionEvent.ACTION_UP:
//                    if(event.get){
//
//                    }
//                    else{
                        v.setX(oldSquarePosition.x);
                        v.setY(oldSquarePosition.y);
//                    }
                default:
                    squares[v.getId()].setX(event.getX() + v.getX() - squareWidth / 2);
                    squares[v.getId()].setY(event.getY() + v.getY() - squareWidth / 2);
            }

            return true;
        }
    }
}
