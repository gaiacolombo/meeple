package it.unimib.lapecorafaquack.ui.profilo;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import it.unimib.lapecorafaquack.R;
import it.unimib.lapecorafaquack.adapter.GamesGroupAdapter;
import it.unimib.lapecorafaquack.model.Game;
import it.unimib.lapecorafaquack.model.GamesGroup;
import it.unimib.lapecorafaquack.model.User;
import it.unimib.lapecorafaquack.utils.ResponseCallback;

public class ProfiloFragment extends Fragment implements ResponseCallback {

    private static final String TAG = "ProfiloFragment";
    private View view;
    private ArrayList<Game> mToPlayGames;
    private ArrayList<Game> mPlayedGames;
    private ArrayList<Game> mFavouriteGames;
    private List<GamesGroup> mGamesGroupList;
    private GamesGroupAdapter mGamesGroupAdapter;

    private ProfiloViewModel profiloViewModel;

    private TextView username;
    private TextView email;
    private TextView playedGamesCount;
    private TextView favouriteGamesCount;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mToPlayGames = new ArrayList<>();
        mPlayedGames = new ArrayList<>();
        mFavouriteGames = new ArrayList<>();

        mGamesGroupList = new ArrayList<>();

        mGamesGroupList.add(new GamesGroup(getString(R.string.da_giocare), mToPlayGames));
        mGamesGroupList.add(new GamesGroup(getString(R.string.giocati), mPlayedGames));
        mGamesGroupList.add(new GamesGroup(getString(R.string.preferiti), mFavouriteGames));

        profiloViewModel = new ViewModelProvider(this).get(ProfiloViewModel.class);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.settings_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.settings_menu_item) {
            Log.d(TAG, "onOptionsItemSelected: " + item);
            ProfiloFragmentDirections.ActionSettings action = ProfiloFragmentDirections.actionSettings(true);
            Navigation.findNavController(view).navigate(action);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_profile, container, false);

        Log.d(TAG, "onCreateView");

        mToPlayGames.clear();
        mPlayedGames.clear();
        mFavouriteGames.clear();

        Button buttonAddFriend = view.findViewById(R.id.button_addFriend);
        buttonAddFriend.setVisibility(View.GONE);

        RecyclerView mRecyclerViewHome = view.findViewById(R.id.search_recyclerview);
        mRecyclerViewHome.setLayoutManager(new LinearLayoutManager(getContext()));

        mGamesGroupAdapter = new GamesGroupAdapter(mGamesGroupList, view, getActivity().getApplication());

        mRecyclerViewHome.setAdapter(mGamesGroupAdapter);

        username = view.findViewById(R.id.username_field);
        email = view.findViewById(R.id.email_field);
        playedGamesCount = view.findViewById(R.id.played_games);
        favouriteGamesCount = view.findViewById(R.id.favourite_games);

        User utente = profiloViewModel.getUtente();

        if (utente != null)
            Log.d(TAG, utente.toString());
        else
            Log.d(TAG, "utente == null");

        username.setText(utente.getUsername());
        email.setText(utente.getEmail());
        playedGamesCount.setText(String.valueOf(utente.getPlayedGames().size()));
        if (utente.getPlayedGames().size() == 1) {
            TextView textPlayed = view.findViewById(R.id.text_played);
            textPlayed.setText(R.string.giocato);
        }
        favouriteGamesCount.setText(String.valueOf(utente.getFavouriteGames().size()));
        if (utente.getPlayedGames().size() == 1) {
            TextView textFavourite = view.findViewById(R.id.text_favourites);
            textFavourite.setText(R.string.preferito);
        }

        profiloViewModel.getToPlayGamesLiveDataList().observe(getViewLifecycleOwner(), new Observer<List<Game>>() {
            @Override
            public void onChanged(List<Game> games) {
                mToPlayGames.addAll(profiloViewModel.filterGames(games));
                mGamesGroupAdapter.notifyDataSetChanged();
            }
        });

        profiloViewModel.getPlayedGamesLiveDataList().observe(getViewLifecycleOwner(), new Observer<List<Game>>() {
            @Override
            public void onChanged(List<Game> games) {
                mPlayedGames.addAll(profiloViewModel.filterGames(games));
                mGamesGroupAdapter.notifyDataSetChanged();
            }
        });

        profiloViewModel.getFavouriteGamesLiveDataList().observe(getViewLifecycleOwner(), new Observer<List<Game>>() {
            @Override
            public void onChanged(List<Game> games) {
                mFavouriteGames.addAll(profiloViewModel.filterGames(games));
                mGamesGroupAdapter.notifyDataSetChanged();
            }
        });

        ImageView profileImage = view.findViewById(R.id.profile_image);
        profileImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.bio)
                        .setMessage(utente.getBio())
                        .setPositiveButton(R.string.chiudi, null)
                        .setIcon(R.drawable.green_meeple)
                        .show();
                return true;
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResponse(List<Game> gamesList, String i) {
        Log.d(TAG, i);
        if (i.equals("addMissingGames")) {
            Log.d(TAG, i + gamesList.toString());
        }

        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mGamesGroupAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onFailure(String errorMessage) {
        Snackbar.make(requireActivity().findViewById(android.R.id.content), errorMessage, Snackbar.LENGTH_LONG).show();
    }
}