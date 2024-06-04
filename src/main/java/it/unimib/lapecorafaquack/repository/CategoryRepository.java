package it.unimib.lapecorafaquack.repository;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;
import java.util.concurrent.ExecutionException;

import it.unimib.lapecorafaquack.database.CategoriesDao;
import it.unimib.lapecorafaquack.database.GamesRoomDatabase;
import it.unimib.lapecorafaquack.model.CategoriesResponse;
import it.unimib.lapecorafaquack.model.Category;
import it.unimib.lapecorafaquack.service.GamesApiService;
import it.unimib.lapecorafaquack.utils.CategoryResponseCallback;
import it.unimib.lapecorafaquack.utils.Constants;
import it.unimib.lapecorafaquack.utils.ServiceLocator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryRepository implements ICategoryRepository {
    private static final String TAG = "CategoryRepository";

    private final Application mApplication;
    private final GamesApiService mGamesApiService;
    private final CategoriesDao categoriesDao;
    private final CategoryResponseCallback mResponseCallback;



    public CategoryRepository(Application application, CategoryResponseCallback responseCallback) {
        this.mApplication = application;
        this.mGamesApiService = ServiceLocator.getInstance().getGamesApiService();
        GamesRoomDatabase gamesRoomDatabase = ServiceLocator.getInstance().getGamesDao(application);
        this.categoriesDao = gamesRoomDatabase.categoriesDao();
        this.mResponseCallback = responseCallback;
    }

    @Override
    public String getCategoryName(String categoryId){
        AsyncTask asyncTask = new GetCategoryNameAsyncTask(categoriesDao).execute(categoryId);

        try {
            return (String) asyncTask.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
    private static class GetCategoryNameAsyncTask extends AsyncTask<String, Void, String> {
        CategoriesDao categoriesDao;

        private GetCategoryNameAsyncTask(CategoriesDao categoriesDao) {
            this.categoriesDao = categoriesDao;
        }

        @Override
        protected String doInBackground(String... categories) {
            return categoriesDao.getCategoryName(categories[0]);
        }
    }

    @Override
    public String getCategoryId(String categoryName){
        AsyncTask asyncTask = new GetCategoryIdAsyncTask(categoriesDao).execute(categoryName);

        try {
            return (String) asyncTask.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
    private static class GetCategoryIdAsyncTask extends AsyncTask<String, Void, String> {
        CategoriesDao categoriesDao;

        private GetCategoryIdAsyncTask(CategoriesDao categoriesDao) {
            this.categoriesDao = categoriesDao;
        }

        @Override
        protected String doInBackground(String... categories) {
            return categoriesDao.getCategoryId(categories[0]);
        }
    }

    @Override
    public void insertCategories(List<Category> categories){
        AsyncTask asyncTask = new InsertCategoriesAsyncTask(categoriesDao).execute(categories);
    }
    private static class InsertCategoriesAsyncTask extends AsyncTask<List<Category>, Void, Void> {
        private CategoriesDao categoriesDao;

        private InsertCategoriesAsyncTask(CategoriesDao categoriesDao) {
            this.categoriesDao = categoriesDao;
        }

        @Override
        protected Void doInBackground(List<Category>... categories) {
            categoriesDao.insertCategories(categories[0]);
            return null;
        }
    }

    @Override
    public void fetchCategories() {
        Call<CategoriesResponse> mGamesResponseCall = mGamesApiService.getCategories(Constants.API_KEY);
        enqueueCategories(mGamesResponseCall);
    }

    void enqueueCategories(Call<CategoriesResponse> mcategoriesResponseCall) {
        mcategoriesResponseCall.enqueue(new Callback<CategoriesResponse>() {

            @Override
            public void onResponse(Call<CategoriesResponse> call, Response<CategoriesResponse> response) {
                if(response.body().getCategories() != null && response.isSuccessful()) {

                    mResponseCallback.onResponse(response.body().getCategories());

                    Log.d(TAG, "onResponse: " + response.body().getCategories().toString());
                }
                else {
                    mResponseCallback.onFailure("non è stato trovato nessun risultato ?");
                }
            }

            @Override
            public void onFailure(Call<CategoriesResponse> call, Throwable t) {
                mResponseCallback.onFailure(t.getMessage());
            }
        });
    }

}
