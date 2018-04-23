package com.example.naveen.rd_music;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.naveen.rd_music.business.SongsApdater;
import com.example.naveen.rd_music.helper.SeekBarUpdate;
import com.example.naveen.rd_music.modal.SongList;
import com.example.naveen.rd_music.services.BackGroundServices;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity implements HomeView,View.OnClickListener,SeekBarUpdate {

    public static final String Broadcast_PLAY_NEW_AUDIO = "com.valdioveliu.valdio.audioplayer.PlayNewAudio";

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.txtSongName)
    TextView txtSongName;
    @BindView(R.id.imgAlbumArt)
    ImageView imgAlbumArt;
    @BindView(R.id.lnrBottomSheet)
    LinearLayout lnrBottomSheet;
    @BindView(R.id.lnrbtmSongName)
    LinearLayout lnrbtmSongName;

    @BindView(R.id.toolBar)
    Toolbar toolbar;
    @BindView(R.id.appBarLayout)
    AppBarLayout appBarLayout;
    @BindView(R.id.btnPlayPause)
    ImageView btnPlayPause;

    @BindView(R.id.seekBar)
    SeekBar seekBar;

    @BindView(R.id.txtCurrentTime)
    TextView txtCurrentTime;
    @BindView(R.id.txtTotalTime)
    TextView txtTotalTime;

    private HomePresenter homePresenter;
    private BottomSheetBehavior bottomSheetBehavior;
    private List<SongList> songLists;
    private SongList clickedSongList;

    private BackGroundServices player;
    boolean serviceBound = false;

    private long maxSecond = 0;
    private long maxMinutes = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        initialize();
    }

    @Override
    public void initialize() {
        btnPlayPause.setOnClickListener(this);

        homePresenter = new HomePresenter(this, new HomeModal(), this);
        GridLayoutManager linearLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(linearLayoutManager);

        bottomSheetBehavior = BottomSheetBehavior.from(lnrBottomSheet);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        lnrbtmSongName.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                        txtSongName.setTextColor(Color.WHITE);
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        lnrbtmSongName.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                        txtSongName.setTextColor(Color.WHITE);
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        lnrbtmSongName.setBackgroundColor(Color.WHITE);
                        txtSongName.setTextColor(Color.BLACK);
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        lnrbtmSongName.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                        txtSongName.setTextColor(Color.WHITE);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        homePresenter.getSongList();
    }

    @Override
    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void setSongsAdapter(List<SongList> songLists) {
        this.songLists = songLists;
        SongsApdater apdater = new SongsApdater(songLists, this, this);
        recyclerView.setAdapter(apdater);
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void setPlayMode(SongList songList) {
        clickedSongList = songList;
        imgAlbumArt.setImageBitmap(songList.getBitmap());
        txtSongName.setText(songList.getSongName());
    }

    @Override
    public void startPlayAudio() {
        if (clickedSongList != null) {
            playAudio(clickedSongList.getSongPath());
        }
    }


    @Override
    public void onClick(View view) {
        homePresenter.onClick(view);
    }


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            BackGroundServices.LocalBinder binder = (BackGroundServices.LocalBinder) service;
            player = binder.getServices();
            serviceBound = true;

            Toast.makeText(HomeActivity.this, "Service Bound", Toast.LENGTH_SHORT).show();
            player.registerHomeActivity(HomeActivity.this);


        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            serviceBound = false;
        }

    };


    private void playAudio(String media) {
        //Check is service is active
        if (!serviceBound) {
            Intent playerIntent = new Intent(this, BackGroundServices.class);
            playerIntent.putExtra("media", media);
            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            player.nextSong(media);
            //Service is active
            //Send media with BroadcastReceiver
        }
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("ServiceState", serviceBound);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        serviceBound = savedInstanceState.getBoolean("ServiceState");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceBound) {
            unbindService(serviceConnection);
            //service is active
            player.stopSelf();
        }
    }


    private Handler handler = new Handler();

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (player.getMediaPlayer() != null) {
                seekBar.setProgress(player.getMediaPlayer().getCurrentPosition());
                Log.w("HOMECurrentPosition", player.getMediaPlayer().getCurrentPosition() + "");

                long audioSeconds = TimeUnit.MILLISECONDS.toSeconds(seekBar.getProgress());
                long audioMinutes = TimeUnit.MILLISECONDS.toMinutes(seekBar.getProgress());
                if (audioSeconds >= 60) {
                    audioSeconds = audioSeconds % 60;
                }
                if (audioMinutes >= 60) {
                    audioMinutes = audioMinutes % 60;
                }
                txtCurrentTime.setText(audioMinutes +"\t"+":" + audioSeconds);
                maxSecond = TimeUnit.MILLISECONDS.toSeconds(seekBar.getMax());
                maxMinutes = TimeUnit.MILLISECONDS.toMinutes(seekBar.getMax());
                if (maxSecond >= 60) {
                    maxSecond = maxSecond % 60;
                }
                txtTotalTime.setText(maxMinutes +"\t"+":" + maxSecond);
                handler.postDelayed(runnable, 1000);
            }
        }
    };

    @Override
    public void mediaPlayerInitialized() {
        if (player.getMediaPlayer() != null) {
            seekBar.setMax(player.getMediaPlayer().getDuration());
            Log.w("HOMEDuration", player.getMediaPlayer().getDuration() + "");
            handler.postDelayed(runnable, 1000);
            seekBar.setOnSeekBarChangeListener(player);
        }
    }
}
