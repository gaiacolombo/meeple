package it.unimib.lapecorafaquack.ui.friendsSearch;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.google.firebase.auth.FirebaseAuth;

public class FriendsSearchViewModel extends AndroidViewModel {

    private String currentUserEmail;

    public FriendsSearchViewModel(@NonNull Application application) {
        super(application);
        currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    }

    public String getCurrentUserEmail() {
        return currentUserEmail;
    }
}
