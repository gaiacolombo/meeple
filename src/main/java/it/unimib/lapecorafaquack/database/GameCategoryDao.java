package it.unimib.lapecorafaquack.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import it.unimib.lapecorafaquack.model.GameCategory;
import it.unimib.lapecorafaquack.model.Game;

@Dao
public interface GameCategoryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void Insert(GameCategory gameCategory);

    @Delete
    void delete(GameCategory gameCategories);

    @Query("SELECT DISTINCT Game.* FROM GameCategory, Game WHERE GameCategory.categoryId =+ :category AND GameCategory.gameId = Game.id ORDER BY Game.rank")
    LiveData<List<Game>> getGamesByCategory(String category);
}