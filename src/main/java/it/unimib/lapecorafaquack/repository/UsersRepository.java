package it.unimib.lapecorafaquack.repository;


import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import it.unimib.lapecorafaquack.database.GamesRoomDatabase;
import it.unimib.lapecorafaquack.database.UsersDao;
import it.unimib.lapecorafaquack.model.User;
import it.unimib.lapecorafaquack.utils.ServiceLocator;

public class UsersRepository implements IUsersRepository {

    private static final String TAG = "UsersRepository";

    private final Application mApplication;
    private final UsersDao usersDao;

    CollectionReference collectionFirebase = FirebaseFirestore.getInstance().collection("users");

    public UsersRepository(Application application) {
        this.mApplication = application;
        GamesRoomDatabase usersRoomDatabase = ServiceLocator.getInstance().getGamesDao(application);
        this.usersDao = usersRoomDatabase.usersDao();
    }

    @Override
    public User getUser(String email) {
        AsyncTask asyncTask = new GetUserAsyncTask(usersDao).execute(email);
        User utente = null;
        try {
            utente = (User) asyncTask.get();
            Log.d(TAG, "try");
        } catch (ExecutionException e) {
            e.printStackTrace();
            Log.d(TAG, "catch 1");
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d(TAG, "catch 2");
        }
        return utente;
    }
    private static class GetUserAsyncTask extends AsyncTask<String, Void, User> {
        private UsersDao usersDao;

        private GetUserAsyncTask(UsersDao usersDao) {
            this.usersDao = usersDao;
        }

        @Override
        protected User doInBackground(String... email) {
            User utente = usersDao.getUserByEmail(email[0]);
            return utente;
        }
    }

    @Override
    public void insertUser(User user){
        AsyncTask asyncTask = new InsertUserAsyncTask(usersDao).execute(user);
    }
    private static class InsertUserAsyncTask extends AsyncTask<User, Void, Void> {
        private UsersDao usersDao;

        private InsertUserAsyncTask(UsersDao usersDao) {
            this.usersDao = usersDao;
        }

        @Override
        protected Void doInBackground(User... users) {
            usersDao.insertUser(users[0]);
            return null;
        }
    }

    @Override
    public void deleteEverything() {
        Log.d(TAG, "DELETE EVERYTHING");
        AsyncTask asyncTask = new DeleteEverythingAsyncTask(usersDao).execute();
    }
    private static class DeleteEverythingAsyncTask extends AsyncTask<Void, Void, Void> {
        private UsersDao usersDao;

        private DeleteEverythingAsyncTask(UsersDao usersDao) {
            this.usersDao = usersDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            usersDao.deleteEverything();
            return null;
        }
    }

    @Override
    public void addUsername(String email, String username) {
        collectionFirebase.document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update("username", username);
        String[] array = {email, username};
        AsyncTask asyncTask = new AddUsernameAsyncTask(usersDao).execute(array);
    }
    private static class AddUsernameAsyncTask extends AsyncTask<String[], Void, Void> {
        private UsersDao usersDao;

        private AddUsernameAsyncTask(UsersDao usersDao) {
            this.usersDao = usersDao;
        }

        @Override
        protected Void doInBackground(String[]... array) {
            String email = array[0][0];
            String username = array[0][1];
            usersDao.addUsername(email, username);
            return null;
        }
    }

    @Override
    public void addBio(String email, String bio) {
        collectionFirebase.document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update("bio", bio);
        String[] array = {email, bio};
        AsyncTask asyncTask = new AddBioAsyncTask(usersDao).execute(array);
    }
    private static class AddBioAsyncTask extends AsyncTask<String[], Void, Void> {
        private UsersDao usersDao;

        private AddBioAsyncTask(UsersDao usersDao) {
            this.usersDao = usersDao;
        }

        @Override
        protected Void doInBackground(String[]... array) {
            String email = array[0][0];
            String bio = array[0][1];
            usersDao.addBio(email, bio);
            return null;
        }
    }

    @Override
    public void addIsAdult(String email, boolean b) {
        collectionFirebase.document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update("isAdult", b);
        Object[] array = {email, b};
        AsyncTask asyncTask = new AddIsAdultAsyncTask(usersDao).execute(array);
    }
    private static class AddIsAdultAsyncTask extends AsyncTask<Object[], Void, Void> {
        private UsersDao usersDao;

        private AddIsAdultAsyncTask(UsersDao usersDao) {
            this.usersDao = usersDao;
        }

        @Override
        protected Void doInBackground(Object[]... array) {
            String email = (String)array[0][0];
            boolean b = (Boolean)array[0][1];
            usersDao.addIsAdult(email, b);
            return null;
        }
    }

    @Override
    public void addCategoriesToUser(String email, ArrayList<String> categories) {
        String actualCategory = null;
        for(int i = 0; i < categories.size(); i++){
            actualCategory = categories.get(i);
            collectionFirebase.document(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                    update("categoriesOfInterest", FieldValue.arrayUnion(actualCategory));
        }
        Object[] array = {email, categories};
        AsyncTask asyncTask = new AddCategoriesToUserAsyncTask(usersDao).execute(array);
    }
    private static class AddCategoriesToUserAsyncTask extends AsyncTask<Object[], Void, Void> {
        private UsersDao usersDao;

        private AddCategoriesToUserAsyncTask(UsersDao usersDao) {
            this.usersDao = usersDao;
        }

        @Override
        protected Void doInBackground(Object[]... array) {
            String email = (String) array[0][0];
            ArrayList<String> categories = (ArrayList<String>) array[0][1];
            usersDao.addCategoriesToUser(email, categories);
            return null;
        }
    }

    @Override
    public void addToPlayGameToUser(String email, String gameId) {
        User utente = getUser(email);
        utente.addToPlayGame(gameId);
        collectionFirebase.document(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                update("toPlayGames", FieldValue.arrayUnion(gameId));
        Object[] array = {email, utente.getToPlayGames()};
        AsyncTask asyncTask = new AddToPlayGameToUserAsyncTask(usersDao).execute(array);
    }
    private static class AddToPlayGameToUserAsyncTask extends AsyncTask<Object[], Void, Void> {
        private UsersDao usersDao;

        private AddToPlayGameToUserAsyncTask(UsersDao usersDao) {
            this.usersDao = usersDao;
        }

        @Override
        protected Void doInBackground(Object[]... array) {
            String email = (String)array[0][0];
            ArrayList<String> toPlayGames = (ArrayList<String>) array[0][1];
            usersDao.addToPlayGameToUser(email, toPlayGames);
            return null;
        }
    }

    @Override
    public void addPlayedGamesToUser(String email, String playedGames) {
        Log.d(TAG, email);
        User utente = getUser(email);
        Log.d(TAG, utente.toString());
        utente.addPlayedGame(playedGames);
        collectionFirebase.document(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                update("playedGames", FieldValue.arrayUnion(playedGames));
        Object[] array = {email, utente.getPlayedGames()};
        AsyncTask asyncTask = new AddPlayedGamesToUserAsyncTask(usersDao).execute(array);
    }
    private static class AddPlayedGamesToUserAsyncTask extends AsyncTask<Object[], Void, Void> {
        private UsersDao usersDao;

        private AddPlayedGamesToUserAsyncTask(UsersDao usersDao) {
            this.usersDao = usersDao;
        }

        @Override
        protected Void doInBackground(Object[]... array) {
            String email = (String)array[0][0];
            ArrayList<String> playedGames = (ArrayList<String>) array[0][1];
            usersDao.addPlayedGamesToUser(email, playedGames);
            return null;
        }
    }

    @Override
    public void addFavouriteGamesToUser(String email, String favouriteGames) {
        User utente = getUser(email);
        utente.addFavouriteGame(favouriteGames);
        collectionFirebase.document(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                update("favouriteGames", FieldValue.arrayUnion(favouriteGames));
        Object[] array = {email, utente.getFavouriteGames()};
        AsyncTask asyncTask = new AddFavouriteGamesToUserAsyncTask(usersDao).execute(array);
    }
    private static class AddFavouriteGamesToUserAsyncTask extends AsyncTask<Object[], Void, Void> {
        private UsersDao usersDao;

        private AddFavouriteGamesToUserAsyncTask(UsersDao usersDao) {
            this.usersDao = usersDao;
        }

        @Override
        protected Void doInBackground(Object[]... array) {
            String email = (String)array[0][0];
            ArrayList<String> favouriteGames = (ArrayList<String>) array[0][1];
            usersDao.addFavouriteGamesToUser(email, favouriteGames);
            return null;
        }
    }

    @Override
    public void addFriendsToUser(String email, String friends) {
        User utente = getUser(email);
        utente.addFriend(friends);
        collectionFirebase.document(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                update("friends", FieldValue.arrayUnion(friends));
        Object[] array = {email, utente.getFriends()};
        AsyncTask asyncTask = new AddFriendsToUserAsyncTask(usersDao).execute(array);
    }
    private static class AddFriendsToUserAsyncTask extends AsyncTask<Object[], Void, Void> {
        private UsersDao usersDao;

        private AddFriendsToUserAsyncTask(UsersDao usersDao) {
            this.usersDao = usersDao;
        }

        @Override
        protected Void doInBackground(Object[]... array) {
            String email = (String)array[0][0];
            ArrayList<String> friends = (ArrayList<String>) array[0][1];
            usersDao.addFriendsToUser(email, friends);
            return null;
        }
    }

    @Override
    public void removeToPlayGameToUser(String email, String gameId) {
        User utente = getUser(email);
        utente.removeToPlayGame(gameId);
        collectionFirebase.document(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                update("toPlayGames", FieldValue.arrayRemove(gameId));
        Object[] array = {email, utente.getToPlayGames()};
        AsyncTask asyncTask = new RemoveToPlayGameToUserAsyncTask(usersDao).execute(array);
    }
    private static class RemoveToPlayGameToUserAsyncTask extends AsyncTask<Object[], Void, Void> {
        private UsersDao usersDao;

        private RemoveToPlayGameToUserAsyncTask(UsersDao usersDao) {
            this.usersDao = usersDao;
        }

        @Override
        protected Void doInBackground(Object[]... array) {
            String email = (String)array[0][0];
            ArrayList<String> toPlayGames = (ArrayList<String>) array[0][1];
            usersDao.removeToPlayGameToUser(email, toPlayGames);
            return null;
        }
    }

    @Override
    public void removePlayedGameToUser(String email, String gameId) {
        User utente = getUser(email);
        utente.removePlayedGame(gameId);
        collectionFirebase.document(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                update("playedGames", FieldValue.arrayRemove(gameId));
        Object[] array = {email, utente.getPlayedGames()};
        AsyncTask asyncTask = new RemovePlayedGameToUserAsyncTask(usersDao).execute(array);
    }
    private static class RemovePlayedGameToUserAsyncTask extends AsyncTask<Object[], Void, Void> {
        private UsersDao usersDao;

        private RemovePlayedGameToUserAsyncTask(UsersDao usersDao) {
            this.usersDao = usersDao;
        }

        @Override
        protected Void doInBackground(Object[]... array) {
            String email = (String)array[0][0];
            ArrayList<String> playedGames = (ArrayList<String>) array[0][1];
            usersDao.removePlayedGameToUser(email, playedGames);
            return null;
        }
    }

    @Override
    public void removeFavouriteGameToUser(String email, String gameId) {
        User utente = getUser(email);
        utente.removeFavouriteGame(gameId);
        collectionFirebase.document(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                update("favouriteGames", FieldValue.arrayRemove(gameId));
        Object[] array = {email, utente.getFavouriteGames()};
        AsyncTask asyncTask = new RemoveFavouriteGameToUserAsyncTask(usersDao).execute(array);
    }
    private static class RemoveFavouriteGameToUserAsyncTask extends AsyncTask<Object[], Void, Void> {
        private UsersDao usersDao;

        private RemoveFavouriteGameToUserAsyncTask(UsersDao usersDao) {
            this.usersDao = usersDao;
        }

        @Override
        protected Void doInBackground(Object[]... array) {
            String email = (String)array[0][0];
            ArrayList<String> favouriteGames = (ArrayList<String>) array[0][1];
            usersDao.removeFavouriteGameToUser(email, favouriteGames);
            return null;
        }
    }

    @Override
    public void removeFriendToUser(String email, String friendEmail) {
        User utente = getUser(email);
        utente.removeFriend(friendEmail);
        collectionFirebase.document(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                update("friends", FieldValue.arrayRemove(friendEmail));
        Object[] array = {email, utente.getFriends()};
        AsyncTask asyncTask = new RemoveFriendToUserAsyncTask(usersDao).execute(array);
    }
    private static class RemoveFriendToUserAsyncTask extends AsyncTask<Object[], Void, Void> {
        private UsersDao usersDao;

        private RemoveFriendToUserAsyncTask(UsersDao usersDao) {
            this.usersDao = usersDao;
        }

        @Override
        protected Void doInBackground(Object[]... array) {
            String email = (String)array[0][0];
            ArrayList<String> friends = (ArrayList<String>) array[0][1];
            usersDao.removeFriendToUser(email, friends);
            return null;
        }
    }
}