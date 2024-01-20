package com.example.poppop.Adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.example.poppop.Activities.EditProfileActivity;
import com.example.poppop.Model.ImageModel;
import com.example.poppop.Model.UserModel;
import com.example.poppop.R;
import com.example.poppop.Utils.StorageUtils;

import java.util.ArrayList;
import java.util.List;

public class ImageGridAdapter extends BaseAdapter {

    private static final int MAX_IMAGES = 6;
    private Context context;
    private List<ImageModel> imageModels;
    private Activity activity;
    private UserModel userModel;

    public ImageGridAdapter(Context context, List<ImageModel> imageModels, Activity activity, UserModel userModel) {
        this.context = context;
        if (imageModels != null) {
            this.imageModels = imageModels;
        } else {
            this.imageModels = new ArrayList<>();
        }
        this.userModel = userModel;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return MAX_IMAGES;
    }

    @Override
    public Object getItem(int position) {
        return position < imageModels.size() ? imageModels.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_image_card, parent, false);
            holder = new ViewHolder();
            holder.cardView = convertView.findViewById(R.id.image_cardview);
            holder.imageView = convertView.findViewById(R.id.imageView);
            holder.deleteBtn = convertView.findViewById(R.id.deleteBtn);
            holder.addBtn = convertView.findViewById(R.id.addBtn);
            convertView.setTag(holder);

            holder.cardView.setOnClickListener(v -> {
                if (position < imageModels.size()) {
                    Log.d("URL", imageModels.get(position).getUrl());
                } else {
                    Log.d("URL", "Empty");
                }
            });

            holder.addBtn.setOnClickListener(v -> {
                // Start the image picker activity directly in TestActivity
                ((EditProfileActivity) activity).startImagePicker();
            });

            holder.deleteBtn.setOnClickListener(v -> {
                ImageModel imageModelToDelete = imageModels.get(position);
                StorageUtils.deleteImageFromStorage(userModel, imageModelToDelete)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Handle successful deletion
                                // You can update the UI or perform any other actions
                                Log.d("Delete", "Image deleted successfully");
                                // If needed, you can also remove the item from the adapter
                                imageModels.remove(imageModelToDelete);
                                notifyDataSetChanged(); // Notify the adapter about the change
                            } else {
                                // Handle deletion failure
                                Exception exception = task.getException();
                                Log.e("Delete", "Image deletion failed", exception);
                                // Handle the exception
                            }
                        });
            });

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (position < imageModels.size()) {
            Glide.with(context)
                    .load(imageModels.get(position).getUrl())
                    .into(holder.imageView);

            holder.addBtn.setVisibility(View.GONE);
            holder.imageView.setVisibility(View.VISIBLE);
            holder.deleteBtn.setVisibility(View.VISIBLE);
        } else {
            holder.cardView.setBackgroundResource(R.drawable.dotted_rectangle);
            holder.imageView.setVisibility(View.GONE);
            holder.deleteBtn.setVisibility(View.GONE);
            holder.addBtn.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    static class ViewHolder {
        CardView cardView;
        ImageView imageView;
        AppCompatImageButton deleteBtn, addBtn;
    }
}
