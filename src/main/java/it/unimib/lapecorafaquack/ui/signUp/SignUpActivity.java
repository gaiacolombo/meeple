package it.unimib.lapecorafaquack.ui.signUp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import it.unimib.lapecorafaquack.R;
import it.unimib.lapecorafaquack.ui.login.LoginActivity;
import it.unimib.lapecorafaquack.ui.infoProfile.InfoProfileActivity;
import it.unimib.lapecorafaquack.utils.Constants;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "SignupActivity";

    private EditText email;
    private EditText password;

    private ImageView buttonShowPassword;

    private SignUpViewModel signUpViewModel;

    public SignUpActivity() {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(new Bundle());

        signUpViewModel = new ViewModelProvider(this).get(SignUpViewModel.class);

        setContentView(R.layout.activity_signup);

        email = (EditText)findViewById(R.id.editText_email_signUp);
        password = (EditText)findViewById(R.id.editText_password_signUp);

        findViewById(R.id.button_sign_up).setOnClickListener(this);
        findViewById(R.id.button_login).setOnClickListener(this);

        buttonShowPassword = (ImageView) findViewById(R.id.button_show_pass);
        buttonShowPassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_sign_up:
                Log.d(TAG, "clicked on SignUp");
                if(password.getText().length() == 0 || email.getText().length() == 0) {
                    Toast.makeText(SignUpActivity.this, R.string.i_campi_non_possono_essere_vuoti, Toast.LENGTH_SHORT).show();
                }
                else {
                    ProgressDialog dialogLogin = ProgressDialog.show(this, "",
                            getResources().getText(R.string.caricamento), true);
                    signUpViewModel.signUp(email.getText().toString(), password.getText().toString()).observe(this, new Observer<String>() {
                        @Override
                        public void onChanged(String s) {
                            if(s.equals("InfoProfileActivity")) {
                                SharedPreferences settings = getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, 0);
                                settings.edit().putBoolean("logged", true).commit();
                                startActivity(new Intent(SignUpActivity.this, InfoProfileActivity.class));
                            }
                            else {
                                dialogLogin.cancel();
                                InputMethodManager imm = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(findViewById(android.R.id.content).getWindowToken(), 0);

                                if (s.equals("password_debole")) {
                                    Snackbar.make(findViewById(android.R.id.content), R.string.password_debole, Snackbar.LENGTH_LONG).show();
                                } else if (s.equals("email_non_valida")) {
                                    Snackbar.make(findViewById(android.R.id.content), R.string.email_non_valida, Snackbar.LENGTH_LONG).show();
                                } else if (s.equals("account_gia_esistente")) {
                                    Snackbar.make(findViewById(android.R.id.content), R.string.account_gia_esistente, Snackbar.LENGTH_LONG).show();
                                }
                            }
                        }
                    });
                }
                break;
            case R.id.button_login:
                Log.d(TAG, "clicked on Login");
                moveToLogin();
                break;
            case R.id.button_show_pass:
                showHidePassword();
                break;
        }
    }

    private void showHidePassword() {
        if(password.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
            ((ImageView)(buttonShowPassword)).setImageResource(R.drawable.ic_baseline_show_password_24);

            //Show Password
            password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            password.setSelection(password.getText().length());
        }
        else{
            ((ImageView)(buttonShowPassword)).setImageResource(R.drawable.ic_baseline_hide_password_24);

            //Hide Password
            password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            password.setSelection(password.getText().length());
        }
    }

    private void moveToLogin() {
        Intent myIntent = new Intent(SignUpActivity.this, LoginActivity.class);
        SignUpActivity.this.startActivity(myIntent);
    }
}