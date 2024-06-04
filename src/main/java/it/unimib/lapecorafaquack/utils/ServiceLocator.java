package it.unimib.lapecorafaquack.utils;

import android.app.Application;

import it.unimib.lapecorafaquack.database.GamesRoomDatabase;
import it.unimib.lapecorafaquack.service.GamesApiService;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceLocator {
    public static ServiceLocator instance = null;

    private ServiceLocator() {}

    public static ServiceLocator getInstance() {
        if(instance == null) {
            synchronized(ServiceLocator.class) {
                instance = new ServiceLocator();
            }
        }
        return instance;
    }

    public GamesApiService getGamesApiService() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.GAMES_API_BASE_URL).
                addConverterFactory(GsonConverterFactory.create()).build();
        return retrofit.create(GamesApiService.class);
    }

    public GamesRoomDatabase getGamesDao(Application application) {
        return GamesRoomDatabase.getDatabase(application);
    }

}
