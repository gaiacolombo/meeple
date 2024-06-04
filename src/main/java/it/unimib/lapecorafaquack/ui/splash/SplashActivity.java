package it.unimib.lapecorafaquack.ui.splash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import it.unimib.lapecorafaquack.ui.launchScreen.LaunchScreenActivity;
import it.unimib.lapecorafaquack.ui.welcome.WelcomeActivity;
import it.unimib.lapecorafaquack.utils.Constants;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashViewModel splashViewModel = new ViewModelProvider(this).get(SplashViewModel.class);
        SharedPreferences settings = getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, 0);
        if(settings.getBoolean("first_installation", true)) {
            Log.d(TAG, "first installation");
            settings.edit().putBoolean("first_installation", false).commit();
            startActivity(new Intent(SplashActivity.this, WelcomeActivity.class));
        }
        else {
            Log.d(TAG, "not first installation");
            startActivity(new Intent(SplashActivity.this, LaunchScreenActivity.class));
        }
        finish();
    }
}