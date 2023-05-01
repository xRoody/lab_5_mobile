package com.example.notebook.activity;

import android.media.MediaPlayer;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.notebook.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;


public class MusicActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        ScrollView scrollView = findViewById(R.id.list);
        Arrays.stream(R.raw.class.getDeclaredFields()).forEach(x->{
            Button button = new Button(this);
            button.setId(View.generateViewId());
            button.setText(x.getName());
            button.setWidth(200);
            button.setHeight(50);
            button.setOnClickListener(v->songClick(x.getName()));
            scrollView.addView(button);
        });
    }


    public void songClick(String song){
        setMusicFragment(song);
    }

    public void setMusicFragment(String song){
        MusicFragment musicFragment=new MusicFragment();
        musicFragment.setSong(song);
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();

        ft.replace(R.id.frameLayout,musicFragment);
        ft.commit();
    }
}
