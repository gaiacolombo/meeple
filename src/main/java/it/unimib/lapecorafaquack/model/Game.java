package it.unimib.lapecorafaquack.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Game implements Parcelable {

    private static final String TAG = "Games";
    @NonNull
    @PrimaryKey private String id;
    private String name;
    private String image_url;
    private String description_preview;
    private int min_players;
    private int max_players;
    private int rank;
    private int year_published;
    private int min_playtime;
    private int max_playtime;
    private int min_age;
    private float price;
    //private String primary_publisher;

    @Ignore
    @SerializedName("categories")
    private List<APICategory> APICategories;

    public List<APICategory> getAPICategories() {
        return APICategories;
    }

    public void setAPICategories(List<APICategory> APICategories) {
        this.APICategories = APICategories;
    }

    private List<String> mCategories = new ArrayList<>();

    public List<String> getMCategories() {
        return mCategories;
    }

    public void setMCategories(List<String> mCategories) {
        this.mCategories = mCategories;
    }

    public void convertCategories(List<APICategory> APICategories){
        if(APICategories != null) {
            for (int i = 0; i < APICategories.size(); i++) {
                if (mCategories != null) {
                    this.mCategories.add(APICategories.get(i).getId());
                }
            }
        }
        Log.d(TAG, mCategories.toString());
    }

    public Game(){
    };

    @Ignore
    public Game(@NonNull String id, String name, String image_url, String description_preview, int min_players, int max_players, List<String> mCategories, int rank, int year_published, int min_playtime, int max_playtime, int min_age, float price, String primary_publisher) {
        this.id = id;
        this.name = name;
        this.image_url = image_url;
        this.description_preview = description_preview;
        this.min_players = min_players;
        this.max_players = max_players;
        this.mCategories = mCategories;
        this.rank = rank;
        this.year_published = year_published;
        this.min_playtime = min_playtime;
        this.max_playtime = max_playtime;
        this.min_age = min_age;
        this.price = price;
        convertCategories(APICategories);
    }

    protected Game(Parcel in) {
        id = in.readString();
        name = in.readString();
        image_url = in.readString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getDescription_preview() {
        return description_preview;
    }

    public void setDescription_preview(String description_preview) {
        this.description_preview = description_preview;
    }

    public int getMin_players() {
        return min_players;
    }

    public void setMin_players(int min_players) {
        this.min_players = min_players;
    }

    public int getMax_players() {
        return max_players;
    }

    public void setMax_players(int max_players) {
        this.max_players = max_players;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getYear_published() {
        return year_published;
    }

    public void setYear_published(int year_published) {
        this.year_published = year_published;
    }

    public int getMin_playtime() {
        return min_playtime;
    }

    public void setMin_playtime(int min_playtime) {
        this.min_playtime = min_playtime;
    }

    public int getMax_playtime() {
        return max_playtime;
    }

    public void setMax_playtime(int max_playtime) {
        this.max_playtime = max_playtime;
    }

    public int getMin_age() {
        return min_age;
    }

    public void setMin_age(int min_age) {
        this.min_age = min_age;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Games{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", image_url='" + image_url + '\'' +
                ", description_preview='" + description_preview + '\'' +
                ", min_players=" + min_players +
                ", max_players=" + max_players +
                ", rank=" + rank +
                ", year_published=" + year_published +
                ", min_playtime=" + min_playtime +
                ", max_playtime=" + max_playtime +
                ", min_age=" + min_age +
                ", price=" + price +
                ", mCategories=" + mCategories +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(image_url);
    }

    public static final Creator<Game> CREATOR = new Creator<Game>() {
        @Override
        public Game createFromParcel(Parcel in) {
            return new Game(in);
        }

        @Override
        public Game[] newArray(int size) {
            return new Game[size];
        }
    };
}
