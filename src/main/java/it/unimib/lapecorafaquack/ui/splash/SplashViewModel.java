package it.unimib.lapecorafaquack.ui.splash;

import android.app.Application;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;

import java.util.List;

import it.unimib.lapecorafaquack.model.Category;
import it.unimib.lapecorafaquack.model.Game;
import it.unimib.lapecorafaquack.repository.CategoryRepository;
import it.unimib.lapecorafaquack.repository.GamesRepository;
import it.unimib.lapecorafaquack.utils.CategoryResponseCallback;
import it.unimib.lapecorafaquack.utils.ResponseCallback;

public class SplashViewModel extends AndroidViewModel implements ResponseCallback, CategoryResponseCallback {

    private static final String TAG = "AndroidViewModel";

    private GamesRepository gameRepository;
    private CategoryRepository categoryRepository;

    public SplashViewModel(@NonNull Application application) {
        super(application);

        gameRepository = new GamesRepository(application, this);
        categoryRepository = new CategoryRepository(application, this);

        categoryRepository.fetchCategories();
        gameRepository.fetchAllGames();
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onResponse(List<Game> gamesList, String s) {
        for(int i = 0; i < gamesList.size(); i++){
            gamesList.get(i).convertCategories(gamesList.get(i).getAPICategories());
        }
        Log.d(TAG, "onResponse");
        gameRepository.insertGames(gamesList);
    }

    @Override
    public void onFailure(String errorMessage) {

    }

    @Override
    public void onResponse(List<Category> categoryList) {
        categoryRepository.insertCategories(categoryList);
    }
}
