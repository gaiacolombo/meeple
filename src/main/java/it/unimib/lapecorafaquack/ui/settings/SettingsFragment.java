package it.unimib.lapecorafaquack.ui.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import it.unimib.lapecorafaquack.R;
import it.unimib.lapecorafaquack.ui.login.LoginActivity;
import it.unimib.lapecorafaquack.utils.Constants;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "SettingsFragment";

    private SettingsViewModel settingsViewModel;

    private View view;

    public SettingsFragment() {}

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings, container, false);

        Button buttonLogOut = (Button) view.findViewById(R.id.button_log_out);
        buttonLogOut.setOnClickListener(this);

        Button buttonDeleteAccount = (Button) view.findViewById(R.id.button_delete_account);
        buttonDeleteAccount.setOnClickListener(this);

        Button buttonResetPassword = (Button) view.findViewById(R.id.button_reset_password);
        buttonResetPassword.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_log_out:
                Log.d(TAG, "clicked LogOut");
                deleteEverything();
                break;
            case R.id.button_delete_account:
                Log.d(TAG, "clicked DeleteAccount");
                settingsViewModel.deleteAccount();
                logOut();
                break;
            case R.id.button_reset_password:
                Snackbar.make(view, R.string.controlla_email, Snackbar.LENGTH_LONG).show();
                Log.d(TAG, "clicked ResetPassword");
                settingsViewModel.resetPassword();
                break;
        }
    }

    private void logOut() {
        settingsViewModel.logOut();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        SharedPreferences settings = this.getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, 0);
        settings.edit().putBoolean("logged", false).commit();
        Log.d(TAG, "loggedOut");
    }

    public void deleteEverything() {
        settingsViewModel.deleteEverything();
        logOut();
    }
}