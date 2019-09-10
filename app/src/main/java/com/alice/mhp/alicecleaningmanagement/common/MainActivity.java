package com.alice.mhp.alicecleaningmanagement.common;

import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.alice.mhp.adapter.YoutubeVideoAdapter;
import com.alice.mhp.alicecleaningmanagement.R;
import com.alice.mhp.common.AppSettings;
import com.alice.mhp.common.RecyclerViewOnClickListener;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import java.util.Collections;

import java.util.ArrayList;

public class MainActivity extends CommonActivity {

    private RecyclerView recyclerView;
    //youtube player fragment
    private YouTubePlayerSupportFragment youTubePlayerFragment;
    private ArrayList<String> youtubeVideoArrayList;

    //youtube player to play video when new video selected
    private YouTubePlayer youTubePlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle(AppSettings.COMPANY_ID);

            generateDummyVideoList();
            initializeYoutubePlayer();
            setUpRecyclerView();
            populateRecyclerView();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * initialize youtube player via Fragment and get instance of YoutubePlayer
     */
    private void initializeYoutubePlayer() {

        try {
            youTubePlayerFragment = (YouTubePlayerSupportFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.youtube_player_fragment);

            if (youTubePlayerFragment == null)
                return;

            youTubePlayerFragment.initialize(AppSettings.YOUTUBE_PLAYER_KEY, new YouTubePlayer.OnInitializedListener() {

                @Override
                public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player,
                                                    boolean wasRestored) {
                    if (!wasRestored) {
                        youTubePlayer = player;

                        //set the player style default
                        youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);

                        //cue the 1st video by default
                        youTubePlayer.cueVideo(youtubeVideoArrayList.get(0));
                    }
                }

                @Override
                public void onInitializationFailure(YouTubePlayer.Provider arg0, YouTubeInitializationResult arg1) {

                    //print or show error if initialization failed
                    Log.e("YouTube==", "Youtube Player View initialization failed");
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
            * setup the recycler view here
     */
    private void setUpRecyclerView() {

        try {
            recyclerView = findViewById(R.id.recycler_view);
            recyclerView.setHasFixedSize(true);

            //Horizontal direction recycler view
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(linearLayoutManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * populate the recycler view and implement the click event here
     */
    private void populateRecyclerView() {
        try {
            final YoutubeVideoAdapter adapter = new YoutubeVideoAdapter(this, youtubeVideoArrayList);
            recyclerView.setAdapter(adapter);

            //set click event
            recyclerView.addOnItemTouchListener(new RecyclerViewOnClickListener(this, new RecyclerViewOnClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {

                    if (youTubePlayerFragment != null && youTubePlayer != null) {
                        //update selected position
                        adapter.setSelectedPosition(position);

                        //load selected video
                        youTubePlayer.cueVideo(youtubeVideoArrayList.get(position));
                    }

                }
            }));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * method to generate dummy array list of videos
     */
    private void generateDummyVideoList() {

        try {
            youtubeVideoArrayList = new ArrayList<>();

            //get the video id array from strings.xml
            String[] videoIDArray = getResources().getStringArray(R.array.video_id_array);

            //add all videos to array list
            Collections.addAll(youtubeVideoArrayList, videoIDArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
