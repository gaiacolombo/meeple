package it.unimib.lapecorafaquack.ui.search;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import it.unimib.lapecorafaquack.model.Game;
import it.unimib.lapecorafaquack.repository.GamesRepository;
import it.unimib.lapecorafaquack.utils.ResponseCallback;

public class SearchViewModel extends AndroidViewModel implements ResponseCallback {

    private static final String TAG = "SearchViewModel";

    private GamesRepository gamesRepository;

    private MutableLiveData<List<Game>> games;

    public SearchViewModel(@NonNull Application application) {
        super(application);

        gamesRepository = new GamesRepository(application, this);
        games = new MutableLiveData<>();
    }

    public void fetchSearchGames(String query) {
        gamesRepository.fetchSearchGames(query);
    }

    public MutableLiveData<List<Game>> getGames() {
        return games;
    }

    @Override
    public void onResponse(List<Game> gamesList, String i) {
        games.postValue(gamesList);
    }

    @Override
    public void onFailure(String errorMessage) {

    }
}
