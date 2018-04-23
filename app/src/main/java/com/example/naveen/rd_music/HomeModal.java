package com.example.naveen.rd_music;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;

import com.example.naveen.rd_music.modal.SongList;

import java.util.ArrayList;
import java.util.List;

public class HomeModal implements HomeModalImple {
    private List<SongList> songLists = new ArrayList<>();
    private String path;

    @Override
    public void fetchSongs(HomePresenter homePresenter, Context context) {

        final Cursor mCursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.ALBUM_ID}, null, null,
                "LOWER(" + MediaStore.Audio.Media.TITLE + ") ASC");

        int count = mCursor.getCount();

//        String[] songs = new String[count];
//        String[] mAudioPath = new String[count];

        int i = 0;
        if (mCursor.moveToFirst()) {
            do {
//                songs[i] = ;
//                mAudioPath[i] =

                SongList songList = new SongList();
                songList.setSongName(mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)));
                songList.setSongPath(mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
                int albumID = mCursor.getInt(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
                songList.setAlbumID(albumID);
                songList.setAlbumArtPath(getAlbumArtPath(albumID, context));
                songList.setBitmap(getBitmap(getAlbumArtPath(albumID,context)));
                songLists.add(songList);
                i++;
            } while (mCursor.moveToNext());
        }
        mCursor.close();
        homePresenter.setSongsLsit(songLists);
    }


    private String getAlbumArtPath(int albumId, Context context) {
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART},
                MediaStore.Audio.Albums._ID + "=?",
                new String[]{String.valueOf(albumId)},
                null);


        if (cursor!=null && cursor.moveToFirst()) {
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
            // do whatever you need to do
        }

        return path;
    }


    private Bitmap getBitmap(String path){
        return BitmapFactory.decodeFile(path);
    }
}
