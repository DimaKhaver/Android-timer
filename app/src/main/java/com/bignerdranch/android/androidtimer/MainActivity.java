package com.bignerdranch.android.androidtimer;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

// Base timer
public class MainActivity extends AppCompatActivity {

    private final int SEC_UPDATE = 1000;
    private final int SEC_IN_MIN = 60;

    private Handler mHandler = new Handler();
    private int mValue;

    @BindView(R.id.timer_value) TextView mTimerValue;
    @BindView(R.id.keyboard_value) EditText mEnter;
    @BindView(R.id.start_button) Button mStartButton;
    @BindView(R.id.stop_button) Button mStopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        if (!mEnter.getText().toString().isEmpty() && Integer.valueOf(mEnter.getText().toString()) > 0)
            mValue = Integer.valueOf(mEnter.getText().toString());

        mEnter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!mEnter.getText().toString().isEmpty() && Integer.valueOf(mEnter.getText().toString()) > 0)
                    mValue = Integer.valueOf(mEnter.getText().toString());
                else {
                    mValue = 0;
                    mHandler.removeCallbacks(timerRunnable);
                }
            }
        });
    }

    @OnClick(R.id.stop_button) public void onStopButtonClick() {
        mHandler.removeCallbacks(timerRunnable);
        mStartButton.setClickable(true);
        mEnter.setFocusableInTouchMode(true);
    }

    @OnClick(R.id.start_button) public void onStartButtonClick() {
        if (mEnter.getText().toString().isEmpty()) {
            mHandler.removeCallbacks(timerRunnable);
        } else {
            mStartButton.setClickable(false);
            mEnter.setFocusable(false);
            setTimerValue(mValue);
            mHandler.postDelayed(timerRunnable, SEC_UPDATE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mEnter.getText().toString().isEmpty())
            mValue = Integer.valueOf(mEnter.getText().toString());

        mStartButton.setClickable(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mEnter.getText().toString().isEmpty())
            mValue = Integer.valueOf(mEnter.getText().toString());

        mStartButton.setClickable(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacks(timerRunnable);
        mStartButton.setClickable(false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHandler.removeCallbacks(timerRunnable);
        mStartButton.setClickable(false);
    }

    private void setTimerValue(int mValue) {
        if (mValue != 0) {
            long mins = TimeUnit.SECONDS.toMinutes(mValue);
            long secs = TimeUnit.SECONDS.toSeconds(mValue % SEC_IN_MIN);
            mTimerValue.setText(String.format("%02d:%02d", mins, secs));
        }
    }

    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (mValue > 0) {
                mValue -= 1;
                setTimerValue(mValue);
                mHandler.postDelayed(this, SEC_UPDATE);
            } else if (mValue == 0){

                long mins = TimeUnit.SECONDS.toMinutes(Integer.valueOf(mEnter.getText().toString()));
                long secs = TimeUnit.SECONDS.toSeconds(Integer.valueOf(mEnter.getText().toString()) % SEC_IN_MIN);
                mTimerValue.setText(String.format("%02d:%02d", mins, secs));

                if (!mEnter.getText().toString().isEmpty() && Integer.valueOf(mEnter.getText().toString()) != 0){
                    mValue = Integer.valueOf(mEnter.getText().toString());
                }
                mStartButton.setClickable(true);
                mHandler.removeCallbacks(timerRunnable);
                mEnter.setFocusableInTouchMode(true);
            }
        }
    };
}


/*

   Custom GameTimer class which I use in the game "Bulls and Cows".
   It interacts with the data layer in the app.

*/


/*

public class GameTimer {

    @Inject PlayerManager mPlayerManager;

    private static final String TAG = "AndroidTimer";
    private static final int SEC_UPDATE = 1000, MS_IN_SEC = 1000;
    private static final int SEC_IN_HOUR = 3600;
    private static final int SEC_IN_MIN = 60;

    private long timeInMilliseconds = 0L;
    private long timeSwapBuff = 0L;
    private long updatedTime = 0L;
    private long startTime = 0L;

    private Handler handler = new Handler();

    //  Only two conditions: start and stop
    public GameTimer() {
        App.getAppComponent().inject(this);
    }

    public void startTimer() {
        startTime = SystemClock.uptimeMillis();
        handler.postDelayed(runnable, SEC_UPDATE);

        Log.i(TAG, "Timer is started; timeSwapBuff = " + timeSwapBuff);
    }

    public void stopTimer(final String prevTime) {
        timeSwapBuff += timeInMilliseconds;
        saveCurrentTime(prevTime);
        handler.removeCallbacks(runnable);

        Log.i(TAG, "Timer is stopped; timeSwapBuff = " + timeSwapBuff);
    }

    public void setToZero() {
        timeSwapBuff = 0L;
        updatedTime = 0L;
        timeInMilliseconds = 0L;
    }

    public void resumeTimer() {
        final Game game = mPlayerManager.getPlayer().getCurrentGame();
        final String reloadedTime = game.getGameTime();

        if (reloadedTime != null) {
            String[] units = reloadedTime.split(":");

            final int hours = Integer.parseInt(units[0]);
            final int mins = Integer.parseInt(units[1]);
            final int secs = Integer.parseInt(units[2]);
            // convert everything into MS
            timeSwapBuff = (SEC_IN_HOUR * hours + SEC_IN_MIN * mins + secs) * MS_IN_SEC;
        }

        handler.postDelayed(runnable, SEC_UPDATE);

        Log.i("RELOADED TIME:", reloadedTime);
        Log.i("\nRESET TIME:", String.valueOf(timeSwapBuff));
    }

    private void saveCurrentTime(final String prevTime) {
        final String currentTime = convertTime(prevTime);
        final Game game = mPlayerManager.getPlayer().getCurrentGame();

        game.setGameTime(currentTime);
    }

    private String convertTime(final String prevTime) {

        if (prevTime != null) {
            String[] units = prevTime.split(":");

            final int hours = Integer.parseInt(units[0]);
            final int mins = Integer.parseInt(units[1]);
            final int secs = Integer.parseInt(units[2]);
            // convert everything into MS
            timeSwapBuff += (SEC_IN_HOUR * hours + SEC_IN_MIN * mins + secs) * MS_IN_SEC;

            Log.i(TAG, "CONVERT_TIME = " + String.valueOf(timeSwapBuff));
        }

        return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(updatedTime),
                TimeUnit.MILLISECONDS.toMinutes(updatedTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(updatedTime)),
                TimeUnit.MILLISECONDS.toSeconds(updatedTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(updatedTime)));
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;
            handler.postDelayed(this, SEC_UPDATE);
        }
    };
}


*/