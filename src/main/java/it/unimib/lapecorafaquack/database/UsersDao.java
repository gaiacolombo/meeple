package it.unimib.lapecorafaquack.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.ArrayList;

import it.unimib.lapecorafaquack.model.User;

@Dao
public interface UsersDao {

    @Query("SELECT * FROM User WHERE email= + :email")
    User getUserByEmail(String email);

    @Delete
    void delete(User user);

    @Query("DELETE FROM User")
    void deleteEverything();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertUser(User user);

    @Query("UPDATE User SET toPlayGames= + :toPlayGames WHERE email= + :email")
    void addToPlayGameToUser(String email, ArrayList<String> toPlayGames);

    @Query("UPDATE User SET username= + :username WHERE email= + :email")
    void addUsername(String email, String username);

    @Query("UPDATE User SET bio= + :bio WHERE email= + :email")
    void addBio(String email, String bio);

    @Query("UPDATE User SET isAdult= + :b WHERE email= + :email")
    void addIsAdult(String email, boolean b);

    @Query("UPDATE User SET categoriesOfInterest= + :categories WHERE email= + :email")
    void addCategoriesToUser(String email, ArrayList<String> categories);

    @Query("UPDATE User SET playedGames= + :playedGames WHERE email= + :email")
    void addPlayedGamesToUser(String email, ArrayList<String> playedGames);

    @Query("UPDATE User SET favouriteGames= + :favouriteGames WHERE email= + :email")
    void addFavouriteGamesToUser(String email, ArrayList<String> favouriteGames);

    @Query("UPDATE User SET friends= + :friends WHERE email= + :email")
    void addFriendsToUser(String email, ArrayList<String> friends);

    @Query("UPDATE User SET toPlayGames= + :toPlayGames WHERE email= + :email")
    void removeToPlayGameToUser(String email, ArrayList<String> toPlayGames);

    @Query("UPDATE User SET playedGames= + :playedGames WHERE email= + :email")
    void removePlayedGameToUser(String email, ArrayList<String> playedGames);

    @Query("UPDATE User SET favouriteGames= + :favouriteGames WHERE email= + :email")
    void removeFavouriteGameToUser(String email, ArrayList<String> favouriteGames);

    @Query("UPDATE User SET friends= + :friends WHERE email= + :email")
    void removeFriendToUser(String email, ArrayList<String> friends);
}
