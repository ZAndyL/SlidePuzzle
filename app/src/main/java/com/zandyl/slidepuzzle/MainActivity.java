package com.zandyl.slidepuzzle;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;


public class MainActivity extends ActionBarActivity {
    DisplayMetrics displaymetrics;
    static int height;
    static int width;

    static final int numSquares = 2;

    static ImageView[] squares = new ImageView[numSquares];

    static Bitmap[] bitmapsArray = new Bitmap[numSquares];

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
        squareWidth = (float) width / (numSquares);
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
    public static class PlaceholderFragment extends Fragment implements View.OnTouchListener, GameView {

        GameModel gameModel;
        RelativeLayout layout;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            layout = (RelativeLayout) rootView.findViewById(R.id.root);

            gameModel = new GameModel(getActivity(), this, this, width, height, 3, 3, R.drawable.selfie);
            return rootView;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    Log.d("you touch down", " at: " + event.getX() + " " + event.getY());
                    v.bringToFront();
                    gameModel.setCurrentlySelectedPiece(v.getId());
                case MotionEvent.ACTION_UP:

                    Log.d("you touch up", " at: " + event.getX() + " " + event.getY());
                    int posNum = gameModel.getPositionNumForXY(new PointF(event.getX() + v.getX(), event.getY() + v.getY()));
                    if (posNum != -1) {

                        gameModel.swapPieces(gameModel.currentlySelectedI, gameModel.currentlySelectedJ, posNum / gameModel.rows, posNum % gameModel.rows);

                    } else {
                        v.setX(gameModel.getCurrentlySelectedPosition().x);
                        v.setY(gameModel.getCurrentlySelectedPosition().y);
                    }
                default:
                    gameModel.pieces[v.getId()/gameModel.rows][v.getId()%gameModel.rows].setX(event.getX() + v.getX() - squareWidth / 2);
                    gameModel.pieces[v.getId()/gameModel.rows][v.getId()%gameModel.rows].setY(event.getY() + v.getY() - squareWidth / 2);
            }

            return true;
        }

        @Override
        public void updatePieces() {

        }

        @Override
        public void addToLayout(ImageView[][] pieces) {
            for(int i = 0; i < pieces.length; i++){
                for(int j = 0; j < pieces[0].length; j++){
                    layout.addView(pieces[i][j]);
                }
            }
        }
    }
}
