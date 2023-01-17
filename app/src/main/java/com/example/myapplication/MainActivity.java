package com.example.myapplication;


import androidx.annotation.NonNull;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    TextView tv, tv2, tv3, tv4, city_logo, d1, t1, d2, t2, d3, t3, d4, t4, d5, t5, Kakajatemp, Kakojden, textView2, textView3, textView4, date_time;
    EditText get_city;
    String city;
    Button button;
    ImageView imageView, imageView1, imageView2, imageView3, imageView4, imageView5;
    ConstraintLayout layout;

    WeatherDB cur_db;

    FusedLocationProviderClient fusedLocationProviderClient;
    public static String latitude, longitude;
    private final static int REQUEST_CODE = 100;

    @SuppressLint({"UseCompatLoadingForDrawables", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layout = findViewById(R.id.linearLayout);
        tv = findViewById(R.id.out_deg);
        tv2 = findViewById(R.id.out_deg2);
        tv3 = findViewById(R.id.out_deg3);
        tv4 = findViewById(R.id.out_deg4);
        get_city = findViewById(R.id.get_city);
        imageView = findViewById(R.id.imageView);
        city_logo = findViewById(R.id.city_logo);
        date_time = findViewById(R.id.date_time);

        Kakajatemp = findViewById(R.id.Kakajatemp);
        Kakojden = findViewById(R.id.Kakojden);
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);
        textView4 = findViewById(R.id.textView4);

        t1 = findViewById(R.id.temp1);
        d1 = findViewById(R.id.day1);
        imageView1 = findViewById(R.id.imageView1);

        t2 = findViewById(R.id.temp2);
        d2 = findViewById(R.id.day2);
        imageView2 = findViewById(R.id.imageView2);

        t3 = findViewById(R.id.temp3);
        d3 = findViewById(R.id.day3);
        imageView3 = findViewById(R.id.imageView3);

        t4 = findViewById(R.id.temp4);
        d4 = findViewById(R.id.day4);
        imageView4 = findViewById(R.id.imageView4);

        t5 = findViewById(R.id.temp5);
        d5 = findViewById(R.id.day5);
        imageView5 = findViewById(R.id.imageView5);

        cur_db = new WeatherDB(this);

        textView2.setVisibility(View.INVISIBLE);
        textView3.setVisibility(View.INVISIBLE);
        textView4.setVisibility(View.INVISIBLE);
        Kakojden.setVisibility(View.INVISIBLE);
        Kakajatemp.setVisibility(View.INVISIBLE);
        date_time.setVisibility(View.INVISIBLE);

        Date date = new Date();
        Locale local = new Locale("ru");
        SimpleDateFormat formatForDateNow = new SimpleDateFormat(" E dd.MM.yyyy \n\n' Время:\n' hh:mm:ss a", local);
        date_time.setText("Текущая дата: " + "\n" + formatForDateNow.format(date));

        get_background(layout);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        getLastLocation();

        get_city.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    city = get_city.getText().toString().trim();
                    String[] a = {city};
                    get_weather(a);
                    city_logo.setVisibility(View.VISIBLE);
                    get_city.setVisibility(View.INVISIBLE);
                    clear(v);
                    return false;
                }
                return false;
            }
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getLastLocation();
    }

    public void get_weather_icon(String icon) {
        try {
            InputStream ims = getAssets().open(icon + ".png");
            Drawable d = Drawable.createFromStream(ims, null);
            imageView.setImageDrawable(d);
            imageView.setVisibility(View.VISIBLE);
        } catch (IOException ex) {
            return;
        }
    }

    public void clear(View c) { // Процедура, очищающая поле для ввода текста
        get_city.setText("");
    }

    public void click_city(View c) {
        city_logo.setVisibility(View.INVISIBLE);
        get_city.setVisibility(View.VISIBLE);
        get_city.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(get_city, InputMethodManager.SHOW_IMPLICIT);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void set_back_assets(String state_day) {
        InputStream back = null;
        try {
            back = getAssets().open(state_day + ".jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Drawable background = Drawable.createFromStream(back, null);
        layout.setBackground(background);
    }

    public void get_background(ConstraintLayout layout) {

        Calendar cal = Calendar.getInstance();
        int time = Integer.parseInt(String.valueOf(cal.get(Calendar.HOUR_OF_DAY)));
        System.out.println(time);

        if ((time == 23) || ((time >= 0) && (time < 3))) {
            String state_day = "backgroundnight";
            set_back_assets(state_day);
            {
                Kakajatemp.setTextColor(this.getResources().getColor(R.color.white));
                Kakojden.setTextColor(this.getResources().getColor(R.color.white));
                textView2.setTextColor(this.getResources().getColor(R.color.white));
                textView3.setTextColor(this.getResources().getColor(R.color.white));
                textView4.setTextColor(this.getResources().getColor(R.color.white));
                get_city.setTextColor(this.getResources().getColor(R.color.white));
                city_logo.setTextColor(this.getResources().getColor(R.color.white));
                date_time.setTextColor(this.getResources().getColor(R.color.white));
                tv.setTextColor(this.getResources().getColor(R.color.white));
                tv2.setTextColor(this.getResources().getColor(R.color.white));
                tv3.setTextColor(this.getResources().getColor(R.color.white));
                tv4.setTextColor(this.getResources().getColor(R.color.white));
                t1.setTextColor(this.getResources().getColor(R.color.white));
                t2.setTextColor(this.getResources().getColor(R.color.white));
                t3.setTextColor(this.getResources().getColor(R.color.white));
                t4.setTextColor(this.getResources().getColor(R.color.white));
                t5.setTextColor(this.getResources().getColor(R.color.white));
                d1.setTextColor(this.getResources().getColor(R.color.white));
                d2.setTextColor(this.getResources().getColor(R.color.white));
                d3.setTextColor(this.getResources().getColor(R.color.white));
                d4.setTextColor(this.getResources().getColor(R.color.white));
                d5.setTextColor(this.getResources().getColor(R.color.white));
            }
        }

        if ((time >= 3) && (time < 6)) {

            String state_day = "backgroundmorning";
            set_back_assets(state_day);
            {
                Kakajatemp.setTextColor(this.getResources().getColor(R.color.black));
                Kakojden.setTextColor(this.getResources().getColor(R.color.black));
                textView2.setTextColor(this.getResources().getColor(R.color.black));
                textView3.setTextColor(this.getResources().getColor(R.color.black));
                textView4.setTextColor(this.getResources().getColor(R.color.black));
                get_city.setTextColor(this.getResources().getColor(R.color.black));
                city_logo.setTextColor(this.getResources().getColor(R.color.black));
                date_time.setTextColor(this.getResources().getColor(R.color.black));
                tv.setTextColor(this.getResources().getColor(R.color.black));
                tv2.setTextColor(this.getResources().getColor(R.color.black));
                tv3.setTextColor(this.getResources().getColor(R.color.black));
                tv4.setTextColor(this.getResources().getColor(R.color.black));
                t1.setTextColor(this.getResources().getColor(R.color.black));
                t2.setTextColor(this.getResources().getColor(R.color.black));
                t3.setTextColor(this.getResources().getColor(R.color.black));
                t4.setTextColor(this.getResources().getColor(R.color.black));
                t5.setTextColor(this.getResources().getColor(R.color.black));
                d1.setTextColor(this.getResources().getColor(R.color.black));
                d2.setTextColor(this.getResources().getColor(R.color.black));
                d3.setTextColor(this.getResources().getColor(R.color.black));
                d4.setTextColor(this.getResources().getColor(R.color.black));
                d5.setTextColor(this.getResources().getColor(R.color.black));
            }
        }

        if ((time >= 6) && (time < 12)) {

            String state_day = "backgroundmorning";
            set_back_assets(state_day);
            {
                Kakajatemp.setTextColor(this.getResources().getColor(R.color.black));
                Kakojden.setTextColor(this.getResources().getColor(R.color.black));
                textView2.setTextColor(this.getResources().getColor(R.color.black));
                textView3.setTextColor(this.getResources().getColor(R.color.black));
                textView4.setTextColor(this.getResources().getColor(R.color.black));
                get_city.setTextColor(this.getResources().getColor(R.color.black));
                city_logo.setTextColor(this.getResources().getColor(R.color.black));
                date_time.setTextColor(this.getResources().getColor(R.color.black));
                tv.setTextColor(this.getResources().getColor(R.color.black));
                tv2.setTextColor(this.getResources().getColor(R.color.black));
                tv3.setTextColor(this.getResources().getColor(R.color.black));
                tv4.setTextColor(this.getResources().getColor(R.color.black));
                t1.setTextColor(this.getResources().getColor(R.color.black));
                t2.setTextColor(this.getResources().getColor(R.color.black));
                t3.setTextColor(this.getResources().getColor(R.color.black));
                t4.setTextColor(this.getResources().getColor(R.color.black));
                t5.setTextColor(this.getResources().getColor(R.color.black));
                d1.setTextColor(this.getResources().getColor(R.color.black));
                d2.setTextColor(this.getResources().getColor(R.color.black));
                d3.setTextColor(this.getResources().getColor(R.color.black));
                d4.setTextColor(this.getResources().getColor(R.color.black));
                d5.setTextColor(this.getResources().getColor(R.color.black));
            }
        }

        if ((time >= 12) && (time < 17)) {

            String state_day = "backgroundday";
            set_back_assets(state_day);
            {
                Kakajatemp.setTextColor(this.getResources().getColor(R.color.black));
                Kakojden.setTextColor(this.getResources().getColor(R.color.black));
                textView2.setTextColor(this.getResources().getColor(R.color.black));
                textView3.setTextColor(this.getResources().getColor(R.color.black));
                textView4.setTextColor(this.getResources().getColor(R.color.black));
                get_city.setTextColor(this.getResources().getColor(R.color.black));
                city_logo.setTextColor(this.getResources().getColor(R.color.black));
                date_time.setTextColor(this.getResources().getColor(R.color.black));
                tv.setTextColor(this.getResources().getColor(R.color.black));
                tv2.setTextColor(this.getResources().getColor(R.color.black));
                tv3.setTextColor(this.getResources().getColor(R.color.black));
                tv4.setTextColor(this.getResources().getColor(R.color.black));
                t1.setTextColor(this.getResources().getColor(R.color.black));
                t2.setTextColor(this.getResources().getColor(R.color.black));
                t3.setTextColor(this.getResources().getColor(R.color.black));
                t4.setTextColor(this.getResources().getColor(R.color.black));
                t5.setTextColor(this.getResources().getColor(R.color.black));
                d1.setTextColor(this.getResources().getColor(R.color.black));
                d2.setTextColor(this.getResources().getColor(R.color.black));
                d3.setTextColor(this.getResources().getColor(R.color.black));
                d4.setTextColor(this.getResources().getColor(R.color.black));
                d5.setTextColor(this.getResources().getColor(R.color.black));
            }
        }

        if ((time >= 17) && (time < 23)) {

            String state_day = "backgroundevening";
            set_back_assets(state_day);
            {
                Kakajatemp.setTextColor(this.getResources().getColor(R.color.white));
                Kakojden.setTextColor(this.getResources().getColor(R.color.white));
                textView2.setTextColor(this.getResources().getColor(R.color.white));
                textView3.setTextColor(this.getResources().getColor(R.color.white));
                textView4.setTextColor(this.getResources().getColor(R.color.white));
                get_city.setTextColor(this.getResources().getColor(R.color.white));
                city_logo.setTextColor(this.getResources().getColor(R.color.white));
                date_time.setTextColor(this.getResources().getColor(R.color.white));
                tv.setTextColor(this.getResources().getColor(R.color.white));
                tv2.setTextColor(this.getResources().getColor(R.color.white));
                tv3.setTextColor(this.getResources().getColor(R.color.white));
                tv4.setTextColor(this.getResources().getColor(R.color.white));
                t1.setTextColor(this.getResources().getColor(R.color.white));
                t2.setTextColor(this.getResources().getColor(R.color.white));
                t3.setTextColor(this.getResources().getColor(R.color.white));
                t4.setTextColor(this.getResources().getColor(R.color.white));
                t5.setTextColor(this.getResources().getColor(R.color.white));
                d1.setTextColor(this.getResources().getColor(R.color.white));
                d2.setTextColor(this.getResources().getColor(R.color.white));
                d3.setTextColor(this.getResources().getColor(R.color.white));
                d4.setTextColor(this.getResources().getColor(R.color.white));
                d5.setTextColor(this.getResources().getColor(R.color.white));
            }
        }


    }


    public void get_weather(String[] city_id) {
        String userAPI = "c341e34f9b7c327502cde34aa7817c5f";
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        String url;

        if (city_id.length == 1) {
            url = "https://api.openweathermap.org/data/2.5/weather?q=" + city_id[0] + "&units=metric&lang=ru&appid=" + userAPI;
        } else {
            url = "https://api.openweathermap.org/data/2.5/weather?lat=" + city_id[0] + "&lon=" + city_id[1] + "&units=metric&lang=ru&appid=" + userAPI;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject main = (JSONObject) response.get("main");
                    JSONObject wind = (JSONObject) response.get("wind");
                    String s = main.getString("temp");
                    String s2 = main.getString("feels_like");
                    String s3 = main.getString("humidity");
                    String s4 = wind.getString("speed");
                    int i = (int)Float.parseFloat(s.trim());
                    s = String.valueOf(i);
                    int j = (int)Float.parseFloat(s2.trim());
                    s2 = String.valueOf(j);
                    int z = (int)Float.parseFloat(s4.trim());
                    s4 = String.valueOf(z);
                    tv.setText(s + "°C");
                    tv2.setText(s2 + "°C");
                    tv3.setText(s3 + "%");
                    tv4.setText(s4 + " км/ч");


                    JSONArray coord = (JSONArray) response.get("weather");
                    JSONObject sec = (JSONObject) coord.get(0);
                    String icon = sec.getString("icon");
                    get_weather_icon(icon);

                    String city_name = response.getString("name");
                    city_logo.setText(city_name);
                    System.out.println(city_name);
                    System.out.println("-----------------------------------------");
                    cur_db.insertData(city_name, s +"°C", icon, s2 + "°C", s3 + "%", s4 + "км/ч");
                    textView2.setVisibility(View.VISIBLE);
                    textView3.setVisibility(View.VISIBLE);
                    textView4.setVisibility(View.VISIBLE);
                    Kakojden.setVisibility(View.VISIBLE);
                    Kakajatemp.setVisibility(View.VISIBLE);
                    date_time.setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Ошибка!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Ошибка!", Toast.LENGTH_SHORT).show();
                        offline_weather();
                    }
                });

        queue.add(request);

        String url2;

        if (city_id.length == 1) {
            url2 = "https://api.openweathermap.org/data/2.5/forecast?q=" + city_id[0] + "&units=metric&lang=ru&appid=" + userAPI;
        } else {
            url2 = "https://api.openweathermap.org/data/2.5/forecast?lat=" + city_id[0] + "&lon=" + city_id[1] + "&units=metric&lang=ru&appid=" + userAPI;
        }

        JsonObjectRequest request2 = new JsonObjectRequest(Request.Method.GET, url2, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONArray list = (JSONArray) response.get("list");

                    JSONObject s1 = (JSONObject) list.get(0);
                    JSONObject main1 = (JSONObject) s1.get("main");
                    String str1 = main1.getString("temp");
                    String day1 = s1.getString("dt_txt").substring(8,10);
                    JSONArray weat1 = (JSONArray) s1.get("weather");
                    JSONObject sec1 = (JSONObject) weat1.get(0);
                    String icon1 = sec1.getString("icon");
                    try {
                        InputStream ims = getAssets().open(icon1 + ".png");
                        Drawable d = Drawable.createFromStream(ims, null);
                        imageView1.setImageDrawable(d);
                        imageView1.setVisibility(View.VISIBLE);
                    } catch (IOException ex) {
                        return;
                    }

                    JSONObject s2 = (JSONObject) list.get(8);
                    JSONObject main2 = (JSONObject) s2.get("main");
                    String str2 = main2.getString("temp");
                    String day2 = s2.getString("dt_txt").substring(8,10);
                    JSONArray weat2 = (JSONArray) s2.get("weather");
                    JSONObject sec2 = (JSONObject) weat2.get(0);
                    String icon2 = sec2.getString("icon");
                    try {
                        InputStream ims = getAssets().open(icon2 + ".png");
                        Drawable d = Drawable.createFromStream(ims, null);
                        imageView2.setImageDrawable(d);
                        imageView2.setVisibility(View.VISIBLE);
                    } catch (IOException ex) {
                        return;
                    }

                    JSONObject s3 = (JSONObject) list.get(16);
                    JSONObject main3 = (JSONObject) s3.get("main");
                    String str3 = main3.getString("temp");
                    String day3 = s3.getString("dt_txt").substring(8,10);
                    JSONArray weat3 = (JSONArray) s3.get("weather");
                    JSONObject sec3 = (JSONObject) weat3.get(0);
                    String icon3 = sec3.getString("icon");
                    try {
                        InputStream ims = getAssets().open(icon3 + ".png");
                        Drawable d = Drawable.createFromStream(ims, null);
                        imageView3.setImageDrawable(d);
                        imageView3.setVisibility(View.VISIBLE);
                    } catch (IOException ex) {
                        return;
                    }

                    JSONObject s4 = (JSONObject) list.get(24);
                    JSONObject main4 = (JSONObject) s4.get("main");
                    String str4 = main4.getString("temp");
                    String day4 = s4.getString("dt_txt").substring(8,10);
                    JSONArray weat4 = (JSONArray) s4.get("weather");
                    JSONObject sec4 = (JSONObject) weat4.get(0);
                    String icon4 = sec4.getString("icon");
                    try {
                        InputStream ims = getAssets().open(icon4 + ".png");
                        Drawable d = Drawable.createFromStream(ims, null);
                        imageView4.setImageDrawable(d);
                        imageView4.setVisibility(View.VISIBLE);
                    } catch (IOException ex) {
                        return;
                    }

                    JSONObject s5 = (JSONObject) list.get(32);
                    JSONObject main5 = (JSONObject) s5.get("main");
                    String str5 = main5.getString("temp");
                    String day5 = s5.getString("dt_txt").substring(8,10);
                    JSONArray weat5 = (JSONArray) s5.get("weather");
                    JSONObject sec5 = (JSONObject) weat5.get(0);
                    String icon5 = sec5.getString("icon");
                    try {
                        InputStream ims = getAssets().open(icon5 + ".png");
                        Drawable d = Drawable.createFromStream(ims, null);
                        imageView5.setImageDrawable(d);
                        imageView5.setVisibility(View.VISIBLE);
                    } catch (IOException ex) {
                        return;
                    }

                    int i1 = (int)Float.parseFloat(str1.trim());
                    str1 = String.valueOf(i1);

                    d1.setText(day1);
                    t1.setText(str1 + "°C");

                    int i2 = (int)Float.parseFloat(str2.trim());
                    str2 = String.valueOf(i2);

                    d2.setText(day2);
                    t2.setText(str2 + "°C");

                    int i3 = (int)Float.parseFloat(str3.trim());
                    str3 = String.valueOf(i3);

                    d3.setText(day3);
                    t3.setText(str3 + "°C");

                    int i4 = (int)Float.parseFloat(str4.trim());
                    str4 = String.valueOf(i4);

                    d4.setText(day4);
                    t4.setText(str4 + "°C");

                    int i5 = (int)Float.parseFloat(str5.trim());
                    str5 = String.valueOf(i5);

                    d5.setText(day5);
                    t5.setText(str5 + "°C");

                    cur_db.insertData2(icon1+" "+icon2+" "+icon3+" "+icon4+" "+icon5, day1+" "+day2+" "+day3+" "+day4+" "+day5, str1 + "°C "+str2 + "°C "+str3 + "°C "+str4 + "°C "+str5 + "°C");


                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Ошибка!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Ошибка!", Toast.LENGTH_SHORT).show();

                    }
                });

        queue.add(request2);
    }

    private void getLastLocation() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();

            fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.getToken())
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onSuccess(Location location) {
                            if (location !=null){
                                latitude = String.valueOf(location.getLatitude());
                                longitude = String.valueOf(location.getLongitude());
                                String[] city_id = {latitude, longitude};
                                get_weather(city_id);
                                textView2.setVisibility(View.VISIBLE);
                                textView3.setVisibility(View.VISIBLE);
                                textView4.setVisibility(View.VISIBLE);
                                Kakojden.setVisibility(View.VISIBLE);
                                Kakajatemp.setVisibility(View.VISIBLE);
                                date_time.setVisibility(View.VISIBLE);
                            }
                        }
                    });

        }else
        {
            askPermission();
        }
    }

    private void askPermission() { // Запрос прав доступа к сервисам геолокации
        ActivityCompat.requestPermissions(MainActivity.this, new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    @Override // Проверка прав доступа к сервисам геолокации
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(this, "Required Permission", Toast.LENGTH_SHORT).show();
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    public void offline_weather() {
        ArrayList<String> last_weather = cur_db.get_cur_data();
        System.out.println(last_weather);
        System.out.println("-----------------------------------------");
        if (last_weather.size() != 0) {
            city_logo.setText(last_weather.get(0));
            city_logo.setVisibility(View.VISIBLE);
            get_city.setVisibility(View.INVISIBLE);
            tv.setText(last_weather.get(1));
            System.out.println(last_weather);
            System.out.println("-----------------------------------------");
            tv2.setText(last_weather.get(3));
            tv3.setText(last_weather.get(4));
            tv4.setText(last_weather.get(5));
            get_weather_icon(last_weather.get(2));
        }
        ArrayList<String> last_weather2 = cur_db.get_cur_data1();
        if (last_weather2.size() != 0) {
            System.out.println(last_weather2);
            String[] icons = last_weather2.get(0).split(" ");
            String[] dates = last_weather2.get(1).split(" ");
            String[] temps = last_weather2.get(2).split(" ");

            d1.setText(dates[0]);
            t1.setText(temps[0]);
            try {
                InputStream ims = getAssets().open(icons[0] + ".png");
                Drawable d = Drawable.createFromStream(ims, null);
                imageView1.setImageDrawable(d);
                imageView1.setVisibility(View.VISIBLE);
            } catch (IOException ex) {
                return;
            }

            d2.setText(dates[1]);
            t2.setText(temps[1]);
            try {
                InputStream ims = getAssets().open(icons[1] + ".png");
                Drawable d = Drawable.createFromStream(ims, null);
                imageView2.setImageDrawable(d);
                imageView2.setVisibility(View.VISIBLE);
            } catch (IOException ex) {
                return;
            }

            d3.setText(dates[2]);
            t3.setText(temps[2]);
            try {
                InputStream ims = getAssets().open(icons[2] + ".png");
                Drawable d = Drawable.createFromStream(ims, null);
                imageView3.setImageDrawable(d);
                imageView3.setVisibility(View.VISIBLE);
            } catch (IOException ex) {
                return;
            }

            d4.setText(dates[3]);
            t4.setText(temps[3]);
            try {
                InputStream ims = getAssets().open(icons[3] + ".png");
                Drawable d = Drawable.createFromStream(ims, null);
                imageView4.setImageDrawable(d);
                imageView4.setVisibility(View.VISIBLE);
            } catch (IOException ex) {
                return;
            }

            d5.setText(dates[4]);
            t5.setText(temps[4]);
            try {
                InputStream ims = getAssets().open(icons[4] + ".png");
                Drawable d = Drawable.createFromStream(ims, null);
                imageView5.setImageDrawable(d);
                imageView5.setVisibility(View.VISIBLE);
            } catch (IOException ex) {
                return;
            }
        }

    }
}

