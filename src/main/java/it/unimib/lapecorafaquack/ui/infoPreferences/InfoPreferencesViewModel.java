package it.unimib.lapecorafaquack.ui.infoPreferences;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import it.unimib.lapecorafaquack.model.User;
import it.unimib.lapecorafaquack.repository.UsersRepository;

public class InfoPreferencesViewModel extends AndroidViewModel {

    private UsersRepository usersRepository;

    private User currentUser;

    public InfoPreferencesViewModel(@NonNull Application application) {
        super(application);
        usersRepository = new UsersRepository(getApplication());

        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        currentUser = usersRepository.getUser(email);
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void addCategoriesToUser(String email, ArrayList<String> arrayList) {
        usersRepository.addCategoriesToUser(email, arrayList);
    }

}
