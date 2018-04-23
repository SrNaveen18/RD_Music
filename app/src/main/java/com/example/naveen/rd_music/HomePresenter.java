package com.example.naveen.rd_music;


import android.content.Context;
import android.view.View;

import com.example.naveen.rd_music.modal.SongList;

import java.util.List;

public class HomePresenter implements HomePresenterImple {
    private HomeView homeView;
    private HomeModal homeModal;
    private Context context;

    HomePresenter(HomeView homeView, HomeModal homeModal, Context context) {
        this.homeView = homeView;
        this.homeModal = homeModal;
        this.context = context;
    }

    @Override
    public void getSongList() {
        if (homeView != null) {
            homeView.showProgressBar();
            homeModal.fetchSongs(this, context);
        }
    }

    @Override
    public void setSongsLsit(List<SongList> songLists) {
        if (homeView != null) {
            homeView.hideProgressBar();
            homeView.setSongsAdapter(songLists);
        }
    }

    void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtSongName:
                SongList songList = (SongList) view.getTag(R.id.txtSongName);
//                homeView.showToast(songList.getSongName());
                homeView.setPlayMode(songList);
                break;
            case R.id.btnPlayPause:
                homeView.startPlayAudio();
                break;
            default:
                break;
        }
    }
}
