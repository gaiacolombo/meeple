package it.unimib.lapecorafaquack.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import it.unimib.lapecorafaquack.model.Game;
import it.unimib.lapecorafaquack.model.User;
import it.unimib.lapecorafaquack.repository.GamesRepository;
import it.unimib.lapecorafaquack.repository.UsersRepository;
import it.unimib.lapecorafaquack.utils.ResponseCallback;

public class HomeViewModel extends AndroidViewModel implements ResponseCallback {

    private LiveData<List<Game>> bestGamesLiveDataList;
    private LiveData<List<Game>> quickGamesLiveDataList;
    private LiveData<List<Game>> newestGamesLiveDataList;
    private LiveData<List<Game>> gamesByCategoryLiveDataList;
    private LiveData<List<Game>> gamesByAgeLiveDataList;

    private GamesRepository gamesRepository;
    private UsersRepository usersRepository;

    private User currentUser;
    private String currentUserEmail;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        gamesRepository = new GamesRepository(application, this);
        usersRepository = new UsersRepository(application);

        bestGamesLiveDataList = gamesRepository.fetchBestGames();
        quickGamesLiveDataList = gamesRepository.fetchQuickGames();
        newestGamesLiveDataList = gamesRepository.fetchNewestGames();
        gamesByCategoryLiveDataList = gamesRepository.fetchGamesByCategory();
        gamesByAgeLiveDataList = gamesRepository.fetchGamesByAge();

        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            logOut();
        }
        else {
            currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            currentUser = usersRepository.getUser(currentUserEmail);
        }
    }

    private void logOut() {
        FirebaseAuth.getInstance().signOut();
    }

    public User getCurrentUser() {
        if(currentUser != null) {
            return currentUser;
        }
        else {
            logOut();
            return null;
        }
    }

    public LiveData<List<Game>> getBestGamesLiveDataList() {
        return bestGamesLiveDataList;
    }

    public LiveData<List<Game>> getGamesByCategoryLiveDataList() {
        return gamesByCategoryLiveDataList;
    }

    public LiveData<List<Game>> getQuickGamesLiveDataList() {
        return quickGamesLiveDataList;
    }

    public LiveData<List<Game>> getNewestGamesLiveDataList() {
        return newestGamesLiveDataList;
    }

    public LiveData<List<Game>> getGamesByAgeLiveDataList() {
        return gamesByAgeLiveDataList;
    }

    @Override
    public void onResponse(List<Game> gamesList, String i) {

    }

    @Override
    public void onFailure(String errorMessage) {

    }
}
