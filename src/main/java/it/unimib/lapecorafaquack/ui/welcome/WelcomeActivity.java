package it.unimib.lapecorafaquack.ui.welcome;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import it.unimib.lapecorafaquack.R;
import it.unimib.lapecorafaquack.adapter.WelcomeAdapter;
import it.unimib.lapecorafaquack.model.WelcomeCard;
import it.unimib.lapecorafaquack.ui.login.LoginActivity;

public class WelcomeActivity extends AppCompatActivity {

    private final String TAG = "WelcomeActivity";

    private ViewPager viewPager;

    private ArrayList<WelcomeCard> modelArrayList;
    private WelcomeAdapter welcomeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Button skipButton = findViewById(R.id.skip_welcome_button);
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "skipButton");
                startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
            }
        });

        viewPager = findViewById(R.id.viewPager);
        loadcards();
    }

    private void loadcards(){
        modelArrayList = new ArrayList<>();

        modelArrayList.add(new WelcomeCard(getResources().getString(R.string.titoloCard1),
                getResources().getString(R.string.descrizioneCard1),
                R.drawable.pila_giochi));
        modelArrayList.add(new WelcomeCard(getResources().getString(R.string.titoloCard2),
                getResources().getString(R.string.descrizioneCard2),
                R.drawable.tavolo));
        modelArrayList.add(new WelcomeCard(getResources().getString(R.string.titoloCard1),
                getResources().getString(R.string.descrizioneCard3),
                R.drawable.meeple_welcome));

        welcomeAdapter = new WelcomeAdapter(this, modelArrayList);
        viewPager.setAdapter(welcomeAdapter);
    }
}