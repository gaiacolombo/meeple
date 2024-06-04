package it.unimib.lapecorafaquack.ui.friends;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
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

import java.util.List;

import it.unimib.lapecorafaquack.R;
import it.unimib.lapecorafaquack.adapter.UsersAdapter;
import it.unimib.lapecorafaquack.model.User;

public class FriendsFragment extends Fragment {

    private static final String TAG = "FriendsFragment";

    private List<User> mResults;
    private UsersAdapter mResultsAdapter;
    private TextView mNoResultsTextView;
    private ProgressBar mProgressBar;
    private String currentUserEmail;

    private FriendsViewModel friendsViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        friendsViewModel = new ViewModelProvider(this).get(FriendsViewModel.class);

        currentUserEmail = friendsViewModel.getCurrentUser().getEmail();

        mProgressBar = view.findViewById(R.id.search_progress_bar);
        mProgressBar.setVisibility(View.GONE);

        RecyclerView mRecyclerViewHome = view.findViewById(R.id.search_recyclerview);
        mRecyclerViewHome.setLayoutManager(new LinearLayoutManager(getContext()));

        mResultsAdapter = new UsersAdapter(mResults, new UsersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(User user) {
                Log.d(TAG, "OnItemClick " + user.toString());
                if(user.getEmail().equals(currentUserEmail)) {
                    FriendsFragmentDirections.ActionFriendsToProfile action = FriendsFragmentDirections.actionFriendsToProfile(0);
                    Navigation.findNavController(view).navigate(action);
                }
                else {
                    FriendsFragmentDirections.ActionFriendsToFriendDetailsFragment action = FriendsFragmentDirections.actionFriendsToFriendDetailsFragment(user);
                    Navigation.findNavController(view).navigate(action);
                }
            }
        }, R.layout.friend_search_list_item, getActivity().getApplication());

        mRecyclerViewHome.setAdapter(mResultsAdapter);

        SearchView searchView = view.findViewById(R.id.search_view);
        searchView.setQueryHint(getResources().getString(R.string.cerca_un_utente));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "onQueryTextSubmit In Ricerca");
                FriendsFragmentDirections.ActionFriendsSearch action = FriendsFragmentDirections.actionFriendsSearch(query);
                Navigation.findNavController(view).navigate(action);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "writing");
                return false;
            }
        });

        mNoResultsTextView = view.findViewById(R.id.no_results_textView);

        friendsViewModel.addFriendsToList().observe(getViewLifecycleOwner(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> usersList) {
                if(usersList == null || usersList.size() == 0) {
                    mNoResultsTextView.setText(R.string.nessun_amico);
                    mNoResultsTextView.setVisibility(View.VISIBLE);
                }
                else {
                    mNoResultsTextView.setVisibility(View.GONE);
                }
                mResultsAdapter.updateData(usersList);
                mResultsAdapter.notifyDataSetChanged();
            }
        });

        return view;
    }

}