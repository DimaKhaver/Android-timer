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
