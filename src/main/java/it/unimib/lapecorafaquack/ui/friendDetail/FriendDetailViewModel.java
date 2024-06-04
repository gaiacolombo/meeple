package it.unimib.lapecorafaquack.ui.friendDetail;

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
import it.unimib.lapecorafaquack.repository.IGamesRepository;
import it.unimib.lapecorafaquack.repository.UsersRepository;
import it.unimib.lapecorafaquack.utils.ResponseCallback;

public class FriendDetailViewModel extends AndroidViewModel implements ResponseCallback {

    private static final String TAG = "FriendDetailViewModel";

    private IGamesRepository mIGamesRepository;
    private UsersRepository usersRepository;

    private LiveData<List<Game>> toPlayGames;
    private LiveData<List<Game>> playedGames;
    private LiveData<List<Game>> favouriteGames;

    private User loggedUser;
    private User user;

    public FriendDetailViewModel(@NonNull Application application) {
        super(application);

        mIGamesRepository = new GamesRepository(application, this);
        usersRepository = new UsersRepository(application);

        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        loggedUser = usersRepository.getUser(email);
    }

    public void initialize(User user) {
        this.user = user;
        toPlayGames = mIGamesRepository.getUserToPlayGamesLiveData(user);
        playedGames = mIGamesRepository.getUserPlayedGamesLiveData(user);
        favouriteGames = mIGamesRepository.getUserFavouriteGamesLiveData(user);
    }

    User getLoggedUser() {
        return loggedUser;
    }

    public LiveData<List<Game>> getToPlayGames() {
        return toPlayGames;
    }

    public LiveData<List<Game>> getPlayedGames() {
        return playedGames;
    }

    public LiveData<List<Game>> getFavouriteGames() {
        return favouriteGames;
    }

    public void addMissingGames(User user) {
        ArrayList<String> mMissingGames = new ArrayList<>();
        try {
            for (int i = 0; i < user.getToPlayGames().size(); i++) {
                if(!mIGamesRepository.isGamePresent(user.getToPlayGames().get(i))){
                    if(!mMissingGames.contains(user.getToPlayGames().get(i))){
                        mMissingGames.add(user.getToPlayGames().get(i));
                    }
                }
            }
            for (int i = 0; i < user.getPlayedGames().size(); i++) {
                if(!mIGamesRepository.isGamePresent(user.getPlayedGames().get(i))){
                    if(!mMissingGames.contains(user.getPlayedGames().get(i))){
                        mMissingGames.add(user.getPlayedGames().get(i));
                    }
                }
            }
            for (int i = 0; i < user.getFavouriteGames().size(); i++) {
                if(!mIGamesRepository.isGamePresent(user.getFavouriteGames().get(i))){
                    if(!mMissingGames.contains(user.getFavouriteGames().get(i))){
                        mMissingGames.add(user.getFavouriteGames().get(i));
                    }
                }
            }
            if(!mMissingGames.isEmpty()){
                mIGamesRepository.addGames(mMissingGames);
            }
        } catch (Exception e) {
            Log.d(TAG, "ECC: " + e.toString());
        }
    }

    public List<Game> filterGames(List<Game> games) {
        for (int i = 0; i < games.size(); i++) {
            if (games.get(i) != null) {
                if (!mIGamesRepository.isGamePresent(games.get(i).getId())) {
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

    public void addFriend(User user) {
        loggedUser.addFriend(user.getEmail());
        usersRepository.addFriendsToUser(loggedUser.getEmail(), user.getEmail());
    }

    public void removeFriend(User user) {
        loggedUser.removeFriend(user.getEmail());
        usersRepository.removeFriendToUser(loggedUser.getEmail(), user.getEmail());
    }

    @Override
    public void onResponse(List<Game> gamesList, String i) {

    }

    @Override
    public void onFailure(String errorMessage) {

    }
}
