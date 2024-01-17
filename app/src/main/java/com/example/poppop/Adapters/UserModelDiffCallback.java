package com.example.poppop.Adapters;

import androidx.recyclerview.widget.DiffUtil;

import com.example.poppop.Model.UserModel;

import java.util.List;
import java.util.Objects;

public class UserModelDiffCallback extends DiffUtil.Callback {

    private final List<UserModel> oldList;
    private final List<UserModel> newList;

    public UserModelDiffCallback(List<UserModel> oldList, List<UserModel> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return Objects.equals(oldList.get(oldItemPosition).getUserId(), newList.get(newItemPosition).getUserId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
    }
}
