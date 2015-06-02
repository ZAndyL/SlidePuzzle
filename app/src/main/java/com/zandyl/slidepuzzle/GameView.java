package com.zandyl.slidepuzzle;

import android.widget.ImageView;

/**
 * Created by greencap on 6/1/2015.
 */
interface GameView {
    void updatePieces();
    void addToLayout(ImageView[][] pieces);
}
