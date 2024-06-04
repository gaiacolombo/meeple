package it.unimib.lapecorafaquack.utils;

import java.util.List;

import it.unimib.lapecorafaquack.model.Game;

public interface ResponseCallback {
    public void onResponse(List<Game> gamesList, String i);
    public void onFailure(String errorMessage);
}
