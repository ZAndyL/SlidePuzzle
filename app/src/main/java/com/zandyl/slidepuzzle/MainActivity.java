package com.zandyl.slidepuzzle;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    DisplayMetrics displaymetrics;
    static int height;
    static int width;
    static int actionBarHeight;
    static Bitmap img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        if(getIntent().getBooleanExtra("didDownloadImage", false)) {
            String filePath = getIntent().getStringExtra("filePath");
            img = BitmapFactory.decodeFile(filePath);
        } else {
            img = BitmapFactory.decodeResource(MainActivity.this.getResources(), R.drawable.selfie);
        }
        displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        height = displaymetrics.heightPixels;
        width = displaymetrics.widthPixels;
        final TypedArray styledAttributes = getApplication().getTheme().obtainStyledAttributes(
                new int[] { android.R.attr.actionBarSize });
        actionBarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();
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
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.scramble){
            PlaceholderFragment fragment = (PlaceholderFragment) getSupportFragmentManager().findFragmentById(R.id.container);
            fragment.gameModel.scramble(15);
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

            gameModel = new GameModel(getActivity(), this, this, width, height - ContextManager.getStatusBarHeight(getActivity()) - actionBarHeight, 4, 3, img);
            gameModel.scramble(15);
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
                        if(gameModel.isSolved()){
                            ContextManager.showToast("You solved it!", getActivity());
                        }
                    }
                    gameModel.setCurrentlySelectedPiece(v.getId());
                    gameModel.pieces[v.getId() / gameModel.cols][v.getId() % gameModel.cols].setX(event.getX() + v.getX() - gameModel.pieceWidth / 2);
                    gameModel.pieces[v.getId() / gameModel.cols][v.getId() % gameModel.cols].setY(event.getY() + v.getY() - gameModel.pieceHeight / 2);
            }

            return true;
        }

        @Override
        public void addToLayout(ImageView[][] pieces) {
            for (ImageView[] rows : pieces) {
                for (ImageView img : rows) {
                    layout.addView(img);
                }
            }
        }
    }
}
