package ru.housekeeper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.net.MalformedURLException;
import java.net.URL;

public class StartActivity extends AppCompatActivity {
    EditText address;
    Button connect;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Log.d("StartActivity", "onCreate");
        address = findViewById(R.id.address);
        connect = findViewById(R.id.connect);
        sp = getSharedPreferences("address", MODE_PRIVATE);
        load();
    }

    public void connect(View view) {
        String addressToSave = address.getText().toString();
        Log.d("StartActivity", "saved " + addressToSave);
        try {
            new URL(addressToSave);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("address", addressToSave);
            editor.commit();
            Intent switchActivityIntent = new Intent(this, MainActivity.class);
            startActivity(switchActivityIntent);
        } catch (MalformedURLException e) {
            Toast.makeText(this, "Введите корректный адрес", Toast.LENGTH_LONG).show();
        }
    }

    public void load() {
        Log.d("StartActivity", "load");
        address.setText(sp.getString("address", ""));
    }
}