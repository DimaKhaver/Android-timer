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


public class MainActivity extends AppCompatActivity {

    private final int SEC_UPDATE = 1000;
    private final int SEC_IN_MIN = 60;

    private TextView mTimerValue;
    private EditText mEnter;
    private Button mStartButton;
    private Handler mHandler = new Handler();
    private int mValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTimerValue = (TextView) findViewById(R.id.timer_value);
        mEnter = (EditText) findViewById(R.id.keyboard_value);
        mStartButton = (Button) findViewById(R.id.start_button);
        Button stopButton = (Button) findViewById(R.id.stop_button);

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

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEnter.getText().toString().isEmpty()) {
                    mHandler.removeCallbacks(timerRunnable);
                } else {
                    mStartButton.setClickable(false);
                    mEnter.setFocusable(false);
                    setTimerValue(mValue);
                    mHandler.postDelayed(timerRunnable, SEC_UPDATE);
                }
            }
        });

        if (stopButton != null) {
            stopButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mHandler.removeCallbacks(timerRunnable);
                    mStartButton.setClickable(true);
                    mEnter.setFocusableInTouchMode(true);
                }
            });
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
