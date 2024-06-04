package it.unimib.lapecorafaquack.ui.infoPreferences;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;

import it.unimib.lapecorafaquack.R;
import it.unimib.lapecorafaquack.ui.infoProfile.InfoProfileActivity;
import it.unimib.lapecorafaquack.ui.main.MainActivity;

public class InfoPreferencesActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "InfoPreferencesActivity";

    private CheckBox checkBoxCardGame;
    private CheckBox checkBoxPartyGame;
    private CheckBox checkBoxAdventureGame;
    private CheckBox checkBoxCooperativeGame;
    private CheckBox checkBoxDiceGame;
    private CheckBox checkBoxFantasyGame;

    private InfoPreferencesViewModel infoPreferencesViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_preferences);
        findViewById(R.id.button_avanti).setOnClickListener(this);
        findViewById(R.id.button_indietro).setOnClickListener(this);
        checkBoxCardGame = findViewById(R.id.checkBox_card_game);
        checkBoxPartyGame = findViewById(R.id.checkBox_party_game);
        checkBoxAdventureGame = findViewById(R.id.checkBox_adventure_game);
        checkBoxCooperativeGame = findViewById(R.id.checkBox_cooperative_game);
        checkBoxDiceGame = findViewById(R.id.checkBox_dice_game);
        checkBoxFantasyGame = findViewById(R.id.checkBox_fantasy_game);

        infoPreferencesViewModel = new ViewModelProvider(this).get(InfoPreferencesViewModel.class);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_avanti:
                ArrayList<String> arrayList = new ArrayList<>();
                boolean atLeastOneIsChecked = false;
                if(checkBoxCardGame.isChecked()){
                    arrayList.add("eX8uuNlQkQ");
                    atLeastOneIsChecked = true;
                }
                if(checkBoxPartyGame.isChecked()){
                    arrayList.add("X8J7RM6dxX");
                    atLeastOneIsChecked = true;
                }
                if(checkBoxAdventureGame.isChecked()){
                    arrayList.add("KUBCKBkGxV");
                    atLeastOneIsChecked = true;
                }
                if(checkBoxCooperativeGame.isChecked()){
                    arrayList.add("ge8pIhEUGE");
                    atLeastOneIsChecked = true;
                }
                if(checkBoxDiceGame.isChecked()){
                    arrayList.add("mavSOM8vjH");
                    atLeastOneIsChecked = true;
                }
                if(checkBoxFantasyGame.isChecked()){
                    arrayList.add("ZTneo8TaIO");
                    atLeastOneIsChecked = true;
                }
                if(atLeastOneIsChecked) {
                    String email = infoPreferencesViewModel.getCurrentUser().getEmail();
                    infoPreferencesViewModel.addCategoriesToUser(email, arrayList);
                    startActivity(new Intent(InfoPreferencesActivity.this, MainActivity.class));
                }
                break;
            case R.id.button_indietro:
                startActivity(new Intent(InfoPreferencesActivity.this, InfoProfileActivity.class));
                break;
        }
    }
}