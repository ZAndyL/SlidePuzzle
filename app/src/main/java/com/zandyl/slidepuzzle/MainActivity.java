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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

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

            gameModel = new GameModel(getActivity(), this, this, width, height, 3, 4, R.drawable.selfie);
            return rootView;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.bringToFront();
                    gameModel.setCurrentlySelectedPiece(v.getId());
                    break;
                case MotionEvent.ACTION_UP:

                    Log.d("you touch up", " at: " + event.getX() + " " + event.getY());

                    v.setX(gameModel.getCurrentlySelectedPosition().x);
                    v.setY(gameModel.getCurrentlySelectedPosition().y);

                    break;
                default:
                    int posNum = gameModel.getPositionNumForXY(new PointF(event.getX() + v.getX(), event.getY() + v.getY()));

                    Log.d("You moved piece: ", v.getId() + " to " + posNum);
                    if (posNum != -1 && posNum != v.getId()) {
                        gameModel.swapPieces(gameModel.currentlySelectedI, gameModel.currentlySelectedJ, posNum / gameModel.cols, posNum % gameModel.cols);
                    }
                    gameModel.setCurrentlySelectedPiece(v.getId());
                    gameModel.pieces[v.getId() / gameModel.cols][v.getId() % gameModel.cols].setX(event.getX() + v.getX() - gameModel.pieceWidth / 2);
                    gameModel.pieces[v.getId() / gameModel.cols][v.getId() % gameModel.cols].setY(event.getY() + v.getY() - gameModel.pieceHeight / 2);
            }

            return true;
        }

        @Override
        public void addToLayout(ImageView[][] pieces) {
            for(ImageView[] rows: pieces){
                for(ImageView img: rows){
                    layout.addView(img);
                }
            }
        }
    }
}
