package com.example.macos.activities;

import android.graphics.Bitmap;
import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Transition;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.VideoView;

import com.example.macos.duan.R;

public class AcVideo extends AppCompatActivity {

    private VideoView mVideoView;
    private SeekBar seekBar;
    private Uri videoUrl;
    private ImageView imgVideoView;
    private ImageButton btnPlayVideo;
    private int currentPosition;
    private final Handler mHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.ac_video);

        initLayoutAndData();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
        }else{
        }
    }

    private void initLayoutAndData(){
        mVideoView = (VideoView) findViewById(R.id.mVideoView);
        seekBar = (SeekBar) findViewById(R.id.seekbar);
        imgVideoView = (ImageView) findViewById(R.id.imgVideoView);
        btnPlayVideo = (ImageButton) findViewById(R.id.imgPlayVideo);

        videoUrl = Uri.parse(getIntent().getStringExtra("videoUrl"));
        currentPosition = getIntent().getIntExtra("position", 0);

        mVideoView.setVideoURI(videoUrl);
//        mVideoView.seekTo(currentPosition);

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(this, videoUrl);
        Bitmap thumb = retriever.getFrameAtTime(0);
        imgVideoView.setImageBitmap(thumb);

        btnPlayVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgVideoView.setVisibility(View.GONE);
                btnPlayVideo.setVisibility(View.GONE);
                mVideoView.setVisibility(View.VISIBLE);
                seekBar.setVisibility(View.VISIBLE);
                mVideoView.start();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b)
                    mVideoView.seekTo(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                imgVideoView.setVisibility(View.VISIBLE);
                btnPlayVideo.setVisibility(View.VISIBLE);
                mVideoView.setVisibility(View.GONE);
                seekBar.setVisibility(View.GONE);
            }
        });

        updateProgressBar();
    }

    private void updateProgressBar() {
        mHandler.postDelayed(updateTimeTask, 100);
    }

    private Runnable updateTimeTask = new Runnable() {
        public void run() {
            seekBar.setProgress(mVideoView.getCurrentPosition());
            seekBar.setMax(mVideoView.getDuration());
            mHandler.postDelayed(this, 100);
        }
    };

}
