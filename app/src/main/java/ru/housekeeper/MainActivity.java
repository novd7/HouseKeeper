package ru.housekeeper;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import com.longdo.mjpegviewer.MjpegView;
import io.github.controlwear.virtual.joystick.android.JoystickView;

public class MainActivity extends AppCompatActivity /*implements JoystickView.JoystickListener*/ {
    private MjpegView mjpegView;
    private TextView mTextViewAngle;
    private TextView mTextViewStrength;
    private TextView mTextViewCoordinate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextViewAngle = (TextView) findViewById(R.id.textView_angle_right);
        mTextViewStrength = (TextView) findViewById(R.id.textView_strength_right);
        mTextViewCoordinate = findViewById(R.id.textView_coordinate_right);

        final JoystickView joystickRight = (JoystickView) findViewById(R.id.joystickView_right);
        joystickRight.setOnMoveListener(new JoystickView.OnMoveListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onMove(int angle, int strength) {
                mTextViewAngle.setText(angle + "°");
                mTextViewStrength.setText(strength + "%");
                mTextViewCoordinate.setText(
                        String.format("x%03d:y%03d",
                                joystickRight.getNormalizedX(),
                                joystickRight.getNormalizedY())
                );
            }
        });

        mjpegView = findViewById(R.id.mjpegview);
        mjpegView.setAdjustHeight(true);
        mjpegView.setMode(MjpegView.MODE_FIT_WIDTH);
        mjpegView.setUrl("http://192.168.2.254:8000/stream.mjpg");
        mjpegView.setRecycleBitmap(true);
    }



    @Override
    protected void onResume() {
        mjpegView.startStream();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mjpegView.stopStream();
        super.onPause();
    }

    @Override
    protected void onStop() {
        mjpegView.stopStream();
        super.onStop();
    }
}