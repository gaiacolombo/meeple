package it.unimib.lapecorafaquack.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.unimib.lapecorafaquack.model.Category;
import it.unimib.lapecorafaquack.utils.Converters;
import it.unimib.lapecorafaquack.model.Game;
import it.unimib.lapecorafaquack.model.GameCategory;
import it.unimib.lapecorafaquack.model.User;
import it.unimib.lapecorafaquack.utils.Constants;

@Database(entities = {Game.class, User.class, Category.class, GameCategory.class}, version = Constants.DATABASE_VERSION)
@TypeConverters({Converters.class})
public abstract class GamesRoomDatabase extends RoomDatabase{

    public abstract GamesDao gamesDao();
    public abstract UsersDao usersDao();
    public abstract CategoriesDao categoriesDao();
    public abstract GameCategoryDao gameCategoriesDao();

    private static volatile GamesRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static GamesRoomDatabase getDatabase(final Context context) {
        if(INSTANCE == null) {
            synchronized (GamesRoomDatabase.class) {
                if(INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            GamesRoomDatabase.class, Constants.GAMES_DATABASE_NAME).fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
