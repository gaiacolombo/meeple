package it.unimib.lapecorafaquack.ui.friendDetail;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.unimib.lapecorafaquack.R;
import it.unimib.lapecorafaquack.adapter.GamesGroupAdapter;
import it.unimib.lapecorafaquack.model.Game;
import it.unimib.lapecorafaquack.model.GamesGroup;
import it.unimib.lapecorafaquack.model.User;

public class FriendDetailsFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "FriendDetailsFragment";

    private User user;
    private ArrayList<Game> mToPlayGames;
    private ArrayList<Game> mPlayedGames;
    private ArrayList<Game> mFavouriteGames;
    private GamesGroupAdapter mGamesGroupAdapter;
    private List<GamesGroup> mGamesGroupList;

    private User loggedUser;

    private Button buttonAddFriend;

    private FriendDetailViewModel friendDetailViewModel;

    public FriendDetailsFragment() {
    }

    public static FriendDetailsFragment newInstance() {
        FriendDetailsFragment fragment = new FriendDetailsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mToPlayGames = new ArrayList<>();
        mPlayedGames = new ArrayList<>();
        mFavouriteGames = new ArrayList<>();

        mGamesGroupList = new ArrayList<>();

        mGamesGroupList.add(new GamesGroup(getString(R.string.da_giocare), mToPlayGames));
        mGamesGroupList.add(new GamesGroup(getString(R.string.giocati), mPlayedGames));
        mGamesGroupList.add(new GamesGroup(getString(R.string.preferiti), mFavouriteGames));

        user = FriendDetailsFragmentArgs.fromBundle(getArguments()).getUser();
        Log.d(TAG, user.toString());

        friendDetailViewModel = new ViewModelProvider(this).get(FriendDetailViewModel.class);
        friendDetailViewModel.initialize(user);
        loggedUser = friendDetailViewModel.getLoggedUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        buttonAddFriend = view.findViewById(R.id.button_addFriend);
        buttonAddFriend.setVisibility(View.VISIBLE);


        if(loggedUser.getFriends().contains(user.getEmail())) {
            buttonAddFriend.setText(buttonAddFriend.getText() + " ✓");
        }

        buttonAddFriend.setOnClickListener(this);

        RecyclerView mRecyclerViewHome = view.findViewById(R.id.search_recyclerview);
        mRecyclerViewHome.setLayoutManager(new LinearLayoutManager(getContext()));

        mGamesGroupAdapter = new GamesGroupAdapter(mGamesGroupList, view, getActivity().getApplication());

        mRecyclerViewHome.setAdapter(mGamesGroupAdapter);

        TextView username = view.findViewById(R.id.username_field);
        username.setText(user.getUsername());

        TextView email = view.findViewById(R.id.email_field);
        email.setText(user.getEmail());

        TextView favouriteGamesCount = view.findViewById(R.id.favourite_games);
        if(user.getPlayedGames().size() == 1)  {
            TextView textPlayed = view.findViewById(R.id.text_played);
            textPlayed.setText(R.string.giocato);
        }
        if(user.getPlayedGames().size() == 1)  {
            TextView textFavourite = view.findViewById(R.id.text_favourites);
            textFavourite.setText(R.string.preferito);
        }

        TextView playedGames = view.findViewById(R.id.played_games);
        playedGames.setText(String.valueOf(user.getPlayedGames().size()));

        TextView favouriteGames = view.findViewById(R.id.favourite_games);
        favouriteGames.setText(String.valueOf(user.getFavouriteGames().size()));

        friendDetailViewModel.addMissingGames(user);
        Log.d(TAG, "user " + user.toString());

        friendDetailViewModel.getToPlayGames().observe(getViewLifecycleOwner(), new Observer<List<Game>>() {
            @Override
            public void onChanged(List<Game> games) {
                mToPlayGames.addAll(friendDetailViewModel.filterGames(games));
                mGamesGroupAdapter.notifyDataSetChanged();
            }
        });

        friendDetailViewModel.getPlayedGames().observe(getViewLifecycleOwner(), new Observer<List<Game>>() {
            @Override
            public void onChanged(List<Game> games) {
                mPlayedGames.addAll(friendDetailViewModel.filterGames(games));
                mGamesGroupAdapter.notifyDataSetChanged();
            }
        });

        friendDetailViewModel.getFavouriteGames().observe(getViewLifecycleOwner(), new Observer<List<Game>>() {
            @Override
            public void onChanged(List<Game> games) {
                mFavouriteGames.addAll(friendDetailViewModel.filterGames(games));
                mGamesGroupAdapter.notifyDataSetChanged();
            }
        });

        ImageView profileImage = view.findViewById(R.id.profile_image);
        profileImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.bio)
                        .setMessage(user.getBio())
                        .setPositiveButton(R.string.chiudi, null)
                        .setIcon(R.drawable.green_meeple)
                        .show();
                return true;
            }
        });

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_addFriend:
                if(loggedUser.getFriends().contains(user.getEmail())) {
                    buttonAddFriend.setText(getString(R.string.aggiungi_amico));
                    friendDetailViewModel.removeFriend(user);
                }
                else {
                    buttonAddFriend.setText(buttonAddFriend.getText() + " ✓");
                    friendDetailViewModel.addFriend(user);
                }
                break;
        }
    }
}