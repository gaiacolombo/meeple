package it.unimib.lapecorafaquack.ui.friends;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import it.unimib.lapecorafaquack.model.User;
import it.unimib.lapecorafaquack.repository.UsersRepository;

public class FriendsViewModel  extends AndroidViewModel {

    private static final String TAG = "FriendsViewModel";

    private UsersRepository usersRepository;
    private User currentUser;
    private FirebaseFirestore db;

    private MutableLiveData<List<User>> mResults;


    public FriendsViewModel(@NonNull Application application) {
        super(application);
        usersRepository = new UsersRepository(application);
        db = FirebaseFirestore.getInstance();
        String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        currentUser = usersRepository.getUser(currentUserEmail);

        mResults = new MutableLiveData<>();
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public MutableLiveData<List<User>> addFriendsToList() {
        //ArrayList<String> friends = usersRepository.getUser(currentUserEmail).getFriends();

        ArrayList<String> friends = currentUser.getFriends();


        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<User> arrayList = new ArrayList<>();
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        //Log.d(TAG, "onComplete:" + document.getId() + " => " + document.getData());
                        if (friends.contains(document.get("email").toString().toLowerCase())) {

                            User user = new User(document.get("email").toString(), document.get("username").toString(), document.get("bio").toString(), (Boolean) document.get("isAdult"), (ArrayList<String>) document.get("categories"), (ArrayList<String>) document.get("toPlayGames"), (ArrayList<String>) document.get("playedGames"), (ArrayList<String>) document.get("favouriteGames"), (ArrayList<String>) document.get("friends"));
                            arrayList.add(user);


                            Log.d(TAG, "User Username: " + document.get("username").toString());
                        }
                        mResults.postValue(arrayList);
                    }
                } else {
                    Log.d(TAG, "Error getting documents.", task.getException());
                }
            }
        });

        return mResults;
    }
}