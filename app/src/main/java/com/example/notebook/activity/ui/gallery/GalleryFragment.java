package com.example.notebook.activity.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.notebook.R;
import com.example.notebook.activity.MusicFragment;
import com.example.notebook.databinding.FragmentGalleryBinding;

public class GalleryFragment extends Fragment {
    MusicFragment musicFragment;

    private FragmentGalleryBinding binding;

    @Override
    public void onViewCreated(View view,@Nullable Bundle savedInstanceState){
        getView().findViewById(R.id.song1new).setOnClickListener(v -> songClick("cool_song_1"));
        getView().findViewById(R.id.song2new).setOnClickListener(v -> songClick("cool_song_2"));
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        //final TextView textView = binding.textGallery;
        //galleryViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }
    public void songClick(String song){
        setMusicFragment(song);
    }

    public void setMusicFragment(String song){
        musicFragment=new MusicFragment();
        musicFragment.setSong(song);
        FragmentTransaction ft=getChildFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayoutNew,musicFragment);
        ft.commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        *//*MediaPlayer mediaPlayer=MediaPlayer.create(this,R.raw.cool_song_1);
        mediaPlayer.start();*//*
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
    }*/