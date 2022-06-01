package ru.housekeeper;  // http://192.168.2.254:8000

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.longdo.mjpegviewer.MjpegView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class MainActivity extends AppCompatActivity {
    private final Gson gson = new Gson();
    SharedPreferences sp;
    String urlText;
    URL url;
    private MjpegView mjpegView;
    private TextView mTextViewAngle;
    private TextView mTextViewStrength;
    private TextView mTextViewCoordinate;
    private ExecutorService executorService;
    private AtomicBoolean isServerBusy;

    protected String doPost(int angle, int strength) {
        Log.d("doInBackground", "" + angle + " " + strength);

        try {
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setConnectTimeout(1000);
            con.setReadTimeout(5000);
            con.setDoOutput(true);


            String json = gson.toJson(new Data(angle, strength));
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = json.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                String responseBody = response.toString();
                Log.d("doInBackground", "resp " + responseBody);
                return responseBody;
            }

        } catch (Exception e) {
            Log.e("", "doInBackground: ", e);
            return e.getMessage();
        } finally {
            isServerBusy.set(false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sp = getSharedPreferences("address", MODE_PRIVATE);
        urlText = sp.getString("address", "not defined");
        url = getUrl();
        mTextViewAngle = (TextView) findViewById(R.id.textView_angle_right);
        mTextViewStrength = (TextView) findViewById(R.id.textView_strength_right);
        mTextViewCoordinate = findViewById(R.id.textView_coordinate_right);
        isServerBusy = new AtomicBoolean(false);
        executorService = Executors.newFixedThreadPool(1);
        final JoystickView joystickRight = (JoystickView) findViewById(R.id.joystickView_right);
        joystickRight.setOnMoveListener(new JoystickView.OnMoveListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onMove(int angle, int strength) {
                Log.d("myOnMove", "" + angle + " " + strength);
                if (isServerBusy.get()) {
                    return;
                }
                isServerBusy.set(true);
                executorService.submit(() -> doPost(angle, strength));
                // todo Toast.makeText(this, s, Toast.LENGTH_SHORT).show();

                mTextViewAngle.setText(angle + "°");
                mTextViewStrength.setText(strength + "%");
            }
        });

        mjpegView = findViewById(R.id.mjpegview);
        mjpegView.setAdjustHeight(true);
        mjpegView.setMode(MjpegView.MODE_FIT_WIDTH);
        mjpegView.setUrl(urlText + "/stream.mjpg");
        mjpegView.setRecycleBitmap(true);
    }

    @NonNull
    private URL getUrl() {
        try {
            return new URL(urlText + "/server");
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
        }
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