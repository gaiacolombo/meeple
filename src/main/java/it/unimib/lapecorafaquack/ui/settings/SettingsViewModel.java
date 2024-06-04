package it.unimib.lapecorafaquack.ui.settings;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import it.unimib.lapecorafaquack.model.User;
import it.unimib.lapecorafaquack.repository.UsersRepository;

public class SettingsViewModel extends AndroidViewModel {

    private static final String TAG = "SettingsViewModel";

    private UsersRepository usersRepository;
    private User utente;
    private FirebaseAuth auth;

    public SettingsViewModel(@NonNull Application application) {
        super(application);

        auth = FirebaseAuth.getInstance();

        usersRepository = new UsersRepository(application);
        String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        utente = usersRepository.getUser(currentUserEmail);
    }

    public void deleteEverything() {
        usersRepository.deleteEverything();
    }

    public void resetPassword() {
        String emailAddress = auth.getCurrentUser().getEmail();

        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                        }
                    }
                });
    }

    public void deleteAccount() {
        FirebaseUser user = auth.getCurrentUser();
        String uid = user.getUid();
        Log.d(TAG, "uid: " + uid);
        FirebaseFirestore.getInstance().collection("users").document(uid)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Error deleting document", e);
                    }
                });

        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User account deleted.");
                        }
                    }
                });
        deleteEverything();
    }

    public void logOut() {
        FirebaseAuth.getInstance().signOut();
    }
}
