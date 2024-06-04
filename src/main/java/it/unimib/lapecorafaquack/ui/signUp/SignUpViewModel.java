package it.unimib.lapecorafaquack.ui.signUp;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import it.unimib.lapecorafaquack.model.User;
import it.unimib.lapecorafaquack.repository.UsersRepository;

public class SignUpViewModel extends AndroidViewModel {
    private static final String TAG = "SignUpViewModel";

    private UsersRepository usersRepository;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseFirestore db;

    private MutableLiveData<String> signUpMessage;

    public SignUpViewModel(@NonNull Application application) {
        super(application);

        usersRepository = new UsersRepository(getApplication());
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        db = FirebaseFirestore.getInstance();

        signUpMessage = new MutableLiveData<>();
    }

    public void insertUser(User user) {
        usersRepository.insertUser(user);
    }

    public LiveData<String> signUp(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                String errorMessage = "";
                Log.d(TAG, "onComplete");
                if (task.isSuccessful()) {
                    Map<String, Object> user = new HashMap<>();
                    user.put("email", email);
                    user.put("username", "");
                    user.put("bio", "");
                    user.put("isAdult", false);
                    ArrayList<String> categoriesOfInterest = new ArrayList<>();
                    user.put("categoriesOfInterest", categoriesOfInterest);
                    ArrayList<String> toPlayGames = new ArrayList<>();
                    user.put("toPlayGames", toPlayGames);
                    ArrayList<String> playedGames = new ArrayList<>();
                    user.put("playedGames", playedGames);
                    ArrayList<String> favouriteGames = new ArrayList<>();
                    user.put("favouriteGames", favouriteGames);
                    ArrayList<String> friends = new ArrayList<>();
                    user.put("friends", friends);

                    String userId = mAuth.getCurrentUser().getUid();
                    db.collection("users").
                            document(userId).
                            set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(@NonNull Void unused) {
                            Log.d(TAG, "Document added in " + userId);
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "Error adding document", e);
                                }
                            });

                    Log.d(TAG, "createUserWithEmail: success");
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    User utente = new User(email);
                    insertUser(utente);

                    errorMessage = "InfoProfileActivity";

                } else {
                    Log.d(TAG, "createUserWithEmail: failure", task.getException());
                    try {
                        throw task.getException();
                    } catch(FirebaseAuthWeakPasswordException e) {
                        errorMessage = "password_debole";
                    } catch(FirebaseAuthInvalidCredentialsException e) {
                        errorMessage = "email_non_valida";
                    } catch(FirebaseAuthUserCollisionException e) {
                        errorMessage = "account_gia_esistente";
                    } catch(Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                    }
                signUpMessage.postValue(errorMessage);
            }
        });
        return  signUpMessage;
    }
}
