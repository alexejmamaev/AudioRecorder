package com.mamaevaleksej.audiorecorder.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mamaevaleksej.audiorecorder.R;
import com.mamaevaleksej.audiorecorder.ui.views.ScrollingTextView;

import java.io.File;

public class PlayingIndicatorFragment extends Fragment {

    private static final String TAG = PlayingIndicatorFragment.class.getSimpleName();
    private RecorderActivityViewModel activityViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null){
            activityViewModel = ViewModelProviders.of(getActivity())
                    .get(RecorderActivityViewModel.class);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.play_information_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ScrollingTextView songNameTV = view.findViewById(R.id.scrollingTextView);
        activityViewModel.getRecord().observe(this, record -> {
            if (record != null){
                songNameTV.setText(new File(record.getFilePath()).getName());
            }
        });

        ImageView playButton = view.findViewById(R.id.progressBar_play_button);
        playButton.setOnClickListener(v -> {
//            AudioTrackPlayer.resumePlaying();
        });

        ImageView pauseButton = view.findViewById(R.id.progressBar_pause_button);
        pauseButton.setOnClickListener(v -> {
//            AudioTrackPlayer.pausePlaying();
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "On DESTROY FRAGMENT --------------->>>>");
    }
}
