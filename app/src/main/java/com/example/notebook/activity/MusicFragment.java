package com.example.notebook.activity;

import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.notebook.R;

import java.lang.reflect.Field;
import java.util.Arrays;


public class MusicFragment extends Fragment {
    private String song;
    private MediaPlayer mediaPlayer;

    public void setSong(String song) {
        this.song = song;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        int song_num=0;
        Field field1=Arrays.stream(R.raw.class.getDeclaredFields()).filter(field -> field.getName().equals(song)).findFirst().get();
        try {
            song_num=field1.getInt(null);

        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        mediaPlayer=MediaPlayer.create(getContext(),song_num);

        return inflater.inflate(R.layout.fragment_music, container, false);
    }
    @Override
    public void onViewCreated(View view,@Nullable Bundle savedInstanceState){
        getView().findViewById(R.id.stop).setOnClickListener(v -> stop());
        getView().findViewById(R.id.play).setOnClickListener(v -> mediaPlayer.start());
        getView().findViewById(R.id.pause).setOnClickListener(v -> mediaPlayer.pause());
    }

    public void stop(){
        mediaPlayer.pause();
        mediaPlayer.seekTo(0);
    }

}