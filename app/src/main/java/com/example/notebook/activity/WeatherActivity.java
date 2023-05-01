package com.example.notebook.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.notebook.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.URL;

import kotlin.text.Charsets;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    //example url https://api.openweathermap.org/data/2.5/weather?q=Saratov&appid=5c8a06c123a40d90cab8ee594cb17be7&units=metric
    //api key 5c8a06c123a40d90cab8ee594cb17be7
    String apiKey="5c8a06c123a40d90cab8ee594cb17be7";
    String defaultCity="Saratov";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        EditText searchCity=findViewById(R.id.searchCity);
        Button searchButton=findViewById(R.id.searchButton);
        searchButton.setOnClickListener(v -> search(searchCity.getText().toString()));
        /*MediaPlayer mediaPlayer=MediaPlayer.create(this,R.raw.cool_song_1);
        mediaPlayer.start();
        findViewById(R.id.song1).setOnClickListener(v -> songClick("cool_song_1"));
        findViewById(R.id.song2).setOnClickListener(v -> songClick("cool_song_2"));*/

    }



    private void search(String city){
        TextView temperature=findViewById(R.id.temperature);
        TextView wind=findViewById(R.id.wind);
        OkHttpClient client=new OkHttpClient();
        String url="https://api.openweathermap.org/data/2.5/weather?q="+city+"&appid="+apiKey+"&units=metric";
        Request request=new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                WeatherActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        temperature.setText("Cant find this city");
                        temperature.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    WeatherActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject result = new JSONObject(response.body().string());
                        /*System.out.println(result.getString("name"));
                        System.out.println(result.getString("cod"));
                        System.out.println(result.getString("clouds"));*/
                                temperature.setText("Температура:"+result.getJSONObject("main").getString("temp"));
                                wind.setText("Скорость ветра:"+result.getJSONObject("wind").getString("speed"));
                                temperature.setVisibility(View.VISIBLE);
                                wind.setVisibility(View.VISIBLE);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                }
                else{
                    WeatherActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            temperature.setText("Cant find this city!");
                            temperature.setVisibility(View.VISIBLE);
                            wind.setVisibility(View.VISIBLE);
                            wind.setText("");
                        }
                    });
                }
            }
        });
    }

    /*private class WeatherTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {
            String[]response;
            try {
                response= new URL(Charsets.UTF_8,"https://api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&appid={API key}");
            }
            catch (Exception e){
                response=null;
            }
            return null;
        }
    }*/
}
