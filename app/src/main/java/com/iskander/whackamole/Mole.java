package com.iskander.whackamole;

import android.view.View;
import android.widget.ImageView;

import java.util.Random;

class Mole {
    int imageWhenPopped = R.drawable.mole_game_out;
    int imageWhenHidden = R.drawable.mole_game_in;
    int currentImage;
    boolean isOut;
    ImageView view;
    int lifeCounter;

    public Mole(View view) {
        this.view = ((ImageView)view);
        currentImage = imageWhenHidden;
        isOut = false;
        lifeCounter = 0;
    }

    void popOut() {
        if (view == null) {
            return;
        }
        view.setImageResource(imageWhenPopped);
        currentImage = imageWhenPopped;
        isOut = true;
    }

    void hide() {
        if (view == null) {
            return;
        }
        view.setImageResource(imageWhenHidden);
        currentImage = imageWhenHidden;
        isOut = false;
    }

    public View getView() {
        return view;
    }

    public void incrementLifeCounter() {
        lifeCounter++;
        if (lifeCounter == 4) {
            lifeCounter = 0;
            hide();
        }
    }
}
