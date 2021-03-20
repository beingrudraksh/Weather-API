package com.rudraksh.weather;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    Button weather;
    EditText cityName;
    TextView label,data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weather=findViewById(R.id.showWeatherButton);
        cityName=findViewById(R.id.cityNameInput);
        label=findViewById(R.id.labelCity);
        data=findViewById(R.id.labelWeatherData);

        label.setText("");
        data.setText("");
        cityName.setText("");
        cityName.setVisibility(View.VISIBLE);
        weather.setVisibility(View.VISIBLE);
    }

    public void showWeather(View view) {

        cityName.setVisibility(View.GONE);
        weather.setVisibility(View.GONE);

        new downloadWeatherData().execute("https://api.openweathermap.org/data/2.5/weather?q="
                +cityName.getText().toString()
                +"&appid=d5acd5fd438d283e496dbb2b249d8cb6");

        //FOR HIDING THE KEYBOARD AS THE BUTTON IS PRESSED AFTER ENTERING THE TEXT
        InputMethodManager manager= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(cityName.getWindowToken(),0);
    }

    @SuppressLint("StaticFieldLeak")
    class downloadWeatherData extends AsyncTask<String,Void,String >
    {

        @Override
        protected String doInBackground(String... strings) {

            URL url;
            HttpURLConnection urlConnection;

            try {
                url=new URL(strings[0]);

                urlConnection = (HttpURLConnection) url.openConnection();

                InputStreamReader reader = new InputStreamReader(urlConnection.getInputStream());

                int weatherData = reader.read();

                String weatherInfo = "";
                while (weatherData != -1)
                {
                    weatherInfo += (char)weatherData;
                    weatherData = reader.read();
                }

                return weatherInfo;

            } catch (IOException e) {

                e.printStackTrace();

                Toast.makeText(getApplicationContext(),R.string.error_msg,Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {

                JSONObject jsonObject = new JSONObject(s);

                /** IF WE CHECK THE WEATHER JSON RECEIVED, IT HAS A SQUARE BRACKET BEFORE THE CURLY ONES IN "weather" COLUMN
                 * THAT'S WHY WE FIRST USE JSONArray FOR IT TO STORE ALL THE DATA AND THEN RETRIEVE IT BY STORING IN JSONObject ONE BY ONE
                 * WHEREAS IN "main"  AND ALL OTHER COLUMNS, THEY ARE ALL PART OF THE MAIN(PARENT) ARRAY
                 * HENCE, WE ACCESS THEM DIRECTLY THROUGH THE JSONObject
                 * THE THREE COLUMNS IN LAST (id,name,cod) ARE ACCESSED DIRECTLY BY THE MAIN JSONObject AS THEY ARE PART OF THE MAIN JSON DATA  */

                String WEATHER = "",TITLE = "";

                /** GETTING WEATHER DESCRIPTION  */

                JSONArray jsonArray = new JSONArray(jsonObject.getString("weather"));

                for (int i=0;i<jsonArray.length();i++)
                {

                    JSONObject obj=jsonArray.getJSONObject(i);

                    WEATHER+="Currently : "+obj.getString("main") +"\n\n";

                }

                /** GETTING TEMPERATURE AND HUMIDITY */

                JSONObject temp=new JSONObject(jsonObject.getString("main"));

                WEATHER+="Temperature : "+temp.getString("temp")+"°C" +"\n\n";

                WEATHER+="Humidity : "+temp.getString("humidity")+" gm/m^3" +"\n\n";

                /** GETTING WIND SPEED AND DIRECTION */

                JSONObject wind=new JSONObject(jsonObject.getString("wind"));

                WEATHER+="Wind Speed : "+wind.getString("speed")+" km/hr" +"\n\n";

                WEATHER+="Wind Direction : "+wind.getString("deg")+"°" +"\n\n";

                /** GETTING CLOUD LEVEL  */

                JSONObject cloud=new JSONObject(jsonObject.getString("clouds"));

               WEATHER+="Cloud Level : "+cloud.getString("all")+" oktas" +"\n\n";

                /** GETTING LATITUDE AND LONGITUDE  */

                JSONObject latlang=new JSONObject(jsonObject.getString("coord"));

                WEATHER+="Latitude : "+latlang.getString("lat")+"°" +"\n\n";

                WEATHER+="Longitude : "+latlang.getString("lon")+"°" +"\n\n";

                /**  GETTING THE NAME OF THE CITY  */

                TITLE=jsonObject.getString("name");

                label.setText(TITLE);
                data.setText(WEATHER);

                label.setVisibility(View.VISIBLE);
                data.setVisibility(View.VISIBLE);

            } catch (JSONException e) {
                e.printStackTrace();

                Toast.makeText(getApplicationContext(),R.string.error_msg,Toast.LENGTH_SHORT).show();
            }
        }
    }
}
