package it.unimib.lapecorafaquack.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
@Entity
public class User implements Parcelable {

    private static final String TAG = "Users";
    @NonNull
    @PrimaryKey
    private String email;
    private String username;
    private String bio;
    private boolean isAdult;
    private ArrayList<String> categoriesOfInterest;
    private ArrayList<String> toPlayGames;
    private ArrayList<String> playedGames;
    private ArrayList<String> favouriteGames;
    private ArrayList<String> friends;

    public User(){

    }

    public User(@NonNull String email) {
        if(FirebaseAuth.getInstance() != null) {
            Log.d(TAG, String.valueOf(FirebaseAuth.getInstance()));
        }
        this.email = email;
        this.username = "";
        this.bio = "";
        this.isAdult = true;
        this.categoriesOfInterest = new ArrayList<>();
        this.toPlayGames = new ArrayList<>();
        this.playedGames = new ArrayList<>();
        this.favouriteGames = new ArrayList<>();
        this.friends = new ArrayList<>();
    }

    public User(@NonNull String email, String username, String bio, boolean isAdult, ArrayList<String> categoriesOfInterest, ArrayList<String> toPlayGames, ArrayList<String> playedGames, ArrayList<String> favouriteGames, ArrayList<String> friends) {
        this.email = email;
        this.username = username;
        this.bio = bio;
        this.isAdult = isAdult;
        this.categoriesOfInterest = new ArrayList<>();
        this.toPlayGames = new ArrayList<>();
        this.playedGames = new ArrayList<>();
        this.favouriteGames = new ArrayList<>();
        this.friends = new ArrayList<>();
        this.categoriesOfInterest = categoriesOfInterest;
        this.playedGames = playedGames;
        this.toPlayGames = toPlayGames;
        this.favouriteGames = favouriteGames;
        this.friends = friends;
    }

    protected User(Parcel in) {
        email = in.readString();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public boolean isAdult() {
        return isAdult;
    }

    public void setAdult(boolean adult) {
        isAdult = adult;
    }

    public ArrayList<String> getCategoriesOfInterest() {
        return categoriesOfInterest;
    }

    public void setCategoriesOfInterest(ArrayList<String> categoriesOfInterest) {
        this.categoriesOfInterest = categoriesOfInterest;
    }

    public void addCategoriesOfInterest(String categoriesOfInterest) {
        if(this.categoriesOfInterest == null) {
            this.categoriesOfInterest = new ArrayList<>();
        }
        this.categoriesOfInterest.add(categoriesOfInterest);
    }

    public ArrayList<String> getToPlayGames() {
        return toPlayGames;
    }

    public void setToPlayGames(ArrayList<String> toPlayGames) {
        this.toPlayGames = toPlayGames;
    }

    public void addToPlayGame(String toPlayGame) {
        if(this.toPlayGames == null) {
            this.toPlayGames = new ArrayList<>();
        }
        if(!this.toPlayGames.contains(toPlayGame)) {
            this.toPlayGames.add(toPlayGame);
        }
    }

    public ArrayList<String> getPlayedGames() {
        return playedGames;
    }

    public void setPlayedGames(ArrayList<String> playedGames) {
        this.playedGames = playedGames;
    }

    public void addPlayedGame(String playedGame) {
        if(this.playedGames == null) {
            this.playedGames = new ArrayList<>();
        }
        if(!this.playedGames.contains(playedGame)) {
            this.playedGames.add(playedGame);
        }
    }

    public ArrayList<String> getFavouriteGames() {
        return favouriteGames;
    }

    public void setFavouriteGames(ArrayList<String> favouriteGames) {
        this.favouriteGames = favouriteGames;
    }

    public void addFavouriteGame(String favouriteGame) {
        if(this.favouriteGames == null) {
            this.favouriteGames = new ArrayList<>();
        }
        if(!this.favouriteGames.contains(favouriteGame)) {
            this.favouriteGames.add(favouriteGame);
        }
    }

    public ArrayList<String> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<String> friends) {
        this.friends = friends;
    }

    public void addFriend(String friend) {
        if(this.friends == null) {
            this.friends = new ArrayList<>();
        }
        if(!this.friends.contains(friend)) {
            this.friends.add(friend);
        }
    }

    public void removeToPlayGame(String gameId) {
        if(this.toPlayGames.contains(gameId)) {
            this.toPlayGames.remove(gameId);
        }
    }

    public void removePlayedGame(String gameId) {
        if(this.playedGames.contains(gameId)) {
            this.playedGames.remove(gameId);
        }
    }

    public void removeFavouriteGame(String gameId) {
        if(this.favouriteGames.contains(gameId)) {
            this.favouriteGames.remove(gameId);
        }
    }

    public void removeFriend(String userEmail) {
        if(this.friends.contains(userEmail)) {
            this.friends.remove(userEmail);
        }
    }

    @Override
    public String toString() {
        return "Users{" +
                "email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", bio='" + bio + '\'' +
                ", isAdult = '" + isAdult + '\'' +
                ", categoriesOfInterest=" + categoriesOfInterest +
                ", playedGames=" + playedGames +
                ", favouriteGames=" + favouriteGames +
                ", friends=" + friends +
                '}';
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(email);
    }
}
