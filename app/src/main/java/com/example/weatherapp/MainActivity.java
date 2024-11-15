package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    public static JSONObject info;
    public static TextView textview;
    TextView timeText;
    EditText editText;
    Button button;
    TextView dateTimeTextView;
    TextView location;
    ImageView img;
    TextView temp;
    TextView description;
    SeekBar seekBar;
    TextView direction;
    ArrayList<WeatherItem> weatherItemArrayList= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("STARTING","starting app");
        setContentView(R.layout.activity_main);

        timeText = findViewById(R.id.id_time);
        editText = findViewById(R.id.id_editText);
        button = findViewById(R.id.id_button);
        dateTimeTextView = findViewById(R.id.id_dateTime);
        location = findViewById(R.id.id_location);
        img = findViewById(R.id.id_imageView);
        temp = findViewById(R.id.id_temp);
        description = findViewById(R.id.id_description);
        seekBar = findViewById(R.id.seekBar);
        direction = findViewById(R.id.textView6);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncThread myThread = new AsyncThread();
                myThread.execute(editText.getText().toString());
                //takes in the zipcode as a string then makes an array with it which is
                //then used in doInBackground
                //new thread created each time when button is clicked with new zipcode
            }
        });

        //seekbar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress==0)
                {
                    seekBar.setProgress(0);
                }

                try {
                    updateUI(progress);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void updateUI(int position) throws ParseException, JSONException {
        if (position == 0) {
            seekBar.setProgress(0);
        }

        //update img, temp, time, location, description
        temp.setText(weatherItemArrayList.get(position).getTemperature() + "°F");
        dateTimeTextView.setText(weatherItemArrayList.get(position).getTime());
        location.setText(weatherItemArrayList.get(position).getLocation());
        description.setText(weatherItemArrayList.get(position).getDescription());

        LocalDateTime now = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            now = LocalDateTime.now();
        }
        DateTimeFormatter formatter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");
        }
        LocalDateTime timeAfterAddingHours = addHoursToDateTime(now, position * 3);
        String formattedTimeAfterAddingHours = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formattedTimeAfterAddingHours = timeAfterAddingHours.format(formatter);
        }
        timeText.setText(formattedTimeAfterAddingHours);


        //glide for picture
        String iconCode = weatherItemArrayList.get(position).getImgCode();
        String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";
        //https://openweathermap.org/img/wn/03n@4x.png
        //Â°F
        //Glide.with(this).load(iconUrl).fitCenter().into(img);

        Glide.with(this)
                .load(iconUrl)
                .fitCenter()
                .into(img);

        //JSONObject info = new JSONObject(String.valueOf(result));

    }

    public LocalDateTime addHoursToDateTime(LocalDateTime dateTime, long hoursToAdd) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return dateTime.plusHours(hoursToAdd);
        }
        return null;
    }


    //initialize ui components
    //String is what is the input and arraylist is the output
    public class AsyncThread extends AsyncTask<String, Void, ArrayList<WeatherItem>> {
        @Override

        protected void onPostExecute(ArrayList<WeatherItem> result) {
            // Update UI component here
            //super.onPostExecute(result);  --- why do we need this?
            //textview.setText(""+result);
            weatherItemArrayList.clear();
            weatherItemArrayList.addAll(result);
            //sets the array with array with WeatherItem objects




            try {
                updateUI(0);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }


        }

        //remove double
        //ask if you're supposed to make this Double
        @Override
        protected ArrayList<WeatherItem> doInBackground(String... strings) {

            //turns strings given into array
            Log.d("TEST", "starting background task");
            String zipcode;
            if(strings==null)
            {
                zipcode="08852";
            }
            else
            {
                zipcode = strings[0];
            }
            URL weather;
            try {
                weather = new URL(
                        "https://api.openweathermap.org/data/2.5/forecast?zip="+
                                zipcode+",us&appid=9f52c4a9441b7b3f9f4da4a9a1252e98" +
                                "&units=imperial&tz=+05:00"
//https://api.openweathermap.org/data/2.5/forecast?zip=08512,us&appid=9f52c4a9441b7b3f9f4da4a9a1252e98&units=imperial&tz=-05:00&dt=1707860312
                );
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            //weather.put("9f52c4a9441b7b3f9f4da4a9a1252e98");
            Log.d("TEST", "starting background task");
            URLConnection connect;
            InputStream input;
            try {
                connect = weather.openConnection();
                input = connect.getInputStream();
            } catch (IOException e) {
                throw new RuntimeException(e);
                //Log.d("TESTONE",e.toString());
            }
            Log.d("INPUT", input.toString());
            BufferedReader buffer = new BufferedReader(new InputStreamReader(input));

            //step 5

            StringBuilder result = new StringBuilder();
            String line;
            while (true) {
                try {
                    if ((line = buffer.readLine()) == null)
                        break;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                result.append(line);
            }
            Log.d("TEST", String.valueOf(result));


            //make arraylist
            JSONObject info;
            String temp;
            String time;
            String city;
            String country;
            String icon;
            String description;
            String dt;
            ArrayList<WeatherItem> arr = new ArrayList();
            try {
                info = new JSONObject(String.valueOf(result));
                //JSONArray weatherarray = info.getJSONArray("forms");
                //square bracket : []

                JSONArray list = info.getJSONArray("list");
                JSONObject locationObj = info.getJSONObject("city");

                city = locationObj.getString("name");
                country = locationObj.getString("country");
                //for loop goes through this list
                for(int x=0; x<list.length(); x++) {
                    JSONObject weatherItem = list.getJSONObject(x);
                    dt = weatherItem.getString("dt");

                    //icon
                    JSONArray weatherArr = weatherItem.getJSONArray("weather");
                    icon = weatherArr.getJSONObject(0).getString("icon");
                    description = weatherArr.getJSONObject(0).getString("description");
                    time = "";
                    JSONObject main = weatherItem.getJSONObject("main");
                    temp = main.getString("temp");
                    WeatherItem obj = new WeatherItem(time, city, temp, icon, country, description);
                    arr.add(obj);


                }
                //make new objects in the loop and add it to arr
                //add weatherItem to arraylist - then return outside try/catch
                //JSONObject weatherItem = list.getJSONObject(0);
                //get time from weatheritem
                //String time = main.getDouble("temp");
                //JSONObject main = weatherItem.getJSONObject("main");
                //temp = main.getString("temp");

                //WeatherItem obj = new WeatherItem();
                //temp.toString to put in above object
                //textview.setText("" + temp);
                //sprites is the name of the "tag" for {}

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            //return arraylist that contains
            //textview.setText("" + temp);
            Log.d("ARR", String.valueOf(arr));
            return arr;

        }






    }

}