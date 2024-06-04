package it.unimib.lapecorafaquack.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesProvider {
    private final Application mApplication;

    public SharedPreferencesProvider(Application application) {
        this.mApplication = application;
    }

    public String getCategory() {
        SharedPreferences sharedPref =
                mApplication.getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPref.getString(Constants.SHARED_PREFERENCES_CATEGORIES_OF_INTEREST, null);
    }
}
