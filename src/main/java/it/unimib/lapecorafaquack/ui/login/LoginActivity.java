package it.unimib.lapecorafaquack.ui.login;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseUser;

import it.unimib.lapecorafaquack.R;
import it.unimib.lapecorafaquack.model.User;
import it.unimib.lapecorafaquack.repository.UsersRepository;
import it.unimib.lapecorafaquack.ui.infoProfile.InfoProfileActivity;
import it.unimib.lapecorafaquack.ui.main.MainActivity;
import it.unimib.lapecorafaquack.ui.signUp.SignUpActivity;
import it.unimib.lapecorafaquack.utils.Constants;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 200;

    private EditText email;
    private EditText password;
    private GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(Constants.DEFAULT_WEB_CLIENT_ID).requestEmail().build();

    private ImageView buttonShowPassword;
    private GoogleSignInClient mGoogleSignInClient;

    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        findViewById(R.id.button_sign_in_google).setOnClickListener(this);
        findViewById(R.id.button_sign_up).setOnClickListener(this);
        findViewById(R.id.button_login).setOnClickListener(this);

        email = (EditText)findViewById(R.id.editText_email_signUp);
        password = (EditText)findViewById(R.id.editText_password_signUp);

        buttonShowPassword = (ImageView) findViewById(R.id.button_show_pass_login);
        buttonShowPassword.setOnClickListener(this);

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = loginViewModel.getCurrentUser();
        if(currentUser != null){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_sign_up:
                Log.d(TAG, "clicked SignUP");
                moveToSignUp();
                break;
            case R.id.button_sign_in_google:
                Log.d(TAG, "clicked SignInGoogle");
                ProgressDialog dialogGoogle = ProgressDialog.show(this, "",
                        getResources().getText(R.string.caricamento), true);
                googleSignIn();
                break;
            case R.id.button_login:
                Log.d(TAG, "clicked Login");
                if(email.getText().length()==0||password.getText().length()==0){
                    Snackbar.make(findViewById(android.R.id.content), R.string.i_campi_non_possono_essere_vuoti, Snackbar.LENGTH_LONG).show();
                }
                else{
                    login();
                }
                break;
            case R.id.button_show_pass_login:
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

    private void moveToSignUp() {
        Log.d(TAG, "moveToSignUp");
        Intent myIntent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(myIntent);
    }

    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        loginViewModel.firebaseAuthWithGoogle(null).observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if(s.equals("MainActivity")) {
                    Intent homepage = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(homepage);
                }
                else if(s.equals("InfoProfileActivity")) {
                    Intent homepage = new Intent(LoginActivity.this, InfoProfileActivity.class);
                    startActivity(homepage);
                }
                else {
                    Log.d(TAG, "error");
                }
            }
        });

        if (requestCode == RC_SIGN_IN) {
            GoogleSignIn.getSignedInAccountFromIntent(data).addOnCompleteListener(new OnCompleteListener<GoogleSignInAccount>() {
                @Override
                public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                    try {
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        Log.d(TAG, "onActivityResult:" + account.getId());
                        loginViewModel.firebaseAuthWithGoogle(account);
                    } catch (ApiException e) {
                        Log.d(TAG, "Google sign in failed", e);
                    }
                }
            });
        }
    }

    private void login(){
        ProgressDialog dialogLogin = ProgressDialog.show(this, "",
                getResources().getText(R.string.caricamento), true);
        loginViewModel.emailSignIn(email.getText().toString(), password.getText().toString()).observe(this, new Observer<String>() {
            @Override
            public void onChanged(String emailSignInErrorMessage) {
                if(emailSignInErrorMessage.equals("")) {
                    Intent homepage = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(homepage);
                }
                else {
                    dialogLogin.cancel();
                    InputMethodManager imm = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(findViewById(android.R.id.content).getWindowToken(), 0);

                    if (emailSignInErrorMessage.equals("password_errata")) {
                        Snackbar.make(findViewById(android.R.id.content), R.string.password_errata, Snackbar.LENGTH_LONG).show();
                    }
                    else if (emailSignInErrorMessage.equals("login_failed")) {
                        Snackbar.make(findViewById(android.R.id.content), R.string.login_failed, Snackbar.LENGTH_LONG).show();
                    }
                    else if (emailSignInErrorMessage.equals("account_inesistente")) {
                        Snackbar.make(findViewById(android.R.id.content), R.string.account_inesistente, Snackbar.LENGTH_LONG).show();
                    }
                }
            }
        });

        SharedPreferences settings = getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, 0);
        settings.edit().putBoolean("logged", true).commit();

        UsersRepository usersRepository = new UsersRepository(getApplication());
        User utente = usersRepository.getUser(email.getText().toString());

        if(utente == null) {
            utente = new User(email.getText().toString());
            usersRepository.insertUser(utente);
        }
        else {
            Log.d(TAG, "user already in database: " + utente.toString());
        }
    }
}