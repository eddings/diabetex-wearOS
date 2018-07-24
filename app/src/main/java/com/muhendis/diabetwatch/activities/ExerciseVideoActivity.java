package com.muhendis.diabetwatch.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.muhendis.Keys;
import com.muhendis.diabetwatch.R;

import java.io.File;

public class ExerciseVideoActivity extends WearableActivity {

    SimpleExoPlayerView mExoPlayerView;
    SimpleExoPlayer mExoPlayer;
    private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    private static final int BUFFER_SEGMENT_COUNT = 256;
    String localVideoPath,localImagePath,videoUrl,imageUrl;
    File videoFile,imageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_video);

        // Enables Always-on
        setAmbientEnabled();

        Intent intent = getIntent();
        localVideoPath = intent.getStringExtra(Keys.LOCAL_VIDEO_KEY);
        localImagePath = intent.getStringExtra(Keys.LOCAL_IMAGE_KEY);

        imageUrl = intent.getStringExtra(Keys.IMAGE_KEY);
        videoUrl = intent.getStringExtra(Keys.VIDEO_KEY);

        Log.d("exerciseVideo","ımage file path:"+localImagePath);
        videoFile = new File(localVideoPath);
        imageFile = new File(localImagePath);
        ImageView imageView = findViewById(R.id.videoGifImage);

        if(videoFile.exists()){
            createExoPlayer(localVideoPath);
        }
        else{
            createExoPlayer(videoUrl);
        }


        RequestOptions requestOptions = new RequestOptions();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);

        DrawableTransitionOptions to = new DrawableTransitionOptions();
        to.crossFade();
        if(imageFile.exists()){
            Glide.with(this).load(imageFile).transition(to).apply(requestOptions).into(imageView).clearOnDetach();
        }
        else{
            Glide.with(this).load(imageUrl).transition(to).apply(requestOptions).into(imageView).clearOnDetach();
        }




    }

    private void createExoPlayer(String url)
    {
        mExoPlayerView = findViewById(R.id.mExoPlayer);
        Context context = getApplicationContext();
        // 1. Create a default TrackSelector
        Handler mainHandler = new Handler();
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);

        DefaultRenderersFactory drf = new DefaultRenderersFactory(this,null,DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER);
        // 2. Create the player
        mExoPlayer =
                ExoPlayerFactory.newSimpleInstance(drf, trackSelector);

        mExoPlayerView.setPlayer(mExoPlayer);

        // Measures bandwidth during playback. Can be null if not required.
        DefaultBandwidthMeter bandwidthMtr = new DefaultBandwidthMeter();

        //Uri mp4VideoUri = Uri.parse("http://diabetexercise.com/videos/4.mp4");

        //Uri mp4VideoUri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.test);


        Uri mp4VideoUri = Uri.fromFile(videoFile);

        Log.d("ExerciseVideoActivity","VİDEO URL:"+url);
        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, "Diabetex"), bandwidthMtr);
        // This is the MediaSource representing the media to be played.
        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(mp4VideoUri);
        mExoPlayer.addListener(new Player.DefaultEventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                super.onPlayerStateChanged(playWhenReady, playbackState);
                //Log.d(TAG,"PLAYER STATE CHANGED");
                //Log.d(TAG,"Playback state: "+playbackState);
            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
                super.onPlaybackParametersChanged(playbackParameters);
                //Log.d(TAG,"PLAYBACK PARAMETERS CHANGED");
            }

        });
        // Prepare the player with the source.
        mExoPlayer.prepare(videoSource);
        mExoPlayer.setPlayWhenReady(true);
        mExoPlayer.setRepeatMode(Player.REPEAT_MODE_ALL);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mExoPlayer.stop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mExoPlayer.setPlayWhenReady(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mExoPlayer.setPlayWhenReady(true);
    }
}
