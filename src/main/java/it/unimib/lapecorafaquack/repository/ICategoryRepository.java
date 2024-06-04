package it.unimib.lapecorafaquack.repository;

import java.util.List;

import it.unimib.lapecorafaquack.model.Category;

public interface ICategoryRepository {

    void fetchCategories();

    String getCategoryId(String categoryName);

    void insertCategories(List<Category> categories);

    String getCategoryName(String categoryId);
}
