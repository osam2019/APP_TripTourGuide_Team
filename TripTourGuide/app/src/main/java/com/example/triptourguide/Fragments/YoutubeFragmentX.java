package com.example.triptourguide.Fragments;


import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.triptourguide.DBopenHelper;
import com.example.triptourguide.ListnersAndAdapter.YtPlayerStateChangeListener;
import com.example.triptourguide.MainActivity;
import com.example.triptourguide.Models.CityTripEntity;
import com.example.triptourguide.Models.MusicItemEntity;
import com.example.triptourguide.Models.YoutubeMusicTitleAdapter;
import com.example.triptourguide.MusicRankCollector;
import com.example.triptourguide.R;
import com.example.triptourguide.ListnersAndAdapter.YtListener;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragmentX;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class YoutubeFragmentX extends Fragment {

    List<CityTripEntity> _cityTripEntity;


    public YoutubeFragmentX() {
        _cityTripEntity = new DBopenHelper(getActivity()).RetrieveTripDetail(MainActivity.db, MainActivity.GetCurrentTripName());

    }


    private static final String API_KEY = "AIzaSyA25f1_VvgFd8z8ohCNW8A5jCzjHE9kbag";


    private ListView musicNameListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_youtube_fragment_x, container, false);
        musicNameListView = rootView.findViewById(R.id.MusicTitleList);

        YouTubePlayerSupportFragmentX youTubePlayerFragment = YouTubePlayerSupportFragmentX.newInstance();

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.youtube_layout, youTubePlayerFragment).commit();

        youTubePlayerFragment.initialize(API_KEY, new YouTubePlayer.OnInitializedListener() {

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
                if (!wasRestored) {

                    Thread thread = new Thread(new YoutubeSearchRunner(getActivity(), player, musicNameListView));
                    thread.start();
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult error) {
                // YouTube error
                String errorMessage = error.toString();
                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
                Log.d("errorMessage:", errorMessage);
            }
        });

        return rootView;
    }

    class YoutubeSearchRunner implements Runnable {

        private YouTubePlayer _youtubePlayer;

        ListView _musicListView;
        Activity _context;

        public YoutubeSearchRunner(Activity context, YouTubePlayer youTubePlayer, ListView musicListView) {
            _context = context;
            _youtubePlayer = youTubePlayer;
            _musicListView = musicListView;
        }


        private List<SearchResult> GetYoutubeSearch(String searchTerm) {
            try {

                HttpTransport transport = new NetHttpTransport();
                JsonFactory jsonFactory = new JacksonFactory();
                GoogleCredential credential = new GoogleCredential();

                YouTube youtube = new YouTube.Builder(transport, jsonFactory, credential).setApplicationName("youtube-cmdline-search-sample").build();
                YouTube.Search.List search = youtube.search().list("id,snippet");
                search.setKey(API_KEY);
                search.setQ(searchTerm);
                search.setType("video");
                search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
                search.setMaxResults(1L);

                // Call the API and print results.
                SearchListResponse searchResponse = search.execute();
                List<SearchResult> searchResultList = searchResponse.getItems();
                if (searchResultList != null) {
                    return searchResultList;
                }
            } catch (GoogleJsonResponseException e) {
                System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                        + e.getDetails().getMessage());
            } catch (IOException e) {
                System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
            } catch (Throwable t) {
                t.printStackTrace();
            }
            return null;
        }

        @Override
        public void run() {
            String countryName = getProperCountryName(_cityTripEntity);

            List<String> rankedMusicNameList= MusicRankCollector.GetCountryMusicRank(countryName);

            final List<String> youTubeIdList = new ArrayList<>();
            final List<String> playableMusicNameList = new ArrayList<>();


            DBopenHelper dbHelper = new DBopenHelper(getActivity());
            List<MusicItemEntity> musicItemEntityList = dbHelper.getMusicRank(countryName);
            if (musicItemEntityList.size() < 20) {
                for (int i = 0; i < Math.min(20, rankedMusicNameList.size()); i++) {
                    String songName = rankedMusicNameList.get(i);

                    List<SearchResult> searchResults = GetYoutubeSearch(songName);

                    //In case youtube api runs out of quota!!!
                    // Youtube quota is very limited ㅠㅠ
                    if (searchResults == null) {
                        for (int j = 0; j < songsTitles.length; j++) {
                            youTubeIdList.add(videoIds[j]);
                            playableMusicNameList.add(songsTitles[i]);
                        }

                    } else if (!searchResults.isEmpty()) {
                        youTubeIdList.add(searchResults.get(0).getId().getVideoId());
                        playableMusicNameList.add(rankedMusicNameList.get(i));
                    }
                }
                dbHelper.updateMusicRank(countryName, rankedMusicNameList, youTubeIdList);
            } else {
                for (MusicItemEntity musicItemEntity : musicItemEntityList) {
                    youTubeIdList.add(musicItemEntity.VideoId);
                    playableMusicNameList.add(musicItemEntity.MusicTitle);
                }
            }


            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    _musicListView.setAdapter(new YoutubeMusicTitleAdapter(playableMusicNameList, _context));
                }
            });

            _youtubePlayer.setPlaylistEventListener(new YtListener(playableMusicNameList, _musicListView, _context));
            _youtubePlayer.setPlayerStateChangeListener(new YtPlayerStateChangeListener(_musicListView));

            _youtubePlayer.cueVideos(youTubeIdList);
            _youtubePlayer.play();

        }

        private String getProperCountryName(List<CityTripEntity> cityTripEntityList) {
            long currentDiff = Math.abs(cityTripEntityList.get(0).StartDate.getTimeInMillis() - Calendar.getInstance().getTimeInMillis());
            String closestCountryName = cityTripEntityList.get(0).CountryName;
            for (CityTripEntity cityTripEntity : cityTripEntityList) {
                if (currentDiff > Math.abs(cityTripEntity.StartDate.getTimeInMillis() - Calendar.getInstance().getTimeInMillis())) {
                    currentDiff = Math.abs(cityTripEntity.StartDate.getTimeInMillis() - Calendar.getInstance().getTimeInMillis());
                    closestCountryName = cityTripEntity.CountryName;
                }
                if (currentDiff > Math.abs(cityTripEntity.EndDate.getTimeInMillis() - Calendar.getInstance().getTimeInMillis())) {
                    currentDiff = Math.abs(cityTripEntity.EndDate.getTimeInMillis() - Calendar.getInstance().getTimeInMillis());
                    closestCountryName = cityTripEntity.CountryName;
                }
            }
            return closestCountryName;
        }
    }

    String[] songsTitles = new String[]{
            "Truth Hurts", "Senorita", "Someone you Loved", "Circles", "No Guidance", "HIGHEST IN THE ROOM", "RAN$OM", "Bad Guy", "Panini", "Bandit", "10,000 Hours", "Memories", "Goodbyes", "Beautiful People", "I Don't Care", "Old Town Road", "Lights Up", "Talk", "Sunflower (Spider-Man:Into the Spider-Verse)", "Good As Hell"
    };
    String[] videoIds = new String[]{
            "P00HMxdsVZI", "Pkh8UtuejGw", "zABLecsR5UE", "wXhTHyIgQ_U", "6L_k74BOLag", "tfSS1e3kYeo", "1XzY2ij_vL4", "DyDfgMOUjCI", "bXcSLI58-h8", "Sw5fNI400E4", "Y2E71oe0aSM", "SlPhMPnQ58k", "QumPpMqaxxo", "mj0XInqZMHY", "y83x7MgzWOA", "w2Ov5jzm3j8", "9NZvM1918_E", "hE2Ira-Cwxo", "ApXoWvfEYVU", "SmbmeOgWsqE"
    };



}
