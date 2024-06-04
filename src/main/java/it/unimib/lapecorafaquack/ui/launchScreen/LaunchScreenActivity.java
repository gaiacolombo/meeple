package it.unimib.lapecorafaquack.ui.launchScreen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import it.unimib.lapecorafaquack.R;
import it.unimib.lapecorafaquack.ui.login.LoginActivity;
import it.unimib.lapecorafaquack.ui.main.MainActivity;
import it.unimib.lapecorafaquack.utils.Constants;

public class LaunchScreenActivity extends AppCompatActivity {

    private static final String TAG = "LaunchScreenActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_screen);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container_view_main);
        NavController navController = navHostFragment.getNavController();


        SharedPreferences settings = getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, 0);
        if(settings.getBoolean("logged", true)) {
            Log.d(TAG, "logged");
            startActivity(new Intent(LaunchScreenActivity.this, MainActivity.class));
        }
        else {
            Log.d(TAG, "not logged");
            startActivity(new Intent(LaunchScreenActivity.this, LoginActivity.class));
        }
        finish();
    }
}