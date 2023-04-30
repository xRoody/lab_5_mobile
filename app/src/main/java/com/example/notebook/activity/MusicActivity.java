package com.example.notebook.activity;

import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.notebook.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;


public class MusicActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        /*MediaPlayer mediaPlayer=MediaPlayer.create(this,R.raw.cool_song_1);
        mediaPlayer.start();*/
        findViewById(R.id.song1).setOnClickListener(v -> songClick("cool_song_1"));
        findViewById(R.id.song2).setOnClickListener(v -> songClick("cool_song_2"));
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
