package com.example.poppop.Adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.poppop.Model.UserModel;
import com.example.poppop.R;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private final String TAG = "UserAdaptedr";
    private List<UserModel> userList;
    private OnUserClickListener onUserClickListener;
    private final String REGULAR = "REGULAR";
    private final String PREMIUM = "PREMIUM";
    private final String BAN = "BANNED";
    private final String NOTBANNED = "ACTIVE";



    public UserAdapter(List<UserModel> userList, OnUserClickListener onUserClickListener) {
        this.userList = userList;
        this.onUserClickListener = onUserClickListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_in_list, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserModel user = userList.get(position);

        Boolean premium = user.getPremium();
        String role = "Role: " + ((premium != null && premium) ? PREMIUM : REGULAR);

        holder.role.setText(role);
        String status;
        if(user.getBanned() != null && user.getBanned()){
            status = "Status: " + BAN;
        }else {
            status = "Status: " + NOTBANNED;
        }

        holder.status.setText(status);

        holder.userName.setText(user.getName());
        holder.userId.setText(user.getUserId());

        // Handle item click
        holder.itemView.setOnClickListener(v -> {
            if (onUserClickListener != null) {
                onUserClickListener.onUserClick(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    // Method to update the user list and refresh the adapter
    public void updateUserList(List<UserModel> newUserList) {
        userList.clear();
        userList.addAll(newUserList);
        notifyDataSetChanged();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userName, userId, role, status;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            userId = itemView.findViewById(R.id.userId);
            role = itemView.findViewById(R.id.role);
            status = itemView.findViewById(R.id.status);
        }
    }

    public interface OnUserClickListener {
        void onUserClick(UserModel user);
    }
}
