package it.unimib.lapecorafaquack.model;

import java.util.List;

public class GamesGroup {
    private String groupTitle;
    private List<Game> gamesList;

    public GamesGroup(String groupTitle, List<Game> gamesList) {
        this.groupTitle = groupTitle;
        this.gamesList = gamesList;
    }

    public String getGroupTitle() {
        return groupTitle;
    }

    public void setGroupTitle(String groupTitle) {
        this.groupTitle = groupTitle;
    }

    public List<Game> getGamesList() {
        return gamesList;
    }

    public void setGamesList(List<Game> gamesList) {
        this.gamesList = gamesList;
    }
}
