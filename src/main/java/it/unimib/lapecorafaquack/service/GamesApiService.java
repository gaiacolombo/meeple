package it.unimib.lapecorafaquack.service;

import it.unimib.lapecorafaquack.model.CategoriesResponse;
import it.unimib.lapecorafaquack.model.GamesResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GamesApiService {
    @GET("search")
    Call<GamesResponse> getMissingGames(
            @Query("ids") String ids,
            @Query("client_id") String apiKey);

    @GET("search")
    Call<GamesResponse> getAllGames(
            @Query("limit") Number limit,
            @Query("skip") Number skip,
            @Query("order_by") String rank,
            @Query("client_id") String apiKey);

    @GET("search")
    Call<GamesResponse> getSearchGames(
            @Query("limit") Number limit,
            @Query("fuzzy_match") Boolean fuzzy_match,
            @Query("order_by") String rank,
            @Query("name") String name,
            @Query("client_id") String apiKey);

    @GET("game/categories")
    Call<CategoriesResponse> getCategories(
            @Query("client_id") String apiKey);
}