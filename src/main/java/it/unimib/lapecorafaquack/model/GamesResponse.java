package it.unimib.lapecorafaquack.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GamesResponse {
    @SerializedName("count") private int count;
    private List<Game> games;

    public GamesResponse(String status, int count, List<Game> games) {
        this.count = count;
        this.games = games;
    }

    public GamesResponse() {
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<Game> getGames() {
        return games;
    }

    public void setGames(List<Game> games) {
        this.games = games;
    }

    @Override
    public String toString() {
        return "GamesResponse{" +
                ", count=" + count +
                ", games=" + games +
                '}';
    }
}
