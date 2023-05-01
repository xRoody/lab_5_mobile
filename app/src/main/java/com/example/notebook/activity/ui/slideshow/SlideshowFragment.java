package com.example.notebook.activity.ui.slideshow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.notebook.R;
import com.example.notebook.activity.MenuActivity;
import com.example.notebook.activity.WeatherActivity;
import com.example.notebook.databinding.FragmentSlideshowBinding;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SlideshowFragment extends Fragment {

    //example url https://api.openweathermap.org/data/2.5/weather?q=Saratov&appid=5c8a06c123a40d90cab8ee594cb17be7&units=metric
    //api key 5c8a06c123a40d90cab8ee594cb17be7
    String apiKey="5c8a06c123a40d90cab8ee594cb17be7";
    String defaultCity="Saratov";
    EditText searchCity;
    Button searchButton;

    private FragmentSlideshowBinding binding;

    @Override
    public void onViewCreated(View view,@Nullable Bundle savedInstanceState){
        searchCity=getView().findViewById(R.id.searchCityNew);
        searchButton=getView().findViewById(R.id.searchButtonNew);
        searchButton.setOnClickListener(v -> search(searchCity.getText().toString()));
    }
    private void search(String city){
        TextView temperature=getView().findViewById(R.id.temperatureNew);
        TextView wind=getView().findViewById(R.id.windNew);
        OkHttpClient client=new OkHttpClient();
        String url="https://api.openweathermap.org/data/2.5/weather?q="+city+"&appid="+apiKey+"&units=metric";
        Request request=new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
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
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject result = new JSONObject(response.body().string());
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
                    getActivity().runOnUiThread(new Runnable() {
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


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SlideshowViewModel slideshowViewModel =
                new ViewModelProvider(this).get(SlideshowViewModel.class);

        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //final TextView textView = binding.textSlideshow;
        //slideshowViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}