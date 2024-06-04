package it.unimib.lapecorafaquack.repository;

import android.app.Application;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import it.unimib.lapecorafaquack.database.GameCategoryDao;
import it.unimib.lapecorafaquack.database.GamesDao;
import it.unimib.lapecorafaquack.database.GamesRoomDatabase;
import it.unimib.lapecorafaquack.model.Game;
import it.unimib.lapecorafaquack.model.GameCategory;
import it.unimib.lapecorafaquack.model.GamesResponse;
import it.unimib.lapecorafaquack.model.User;
import it.unimib.lapecorafaquack.service.GamesApiService;
import it.unimib.lapecorafaquack.utils.Constants;
import it.unimib.lapecorafaquack.utils.ResponseCallback;
import it.unimib.lapecorafaquack.utils.ServiceLocator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GamesRepository implements IGamesRepository {

    private static final String TAG = "GamesRepository";

    private final Application mApplication;
    private final GamesApiService mGamesApiService;
    private final GamesDao gamesDao;
    private final GameCategoryDao gameCategoryDao;
    private final ResponseCallback mResponseCallback;
    private final UsersRepository usersRepository;
    User utente;

    private LiveData<List<Game>> bestGamesLiveDataList;
    private LiveData<List<Game>> quickGamesLiveDataList;
    private LiveData<List<Game>> newestGamesLiveDataList;
    private LiveData<List<Game>> gamesByCategoryLiveDataList;
    private LiveData<List<Game>> gamesByAgeLiveDataList;

    private MutableLiveData<List<Game>> toPlayGamesLiveDataList = new MutableLiveData<>();
    private MutableLiveData<List<Game>> playedGamesLiveDataList = new MutableLiveData<>();
    private MutableLiveData<List<Game>> favouriteGamesLiveDataList = new MutableLiveData<>();

    public GamesRepository(Application application, ResponseCallback responseCallback) {
        this.mApplication = application;
        this.mGamesApiService = ServiceLocator.getInstance().getGamesApiService();
        GamesRoomDatabase gamesRoomDatabase = ServiceLocator.getInstance().getGamesDao(application);
        this.usersRepository = new UsersRepository(application);
        this.gamesDao = gamesRoomDatabase.gamesDao();
        this.mResponseCallback = responseCallback;
        this.gameCategoryDao = gamesRoomDatabase.gameCategoriesDao();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            utente = usersRepository.getUser(email);
        }

        bestGamesLiveDataList = gamesDao.getBestGames();

        String category = getCategory();
        gamesByCategoryLiveDataList = gameCategoryDao.getGamesByCategory(category);

        int lowerBoundAge = getAgeBounds()[0];
        int upperBoundAge = getAgeBounds()[1];
        gamesByAgeLiveDataList = gamesDao.getGamesByAge(lowerBoundAge, upperBoundAge);

        newestGamesLiveDataList = gamesDao.getNewestGames();
        quickGamesLiveDataList = gamesDao.getQuickGames();

        if(utente != null) {
            ArrayList<String> toPlayGamesIds = utente.getToPlayGames();
            ArrayList<Game> arrayToPlayGames = new ArrayList<>();
            for (int i = 0; i < toPlayGamesIds.size(); i++) {
                arrayToPlayGames.add(getGame(toPlayGamesIds.get(i)));
            }
            toPlayGamesLiveDataList.postValue(arrayToPlayGames);

            ArrayList<String> playedGamesIds = utente.getPlayedGames();
            ArrayList<Game> arrayPlayedGames = new ArrayList<>();
            for (int i = 0; i < playedGamesIds.size(); i++) {
                arrayPlayedGames.add(getGame(playedGamesIds.get(i)));
            }
            playedGamesLiveDataList.postValue(arrayPlayedGames);

            ArrayList<String> favouriteGamesIds = utente.getFavouriteGames();
            ArrayList<Game> arrayFavouriteGames = new ArrayList<>();
            for (int i = 0; i < favouriteGamesIds.size(); i++) {
                arrayFavouriteGames.add(getGame(favouriteGamesIds.get(i)));
            }
            favouriteGamesLiveDataList.postValue(arrayFavouriteGames);
        }
    }

    public boolean isGamePresent(String gameId){
        AsyncTask asyncTask = new IsGamePresentAsyncTask(gamesDao).execute(gameId);
        try {
            return (boolean) asyncTask.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
    private static class IsGamePresentAsyncTask extends AsyncTask<String, Void, Boolean> {
        private GamesDao gamesDao;

        private IsGamePresentAsyncTask(GamesDao gamesDao) {
            this.gamesDao = gamesDao;
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            Game game = this.gamesDao.getGameById(strings[0]);
            if(game == null){
                return false;
            }
            else{
                return true;
            }
        }
    }

    @Override
    public void addGames(ArrayList<String> gamesId) {
        Log.d(TAG, gamesId.toString());
        String stringGamesId =  gamesId.toString().substring(1, gamesId.toString().length()-1);
        stringGamesId = stringGamesId.replaceAll(" ", "");
        Log.d(TAG, stringGamesId);
        Call<GamesResponse> mGamesResponseCall = mGamesApiService.getMissingGames(stringGamesId, Constants.API_KEY);
        Log.d(TAG, "before enqueueGames");

        enqueueGames(mGamesResponseCall, "addMissingGames");
        Log.d(TAG, "after enqueueGames");
    }

    @Override
    public Game getGame(String gameId) {
        AsyncTask asyncTask = new GetGameAsyncTask(gamesDao).execute(gameId);
        try {
            return (Game) asyncTask.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
    private static class GetGameAsyncTask extends AsyncTask<String, Void, Game> {
        private GamesDao gamesDao;

        private GetGameAsyncTask(GamesDao gamesDao) {
            this.gamesDao = gamesDao;
        }

        @Override
        protected Game doInBackground(String... strings) {
            return this.gamesDao.getGameById(strings[0]);
        }
    }

    @Override
    public void insertGame(Game game){
        if(!isGamePresent(game.getId())) {
            AsyncTask asyncTask = new InsertGameAsyncTask(gamesDao).execute(game);
        }
    }
    private static class InsertGameAsyncTask extends AsyncTask<Game, Void, Void> {
        private GamesDao gamesDao;

        private InsertGameAsyncTask(GamesDao gamesDao) {
            this.gamesDao = gamesDao;
        }

        @Override
        protected Void doInBackground(Game... games) {
            this.gamesDao.insertGame(games[0]);
            return null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void insertGames(List<Game> games){
        AsyncTask asyncTaskInsertGames = new InsertGamesAsyncTask(gamesDao).execute(games);
        for(int i = 0; i < games.size(); i++){
            Game tmpGame = games.get(i);
            for(int j = 0; j < tmpGame.getMCategories().size(); j++){
                GameCategory gameCategory = new GameCategory(tmpGame.getId(), tmpGame.getMCategories().get(j));
                AsyncTask asyncTaskGameCategory = new InsertGameCategoryAsyncTask(gameCategoryDao).execute(gameCategory);
            }
        }
    }

    private static class InsertGamesAsyncTask extends AsyncTask<List<Game>, Void, Void> {
        private GamesDao gamesDao;

        private InsertGamesAsyncTask(GamesDao gamesDao) {
            this.gamesDao = gamesDao;
        }

        @Override
        protected Void doInBackground(List<Game>... games) {
            gamesDao.insertGames(games[0]);
            return null;
        }
    }

    private static class InsertGameCategoryAsyncTask extends AsyncTask<GameCategory, Void, Void> {
        private GameCategoryDao gameCategoryDao;

        private InsertGameCategoryAsyncTask(GameCategoryDao gameCategoryDao) {
            this.gameCategoryDao = gameCategoryDao;
        }

        @Override
        protected Void doInBackground(GameCategory... gameCategories) {
            gameCategoryDao.Insert(gameCategories[0]);
            return null;
        }
    }

    @Override
    public LiveData<List<Game>> fetchBestGames() {
        return bestGamesLiveDataList;
    }

    @Override
    public void fetchAllGames() {
        for(int i = 0; i < 10; i++){
            int n = 100 * i;
            Call<GamesResponse> mGamesResponseCall = mGamesApiService.getAllGames(100, n, "rank", Constants.API_KEY);
            enqueueGames(mGamesResponseCall, "fetchAllGames");
        }
    }

    @Override
    public LiveData<List<Game>> fetchGamesByCategory() {
        return gamesByCategoryLiveDataList;
    }

    @Override
    public LiveData<List<Game>> fetchNewestGames() {
        return newestGamesLiveDataList;
    }

    @Override
    public LiveData<List<Game>> fetchQuickGames() {
        return quickGamesLiveDataList;
    }

    @Override
    public LiveData<List<Game>> fetchGamesByAge() {
        return gamesByAgeLiveDataList;
    }


    @Override
    public void fetchSearchGames(String input) {
        Call<GamesResponse> mGamesResponseCall = mGamesApiService.getSearchGames(100, true, "rank", input, Constants.API_KEY);
        enqueueGames(mGamesResponseCall, "fetchSearchGames");
    }

    void enqueueGames(Call<GamesResponse> mGamesResponseCall, String metodoChiamante) {
        Log.d(TAG, "in enqueueGames");
        mGamesResponseCall.enqueue(new Callback<GamesResponse>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<GamesResponse> call, Response<GamesResponse> response) {
                Log.d(TAG, "in onResponse");
                if(response.body().getGames() != null && response.isSuccessful()) {
                    List<Game> games = new ArrayList<>();
                    games = response.body().getGames();
                    Log.d(TAG, metodoChiamante);
                    if (metodoChiamante.equals("addMissingGames")) {
                        Log.d(TAG, metodoChiamante + games.toString());
                        insertGames(games);
                    }

                    mResponseCallback.onResponse(games, metodoChiamante);
                }
                else {
                    mResponseCallback.onFailure("non è stato trovato nessun risultato ?");
                }
            }

            @Override
            public void onFailure(Call<GamesResponse> call, Throwable t) {
                Log.d(TAG, t.toString());
                mResponseCallback.onFailure(t.getMessage());
            }
        });
    }


    private String getCategory() {
        if(utente == null) {
            return null;
        }
        if(utente.getCategoriesOfInterest().size() > 0){
            int random = (int)(Math.random()*utente.getCategoriesOfInterest().size());
            return utente.getCategoriesOfInterest().get(random);
        }
        else{
            return "eX8uuNlQkQ";
        }
    }

    private int[] getAgeBounds() {
        int[] ages = new int[2];
        if(utente != null) {
            if(utente.isAdult()) {
                ages[0] = 16;
                ages[1] = 100;
            }
            else {
                ages[0] = 1;
                ages[1] = 10;
            }
        }
        else {
            ages[0] = 1;
            ages[1] = 10;
        }
        return ages;
    }

    public LiveData<List<Game>> getToPlayGamesLiveDataList() {
        return toPlayGamesLiveDataList;
    }

    public LiveData<List<Game>> getPlayedGamesLiveDataList() {
        return playedGamesLiveDataList;
    }

    public LiveData<List<Game>> getFavouriteGamesLiveDataList() {
        return favouriteGamesLiveDataList;
    }

    public LiveData<List<Game>> getUserToPlayGamesLiveData(User user) {
        ArrayList<String> toPlayGamesIds = user.getToPlayGames();
        ArrayList<Game> arrayToPlayGames = new ArrayList<>();
        for (int i = 0; i < toPlayGamesIds.size(); i++) {
            arrayToPlayGames.add(getGame(toPlayGamesIds.get(i)));
        }
        toPlayGamesLiveDataList.postValue(arrayToPlayGames);
        return toPlayGamesLiveDataList;
    }

    public LiveData<List<Game>> getUserPlayedGamesLiveData(User user) {
        ArrayList<String> playGamesIds = user.getPlayedGames();
        ArrayList<Game> arrayPlayedGames = new ArrayList<>();
        for (int i = 0; i < playGamesIds.size(); i++) {
            arrayPlayedGames.add(getGame(playGamesIds.get(i)));
        }
        playedGamesLiveDataList.postValue(arrayPlayedGames);
        return playedGamesLiveDataList;
    }

    public LiveData<List<Game>> getUserFavouriteGamesLiveData(User user) {
        ArrayList<String> favouriteGamesIds = user.getFavouriteGames();
        ArrayList<Game> arrayFavouriteGames = new ArrayList<>();
        for (int i = 0; i < favouriteGamesIds.size(); i++) {
            arrayFavouriteGames.add(getGame(favouriteGamesIds.get(i)));
        }
        favouriteGamesLiveDataList.postValue(arrayFavouriteGames);
        return favouriteGamesLiveDataList;
    }
}