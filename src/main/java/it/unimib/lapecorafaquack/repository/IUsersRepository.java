package it.unimib.lapecorafaquack.repository;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import it.unimib.lapecorafaquack.model.User;

public interface IUsersRepository {

    User getUser(String email) throws ExecutionException, InterruptedException;

    void insertUser(User user);

    void deleteEverything();

    void addToPlayGameToUser(String email, String gameId);

    void addUsername(String email, String username);

    void addBio(String email, String bio);

    void addCategoriesToUser(String email, ArrayList<String> categories);

    void addPlayedGamesToUser(String email, String playedGames);

    void addFavouriteGamesToUser(String email, String favouriteGames);

    void addFriendsToUser(String email, String friends);

    void addIsAdult(String email, boolean b);

    void removeToPlayGameToUser(String email, String gameId);

    void removePlayedGameToUser(String email, String gameId);

    void removeFavouriteGameToUser(String email, String gameId);

    void removeFriendToUser(String email, String friendEmail);
}
