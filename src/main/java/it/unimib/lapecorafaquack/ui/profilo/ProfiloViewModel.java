package it.unimib.lapecorafaquack.ui.profilo;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import it.unimib.lapecorafaquack.model.Game;
import it.unimib.lapecorafaquack.model.User;
import it.unimib.lapecorafaquack.repository.GamesRepository;
import it.unimib.lapecorafaquack.repository.UsersRepository;
import it.unimib.lapecorafaquack.utils.ResponseCallback;

public class ProfiloViewModel extends AndroidViewModel implements ResponseCallback {

    private static final String TAG = "ProfiloViewModel";

    private LiveData<List<Game>> toPlayGamesLiveDataList;
    private LiveData<List<Game>> playedGamesLiveDataList;
    private LiveData<List<Game>> favouriteGamesLiveDataList;

    private GamesRepository gamesRepository;
    private UsersRepository usersRepository;
    private User utente;

    public ProfiloViewModel(@NonNull Application application) {
        super(application);
        gamesRepository = new GamesRepository(application, this);
        usersRepository = new UsersRepository(application);
        String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        utente = usersRepository.getUser(currentUserEmail);

        toPlayGamesLiveDataList = gamesRepository.getToPlayGamesLiveDataList();
        playedGamesLiveDataList = gamesRepository.getPlayedGamesLiveDataList();
        favouriteGamesLiveDataList = gamesRepository.getFavouriteGamesLiveDataList();

        addMissingGames();
    }

    public LiveData<List<Game>> getToPlayGamesLiveDataList() {
        return toPlayGamesLiveDataList;
    }

    public LiveData<List<Game>> getPlayedGamesLiveDataList() {
        return playedGamesLiveDataList;
    }

    public LiveData<List<Game>> getFavouriteGamesLiveDataList() {
        return favouriteGamesLiveDataList;
    }

    public User getUtente() {
        return utente;
    }

    private void addMissingGames() {
        ArrayList<String> mMissingGames = new ArrayList<>();
        try {
            for (int i = 0; i < utente.getToPlayGames().size(); i++) {
                if (!gamesRepository.isGamePresent(utente.getToPlayGames().get(i))) {
                    if (!mMissingGames.contains(utente.getToPlayGames().get(i))) {
                        mMissingGames.add(utente.getToPlayGames().get(i));
                    }
                }
            }
            for (int i = 0; i < utente.getPlayedGames().size(); i++) {
                if (!gamesRepository.isGamePresent(utente.getPlayedGames().get(i))) {
                    if (!mMissingGames.contains(utente.getPlayedGames().get(i))) {
                        mMissingGames.add(utente.getPlayedGames().get(i));
                    }
                }
            }
            for (int i = 0; i < utente.getFavouriteGames().size(); i++) {
                if (!gamesRepository.isGamePresent(utente.getFavouriteGames().get(i))) {
                    if (!mMissingGames.contains(utente.getFavouriteGames().get(i))) {
                        mMissingGames.add(utente.getFavouriteGames().get(i));
                    }
                }
            }
            if (!mMissingGames.isEmpty()) {
                Log.d(TAG, "missingGames: " + mMissingGames.toString());
                gamesRepository.addGames(mMissingGames);
                Log.d(TAG, "missingGames: " + mMissingGames.toString());
            }
        } catch (Exception e) {
            Log.d(TAG, "ECC: " + e.toString());
        }
    }

    public List<Game> filterGames(List<Game> games) {
        for (int i = 0; i < games.size(); i++) {
            if (games.get(i) != null) {
                if (!gamesRepository.isGamePresent(games.get(i).getId())) {
                    games.remove(games.get(i));
                    Log.d(TAG, "remove " + games.get(i).getName());
                }
            }
        }
        ArrayList<Game> gamesReverse = new ArrayList<>();
        for (int i = games.size() - 1; i >= 0; i--) {
            if (games.get(i) != null) {
                gamesReverse.add(games.get(i));
                Log.d(TAG, "add " + games.get(i).getName());
            }
        }
        return gamesReverse;
    }

    @Override
    public void onResponse(List<Game> gamesList, String i) {

    }

    @Override
    public void onFailure(String errorMessage) {

    }
}
