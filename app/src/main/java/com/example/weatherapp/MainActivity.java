package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private EditText userField;
    private Button mainBtn;
    private TextView resultInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userField = findViewById(R.id.user_field);
        mainBtn = findViewById(R.id.main_btn);
        resultInfo = findViewById(R.id.result_info);

        mainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userField.getText().toString().trim().equals("")) {
                    Toast.makeText(MainActivity.this, R.string.no_input_city, Toast.LENGTH_LONG)
                            .show();
                } else {
                    String city = userField.getText().toString();
                    String key = "0c3600baa81c04f0fdde61b48f554423";
                    String url = "https://api.openweathermap.org/data/2.5/weather?q=" +
                            city + "&appid=" + key + "&units=metric&lang=ru";
                    new GetURLData().execute(url);
                }
            }
        });

    }

    private class GetURLData extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            resultInfo.setText(R.string.wait_please);
        }

        @Override
        protected String doInBackground(String... strings) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                    return buffer.toString();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null)
                    connection.disconnect();
                try {
                    if (reader != null)
                        reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonObj = new JSONObject(result);
                resultInfo.setText("Город: " + jsonObj.getString("name")
                        + "\nТемпература: " + jsonObj.getJSONObject("main").getDouble("temp")
                + "\nОщущается как: " + jsonObj.getJSONObject("main").getDouble("feels_like")
                + "\nНа улице: " + jsonObj.getJSONArray("weather").getJSONObject(0).getString("description"));
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }
}