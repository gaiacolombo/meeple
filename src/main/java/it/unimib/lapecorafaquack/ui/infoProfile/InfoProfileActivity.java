package it.unimib.lapecorafaquack.ui.infoProfile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import it.unimib.lapecorafaquack.R;
import it.unimib.lapecorafaquack.ui.infoPreferences.InfoPreferencesActivity;

public class InfoProfileActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "InfoProfileActivity";
    private EditText username;
    private EditText bio;
    private RadioButton adult;
    private RadioButton child;
    private InfoProfileViewModel infoProfileViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_profile);
        username = (EditText)findViewById(R.id.editText_username);
        bio = (EditText)findViewById(R.id.editText_bio);
        adult = (RadioButton) findViewById(R.id.adult);
        child = (RadioButton) findViewById(R.id.child);

        findViewById(R.id.button_avanti).setOnClickListener(this);

        infoProfileViewModel = new ViewModelProvider(this).get(InfoProfileViewModel.class);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_avanti:
                Log.d(TAG, "clicked on Avanti");
                if(username.getText().length() == 0) {
                    Toast.makeText(InfoProfileActivity.this, R.string.username_non_può_essere_vuoto, Toast.LENGTH_SHORT).show();
                }
                else {
                    String email = infoProfileViewModel.getCurrentUser().getEmail();
                    infoProfileViewModel.addUsername(email, username.getText().toString());
                    infoProfileViewModel.addBio(email, bio.getText().toString());
                    if(adult.isChecked()) {
                        infoProfileViewModel.addIsAdult(email, true);
                    }
                    else {
                        infoProfileViewModel.addIsAdult(email, false);
                    }

                    startActivity(new Intent(InfoProfileActivity.this, InfoPreferencesActivity.class));
                }
                break;
        }
    }
}