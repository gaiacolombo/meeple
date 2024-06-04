package it.unimib.lapecorafaquack.adapter;

import android.app.Application;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import it.unimib.lapecorafaquack.R;
import it.unimib.lapecorafaquack.model.Game;

public class GamesAdapter extends RecyclerView.Adapter<GamesAdapter.GamesViewHolder> {

    private static final String TAG = "GamesAdapter";
    private View view;

    public interface OnItemClickListener {
        void onItemClick(Game game);
    }

    private List<Game> mGamesList;
    private OnItemClickListener onItemClickListener;
    private int layout;

    public GamesAdapter(List<Game> gamesList, OnItemClickListener onItemClickListener, int layout, Application application) {
        this.mGamesList = gamesList;
        this.onItemClickListener = onItemClickListener;
        this.layout = layout;
        Log.d(TAG, "application " + application.toString());
    }

    @NonNull
    @Override
    public GamesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new GamesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GamesViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: " + position);
        holder.bind(mGamesList.get(position));
    }

    @Override
    public int getItemCount() {
        if(mGamesList != null) {
            return mGamesList.size();
        }
        return 0;
    }

    public class GamesViewHolder extends RecyclerView.ViewHolder {

        private final TextView textViewGamesName;
        private ImageView imageViewGamesImageUrl;


        public GamesViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textViewGamesName = itemView.findViewById(R.id.games_name);
            this.imageViewGamesImageUrl = itemView.findViewById(R.id.games_image_url);
        }
        
        public void bind(Game game) {
            Picasso.with(imageViewGamesImageUrl.getContext()).load(game.getImage_url()).resize(1500, 900).into(imageViewGamesImageUrl);
            this.textViewGamesName.setText(game.getName());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(game);
                }
            });
        }
    }
}