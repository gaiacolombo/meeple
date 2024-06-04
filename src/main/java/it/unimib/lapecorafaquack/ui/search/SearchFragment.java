package it.unimib.lapecorafaquack.ui.search;

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

import java.util.ArrayList;
import java.util.List;

import it.unimib.lapecorafaquack.R;
import it.unimib.lapecorafaquack.adapter.GamesAdapter;
import it.unimib.lapecorafaquack.model.Game;


public class SearchFragment extends Fragment {
    private static final String TAG = "SearchFragment";

    private List<Game> mResults;
    private GamesAdapter mResultsAdapter;
    private TextView mNoResultsTextView;
    private ProgressBar mProgressBar;
    private SearchViewModel searchViewModel;

    public SearchFragment() {
    }

    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mResults = new ArrayList<>();

        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        RecyclerView mRecyclerViewHome = view.findViewById(R.id.search_recyclerview);
        mRecyclerViewHome.setLayoutManager(new LinearLayoutManager(getContext()));

        mResultsAdapter = new GamesAdapter(mResults, new GamesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Game game) {
                Log.d(TAG, "OnItemClick " + game.toString());
                SearchFragmentDirections.ActionSearchGameDetails action = SearchFragmentDirections.actionSearchGameDetails(game);
                Navigation.findNavController(view).navigate(action);
            }
        }, R.layout.search_list_item, getActivity().getApplication());

        mProgressBar = view.findViewById(R.id.search_progress_bar);


        mNoResultsTextView = view.findViewById(R.id.no_results_textView);

        mRecyclerViewHome.setAdapter(mResultsAdapter);
        SearchView searchView = view.findViewById(R.id.search_view);
        searchView.setQueryHint(getResources().getString(R.string.cerca_un_gioco));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mResults.clear();
                mProgressBar.setVisibility(View.VISIBLE);
                mNoResultsTextView.setVisibility(View.GONE);
                searchViewModel.fetchSearchGames(query);
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

        searchViewModel.fetchSearchGames(query);
        searchViewModel.getGames().observe(getViewLifecycleOwner(), new Observer<List<Game>>() {
            @Override
            public void onChanged(List<Game> games) {
                if (games != null) {
                    if (games.size() != 0) {
                        mNoResultsTextView.setVisibility(View.GONE);
                        mProgressBar.setVisibility(View.GONE);
                        mResults.addAll(games);
                        Log.d(TAG, "onResponse" + mResults.toString());
                        mResultsAdapter.notifyDataSetChanged();
                    }
                    else {
                        mNoResultsTextView.setVisibility(View.VISIBLE);
                        mProgressBar.setVisibility(View.GONE);
                        Log.d(TAG, "onResponse: no results");
                    }
                }
            }
        });

        return view;
    }
}