package it.unimib.lapecorafaquack.ui.login;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import it.unimib.lapecorafaquack.model.User;
import it.unimib.lapecorafaquack.repository.UsersRepository;

public class LoginViewModel extends AndroidViewModel {

    private static final String TAG = "LoginViewModel";
    private static final int RC_SIGN_IN = 200;
    private FirebaseAuth mAuth;

    private FirebaseUser currentUser;

    private MutableLiveData<String> emailSignInErrorMessage;
    private MutableLiveData<String> googleSignInMessage;

    public LoginViewModel(@NonNull Application application) {
        super(application);

        mAuth = FirebaseAuth.getInstance();

        emailSignInErrorMessage = new MutableLiveData<>();
        googleSignInMessage = new MutableLiveData<>();

        currentUser = mAuth.getCurrentUser();
    }

    public FirebaseUser getCurrentUser() {
        return currentUser;
    }

    public LiveData<String> emailSignIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        String errorMessage = "";
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail: success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            String id = mAuth.getCurrentUser().getUid();

                            FirebaseFirestore.getInstance().collection("users").
                                    document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document != null) {
                                            String emailFirestore = document.getString("email");
                                            String bio = document.getString("bio");
                                            String username = document.getString("username");
                                            boolean isAdult = (boolean) document.get("isAdult");
                                            ArrayList<String> categoriesOfInterest = (ArrayList<String>) document.get("categoriesOfInterest");
                                            ArrayList<String> favouriteGames = (ArrayList<String>) document.get("favouriteGames");
                                            ArrayList<String> playedGames = (ArrayList<String>) document.get("playedGames");
                                            ArrayList<String> toPlayGames = (ArrayList<String>) document.get("toPlayGames");
                                            ArrayList<String> friends = (ArrayList<String>) document.get("friends");

                                            UsersRepository usersRepository = new UsersRepository(getApplication());
                                            User utente = usersRepository.getUser(emailFirestore);
                                            if(utente == null) {
                                                utente = new User(emailFirestore, username, bio, isAdult, categoriesOfInterest, toPlayGames, playedGames, favouriteGames, friends);
                                                usersRepository.insertUser(utente);
                                                Log.d(TAG, "utente (null): " + utente);
                                            }
                                            else {
                                                usersRepository.addBio(emailFirestore, bio);
                                                usersRepository.addUsername(emailFirestore, username);
                                                usersRepository.addCategoriesToUser(emailFirestore, categoriesOfInterest);
                                                usersRepository.addIsAdult(emailFirestore, isAdult);
                                                for(int i = 0; i < favouriteGames.size(); i++){
                                                    usersRepository.addFavouriteGamesToUser(emailFirestore, favouriteGames.get(i));
                                                }
                                                for(int i = 0; i < playedGames.size(); i++){
                                                    usersRepository.addPlayedGamesToUser(emailFirestore, playedGames.get(i));
                                                }
                                                for(int i = 0; i < toPlayGames.size(); i++){
                                                    usersRepository.addToPlayGameToUser(emailFirestore, toPlayGames.get(i));
                                                }
                                                for(int i = 0; i < friends.size(); i++){
                                                    usersRepository.addFriendsToUser(emailFirestore, friends.get(i));
                                                }

                                                Log.d(TAG, "user already in database: " + usersRepository.getUser(emailFirestore));
                                            }
                                        }
                                    }
                                }
                            });
                            errorMessage = "";
                            emailSignInErrorMessage.postValue(errorMessage);
                        } else {
                            Log.d(TAG, "signInWithEmail: failure", task.getException());
                            try {
                                throw task.getException();
                            } catch(FirebaseAuthWeakPasswordException e) {
                                errorMessage = "password_errata";
                            } catch(FirebaseAuthInvalidCredentialsException e) {
                                errorMessage = "password_errata";
                            } catch(FirebaseAuthUserCollisionException e) {
                                errorMessage = "login_failed";
                            } catch(Exception e) {
                                errorMessage = "account_inesistente";
                            }
                            emailSignInErrorMessage.postValue(errorMessage);
                        }
                    }
                });
        return emailSignInErrorMessage;
    }

    public LiveData<String> firebaseAuthWithGoogle(GoogleSignInAccount account) {
        if(account == null) {
            return emailSignInErrorMessage;
        }
        Log.d(TAG, "firebaseAuthWithGoogle");
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential: success with " + account.getEmail());

                            String userId = mAuth.getCurrentUser().getUid();

                            Log.d(TAG, "userId " + userId);

                            FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
                            DocumentReference docIdRef = rootRef.collection("users").document(userId);

                            FirebaseFirestore.getInstance().collection("users").
                                    document(userId)
                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    String message = "";
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            Log.d(TAG, "Document exists!");
                                            downloadDataFromFirestoreGoogle();

                                            message = "MainActivity";
                                        } else {
                                            Log.d(TAG, "Document does not exist!");
                                            addDocumentInFirestore(userId, mAuth.getCurrentUser().getEmail());

                                            message = "InfoProfileActivity";
                                        }
                                    } else {
                                        Log.d(TAG, "Failed with: ", task.getException());
                                    }
                                    emailSignInErrorMessage.postValue(message);
                                }
                            });

                        } else {
                            Log.d(TAG, "signInWithCredential: failure", task.getException());
                        }
                    }
                });

        UsersRepository usersRepository = new UsersRepository(getApplication());
        User utente = usersRepository.getUser(account.getEmail());
        if(utente == null) {

            utente = new User(account.getEmail());
            usersRepository.insertUser(utente);
        }
        else {
            Log.d(TAG, "user already in database: " + utente.toString());
        }
        return googleSignInMessage;
    }

    private void addDocumentInFirestore(String userId, String currentEmail) {
        Map<String, Object> user = new HashMap<>();
        user.put("email", currentEmail);
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

        FirebaseFirestore.getInstance().collection("users").
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
    }

    private void downloadDataFromFirestoreGoogle() {
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("users").
                document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        String emailFirestore = document.getString("email");
                        String bio = document.getString("bio");
                        String username = document.getString("username");
                        boolean isAdult = (boolean) document.get("isAdult");
                        ArrayList<String> categoriesOfInterest = (ArrayList<String>) document.get("categoriesOfInterest");
                        ArrayList<String> favouriteGames = (ArrayList<String>) document.get("favouriteGames");
                        ArrayList<String> playedGames = (ArrayList<String>) document.get("playedGames");
                        ArrayList<String> toPlayGames = (ArrayList<String>) document.get("toPlayGames");
                        ArrayList<String> friends = (ArrayList<String>) document.get("friends");

                        UsersRepository usersRepository = new UsersRepository(getApplication());
                        User utente = usersRepository.getUser(emailFirestore);
                        if(utente == null) {
                            utente = new User(emailFirestore, username, bio, isAdult, categoriesOfInterest, toPlayGames, playedGames, favouriteGames, friends);
                            usersRepository.insertUser(utente);
                            Log.d(TAG, "utente (null): " + utente);
                        }
                        else {
                            usersRepository.addBio(emailFirestore, bio);
                            usersRepository.addUsername(emailFirestore, username);
                            usersRepository.addCategoriesToUser(emailFirestore, categoriesOfInterest);

                            for(int i = 0; i < favouriteGames.size(); i++){
                                usersRepository.addFavouriteGamesToUser(emailFirestore, favouriteGames.get(i));
                            }
                            for(int i = 0; i < playedGames.size(); i++){
                                usersRepository.addPlayedGamesToUser(emailFirestore, playedGames.get(i));
                            }
                            for(int i = 0; i < toPlayGames.size(); i++){
                                usersRepository.addToPlayGameToUser(emailFirestore, toPlayGames.get(i));
                            }
                            for(int i = 0; i < friends.size(); i++){
                                usersRepository.addFriendsToUser(emailFirestore, friends.get(i));
                            }

                            Log.d(TAG, "user already in database: " + usersRepository.getUser(emailFirestore));
                        }
                    }
                }
            }
        });
    }
}
