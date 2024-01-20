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
    private List<UserModel> userList;
    private OnUserClickListener onUserClickListener;
    private final String REGULAR = "REGULAR";
    private final String PREMIUM = "PREMIUM";


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
        String status = "Status: ";
        String role = "Role: " + (user.getPremium() ? REGULAR : PREMIUM);
        holder.userName.setText(user.getName());
        holder.userId.setText(user.getUserId());
        holder.role.setText(role);
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

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userName, userId, role;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            userId = itemView.findViewById(R.id.userId);
            role = itemView.findViewById(R.id.role);
        }
    }

    public interface OnUserClickListener {
        void onUserClick(UserModel user);
    }
}
