package it.unimib.lapecorafaquack.adapter;

import android.app.Application;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.unimib.lapecorafaquack.R;
import it.unimib.lapecorafaquack.model.User;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersViewHolder> {

    private static final String TAG = "UsersAdapter";
    private View view;

    public interface OnItemClickListener {
        void onItemClick(User user);
    }

    private List<User> mUsersList;
    private OnItemClickListener onItemClickListener;
    private int layout;

    public UsersAdapter(List<User> usersList, OnItemClickListener onItemClickListener, int layout, Application application) {
        this.mUsersList = usersList;
        this.onItemClickListener = onItemClickListener;
        this.layout = layout;
        Log.d(TAG, "application " + application.toString());
    }

    public void updateData(List<User> usersList) {
        this.mUsersList = usersList;
        Log.d(TAG, "updateData: " + mUsersList.toString());
    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new UsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: " + position);
        holder.bind(mUsersList.get(position));
    }

    @Override
    public int getItemCount() {
        if(mUsersList != null) {
            return mUsersList.size();
        }
        return 0;
    }

    public class UsersViewHolder extends RecyclerView.ViewHolder {

        private final TextView textViewUsername;
        private TextView textViewEmail;


        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);

            this.textViewUsername = itemView.findViewById(R.id.usermame);
            this.textViewEmail = itemView.findViewById(R.id.email);
        }

        public void bind(User user) {
            this.textViewUsername.setText(user.getUsername());
            this.textViewEmail.setText(user.getEmail());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(user);
                    Log.d(TAG, "OnItemClick " + user.toString());
                }
            });
        }
    }
}