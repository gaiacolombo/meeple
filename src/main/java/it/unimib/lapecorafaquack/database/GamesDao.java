package it.unimib.lapecorafaquack.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import it.unimib.lapecorafaquack.model.Game;

@Dao
public interface GamesDao {

    @Query("SELECT * FROM Game WHERE id= + :id")
    Game getGameById(String id);

    @Query("SELECT * FROM Game")
    List<Game> getAllGames();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertGame(Game game);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertGames(List<Game> game);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertGamesList(List<Game> gamesList);

    @Delete
    void delete(Game game);

    @Query("SELECT * FROM Game ORDER BY rank")
    LiveData<List<Game>> getBestGames();

    @Query("SELECT * FROM Game ORDER BY year_published DESC, rank")
    LiveData<List<Game>> getNewestGames();

    @Query("SELECT * FROM Game WHERE max_playtime < 21 AND min_playtime > 1 ORDER BY rank")
    LiveData<List<Game>> getQuickGames();

    @Query("SELECT * FROM Game WHERE min_age >= + :minAge AND min_age < + :maxAge ORDER BY rank")
    LiveData<List<Game>> getGamesByAge(int minAge, int maxAge);

    @Query("DELETE FROM Game")
    void deleteEverything();

    @Query("SELECT Game.* FROM Game, gamecategory WHERE gamecategory.categoryId = + :input")
    LiveData<List<Game>> getGamesByCategory(String input);
}
