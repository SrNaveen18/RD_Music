package com.example.naveen.rd_music;


import com.example.naveen.rd_music.modal.SongList;

import java.util.List;

public interface HomeView {

    void initialize();

    void showProgressBar();

    void hideProgressBar();

    void setSongsAdapter(List<SongList> songLists);

    void showToast(String msg);

    void  setPlayMode(SongList songList);


    void startPlayAudio();
}
