package it.unimib.lapecorafaquack.ui.infoProfile;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.google.firebase.auth.FirebaseAuth;

import it.unimib.lapecorafaquack.model.User;
import it.unimib.lapecorafaquack.repository.UsersRepository;

public class InfoProfileViewModel extends AndroidViewModel {

    private UsersRepository usersRepository;

    private User currentUser;

    public InfoProfileViewModel(@NonNull Application application) {
        super(application);
        usersRepository = new UsersRepository(getApplication());

        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        currentUser = usersRepository.getUser(email);
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void addUsername(String email, String username) {
        usersRepository.addUsername(email, username);
    }

    public void addBio(String email, String bio) {
        usersRepository.addBio(email, bio);
    }

    public void addIsAdult(String email, boolean bool) {
        usersRepository.addIsAdult(email, bool);
    }

}
