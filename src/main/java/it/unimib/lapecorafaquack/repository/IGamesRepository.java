package it.unimib.lapecorafaquack.repository;

import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

import it.unimib.lapecorafaquack.model.Game;
import it.unimib.lapecorafaquack.model.User;

public interface IGamesRepository {
    void addGames(ArrayList<String> gamesId) throws InterruptedException;

    Game getGame(String gameId);

    boolean isGamePresent(String gameId);

    void insertGame(Game game);
    void insertGames(List<Game> games);

    LiveData<List<Game>> fetchBestGames();
    LiveData<List<Game>> fetchGamesByCategory();
    LiveData<List<Game>> fetchNewestGames();
    LiveData<List<Game>> fetchQuickGames();
    LiveData<List<Game>> fetchGamesByAge();
    void fetchSearchGames(String input);
    void fetchAllGames();

    LiveData<List<Game>> getUserToPlayGamesLiveData(User user);
    LiveData<List<Game>> getUserPlayedGamesLiveData(User user);
    LiveData<List<Game>> getUserFavouriteGamesLiveData(User user);
}
