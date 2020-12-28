package com.iskander.whackamole;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.airbnb.lottie.LottieAnimationView;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity {
    public long TIMER_INTERVAL = 1000; // 1000 millisec
    public static final long WHACK_SCORE = 25; // score for each whacked mole
    /** Views */
    private FrameLayout animationLayout;
    private TextView scoreTextView;
    private TextView finalScore;
    private LinearLayout gameOver;
    private ImageView quit;
    private ImageView restart;
    private LottieAnimationView speedUpView;
    private TextView speedUpText;
    /** Score variable */
    private int score = 0;
    /** stores all mole objects */
    private ArrayList<Mole> moles;
    /** Timer that will run every 1 second */
    private Timer molesTimer;
    /** mediaPLayer for bg music */
    private MediaPlayer bgMediaPlayer;


    private int timeCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        initViews();
        initScore();
        initMoles();
        initTimer();


        bgMediaPlayer = MediaPlayer.create(this, R.raw.bg_music);
        bgMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        bgMediaPlayer.setLooping(true);
        bgMediaPlayer.start();
    }

    private void initViews() {
        animationLayout = findViewById(R.id.animation_layout);
        scoreTextView = findViewById(R.id.score_text_view);
        finalScore = findViewById(R.id.final_score_text_view);
        gameOver = findViewById(R.id.game_over_layout);
        quit = findViewById(R.id.quit);
        restart = findViewById(R.id.restart);
        speedUpView = findViewById(R.id.speed_up);
        speedUpText = findViewById(R.id.speed_up_text);
    }

    /** init score and holder variable */
    private void initScore() {
        scoreTextView.setText("Score: 0");
        score = 0;
    }

    /** init views and objects for each mole */
    private void initMoles() {
        moles = new ArrayList<>();
        moles.add(new Mole(findViewById(R.id.mole_1_1)));
        moles.add(new Mole(findViewById(R.id.mole_1_2)));
        moles.add(new Mole(findViewById(R.id.mole_1_3)));
        moles.add(new Mole(findViewById(R.id.mole_2_1)));
        moles.add(new Mole(findViewById(R.id.mole_2_2)));
        moles.add(new Mole(findViewById(R.id.mole_2_3)));
        moles.add(new Mole(findViewById(R.id.mole_3_1)));
        moles.add(new Mole(findViewById(R.id.mole_3_2)));
        moles.add(new Mole(findViewById(R.id.mole_3_3)));

        //set onClickListener for each mole
        for (final Mole item : moles) {
            item.getView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //hide if is popped
                    if (item.isOut) {
                        item.hide();
                        //increase score
                        score += WHACK_SCORE;
                        scoreTextView.setText("Score: " + score);
                        displayHitAnim(view);
                        playSound(R.raw.sound_pop, false);
                    }
                }
            });
        }
    }

    /** start timer */
    private void initTimer() {
        TIMER_INTERVAL = 1000;
        timeCounter = 0;
        molesTimer = new Timer();
        molesTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                popRandomMole();
                timeCounter++; // 1 sec
                if (timeCounter == 20) {
                    speedUpTimer();
                    timeCounter = 0;
                }

            }
        }, 0, TIMER_INTERVAL);
    }

    private void speedUpTimer() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playSpeedUpAnim();
            }
        });

        TIMER_INTERVAL -= timeCounter * 10;

        molesTimer.cancel();
        molesTimer = new Timer();
        molesTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                popRandomMole();
                timeCounter++; // 1 sec
                if (timeCounter % 20 == 0 && TIMER_INTERVAL > 600) {
                    System.out.println("wrqweqr " + TIMER_INTERVAL);
                    speedUpTimer();
                }

            }
        }, 0, TIMER_INTERVAL);
    }

    private void playSpeedUpAnim() {
        speedUpText.setVisibility(View.VISIBLE);
        speedUpView.setVisibility(View.VISIBLE);
        speedUpView.playAnimation();
        speedUpView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) { }
            @Override
            public void onAnimationCancel(Animator animation) { }
            @Override
            public void onAnimationRepeat(Animator animation) { }

            @Override
            public void onAnimationEnd(Animator animation) {
                speedUpView.setVisibility(View.GONE);
                speedUpText.setVisibility(View.GONE);
            }
        });
    }

    /** Picks random mole and pops it */
    private void popRandomMole() {
        ArrayList<Mole> hiddenMoles = new ArrayList<>();
        for (Mole item : moles) {
            if (!item.isOut) {
                hiddenMoles.add(item);
            }
        }
        if (!hiddenMoles.isEmpty()) {
            int randomIndex;
            if (hiddenMoles.size() == 1) {
                randomIndex = 0;
            } else  {
                randomIndex = new Random().nextInt(hiddenMoles.size() - 1);
            }
            hiddenMoles.get(randomIndex).popOut();
            checkStatus();
        }
    }

    private void checkStatus() {
        boolean hasEmptyHoles = false;
        for (final Mole item : moles) {
            if (!item.isOut) {
                hasEmptyHoles = true;
            }
        }
        if (!hasEmptyHoles) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gameOver();
                }
            });

        }
    }


    private void displayHitAnim(View view) {
        final LottieAnimationView animationView = new LottieAnimationView(this);
        animationView.setAnimation("pop_lottie.json");
        animationView.setRepeatCount(0);
        animationLayout.addView(animationView);
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) animationView.getLayoutParams();
        lp.width = 500;
        lp.height = 500;

        int[] location = new int[2];
        view.getLocationInWindow(location);
        int viewCenterX = location[0] + view.getWidth() / 2;
        int viewCenterY = location[1] + view.getHeight() / 2;


        lp.leftMargin = viewCenterX - 250;
        lp.topMargin = viewCenterY - 250;

        animationView.setLayoutParams(lp);
        animationView.playAnimation();
        animationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) { }
            @Override
            public void onAnimationCancel(Animator animation) {}
            @Override
            public void onAnimationRepeat(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                try { animationLayout.removeView(animationView); }
                catch(Exception ex) { ex.printStackTrace(); }
            }
        });
    }

    private void playSound(int resource, boolean isLooping) {
        MediaPlayer mMediaPlayer = MediaPlayer.create(this, resource);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setLooping(isLooping);
        mMediaPlayer.start();
    }

    @SuppressLint("SetTextI18n")
    private void gameOver() {
        gameOver.setVisibility(View.VISIBLE);
        gameOver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        finalScore.setText("" + this.score);
        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetGame();
            }
        });
    }

    private void resetGame() {
        initScore();
        for (Mole mole : moles) {
            mole.hide();
        }
        molesTimer.cancel();
        initTimer();
        gameOver.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        molesTimer.cancel(); //cancel timer so it stops after activity is closed
        if (bgMediaPlayer != null) {
            bgMediaPlayer.stop();
        }
        super.onDestroy();
    }
}
