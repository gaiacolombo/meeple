package it.unimib.lapecorafaquack.ui.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

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
import it.unimib.lapecorafaquack.repository.GamesRepository;
import it.unimib.lapecorafaquack.repository.IGamesRepository;
import it.unimib.lapecorafaquack.repository.UsersRepository;
import it.unimib.lapecorafaquack.ui.login.LoginActivity;
import it.unimib.lapecorafaquack.utils.Constants;
import it.unimib.lapecorafaquack.utils.ResponseCallback;
import it.unimib.lapecorafaquack.utils.SharedPreferencesProvider;

public class HomeFragment extends Fragment implements ResponseCallback {

    private int count;

    private static final String TAG = "HomeFragment";

    private List<Game> mBestGames;
    private List<Game> mGamesByCategory;
    private List<Game> mNewestGames;
    private List<Game> mQuickGames;
    private List<Game> mGamesByAge;

    private List<GamesGroup> mGamesGroupList;
    private GamesGroupAdapter mGamesGroupAdapter;
    private IGamesRepository mIGamesRepository;
    private SharedPreferencesProvider mSharedPreferencesProvider;
    private User currentUser;

    private HomeViewModel homeViewModel;

    public HomeFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        count = 5;

        mBestGames = new ArrayList<>();
        mGamesByCategory = new ArrayList<>();
        mNewestGames = new ArrayList<>();
        mQuickGames = new ArrayList<>();
        mGamesByAge = new ArrayList<>();

        mGamesGroupList = new ArrayList<>();

        mGamesGroupList.add(new GamesGroup(getString(R.string.classifica), mBestGames));
        mGamesGroupList.add(new GamesGroup(getString(R.string.consigliati), mGamesByCategory));
        mGamesGroupList.add(new GamesGroup(getString(R.string.novita_quest_anno), mNewestGames));
        mGamesGroupList.add(new GamesGroup(getString(R.string.giochi_veloci), mQuickGames));

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        currentUser = homeViewModel.getCurrentUser();
        if(currentUser == null) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            SharedPreferences settings = this.getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, 0);
            settings.edit().putBoolean("logged", false).commit();
        }

        if(currentUser != null) {
            if (currentUser.isAdult()) {
                mGamesGroupList.add(new GamesGroup(getString(R.string.per_adulti), mGamesByAge));
            } else {
                mGamesGroupList.add(new GamesGroup(getString(R.string.per_bambini), mGamesByAge));
            }
        }
        else {
            mGamesGroupList.add(new GamesGroup(getString(R.string.per_bambini), mGamesByAge));
        }
        mIGamesRepository = new GamesRepository(requireActivity().getApplication(), this);
        mSharedPreferencesProvider = new SharedPreferencesProvider(requireActivity().getApplication());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        mBestGames.clear();
        mGamesByCategory.clear();
        mNewestGames.clear();
        mQuickGames.clear();
        mGamesByAge.clear();

        RecyclerView mRecyclerViewHome = view.findViewById(R.id.search_recyclerview);
        mRecyclerViewHome.setLayoutManager(new LinearLayoutManager(getContext()));

        mGamesGroupAdapter = new GamesGroupAdapter(mGamesGroupList, view, getActivity().getApplication());

        mRecyclerViewHome.setAdapter(mGamesGroupAdapter);

        SearchView searchView = view.findViewById(R.id.search_view);
        searchView.setQueryHint(getResources().getString(R.string.cerca_un_gioco));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "search");
                HomeFragmentDirections.ActionSearch action = HomeFragmentDirections.actionSearch(query);
                Navigation.findNavController(view).navigate(action);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "writing");
                return false;
            }
        });

        UsersRepository usersRepository = new UsersRepository(getActivity().getApplication());

        homeViewModel.getGamesByCategoryLiveDataList().observe(getViewLifecycleOwner(), new Observer<List<Game>>() {
            @Override
            public void onChanged(List<Game> games) {
                mGamesByCategory.addAll(games);
                mGamesGroupAdapter.notifyDataSetChanged();
            }
        });

        homeViewModel.getBestGamesLiveDataList().observe(getViewLifecycleOwner(), new Observer<List<Game>>() {
            @Override
            public void onChanged(List<Game> games) {
                mBestGames.addAll(games);
                Log.d(TAG, "onChanged: " + mBestGames.size());
                mGamesGroupAdapter.notifyDataSetChanged();
            }
        });

        homeViewModel.getNewestGamesLiveDataList().observe(getViewLifecycleOwner(), new Observer<List<Game>>() {
            @Override
            public void onChanged(List<Game> games) {
                mNewestGames.addAll(games);
                mGamesGroupAdapter.notifyDataSetChanged();
            }
        });

        homeViewModel.getQuickGamesLiveDataList().observe(getViewLifecycleOwner(), new Observer<List<Game>>() {
            @Override
            public void onChanged(List<Game> games) {
                mQuickGames.addAll(games);
                mGamesGroupAdapter.notifyDataSetChanged();
            }
        });

        homeViewModel.getGamesByAgeLiveDataList().observe(getViewLifecycleOwner(), new Observer<List<Game>>() {
            @Override
            public void onChanged(List<Game> games) {
                mGamesByAge.addAll(games);
                mGamesGroupAdapter.notifyDataSetChanged();
            }
        });
        return view;
    }


    @Override
    public void onResponse(List<Game> gamesList, String i) {
        if(count == 0) {
            try {
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mGamesGroupAdapter.notifyDataSetChanged();
                    }
                });
            }
            catch (Exception e) {
                Log.d(TAG, "excpetion non capita (neanche da Andre): " + e);
            }
        }


    }

    @Override
    public void onFailure(String errorMessage) {
        Snackbar.make(requireActivity().findViewById(android.R.id.content), errorMessage, Snackbar.LENGTH_LONG).show();
    }
}