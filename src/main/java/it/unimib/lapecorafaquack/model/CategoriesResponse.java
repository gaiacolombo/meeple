package it.unimib.lapecorafaquack.model;

import java.util.List;

//lista di categorie che ritorna l'API quando fai la ricerca di tutte le categorie
public class CategoriesResponse {
    private List<Category> categories;

    public CategoriesResponse(String status, List<Category> categories) {
        this.categories = categories;
    }

    public CategoriesResponse() {
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    @Override
    public String toString() {
        return "GamesResponse{" +
                ", categories=" + categories +
                '}';
    }
}
