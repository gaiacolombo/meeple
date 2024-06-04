package it.unimib.lapecorafaquack.ui.gameDetail;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.squareup.picasso.Picasso;

import java.util.List;

import it.unimib.lapecorafaquack.R;
import it.unimib.lapecorafaquack.model.Game;
import it.unimib.lapecorafaquack.model.User;


public class GameDetailFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "GameDetailsFragment";

    private Game game;

    private String email;
    private User utente;

    private Button buttonToPlay;
    private Button buttonPlayed;
    private Button buttonFavourite;

    private GameDetailViewModel gameDetailViewModel;

    public GameDetailFragment() {
    }

    public static GameDetailFragment newInstance() {
        GameDetailFragment fragment = new GameDetailFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        game = GameDetailFragmentArgs.fromBundle(getArguments()).getGame();
        Log.d(TAG, "gameId " + game.getId());

        gameDetailViewModel = new ViewModelProvider(this).get(GameDetailViewModel.class);
        utente = gameDetailViewModel.getCurrentUser();

        Log.d(TAG, "user: " + utente.getEmail());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_details, container, false);

        TextView titleTextView = view.findViewById(R.id.game_title);
        titleTextView.setText(game.getName());

        ImageView imageView = view.findViewById(R.id.game_image);
        Picasso.with(imageView.getContext()).load(game.getImage_url()).resize(800, 500).into(imageView);

        TextView descriptionTextView = view.findViewById(R.id.game_description);
        descriptionTextView.setText(game.getDescription_preview().trim());

        TextView playersTextView = view.findViewById(R.id.game_number_players);
        playersTextView.setText(game.getMin_players() + " - " + game.getMax_players() + " " + getResources().getString(R.string.giocatori));

        TextView playTimeTextView = view.findViewById(R.id.game_play_time);
        playTimeTextView.setText(game.getMin_playtime() + " - " + game.getMax_playtime() + " " + getResources().getString(R.string.minuti));

        TextView minAgeTextView = view.findViewById(R.id.game_min_age);
        minAgeTextView.setText(getResources().getString(R.string.da) +" " + game.getMin_age()+ " " + getResources().getString(R.string.anni));

        game.convertCategories(game.getAPICategories());

        ChipGroup chipGroup = view.findViewById(R.id.chipGroup);
        List<String> categories = game.getMCategories();
        for(int i = 0; i < categories.size(); i++){
            Log.d(TAG, gameDetailViewModel.getCategoryName(categories.get(i)));
            Chip chip = new Chip(this.getContext());
            chip.setText(gameDetailViewModel.getCategoryName(categories.get(i)));
            chip.setFocusable(false);
            chip.setClickable(false);
            chipGroup.addView(chip);
        }

        buttonToPlay = view.findViewById(R.id.button_addToPlayGames);
        buttonPlayed = view.findViewById(R.id.button_addPlayedGames);
        buttonFavourite = view.findViewById(R.id.button_addFavouriteGames);

        if(utente.getToPlayGames().contains(game.getId())) {
            buttonToPlay.setText(buttonToPlay.getText() + " ✓");
            buttonToPlay.setTextSize(13);
        }
        if(utente.getPlayedGames().contains(game.getId())) {
            buttonPlayed.setText(buttonPlayed.getText() + " ✓");
            buttonToPlay.setTextSize(13);
        }
        if(utente.getFavouriteGames().contains(game.getId())) {
            buttonFavourite.setText(buttonFavourite.getText() + " ✓");
            buttonToPlay.setTextSize(13);
        }

        buttonToPlay.setOnClickListener(this);
        buttonPlayed.setOnClickListener(this);
        buttonFavourite.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        addGame();
        switch (v.getId()) {
            case R.id.button_addToPlayGames:
                if(utente.getToPlayGames().contains(game.getId())) {
                    buttonToPlay.setText(getString(R.string.da_giocare));
                    removeToPlayGames();
                }
                else {
                    buttonToPlay.setText(buttonToPlay.getText() + " ✓");
                    buttonToPlay.setTextSize(13);
                    addToPlayGames();
                }
                break;
            case R.id.button_addPlayedGames:
                if(utente.getPlayedGames().contains(game.getId())) {
                    buttonPlayed.setText(getString(R.string.giocato));
                    removePlayedGames();
                }
                else {
                    buttonPlayed.setText(buttonPlayed.getText() + " ✓");
                    buttonToPlay.setTextSize(13);
                    addPlayedGames();
                }
                break;
            case R.id.button_addFavouriteGames:
                if(utente.getFavouriteGames().contains(game.getId())) {
                    buttonFavourite.setText(getString(R.string.preferito));
                    removeFavouriteGames();
                }
                else {
                    buttonFavourite.setText(buttonFavourite.getText() + " ✓");
                    buttonToPlay.setTextSize(13);
                    addFavouriteGames();
                }
                break;
        }
    }

    private void addFavouriteGames() {
        utente.addFavouriteGame(game.getId());
        gameDetailViewModel.addFavouriteGamesToUser(utente.getEmail(), game.getId());
    }

    private void addPlayedGames() {
        Log.d(TAG, "addPlayedGames game id: " + game.getId());
        Log.d(TAG, "addPlayedGames user email: " + utente.getEmail());
        utente.addPlayedGame(game.getId());
        gameDetailViewModel.addPlayedGamesToUser(utente.getEmail(), game.getId());
    }

    private void addToPlayGames() {
        Log.d(TAG, "addToPlayGames utente: " + utente.toString());
        utente.addToPlayGame(game.getId());
        gameDetailViewModel.addToPlayGamesToUser(utente.getEmail(), game.getId());
    }

    private void removeFavouriteGames() {
        utente.removeFavouriteGame(game.getId());
        gameDetailViewModel.removeFavouriteGamesToUser(utente.getEmail(), game.getId());
    }

    private void removePlayedGames() {
        utente.removePlayedGame(game.getId());
        gameDetailViewModel.removePlayedGamesToUser(utente.getEmail(), game.getId());
    }

    private void removeToPlayGames() {
        utente.removeToPlayGame(game.getId());
        gameDetailViewModel.removeToPlayGamesToUser(utente.getEmail(), game.getId());
    }

    private void addGame() {
        gameDetailViewModel.insertGame(game);
    }
}