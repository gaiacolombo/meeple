package it.unimib.lapecorafaquack.ui.gameDetail;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import it.unimib.lapecorafaquack.model.Category;
import it.unimib.lapecorafaquack.model.Game;
import it.unimib.lapecorafaquack.model.User;
import it.unimib.lapecorafaquack.repository.CategoryRepository;
import it.unimib.lapecorafaquack.repository.GamesRepository;
import it.unimib.lapecorafaquack.repository.UsersRepository;
import it.unimib.lapecorafaquack.utils.CategoryResponseCallback;
import it.unimib.lapecorafaquack.utils.ResponseCallback;

public class GameDetailViewModel extends AndroidViewModel implements ResponseCallback, CategoryResponseCallback {

    private static final String TAG = "GameDetailViewModel";

    private UsersRepository usersRepository;
    private GamesRepository gamesRepository;
    private CategoryRepository categoryRepository;

    private User currentUser;

    public GameDetailViewModel(@NonNull Application application) {
        super(application);
        usersRepository = new UsersRepository(application);
        gamesRepository = new GamesRepository(application, this);
        categoryRepository = new CategoryRepository(application, this);

        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        currentUser = usersRepository.getUser(email);
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public String getCategoryName(String categoryId){
        return categoryRepository.getCategoryName(categoryId);
    }

    public String getCategoryId(String categoryName){
        return categoryRepository.getCategoryId(categoryName);
    }

    public void addFavouriteGamesToUser(String email, String gameId) {
        usersRepository.addFavouriteGamesToUser(email, gameId);
    }

    public void addPlayedGamesToUser(String email, String gameId) {
        Log.d(TAG, email);
        usersRepository.addPlayedGamesToUser(email, gameId);
    }

    public void addToPlayGamesToUser(String email, String gameId) {
       usersRepository.addToPlayGameToUser(email, gameId);
    }

    public void removeFavouriteGamesToUser(String email, String gameId) {
        usersRepository.removeFavouriteGameToUser(email, gameId);
    }

    public void removePlayedGamesToUser(String email, String gameId) {
        usersRepository.removePlayedGameToUser(email, gameId);
    }

    public void removeToPlayGamesToUser(String email, String gameId) {
        usersRepository.removeToPlayGameToUser(email, gameId);
    }

    public void insertGame(Game game) {
        gamesRepository.insertGame(game);
    }

    @Override
    public void onResponse(List<Game> gamesList, String i) {

    }

    @Override
    public void onResponse(List<Category> categoryList) {

    }

    @Override
    public void onFailure(String errorMessage) {

    }
}
