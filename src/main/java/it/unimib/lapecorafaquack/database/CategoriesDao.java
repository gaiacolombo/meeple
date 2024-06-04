package it.unimib.lapecorafaquack.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import it.unimib.lapecorafaquack.model.Category;

@Dao
public interface CategoriesDao {
    @Query("SELECT * FROM category")
    List<Category> getCategories();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertCategories(List<Category> categories);

    @Query("SELECT name FROM category WHERE id =+ :idCategory")
    String getCategoryName(String idCategory);

    @Query("SELECT id FROM category WHERE name =+ :nameCategory")
    String getCategoryId(String nameCategory);
}
