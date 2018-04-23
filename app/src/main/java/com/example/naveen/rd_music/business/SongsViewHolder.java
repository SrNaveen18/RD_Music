package com.example.naveen.rd_music.business;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.example.naveen.rd_music.HomeActivity;
import com.example.naveen.rd_music.R;
import com.example.naveen.rd_music.modal.SongList;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

class SongsViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.txtSongName)
    TextView txtSongName;
    @BindView(R.id.imgAlbumArt)
    ImageView imgAlbumArt;

    private HomeActivity homeActivity;

    SongsViewHolder(View itemView, HomeActivity homeActivity) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.homeActivity = homeActivity;
    }

    void onBind(View.OnClickListener listener, SongList songList, int position) {
        txtSongName.setText(songList.getSongName());
        txtSongName.setTag(R.id.txtSongName, songList);
        txtSongName.setOnClickListener(listener);

       // Bitmap bitmap = BitmapFactory.decodeFile(songList.getAlbumArtPath());
        imgAlbumArt.setImageBitmap(songList.getBitmap());

//        Uri uri = Uri.fromFile(new File(songList.getAlbumArtPath()));
//        Glide.with(homeActivity).load(uri).into(imgAlbumArt);
    }
}
