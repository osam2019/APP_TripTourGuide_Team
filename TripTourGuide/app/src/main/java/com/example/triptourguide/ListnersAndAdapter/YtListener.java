package com.example.triptourguide.ListnersAndAdapter;

import android.app.Activity;
import android.graphics.Color;
import android.widget.ListView;
import android.widget.TextView;

import com.example.triptourguide.Models.YoutubeMusicTitleAdapter;
import com.example.triptourguide.R;
import com.google.android.youtube.player.YouTubePlayer;

import java.util.List;

public class YtListener implements YouTubePlayer.PlaylistEventListener {

    private List<String> _musicNameList;
    private ListView _musicListView;
    private Activity _context;
    private int _currentInd = 0;
    private int _backGroundColor = Color.parseColor("#804A98CC");

    public YtListener(List<String> musicNameList, ListView musicListView, Activity context) {
        _musicNameList = musicNameList;
        _musicListView = musicListView;
        _context = context;
    }

    @Override
    public void onPrevious() {
        ((YoutubeMusicTitleAdapter)_musicListView.getAdapter()).Current_Position =
                Math.max(0, ((YoutubeMusicTitleAdapter)_musicListView.getAdapter()).Current_Position - 1);
        _currentInd = Math.max(0, _currentInd - 1);
        UpdateMusicListVIew();
    }

    @Override
    public void onNext() {
        ((YoutubeMusicTitleAdapter)_musicListView.getAdapter()).Current_Position =
                Math.min(_musicNameList.size(), ((YoutubeMusicTitleAdapter)_musicListView.getAdapter()).Current_Position + 1);
        _currentInd = Math.min(_musicNameList.size(), _currentInd + 1);
        UpdateMusicListVIew();
    }

    @Override
    public void onPlaylistEnded() {

    }

    private void UpdateMusicListVIew() {
        _context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView v = ((YoutubeMusicTitleAdapter)_musicListView.getAdapter()).getViewAt(_currentInd).findViewById(R.id.music_title);
                v.setBackgroundColor(Color.RED);
                if (_currentInd != 0) {
                    TextView vp = ((YoutubeMusicTitleAdapter)_musicListView.getAdapter()).getViewAt(_currentInd - 1).findViewById(R.id.music_title);
                    vp.setBackgroundColor(_backGroundColor);
                }
                if (_currentInd < _musicNameList.size() - 1) {
                    if (((YoutubeMusicTitleAdapter)_musicListView.getAdapter()).getViewAt(_currentInd + 1) == null)
                        return;
                    TextView vp = ((YoutubeMusicTitleAdapter)_musicListView.getAdapter()).getViewAt(_currentInd + 1).findViewById(R.id.music_title);
                    vp.setBackgroundColor(_backGroundColor);
                }
            }
        });
    }




}
