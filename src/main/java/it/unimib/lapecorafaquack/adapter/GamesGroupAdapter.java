package it.unimib.lapecorafaquack.adapter;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Application;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import it.unimib.lapecorafaquack.R;
import it.unimib.lapecorafaquack.model.Game;
import it.unimib.lapecorafaquack.model.GamesGroup;
import it.unimib.lapecorafaquack.ui.friendDetail.FriendDetailsFragmentDirections;
import it.unimib.lapecorafaquack.ui.home.HomeFragmentDirections;
import it.unimib.lapecorafaquack.ui.profilo.ProfiloFragmentDirections;
import it.unimib.lapecorafaquack.ui.search.SearchFragmentDirections;

public class GamesGroupAdapter extends RecyclerView.Adapter<GamesGroupAdapter.ParentViewHolder> {

    private static final String TAG = "GamesGroupAdapter";

    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private List<GamesGroup> itemList;
    private View view;
    private Application application;

    public GamesGroupAdapter(List<GamesGroup> itemList, View view, Application application)
    {
        this.itemList = itemList;
        this.view = view;
        this.application = application;
    }

    @NonNull
    @Override
    public ParentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup,int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.games_group_item, viewGroup, false);

        return new ParentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParentViewHolder parentViewHolder, int position) {

        GamesGroup parentItem = itemList.get(position);
        parentViewHolder.ParentItemTitle.setText(parentItem.getGroupTitle());
        LinearLayoutManager layoutManager = new LinearLayoutManager(parentViewHolder.ChildRecyclerView.getContext(),
                LinearLayoutManager.HORIZONTAL, false);

        layoutManager.setInitialPrefetchItemCount(parentItem.getGamesList().size());

        GamesAdapter childItemAdapter = new GamesAdapter(parentItem.getGamesList(), new GamesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Game games) {
                Log.d(TAG, ": onItemClick: " + games.toString());

                HomeFragmentDirections.ActionExploreToGameDetailsFragment actionHome = HomeFragmentDirections.actionExploreToGameDetailsFragment(games);
                FriendDetailsFragmentDirections.ActionFriendDetailsFragmentToGameDetailsFragment actionFriend = FriendDetailsFragmentDirections.actionFriendDetailsFragmentToGameDetailsFragment(games);
                ProfiloFragmentDirections.ActionExploreToGameDetailsFragment actionProfile = ProfiloFragmentDirections.actionExploreToGameDetailsFragment(games);
                SearchFragmentDirections.ActionSearchGameDetails actionSearch = SearchFragmentDirections.actionSearchGameDetails(games);
                try {
                    Navigation.findNavController(view).navigate(actionHome);
                }
                catch (Exception e) {
                    try {
                        Navigation.findNavController(view).navigate(actionSearch);
                    }
                    catch (Exception e1) {
                        try {
                            Navigation.findNavController(view).navigate(actionProfile);
                        }
                        catch (Exception e2) {
                            Navigation.findNavController(view).navigate(actionFriend);
                        }
                    }
                }

            }
        }, R.layout.games_list_item, application);
        parentViewHolder.ChildRecyclerView.setLayoutManager(layoutManager);
        parentViewHolder.ChildRecyclerView.setAdapter(childItemAdapter);
        parentViewHolder.ChildRecyclerView.setRecycledViewPool(viewPool);
    }

    @Override
    public int getItemCount()
    {
        return itemList.size();
    }

    class ParentViewHolder extends RecyclerView.ViewHolder {

        private TextView ParentItemTitle;
        private RecyclerView ChildRecyclerView;

        ParentViewHolder(final View itemView)
        {
            super(itemView);

            ParentItemTitle = itemView.findViewById(R.id.parent_item_title);
            ChildRecyclerView = itemView.findViewById(R.id.child_recyclerview);
        }
    }
}
