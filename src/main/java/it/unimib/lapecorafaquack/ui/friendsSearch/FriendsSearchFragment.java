package it.unimib.lapecorafaquack.ui.friendsSearch;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import it.unimib.lapecorafaquack.R;
import it.unimib.lapecorafaquack.adapter.UsersAdapter;
import it.unimib.lapecorafaquack.model.User;
import it.unimib.lapecorafaquack.ui.search.SearchFragmentArgs;

public class FriendsSearchFragment extends Fragment {

    private static final String TAG = "FriendsSearchFragment";

    private List<User> mResults;
    private UsersAdapter mResultsAdapter;
    private TextView mNoResultsTextView;
    private ProgressBar mProgressBar;
    private View view;

    private FriendsSearchViewModel friendsSearchViewModel;

    private FirebaseFirestore db;

    public FriendsSearchFragment() {
    }

    public static FriendsSearchFragment newInstance() {
        FriendsSearchFragment fragment = new FriendsSearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mResults = new ArrayList<>();

        friendsSearchViewModel = new ViewModelProvider(this).get(FriendsSearchViewModel.class);

        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);
        RecyclerView mRecyclerViewHome = view.findViewById(R.id.search_recyclerview);
        mRecyclerViewHome.setLayoutManager(new LinearLayoutManager(getContext()));

        mResultsAdapter = new UsersAdapter(mResults, new UsersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(User user) {
                Log.d(TAG, "OnItemClick " + user.toString());
                String currentUserEmail = friendsSearchViewModel.getCurrentUserEmail();
                if(user.getEmail().equals(currentUserEmail)) {
                    FriendsSearchFragmentDirections.ActionFriendsSearchFragmentToProfile action = FriendsSearchFragmentDirections.actionFriendsSearchFragmentToProfile(0);
                    Navigation.findNavController(view).navigate(action);
                }
                else {
                    FriendsSearchFragmentDirections.ActionFriendsSearchFragmentToFriendDetailsFragment action = FriendsSearchFragmentDirections.actionFriendsSearchFragmentToFriendDetailsFragment(user);
                    Navigation.findNavController(view).navigate(action);
                }
            }
        }, R.layout.friend_search_list_item, getActivity().getApplication());

        mRecyclerViewHome.setAdapter(mResultsAdapter);
        SearchView searchView = view.findViewById(R.id.search_view);
        searchView.setQueryHint(getResources().getString(R.string.cerca_un_utente));

        mProgressBar = view.findViewById(R.id.search_progress_bar);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mResults.clear();
                mProgressBar.setVisibility(View.VISIBLE);
                searchFriends(query);
                Log.d(TAG, "onQueryTextSubmit");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "writing");
                return false;
            }
        });

        String query = SearchFragmentArgs.fromBundle(getArguments()).getQuery();
        searchFriends(query);

        mNoResultsTextView = view.findViewById(R.id.no_results_textView);
        mNoResultsTextView.setVisibility(View.GONE);

        Log.d(TAG, query);
        return view;
    }

    private void searchFriends(String query) {
        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                mResults = new ArrayList<>();
                if (task.isSuccessful()) {
                    boolean trovato = false;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.get("email").toString().toLowerCase().contains(query.toLowerCase())) {

                            User user = new User(document.get("email").toString(), document.get("username").toString(), document.get("bio").toString(), (Boolean) document.get("isAdult"), (ArrayList<String>) document.get("categories"), (ArrayList<String>) document.get("toPlayGames"), (ArrayList<String>) document.get("playedGames"), (ArrayList<String>) document.get("favouriteGames"), (ArrayList<String>) document.get("friends"));
                            mResults.add(user);
                            trovato = true;
                            Log.d(TAG, "User Username: " + document.get("username").toString());

                        }
                    }
                    if(!trovato){
                        Log.d(TAG, "Non esiste un utente con questo nome");
                        mProgressBar.setVisibility(View.GONE);
                        mNoResultsTextView.setVisibility(View.VISIBLE);
                    }
                    else {
                        mResultsAdapter.updateData(mResults);
                        mResultsAdapter.notifyDataSetChanged();
                        mProgressBar.setVisibility(View.GONE);
                        mNoResultsTextView.setVisibility(View.GONE);
                    }
                } else {
                    Log.d(TAG, "Error getting documents.", task.getException());
                }
            }
        });
    }
}