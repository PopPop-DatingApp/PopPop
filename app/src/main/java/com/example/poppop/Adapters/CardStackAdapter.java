package com.example.poppop.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.poppop.Model.UserModel;
import com.example.poppop.R;

import java.util.List;

public class CardStackAdapter extends RecyclerView.Adapter<CardStackAdapter.ViewHolder> {

    private List<UserModel> userModelList;
    private int i;

    public CardStackAdapter(List<UserModel> userModelList) {
        this.userModelList = userModelList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        UserModel userModel = userModelList.get(position);
        holder.name.setText(userModel.getName());
        holder.age.setText(String.valueOf(userModel.getAge()));
        if(userModel.getImage_list() != null && userModel.getImage_list().size() != 0){

            Glide.with(holder.image)
                    .load(userModel.getImage_list().get(i).getUrl())
                    .into(holder.image);
            holder.image.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    float x = event.getX();

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            // Determine whether the click is on the left or right side
                            if (x < v.getWidth() / 4) {
                                if(i != 0){
                                    i--;
                                    Glide.with(holder.image)
                                            .load(userModel.getImage_list().get(i).getUrl())
                                            .into(holder.image);
                                }
                            } else if (x > v.getWidth() * 0.75) {
                                if(i != userModel.getImage_list().size() -1){
                                    i++;
                                    Glide.with(holder.image)
                                            .load(userModel.getImage_list().get(i).getUrl())
                                            .into(holder.image);
                                }
                            }
                            return true;
                    }
                    return false;
                }
            });
        }
    }

    public List<UserModel> getUserModels() {
        return userModelList;
    }

    public void setUserModels(List<UserModel> userModels) {
        this.userModelList = userModels;
    }
    @Override
    public int getItemCount() {
        return userModelList != null ? userModelList.size() : 0;
    }

    public void setUserModelList(List<UserModel> userModelList) {
        this.userModelList = userModelList;
    }

    public List<UserModel> getUserModelList() {
        return userModelList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView age;
        ClickableImageView image;

        ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.item_name);
            age = view.findViewById(R.id.item_age);
            image = view.findViewById(R.id.item_image);
        }
    }
}

