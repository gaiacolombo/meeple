package it.unimib.lapecorafaquack.utils;

import java.util.List;

import it.unimib.lapecorafaquack.model.Category;

public interface CategoryResponseCallback {
    public void onResponse(List<Category> categoryList);
    public void onFailure(String errorMessage);
}
