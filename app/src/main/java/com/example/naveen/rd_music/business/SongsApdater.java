package com.example.naveen.rd_music.business;


import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.naveen.rd_music.HomeActivity;
import com.example.naveen.rd_music.R;
import com.example.naveen.rd_music.modal.SongList;

import java.util.List;

public class SongsApdater extends RecyclerView.Adapter<SongsViewHolder> {
    private List<SongList> songList;
    private View.OnClickListener listener;
    private HomeActivity homeActivity;

    public SongsApdater(List<SongList> songList, View.OnClickListener listener, HomeActivity homeActivity) {
        this.songList = songList;
        this.listener = listener;
        this.homeActivity = homeActivity;
    }

    @Override
    public SongsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.songs_adapter, parent, false);
        GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) view.getLayoutParams();
        lp.height = parent.getMeasuredHeight() / 3;
        view.setLayoutParams(lp);

//        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.songs_adapter,parent,false);
        return new SongsViewHolder(view,homeActivity);
    }

    @Override
    public void onBindViewHolder(SongsViewHolder holder, int position) {
       holder.onBind(listener,songList.get(position),position);
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }
}
