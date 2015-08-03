package com.zandyl.slidepuzzle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by Zheng (Andy) Liang on 5/10/2015.
 */
public class GameModel {

    Context mContext;
    GameView ui;

    int screenWidth;
    int screenHeight;

    float pieceHeight;
    float pieceWidth;

    int rows;
    int cols;

    public PointF[][] positions;
    public ImageView[][] pieces;
    public Bitmap[][] bitmapsArray;

    Bitmap image;

    View.OnTouchListener pieceTouchListener;

    int currentlySelectedI;
    int currentlySelectedJ;

    public GameModel(Context context, View.OnTouchListener pieceTouchListener, GameView ui, int screenWidth, int screenHeight, int rows, int cols, Bitmap image) {

        mContext = context;
        this.ui = ui;

        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;
        this.rows = rows;
        this.cols = cols;
        this.pieceTouchListener = pieceTouchListener;
        this.image = image;

        pieceWidth = (float) (screenWidth) / cols;
        pieceHeight = (float) (screenHeight) / rows;

        positions = new PointF[rows][cols];
        pieces = new ImageView[rows][cols];

        createImageArrays();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                positions[i][j] = new PointF((j * pieceWidth),(i * pieceHeight));

                ImageView piece = new ImageView(mContext);
                pieces[i][j] = piece;

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) pieceWidth, (int) pieceHeight);

                pieces[i][j].setLayoutParams(params);
                pieces[i][j].setX(positions[i][j].x);
                pieces[i][j].setY(positions[i][j].y);

                pieces[i][j].setId(i * cols + j);
                pieces[i][j].setOnTouchListener(pieceTouchListener);
                pieces[i][j].setImageBitmap(bitmapsArray[i][j]);
            }
        }
        ui.addToLayout(pieces);
    }

    public void setPiecePosition(int i, int j, PointF pos) {
        pieces[i][j].setX(pos.x);
        pieces[i][j].setY(pos.y);
    }

    public PointF getPiecePosition(int i, int j) {
        return new PointF(pieces[i][j].getX(), pieces[i][j].getY());
    }

    public void swapPieces(int i1, int j1, int i2, int j2) {
        if (i1 == i2 && j1 == j2) {
            return;
        }
        ImageView tmp = pieces[i1][j1];
        pieces[i1][j1] = pieces[i2][j2];
        pieces[i2][j2] = tmp;

        pieces[i1][j1].setId(i1 * cols + j1);
        Log.d("set first piece id to ", "" + (i1 * cols + j1));
        pieces[i2][j2].setId(i2 * cols + j2);
        Log.d("set second piece id to ", "" + (i2 * cols + j2));

        pieces[i1][j1].setX(positions[i1][j1].x);
        pieces[i1][j1].setY(positions[i1][j1].y);

        Log.d("set position ", "of first piece to x:" + positions[i1][j1].x + " y:" + positions[i1][j1].y);

        pieces[i2][j2].setX(positions[i2][j2].x);
        pieces[i2][j2].setY(positions[i2][j2].y);

        Log.d("set position ", "of second piece to x:" + positions[i2][j2].x + " y:" + positions[i2][j2].y);
    }

    public void setCurrentlySelectedPiece(int num) {
        currentlySelectedI = num / cols;
        currentlySelectedJ = num % cols;
    }

    public PointF getCurrentlySelectedPosition() {
        return positions[currentlySelectedI][currentlySelectedJ];
    }

    public int getPositionNumForXY(PointF position) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Rect tempRec = new Rect((int) positions[i][j].x, (int) positions[i][j].y, (int) (positions[i][j].x + pieceWidth), (int) (positions[i][j].y + pieceHeight));
                if (tempRec.contains((int) position.x, (int) position.y)) {
                    return i * cols + j;
                }
            }
        }
        return -1;
    }

    void createImageArrays() {
        Bitmap bMapScaled = Bitmap.createScaledBitmap(image, screenWidth, screenHeight, true);

        bitmapsArray = new Bitmap[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                bitmapsArray[i][j] = Bitmap.createBitmap(bMapScaled, (int) (j * pieceWidth), (int) (i * pieceHeight), (int) pieceWidth, (int) pieceHeight);
            }
        }

    }

}
