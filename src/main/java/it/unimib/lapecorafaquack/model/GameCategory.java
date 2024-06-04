package it.unimib.lapecorafaquack.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

//classe che modella la tabella molti a molti tra giochi e categorie
@Entity
public class GameCategory {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    public int Id;
    public String gameId;
    public String categoryId;

    public GameCategory(){};

    public GameCategory(String gameId, String categoryId) {
        this.gameId = gameId;
        this.categoryId = categoryId;
    }
}
